package devices.sensor;

import java.util.Arrays;
import java.util.List;

import devices.port.Pin;
import devices.sensors.Infrared;

public class ExtendedInfrared extends Infrared implements ExtendedSensor {

	private Pin pin;

	public ExtendedInfrared(Pin pin) {
		super(pin.portNum);
		this.pin = pin;
	}

	@Override
	public synchronized float getDistance() {
		return super.getDistance();
	}

	@Override
	public List<Pin> getPins() {
		return Arrays.asList(pin);
	}
}
