package control;

import control.pid.PIDInputOutput;
import devices.actuators.ExtendedCytron;
import devices.sensor.ExtendedEncoder;

public class MotorSpeedPIDAdapter implements PIDInputOutput {

	private final ExtendedCytron motor;
	private final ExtendedEncoder encoder;
	private long lastTime;
	private double lastAngle;

	public double desiredSpeed;

	public MotorSpeedPIDAdapter(ExtendedCytron motor, ExtendedEncoder encoder) {
		this.motor = motor;
		this.encoder = encoder;
	}

	@Override
	public synchronized double input() {
		if (lastTime == 0) {
			lastTime = System.currentTimeMillis();
			lastAngle = encoder.getTotalAngularDistanceDeg();
			return 0;
		}

		while (System.currentTimeMillis() - lastTime < 50) {
			Thread.yield();
		}
		long time = System.currentTimeMillis();

		double newAngle = encoder.getTotalAngularDistanceDeg();

		double dwDt = 1000 * (newAngle - lastAngle) / (time - lastTime);
		this.lastTime = time;
		this.lastAngle = newAngle;
		System.out.println(dwDt);
		return dwDt - desiredSpeed;

	}

	public synchronized void setTargetSpeed(double speed) {
		desiredSpeed = speed;
	}

	@Override
	public void output(double value) {
		System.out.println("                      " + value);
		this.motor.setSpeed(value);
	}

}
