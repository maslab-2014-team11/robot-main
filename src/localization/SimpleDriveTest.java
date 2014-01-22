package localization;

import java.util.ArrayList;
import java.util.List;

import robot.Constants;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;

import control.MotorSpeedPIDAdapter;
import localization.CarrotPathFollower.Coordinate;
import localization.CarrotPathFollower.PositionSource;
import devices.Maple;
import devices.actuators.ExtendedCytron;
import devices.port.Pin;
import devices.sensor.ExtendedEncoder;

public class SimpleDriveTest implements PositionSource {

	private static final double WHEEL_RADIUS = (3 + 7. / 8) * 2.54 / 2;
	private static final double WHEEL_SEPARATION = 30;

	public static void main(String[] args) throws InterruptedException {
		new SimpleDriveTest().drive();
	}

	private ExtendedEncoder leftEnc;
	private ExtendedEncoder rightEnc;
	private int x;
	private int y;
	private int angle;

	public void drive() {
		Maple m = Maple.getMaple();
		this.leftEnc = new ExtendedEncoder(new Pin(5), new Pin(4));
		this.rightEnc = new ExtendedEncoder(new Pin(8), new Pin(7));

		ExtendedCytron leftMotor = new ExtendedCytron(new Pin(9), new Pin(10));
		ExtendedCytron rightMotor = new ExtendedCytron(new Pin(11), new Pin(12));

		m.registerDevice(leftMotor);
		m.registerDevice(rightMotor);
		m.registerDevice(leftEnc);
		m.registerDevice(rightEnc);

		m.start();

		this.angle = 0;
		this.x = 0;
		this.y = 0;

		List<Coordinate> path = new ArrayList<>();
		path.add(new Coordinate(100, 0));
		path.add(new Coordinate(100, 50));
		path.add(new Coordinate(100, 100));

		MotorSpeedPIDAdapter pidLeft = new MotorSpeedPIDAdapter(leftMotor,
				leftEnc);
		MotorSpeedPIDAdapter pidRight = new MotorSpeedPIDAdapter(rightMotor,
				rightEnc);

		CarrotPathFollower follower = new CarrotPathFollower(this, pidLeft,
				pidRight, Constants.WHEEL_SEPARATION);

		while (true) {
			step();
			System.out.format("pos: %5.2f, %5.2f, %5.2f\n", x, y,
					getHeadingDeg());
			follower.followPath();

			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	void step() {

		// innerRadius * angle = innerDist
		// outerRadius * angle = outerDist
		// outerRadius = innerRadius + WHEEL_SEPARATION
		// angle * (innerRadius + WHEEL_SEPARATION) = outerDist
		// angle * WHEEL_SEPARATION = outerDist - innerDist

		synchronized (rightEnc) {
			synchronized (leftEnc) {

				double rightDist = rightEnc.getTotalAngularDistance()
						* WHEEL_RADIUS;
				double leftDist = leftEnc.getTotalAngularDistance()
						* WHEEL_RADIUS;
				double dTheta = (rightDist - leftDist) / WHEEL_SEPARATION;
				double arcLen = (rightDist + leftDist) / 2 * dTheta;
				x += Math.cos(dTheta + angle) * arcLen;
				y += Math.sin(dTheta + angle) * arcLen;
				angle += dTheta;
				System.out.println("x: " + x + "   y:" + y);
			}
		}
	}

	@Override
	public Coordinate getPos() {
		return new Coordinate(x, y);
	}

	@Override
	public double getHeadingDeg() {
		return this.angle * 180 / Math.PI;
	}
}
