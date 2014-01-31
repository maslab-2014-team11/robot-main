package devices.actuators;

import java.nio.ByteBuffer;

import devices.port.Pin;

public class ExtendedServo extends Servo {

	public ExtendedServo(Pin pin, int minPulseWidth, int maxPulseWidth,
			int minAngle, int maxAngle) {
		super(pin.portNum, minPulseWidth, maxPulseWidth, minAngle, maxAngle);
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
