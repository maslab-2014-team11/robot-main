package localization;

import robot.map.Coordinate;
import devices.sensors.Encoder;

import static robot.Constants.*;

public class EncoderDeadReckoner implements PoseSource {

	private final Encoder rightEnc, leftEnc;
	private double x, y, headingRadians;
	private double lastRight, lastLeft;

	public EncoderDeadReckoner(Encoder rightEnc, Encoder leftEnc) {
		this.rightEnc = rightEnc;
		this.leftEnc = leftEnc;
	}

	@Override
	public Coordinate getPosition() {
		return new Coordinate(x, y);
	}

	@Override
	public double getHeading() {
		double heading = Math.toDegrees(headingRadians);
		if (heading > 180)
			heading -= 360;
		return heading;
	}

	@Override
	public void setCurrentPose(Coordinate position, double heading) {
		this.x = position.x;
		this.y = position.y;
		this.headingRadians = heading;
	}

	@Override
	public void step() {
		// innerRadius * angle = innerDist
		// outerRadius * angle = outerDist
		// outerRadius = innerRadius + WHEEL_SEPARATION
		// angle * (innerRadius + WHEEL_SEPARATION) = outerDist
		// angle * WHEEL_SEPARATION = outerDist - innerDist

		double rightDist = rightEnc.getTotalAngularDistance() * WHEEL_RADIUS
				- lastRight;
		double leftDist = leftEnc.getTotalAngularDistance() * WHEEL_RADIUS
				- lastLeft;
		double dTheta = (rightDist - leftDist) / WHEEL_SEPARATION;
		double arcLen = (rightDist + leftDist) / 2;

		x += Math.cos(headingRadians + dTheta / 2) * arcLen;
		y += Math.sin(headingRadians + dTheta / 2) * arcLen;
		headingRadians = (headingRadians + dTheta) % (2 * Math.PI);

		lastRight += rightDist;
		lastLeft += leftDist;
	}

}
