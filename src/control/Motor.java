package control;

import control.pid.PIDController;
import devices.actuators.ExtendedCytron;
import devices.sensor.ExtendedEncoder;

public class Motor {

	private enum State {
		Floating, Driving
	}

	private final ExtendedCytron motor;
	private final ExtendedEncoder encoder;

	private final PIDController speedPID;
	private final MotorSpeedPIDAdapter speedPIDAdapter;

	private State state;

	private double lastSpeed;

	public Motor(ExtendedCytron motor, ExtendedEncoder encoder) {
		this.motor = motor;
		this.encoder = encoder;

		this.speedPIDAdapter = new MotorSpeedPIDAdapter(motor, encoder);

		this.speedPID = new PIDController(.0002, .001, 0, speedPIDAdapter);

		this.state = State.Floating;
	}

	public void brake() {
		if (this.state != State.Floating) {
			this.state = State.Floating;
			motor.setSpeed(0);
		}
		System.out.println("brake");
	}

	public void setAngularSpeed(double degPerSec) {
		if (this.state != State.Driving) {
			this.state = State.Driving;
			this.speedPID.reset();
			this.lastSpeed = degPerSec;
		}

		// reset the PID controller if the speed has changed a lot
		if (Math.abs(degPerSec - lastSpeed) > 100)
			this.speedPID.reset();
		this.lastSpeed = degPerSec;
		speedPIDAdapter.setTargetSpeed(degPerSec);
	}

	public void step() {
		switch (this.state) {
		case Driving:
			this.speedPID.step();
			break;
		case Floating:
			break;
		}
	}
}
