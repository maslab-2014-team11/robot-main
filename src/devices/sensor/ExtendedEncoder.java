package devices.sensor;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import devices.port.Pin;
import devices.sensors.Encoder;

public class ExtendedEncoder extends Encoder implements ExtendedSensor {

	private final Pin pinA, pinB;

	/**
	 * Create a quadrature encoder connection that will listen for interrupts on
	 * the specified port, and check the checkPin for direction
	 * 
	 * @param interruptPin
	 * @param checkPin
	 */
	public ExtendedEncoder(Pin interruptPin, Pin checkPin) {
		super(interruptPin.portNum, checkPin.portNum);
		this.pinA = interruptPin;
		this.pinB = checkPin;
	}

	@Override
	public synchronized double getDeltaAngularDistance() {
		return super.getDeltaAngularDistance();
	}

	@Override
	public synchronized double getAngularSpeed() {
		return super.getAngularSpeed();
	}

	@Override
	public synchronized double getTotalAngularDistance() {
		return super.getTotalAngularDistance();
	}

	/**
	 * Return the last measured angular speed in degrees/second
	 */
	public synchronized double getAngularSpeedDeg() {
		return getAngularSpeed() * 180.0 / Math.PI;
	}

	/**
	 * Return the number of degrees through which the wheel has turned since the
	 * program started
	 */
	public synchronized double getTotalAngularDistanceDeg() {
		return getTotalAngularDistance() * 180. / Math.PI;
	}

	@Override
	public synchronized void consumeMessageFromMaple(ByteBuffer buff) {
		super.consumeMessageFromMaple(buff);
	}

	@Override
	public List<Pin> getPins() {
		return Arrays.asList(pinA, pinB);
	}

}
