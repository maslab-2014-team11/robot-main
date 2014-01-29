package control;

import localization.PoseSource;
import control.pid.PIDController;
import control.pid.PIDInputOutput;
import robot.map.Coordinate;
import static robot.Constants.*;

public class Drive {

	private Motor right;
	private Motor left;

	private State state;

	private static final double TURN_FINISHED_THRESHOLD = 5.;
	private static final double FORWARD_ANGLE_THRESHOLD = 20.;

	private static final double DISTANCE_THRESHOLD = 5;

	public enum State {
		Stopped, Driving, Turning, Finished
	}

	private final PIDController anglePID;
	private final PIDInputOutput angleIO;

	private double anglePIDInput, anglePIDOutput;
	private PoseSource poseSource;

	public Drive(Motor right, Motor left, PoseSource pos) {
		this.right = right;
		this.left = left;
		this.poseSource = pos;

		this.state = State.Stopped;
		this.angleIO = new PIDInputOutput() {

			@Override
			public void output(double value) {
				Drive.this.anglePIDOutput = value;
			}

			@Override
			public double input() {
				return Drive.this.anglePIDInput;
			}
		};
		this.anglePID = new PIDController(.1, 0, 0, this.angleIO);
	}

	public State driveTowards(Coordinate target) {
		Coordinate currentLoc = poseSource.getPosition();
		double currentHeading = poseSource.getHeading();
		//System.out.format("drive: %s: %s, %.2f to %s\n", this.state,
				//currentLoc, currentHeading, target);
		Coordinate delta = currentLoc.vectorTo(target);

		double deltaAngle = normalizeAngleDeg(delta.angleFromOriginDeg()
				- currentHeading);
		//System.out.println("deltaAngle: " + deltaAngle);
		double deltaAngleMag = Math.abs(deltaAngle);
		double distance = currentLoc.distanceFrom(target);
		switch (this.state) {
		case Driving:
			if (Math.abs(deltaAngle) > FORWARD_ANGLE_THRESHOLD) {
				this.state = State.Turning;
			} else {
				double forwardSpeed = 20; // cm/sec
				double forwardDegPerSec = Math.toDegrees(forwardSpeed
						/ WHEEL_RADIUS);

				double turnSpeed = deltaAngle; // deg/sec
				double turnWheelDegPerSec = turnSpeed * WHEEL_SEPARATION / 2.
						* WHEEL_RADIUS;
				this.anglePIDInput = turnWheelDegPerSec;
				this.anglePID.step();
				double rightWheelSpeed = forwardDegPerSec - this.anglePIDOutput;
				double leftWheelSpeed = forwardDegPerSec + this.anglePIDOutput;
				//System.out.format("%6.2f,  %6.2f\n", forwardDegPerSec,
						//this.anglePIDOutput);
				this.right.setAngularSpeed(rightWheelSpeed);
				this.left.setAngularSpeed(leftWheelSpeed);
			}
			break;
		case Stopped:
		case Finished:
			if (distance > DISTANCE_THRESHOLD) {
				this.state = State.Driving;
				this.left.brake();
				this.right.brake();
				this.anglePID.reset();
			}
			break;
		case Turning:
			if (deltaAngleMag < TURN_FINISHED_THRESHOLD) {
				this.state = State.Stopped;
			} else {
				double turnSpeed = getTurnSpeed(deltaAngleMag)
						* Math.signum(deltaAngle);

				double turnWheelDegPerSec = turnSpeed * WHEEL_SEPARATION / 2.
						* WHEEL_RADIUS;
				double rightWheelSpeed = turnWheelDegPerSec;
				double leftWheelSpeed = -turnWheelDegPerSec;
				this.right.setAngularSpeed(rightWheelSpeed);
				this.left.setAngularSpeed(leftWheelSpeed);
				//System.out.println("angularSpeed: " + turnSpeed);
			}
			break;
		}

		if (distance < DISTANCE_THRESHOLD) {
			this.left.brake();
			this.right.brake();
			this.state = State.Finished;
		}

		this.poseSource.step();

		this.left.step();
		this.right.step();

		return this.state;
	}

	private double getTurnSpeed(double deltaAngleMag) {
		if (deltaAngleMag > 25)
			return 25;
		return (deltaAngleMag * deltaAngleMag) / 25;
	}

	private double normalizeAngleDeg(double angle) {
		angle = angle % 360.;
		if (angle > 180.)
			angle -= 360.;
		else if (angle < -180.)
			angle += 360.;
		return angle;
	}

	private double clip(double deltaAngleMag, double low, double high) {
		return Math.min(Math.max(deltaAngleMag, low), high);
	}

	public void brake() {
		this.state = State.Stopped;
		this.left.brake();
		this.right.brake();
	}
}
