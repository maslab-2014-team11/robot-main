package devices;

import java.util.HashMap;
import java.util.Map;

import comm.MapleComm;
import comm.MapleIO;
import comm.MapleIO.SerialPortType;
import devices.port.Pin;

public class Maple {
	private static Maple singleton;

	public static Maple getMaple() {
		if (singleton != null)
			return singleton;
		Maple singleton = new Maple(new MapleComm(getCommType()));
		return singleton;
	}

	private static MapleIO.SerialPortType getCommType() {
		String osName = System.getProperty("os.name");
		if (osName.startsWith("Linux"))
			return SerialPortType.LINUX;
		else if (osName.startsWith("Windows"))
			return SerialPortType.WINDOWS;
		else
			throw new UnsupportedOperationException(
					"Not supported on this operating system");
	}

	private final Thread ioThread;
	private final MapleComm comm;

	private final Map<Pin, ExtendedDevice> portsInUse;

	private Maple(MapleComm comm) {
		this.comm = comm;
		this.ioThread = new MapleIOThread();
		this.portsInUse = new HashMap<>();
	}

	public void registerDevice(MapleDevice device) {
		if (!(device instanceof ExtendedDevice))
			throw new UnsupportedOperationException(
					"Devices must implement the devices.ExtendedDevice interface");

		for (Pin p : ((ExtendedDevice) device).getPins())
			if (portsInUse.containsKey(p)) {
				throw new RuntimeException("Pin " + p.portNum
						+ " is already in use by " + portsInUse.get(p));
			}
		for (Pin p : ((ExtendedDevice) device).getPins())
			portsInUse.put(p, (ExtendedDevice) device);

		this.comm.registerDevice(device);
	}

	public void start() {
		this.comm.initialize();
		ioThread.start();
	}

	private class MapleIOThread extends Thread {
		@Override
		public void run() {
			comm.transmit();
			comm.updateSensorData();
		}
	}
}
