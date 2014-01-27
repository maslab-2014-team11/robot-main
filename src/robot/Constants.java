package robot;

import devices.port.Pin;

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
}
