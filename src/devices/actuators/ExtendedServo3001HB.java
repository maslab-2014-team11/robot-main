package devices.actuators;

import devices.port.Pin;

public class ExtendedServo3001HB extends ExtendedServo {

	public ExtendedServo3001HB(Pin pin) {
		super(pin, 400, 2250, 0, 180);
	}

}
