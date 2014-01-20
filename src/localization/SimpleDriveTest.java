package localization;

import devices.Maple;
import devices.actuators.ExtendedCytron;
import devices.port.Pin;
import devices.sensor.ExtendedEncoder;

public class SimpleDriveTest {

	private static final double WHEEL_RADIUS = (3 + 7. / 8) * 2.54 / 2;
	private static final double WHEEL_SEPARATION = 30;

	public static void main(String[] args) throws InterruptedException {
		Maple m = Maple.getMaple();
		ExtendedEncoder leftEnc = new ExtendedEncoder(new Pin(5), new Pin(4));
		ExtendedEncoder rightEnc = new ExtendedEncoder(new Pin(8), new Pin(7));

		ExtendedCytron leftMotor = new ExtendedCytron(new Pin(9), new Pin(10));
		ExtendedCytron rightMotor = new ExtendedCytron(new Pin(11), new Pin(12));

		m.registerDevice(leftMotor);
		m.registerDevice(rightMotor);
		m.registerDevice(leftEnc);
		m.registerDevice(rightEnc);

		m.start();

		double angle = 0;
		double x = 0;
		double y = 0;

		while (true) {
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
			Thread.sleep(100);

		}
	}
}
