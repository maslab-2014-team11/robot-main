package control;

import devices.actuators.ExtendedServo;
import devices.sensor.ExtendedAnalogInput;

public class FrontDispenserController {

	private static final double SERVO_NEUTRAL_ANGLE = 100;
	private static final double SERVO_TOP_ANGLE = 150;
	private static final double SERVO_BOTTOM_ANGLE = 60;

	private static final long SERVO_DISPENSE_DELAY = 1000;
	private static final long SERVO_RETURN_DELAY = 800;
	private static final float BREAK_BEAM_THRESHOLD = 200;

	private final ExtendedServo servo;
	private final ExtendedAnalogInput breakBeam;

	public FrontDispenserController(ExtendedAnalogInput breakBeam,
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

		this.servo.setAngle(angle);
		waitFor(SERVO_DISPENSE_DELAY);
		this.servo.setAngle(SERVO_NEUTRAL_ANGLE);
		waitFor(SERVO_RETURN_DELAY);
		return true;
	}

	public boolean hasBall() {
		return breakBeam.getValue() > BREAK_BEAM_THRESHOLD;
	}

	private static void waitFor(long servoDispenseDelay) {
		long now = System.currentTimeMillis();
		while (System.currentTimeMillis() - now < servoDispenseDelay) {
			long timeLeft = servoDispenseDelay
					- (now - System.currentTimeMillis());
			try {
				Thread.sleep(timeLeft - 1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
