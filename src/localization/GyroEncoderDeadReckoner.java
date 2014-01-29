package localization;

import robot.map.Coordinate;
import devices.sensor.ExtendedGyroscope;
import devices.sensors.Encoder;
import static robot.Constants.*;

public class GyroEncoderDeadReckoner implements PoseSource {

	private final Encoder rightEnc, leftEnc;
	private double x, y, headingRadians;
	private double lastRight, lastLeft;
	private final ExtendedGyroscope gyro;
	private double lastHeading;
	private boolean isInitialized;

	public GyroEncoderDeadReckoner(Encoder rightEnc, Encoder leftEnc,
			ExtendedGyroscope gyro) {
		this.rightEnc = rightEnc;
		this.leftEnc = leftEnc;
		this.gyro = gyro;
		this.isInitialized = false;
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
		if (!isInitialized) {
			this.lastRight = rightEnc.getTotalAngularDistance();
			this.lastLeft = leftEnc.getTotalAngularDistance();
			this.lastHeading = gyro.getTotalAngle();
			isInitialized = true;
		}
		// innerRadius * angle = innerDist
		// outerRadius * angle = outerDist
		// outerRadius = innerRadius + WHEEL_SEPARATION
		// angle * (innerRadius + WHEEL_SEPARATION) = outerDist
		// angle * WHEEL_SEPARATION = outerDist - innerDist

		double rightDist = rightEnc.getTotalAngularDistance() * WHEEL_RADIUS
				- lastRight;
		double leftDist = leftEnc.getTotalAngularDistance() * WHEEL_RADIUS
				- lastLeft;
		double dTheta = gyro.getTotalAngle() - lastHeading;
		double arcLen = (rightDist + leftDist) / 2;

		x += Math.cos(headingRadians + dTheta / 2) * arcLen;
		y += Math.sin(headingRadians + dTheta / 2) * arcLen;
		headingRadians = (headingRadians + dTheta) % (2 * Math.PI);
		this.lastHeading += dTheta;

		lastRight += rightDist;
		lastLeft += leftDist;
	}

}
