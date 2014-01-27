package localization;

import java.util.LinkedList;
import java.util.List;

import robot.map.Coordinate;
import control.Drive;
import control.Drive.State;

public class CarrotPathFollower implements PathFollower {

	private final List<Coordinate> path;
	private final PoseSource pos;
	private final double circleRadiusCm;
	private final Drive drive;

	public CarrotPathFollower(PoseSource pos, Drive drive, double circleRadiusCm) {
		this.path = new LinkedList<>();
		this.pos = pos;
		this.circleRadiusCm = circleRadiusCm;
		this.drive = drive;
	}

	@Override
	public void setPath(List<Coordinate> newPath) {
		synchronized (path) {
			path.clear();
			path.addAll(newPath);
		}
	}

	@Override
	public Result followPath() {
		Coordinate robotPos = pos.getPosition();
		synchronized (path) {
			switch (path.size()) {
			case 0:
				drive.brake();
				return Result.Finished;
			case 1:
				Drive.State driveState = drive.driveTowards(path.get(0));
				if (driveState == State.Finished) {
					path.remove(0);
					return Result.Finished;
				}
				return Result.Driving;
			default:
				Coordinate target = path.get(0);
				Coordinate next = path.get(1);

				Coordinate carrot = getCarrot(robotPos, circleRadiusCm, target,
						next);
				driveState = drive.driveTowards(carrot);
				if (driveState == State.Finished)
					path.remove(0);
				return Result.Driving;
			}
		}
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
			distances[i] = possible[i].distanceFrom(next);
		}
		Coordinate carrot = possible[distances[0] < distances[1] ? 0 : 1];
		if (robotLoc.distanceFrom(carrot) > robotLoc.distanceFrom(next))
			return next;
		else
			return carrot;
	}
}
