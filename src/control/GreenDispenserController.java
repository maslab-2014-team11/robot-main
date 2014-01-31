package control;

import devices.actuators.ExtendedServo;
import devices.sensor.ExtendedAnalogInput;

public class GreenDispenserController {

	private static final double SERVO_NEUTRAL_ANGLE = 120;
	private static final double SERVO_TOP_ANGLE = 280;
	private static final double SERVO_BOTTOM_ANGLE = 10;
	private static final long SERVO_DISPENSE_TIME = 1000;

	private static final long SERVO_DISPENSE_DELAY = 1000;
	private static final long SERVO_RETURN_DELAY = 800;
	private static final float BREAK_BEAM_THRESHOLD = 450;

	private final ExtendedServo servo;
	private final ExtendedAnalogInput breakBeam;

	public GreenDispenserController(ExtendedAnalogInput breakBeam,
			ExtendedServo servo) {
		this.servo = servo;
		this.breakBeam = breakBeam;
	}

	public boolean dispenseToTop() {
		return dispense(SERVO_TOP_ANGLE);
	}

	public boolean dispenseToBottom() {
		return dispense(SERVO_BOTTOM_ANGLE);
	}

	private boolean dispense(double angle) {
		boolean hasBall = hasBall();
		if (!hasBall)
			return false;
		double delta = angle - SERVO_NEUTRAL_ANGLE;

		for (int i = 0; i < 20; i++) {
			waitFor(SERVO_DISPENSE_TIME / 20);
			servo.setAngle(SERVO_NEUTRAL_ANGLE + delta * i / 10);
		}
		waitFor(SERVO_DISPENSE_DELAY);

		this.servo.setAngle(SERVO_NEUTRAL_ANGLE);
		waitFor(SERVO_RETURN_DELAY);
		return true;
	}

	public boolean hasBall() {
		return breakBeam.getValue() > BREAK_BEAM_THRESHOLD;
	}

	private static void waitFor(long servoDispenseDelay) {
		try {
			Thread.sleep(servoDispenseDelay);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
