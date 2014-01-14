package devices.sensor;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import devices.port.Pin;
import devices.sensors.AnalogInput;

public class ExtendedAnalogInput extends AnalogInput implements ExtendedSensor {

	private final Pin pin;

	public ExtendedAnalogInput(Pin signalPin) {
		super(signalPin.portNum);
		this.pin = signalPin;
	}

	@Override
	public synchronized void consumeMessageFromMaple(ByteBuffer buff) {
		super.consumeMessageFromMaple(buff);
	}

	@Override
	public synchronized float getValue() {
		return super.getValue();
	}

	@Override
	public List<Pin> getPins() {
		return Arrays.asList(this.pin);
	}
}
