package test;

import static robot.Constants.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import localization.CarrotPathFollower;
import localization.EncoderDeadReckoner;
import localization.GyroEncoderDeadReckoner;
import localization.PathFollower.Result;
import localization.PoseSource;
import robot.map.Coordinate;
import control.Drive;
import control.Motor;
import devices.Maple;
import devices.actuators.ExtendedCytron;
import devices.sensor.ExtendedEncoder;
import devices.sensor.ExtendedGyroscope;

public class SquareDriveTest {

	public static void main(String[] args) throws InterruptedException,
			FileNotFoundException {
		new SquareDriveTest().run();
	}

	private final ExtendedCytron leftMotor;
	private final ExtendedCytron rightMotor;
	private final ExtendedEncoder leftEnc;
	private final ExtendedEncoder rightEnc;
	private final Motor left;
	private final Motor right;
	private final ExtendedGyroscope gyro;

	public SquareDriveTest() throws FileNotFoundException {
		this.leftMotor = new ExtendedCytron(L_WHEEL_DIR_PIN, L_WHEEL_PWM_PIN);
		this.rightMotor = new ExtendedCytron(R_WHEEL_DIR_PIN, R_WHEEL_PWM_PIN);

		this.leftEnc = new ExtendedEncoder(L_ENCODER_TRIGGER_PIN,
				L_ENCODER_READ_PIN);
		this.rightEnc = new ExtendedEncoder(R_ENCODER_TRIGGER_PIN,
				R_ENCODER_READ_PIN);

		this.left = new Motor(leftMotor, leftEnc);
		this.right = new Motor(rightMotor, rightEnc);
		this.gyro = new ExtendedGyroscope(GYRO_SPI_PORT, GYRO_SS_PIN);
		//
		// System.setOut(new PrintStream(new
		// File("C:\\Users\\Alex\\Desktop\\carrot.log")));
	}

	private void run() throws InterruptedException {
		Maple m = Maple.getMaple();
		m.registerDevice(leftMotor);
		m.registerDevice(rightMotor);
		m.registerDevice(leftEnc);
		m.registerDevice(rightEnc);
		m.registerDevice(gyro);

		gyro.setOffset(GYRO_A_OFFSET);
		gyro.setTotalAngleDeg(0);

		leftMotor.setMaxMagnitudeOutput(0.2);
		rightMotor.setMaxMagnitudeOutput(0.2);
		m.start();

		PoseSource posSource = new GyroEncoderDeadReckoner(rightEnc, leftEnc,
				gyro);
		Drive d = new Drive(this.right, this.left, posSource);
		posSource.setCurrentPose(Coordinate.ORIGIN, 0);

		List<Coordinate> path = Arrays.asList(Coordinate.ORIGIN,
				new Coordinate(9.2029, 10.0000), new Coordinate(14.2754,
						30.2899), new Coordinate(22.6812, 52.0290),
				new Coordinate(37.3188, 65.2174), new Coordinate(58.7681,
						76.5217), new Coordinate(82.5362, 86.5217),
				new Coordinate(97.0290, 92.0290));

		CarrotPathFollower follower = new CarrotPathFollower(posSource, d, 30);
		follower.setPath(path);

		while (true) {
			Result r = follower.followPath();

			if (r == Result.Finished)
				System.exit(0);

			Thread.sleep(10);
		}
	}
}
