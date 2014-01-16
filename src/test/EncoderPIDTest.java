package test;

import control.MotorSpeedPIDAdapter;
import control.pid.PIDController;
import devices.Maple;
import devices.actuators.ExtendedCytron;
import devices.port.Pin;
import devices.sensor.ExtendedEncoder;

public class EncoderPIDTest {

	public static void main(String[] args) throws InterruptedException {
		ExtendedCytron motor = new ExtendedCytron(new Pin(7), new Pin(6));
		ExtendedEncoder encoder = new ExtendedEncoder(new Pin(24), new Pin(23));
		MotorSpeedPIDAdapter pid = new MotorSpeedPIDAdapter(motor, encoder);
		Maple maple = Maple.getMaple();
		maple.registerDevice(motor);
		maple.registerDevice(encoder);
		PIDController pidC = new PIDController(0.0001, 0.0001, 0.00001, pid);
		pid.setTargetSpeed(1000);
		maple.start();
		for (;;) {
			pidC.step();
		}
	}
}
