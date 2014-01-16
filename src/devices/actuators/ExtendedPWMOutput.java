package devices.actuators;

import java.util.Arrays;
import java.util.List;

import devices.port.Pin;

public class ExtendedPWMOutput extends PWMOutput implements ExtendedActuator {

	private final Pin pin;

	public ExtendedPWMOutput(Pin pin) {
		super(pin.portNum);
		this.pin = pin;
	}

	@Override
	public List<Pin> getPins() {
		return Arrays.asList(this.pin);
	}
	
	@Override
	public synchronized byte[] generateCommandToMaple() {
		return super.generateCommandToMaple();
	}
	
	@Override
	public synchronized void setValue(double value) {
		super.setValue(value);
	}

}
