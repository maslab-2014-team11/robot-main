package devices.sensor;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import devices.port.Pin;
import devices.sensors.DigitalInput;

public class ExtendedDigitalInput extends DigitalInput implements
		ExtendedSensor {

	private final Pin input;

	public ExtendedDigitalInput(Pin inputPin) {
		super(inputPin.portNum);
		this.input = inputPin;
	}

	@Override
	public synchronized void consumeMessageFromMaple(ByteBuffer buff) {
		super.consumeMessageFromMaple(buff);
	}

	@Override
	public synchronized boolean getValue() {
		return super.getValue();
	}

	@Override
	public List<Pin> getPins() {
		return Arrays.asList(input);
	}
}
