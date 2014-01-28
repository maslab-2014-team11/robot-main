package robot;

import sun.nio.ch.SelectorProviderImpl;
import devices.port.Pin;
import devices.port.SPI;

public class Constants {

	public static final double WHEEL_DIAMETER = 9.6; // cm
	public static final double WHEEL_RADIUS = WHEEL_DIAMETER / 2;
	public static final double WHEEL_CIRCUMFERENCE = WHEEL_DIAMETER * Math.PI;

	public static final double WHEEL_SEPARATION = 28.3; // cm

	public static final Pin L_ENCODER_TRIGGER_PIN = new Pin(30);
	public static final Pin L_ENCODER_READ_PIN = new Pin(29);

	public static final Pin R_ENCODER_TRIGGER_PIN = new Pin(26);
	public static final Pin R_ENCODER_READ_PIN = new Pin(25);

	public static final Pin L_WHEEL_DIR_PIN = new Pin(3);
	public static final Pin L_WHEEL_PWM_PIN = new Pin(2);

	public static final Pin R_WHEEL_DIR_PIN = new Pin(1);
	public static final Pin R_WHEEL_PWM_PIN = new Pin(0);

	public static final SPI GYRO_SPI_PORT = SPI.Two;
	public static final Pin GYRO_SS_PIN = new Pin(36);

	public static final double GYRO_A_OFFSET = -0.0022770001753831016;
	public static final double GYRO_B_OFFSET = -0.005271876088727122; // approximated
}
