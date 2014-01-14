package devices.sensor;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import devices.port.Pin;
import devices.sensors.Ultrasonic;

public class ExtendedUltrasonic extends Ultrasonic implements ExtendedSensor {

	private final Pin trigger;
	private final Pin echo;

	public ExtendedUltrasonic(Pin trigger, Pin echo) {
		super(trigger.portNum, echo.portNum);
		this.trigger = trigger;
		this.echo = echo;
	}

	@Override
	public synchronized void consumeMessageFromMaple(ByteBuffer buff) {
		super.consumeMessageFromMaple(buff);
	}

	/**
	 * Return the sensor's measured distance in centimeters
	 */
	@Override
	public double getDistance() {
		return 100 * super.getDistance();
	}

	@Override
	public List<Pin> getPins() {
		return Arrays.asList(trigger, echo);
	}
}
