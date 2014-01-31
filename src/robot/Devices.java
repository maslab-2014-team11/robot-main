package robot;

import static robot.Constants.*;
import localization.GyroEncoderDeadReckoner;
import localization.PoseSource;
import control.Drive;
import control.FrontRollerControl;
import control.GreenDispenserController;
import control.Motor;
import devices.Maple;
import devices.MapleDevice;
import devices.actuators.ExtendedCytron;
import devices.actuators.ExtendedServo3001HB;
import devices.sensor.ExtendedAnalogInput;
import devices.sensor.ExtendedEncoder;
import devices.sensor.ExtendedGyroscope;

public class Devices {

	private static Devices singleton;

	public static Devices get() {
		if (singleton == null) {
			Devices.singleton = new Devices();
			singleton.init();
			return singleton;
		}
		return singleton;
	}

	public final Maple maple;

	// Drive wheel stuff
	private final ExtendedEncoder leftEncoder, rightEncoder;
	private final ExtendedCytron leftCytron, rightCytron;
	private final Motor leftMotor, rightMotor;

	// Position stuff
	private final ExtendedGyroscope gyro;
	public final PoseSource poseSource;

	// High-level drive stuff
	public final Drive drive;

	// Front roller stuff
	private final ExtendedCytron frontRollerCytron;
	private final ExtendedAnalogInput frontRollerSensor;
	public final FrontRollerControl frontRoller;

	// Green dispenser servo stuff
	private final ExtendedServo3001HB greenDispenserServo;
	private final ExtendedAnalogInput greenDispenserBreakBeam;
	public final GreenDispenserController greenDispenser;

	private Devices() {
		this.maple = Maple.getMaple();

		// Drive wheel stuff
		this.leftEncoder = new ExtendedEncoder(L_ENCODER_TRIGGER_PIN,
				L_ENCODER_READ_PIN);
		this.rightEncoder = new ExtendedEncoder(R_ENCODER_TRIGGER_PIN,
				R_ENCODER_READ_PIN);
		this.leftCytron = new ExtendedCytron(L_WHEEL_DIR_PIN, L_WHEEL_PWM_PIN);
		this.rightCytron = new ExtendedCytron(R_WHEEL_DIR_PIN, R_WHEEL_PWM_PIN);

		this.leftMotor = new Motor(leftCytron, leftEncoder);
		this.rightMotor = new Motor(rightCytron, rightEncoder);

		// Position stuff
		this.gyro = new ExtendedGyroscope(GYRO_SPI_PORT, GYRO_SS_PIN);
		this.poseSource = new GyroEncoderDeadReckoner(rightEncoder,
				leftEncoder, gyro);

		// High-level drive stuff
		this.drive = new Drive(rightMotor, leftMotor, poseSource);

		// Front roller stuff
		this.frontRollerCytron = new ExtendedCytron(FRONT_ROLLER_DIR_PIN,
				FRONT_ROLLER_PWM_PIN);
		this.frontRollerSensor = new ExtendedAnalogInput(
				FRONT_ROLLER_SENSOR_PIN);
		this.frontRoller = new FrontRollerControl(frontRollerCytron,
				frontRollerSensor);

		// Green dispenser stuff
		this.greenDispenserServo = new ExtendedServo3001HB(
				GREEN_DISPENSER_SERVO_PIN);
		this.greenDispenserBreakBeam = new ExtendedAnalogInput(
				GREEN_DISPENSER_BREAK_BEAM_PIN);
		this.greenDispenser = new GreenDispenserController(
				greenDispenserBreakBeam, greenDispenserServo);
	}

	private void init() {
		// The ordering is kind of funny because I had trouble earlier with
		// adding analog input devices before motors. -Alex
		for (MapleDevice device : new MapleDevice[] { this.leftCytron,
				this.rightCytron, this.leftEncoder, this.rightEncoder,
				this.gyro, this.frontRollerCytron, this.greenDispenserServo,
				this.frontRollerSensor, this.greenDispenserBreakBeam }) {
			this.maple.registerDevice(device);
		}
		this.maple.start();
	}
}
