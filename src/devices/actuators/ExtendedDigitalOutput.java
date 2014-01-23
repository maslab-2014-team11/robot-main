package devices.actuators;

import java.util.Arrays;
import java.util.List;

import devices.port.Pin;

public class ExtendedDigitalOutput extends DigitalOutput implements
		ExtendedActuator {

	private Pin pin;

	public ExtendedDigitalOutput(Pin pin) {
		super(pin.portNum);
		this.pin = pin;
	}

	@Override
	public List<Pin> getPins() {
		return Arrays.asList(this.pin);
	}

}
