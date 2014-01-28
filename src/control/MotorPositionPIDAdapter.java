package control;

import control.pid.PIDInputOutput;
import devices.actuators.ExtendedCytron;
import devices.sensor.ExtendedEncoder;

public class MotorPositionPIDAdapter implements PIDInputOutput {

	private final ExtendedCytron motor;
	private final ExtendedEncoder encoder;

	private double targetPositionDeg;

	public MotorPositionPIDAdapter(ExtendedCytron motor, ExtendedEncoder encoder) {
		this.motor = motor;
		this.encoder = encoder;
	}

	public void setTargetPositionDeg(double targetPositionDeg) {
		this.targetPositionDeg = targetPositionDeg;
	}

	@Override
	public double input() {
		double value = encoder.getTotalAngularDistanceDeg() - targetPositionDeg;
		System.out.println("delta: " + value);
		return value;
	}

	@Override
	public void output(double value) {
		System.out.println("output: "+value);
		motor.setSpeed(value);
	}

}
