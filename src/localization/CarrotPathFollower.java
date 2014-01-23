package localization;

import java.util.LinkedList;
import java.util.List;

import robot.Constants;
import control.MotorSpeedPIDAdapter;

public class CarrotPathFollower implements PathFollower {

	public interface PositionSource {
		public Coordinate getPos();

		public double getHeadingDeg();
	}

	public static class Coordinate {
		public final double x, y;

		public Coordinate(double x, double y) {
			this.x = x;
			this.y = y;
		}
	}

	private static final double MIN_FORWARD_SPEED = 5; // cm/sec

	private static final double MAX_FORWARD_SPEED = 30; // cm/sec

	private static final double MAX_ANGULAR_SPEED = 45; // deg/sec

	private final List<Coordinate> path;
	private final PositionSource pos;
	private final double circleRadiusCm;
	private final MotorSpeedPIDAdapter pidLeft;
	private final MotorSpeedPIDAdapter pidRight;

	public CarrotPathFollower(PositionSource pos, MotorSpeedPIDAdapter pidLeft,
			MotorSpeedPIDAdapter pidRight, double circleRadiusCm) {
		this.path = new LinkedList<>();
		this.pos = pos;
		this.circleRadiusCm = circleRadiusCm;
		this.pidLeft = pidLeft;
		this.pidRight = pidRight;
	}

	@Override
	public void setPath(List<Coordinate> newPath) {
		synchronized (path) {
			path.clear();
			path.addAll(newPath);
		}
	}

	@Override
	public void addToPath(List<Coordinate> newPath) {
		synchronized (path) {
			path.addAll(newPath);
		}
	}

	@Override
	public void addToPath(Coordinate nextPoint) {
		synchronized (path) {
			path.add(nextPoint);
		}
	}

	@Override
	public Result followPath() {
		Coordinate robotPos = pos.getPos();
		synchronized (path) {
			switch (path.size()) {
			case 0:
				pidLeft.setTargetSpeed(0);
				pidRight.setTargetSpeed(0);
				return Result.Finished;
			case 1:
				moveTowards(path.get(0));
				if (dist(path.get(0), robotPos) < circleRadiusCm / 2)
					path.remove(0);
				return Result.Driving;
			default:
				Coordinate target = path.get(0);
				Coordinate next = path.get(1);

				Coordinate carrot = getCarrot(robotPos, circleRadiusCm, target,
						next);
				moveTowards(carrot);
				if (dist(carrot, next) < circleRadiusCm / 3)
					path.remove(0);
				else if (dist(robotPos, next) < circleRadiusCm / 2)
					path.remove(0);
				return Result.Driving;
			}
		}
	}

	private void moveTowards(Coordinate target) {
		System.out.format("move: %5.2f, %5.2f\n", target.x, target.y);
		Coordinate robotPos = pos.getPos();

		double targetAngleRad = Math.atan2(target.y - robotPos.y, target.x
				- robotPos.x);

		double deltaAngle = normalizeAngleDeg(targetAngleRad * 180 / Math.PI
				- pos.getHeadingDeg());

		double speedLeft, speedRight;
		if (Math.abs(deltaAngle) < 25) {
			double forwardSpeed = Math.max(
					Math.min(dist(robotPos, target), MAX_FORWARD_SPEED),
					MIN_FORWARD_SPEED);
			double forwardDegPerSec = forwardSpeed
					/ Constants.WHEEL_CIRCUMFERENCE * 360;

			double turnDegPerSec = deltaAngle;
			speedLeft = forwardDegPerSec - turnDegPerSec;
			speedRight = forwardDegPerSec + turnDegPerSec;
		} else {

			double turnDegPerSec = Math.max(-MAX_ANGULAR_SPEED,
					Math.min(deltaAngle, MAX_ANGULAR_SPEED))
					* (Constants.WHEEL_SEPARATION / 2)
					/ (Constants.WHEEL_RADIUS);
			speedLeft = -turnDegPerSec;
			speedRight = turnDegPerSec;
		}

		synchronized (pidLeft) {
			synchronized (pidRight) {
				pidLeft.setTargetSpeed(speedLeft);
				pidRight.setTargetSpeed(speedRight);
			}
		}
	}

	private static double normalizeAngleRad(double angle) {
		angle = angle % (Math.PI * 2);
		if (angle > Math.PI)
			angle -= Math.PI;
		else if (angle < -Math.PI)
			angle += Math.PI;
		return angle;
	}

	private static double normalizeAngleDeg(double angle) {
		angle = angle % (360.);
		if (angle > 180)
			angle -= 360;
		else if (angle < -180)
			angle += 360;
		return angle;
	}

	private static Coordinate getCarrot(Coordinate robotLoc,
			double circleRadiusCm, Coordinate target, Coordinate next) {
		// the -/+ robotLoc.(x|y) is to shift things into and out of the robot
		// coordinate system

		double A = target.x - robotLoc.x;
		double B = target.y - robotLoc.y;
		double dx = target.x - next.x;
		double dy = target.y - next.y;

		double dr = Math.sqrt(dx * dx + dy * dy);

		double discr = circleRadiusCm * circleRadiusCm * dr * dr
				- (A * dy - B * dx) * (A * dy - B * dx);

		double numerPlus = -A * dx - B * dy;

		if (discr < 0) {
			return target;
		} else if (discr == 0) {
			double t = numerPlus / (dr * dr);
			return new Coordinate(target.x + t * dx, target.y + t * dy);
		}
		Coordinate[] possible = new Coordinate[2];
		double[] distances = new double[2];
		for (int i = 0; i <= 1; i++) {
			double t = (numerPlus + Math.sqrt(discr) * (2 * i - 1)) / (dr * dr);
			possible[i] = new Coordinate(target.x + t * dx, target.y + t * dy);
			distances[i] = dist(possible[i], next);
		}
		return possible[distances[0] < distances[1] ? 0 : 1];
	}

	private static double dist(Coordinate a, Coordinate b) {
		double dx = a.x - b.x, dy = a.y - b.y;
		return Math.sqrt(dx * dx + dy * dy);
	}
}
