package test;

import devices.Maple;
import devices.port.Pin;
import devices.sensor.ExtendedUltrasonic;
import devices.sensors.Ultrasonic;

public class UltrasonicTest {

	public static void main(String[] args) throws InterruptedException {
		Maple m = Maple.getMaple();
		Ultrasonic u = new ExtendedUltrasonic(new Pin(27), new Pin(28));
		m.registerDevice(u);
		m.start();
		while (true) {
			System.out.println(u.getDistance());
			Thread.sleep(100);
		}
	}
}
