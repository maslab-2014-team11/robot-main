package devices.sensor;

import java.nio.ByteBuffer;
import java.util.List;

import devices.port.Pin;
import devices.port.SPI;
import devices.sensors.Gyroscope;

public class ExtendedGyroscope extends Gyroscope implements ExtendedSensor {

	private final SPI spiPort;
	private final Pin selSlavePort;

	public ExtendedGyroscope(SPI port, Pin selSlavePort) {
		super(port.getPortNum(), selSlavePort.getPortNum());
		this.spiPort = port;
		this.selSlavePort = selSlavePort;
	}

	@Override
	public synchronized void consumeMessageFromMaple(ByteBuffer buff) {
		super.consumeMessageFromMaple(buff);
	}

	public synchronized double getAngleChangeSinceLastUpdate() {
		return super.getAngleChangeSinceLastUpdate();
	}

	public synchronized double getOmega() {
		return super.getOmega();
	}

	@Override
	public List<Pin> getPins() {
		List<Pin> ports = spiPort.getPins();
		ports.add(selSlavePort);
		return ports;
	}
}
