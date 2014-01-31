package devices.actuators;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import devices.ExtendedDevice;
import devices.port.Pin;

public class ExtendedServo extends Servo implements ExtendedDevice {

	private Pin pin;

	public ExtendedServo(Pin pin, int minPulseWidth, int maxPulseWidth,
			int minAngle, int maxAngle) {
		super(pin.portNum, minPulseWidth, maxPulseWidth, minAngle, maxAngle);

		this.pin = pin;
	}

	@Override
	public List<Pin> getPins() {
		return Arrays.asList(this.pin);
	}

	@Override
	public synchronized void consumeMessageFromMaple(ByteBuffer buff) {
		super.consumeMessageFromMaple(buff);
	}

	@Override
	public synchronized void setAngle(double angle) {
		super.setAngle(angle);
	}

	@Override
	public synchronized byte[] generateCommandToMaple() {
		return super.generateCommandToMaple();
	}

}
