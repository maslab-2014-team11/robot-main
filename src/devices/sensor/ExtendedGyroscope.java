package devices.sensor;

import java.nio.ByteBuffer;
import java.util.List;

import devices.port.Pin;
import devices.port.SPI;
import devices.sensors.Gyroscope;

public class ExtendedGyroscope extends Gyroscope implements ExtendedSensor {

	private final SPI spiPort;
	private final Pin selSlavePort;

	private double omegaOffset;

	private double totalAngle = 0;
	private long lastTime;

	public ExtendedGyroscope(SPI port, Pin selSlavePort) {
		super(port.getPortNum(), selSlavePort.getPortNum());
		this.spiPort = port;
		this.selSlavePort = selSlavePort;
	}

	@Override
	public synchronized void consumeMessageFromMaple(ByteBuffer buff) {
		long now = System.currentTimeMillis();
		super.consumeMessageFromMaple(buff);
		if (lastTime != 0) {
			long delta = now - lastTime;
			totalAngle += getOmega() * (delta / 1000.);
		}
		lastTime = now;
	}

	public synchronized double getAngleChangeSinceLastUpdate() {
		return super.getAngleChangeSinceLastUpdate();
	}

	public synchronized void setOffset(double omegaOffset) {
		this.omegaOffset = omegaOffset;
	}

	public synchronized double getOmega() {
		return -super.getOmega() + this.omegaOffset;
	}

	/**
	 * Return the angular velocity in degrees/second
	 */
	public synchronized double getOmegaDeg() {
		return -super.getOmega() * 180.0 / Math.PI;
	}

	public double getTotalAngle() {
		return totalAngle;
	}

	public double getTotalAngleDeg() {
		return Math.toDegrees(getTotalAngle());
	}

	@Override
	public List<Pin> getPins() {
		List<Pin> ports = spiPort.getPins();
		ports.add(selSlavePort);
		return ports;
	}

	public synchronized void setTotalAngleDeg(double  angle) {
		this.totalAngle = Math.toRadians(angle);
	}
}
