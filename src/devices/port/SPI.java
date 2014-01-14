package devices.port;

import java.util.ArrayList;
import java.util.List;

public enum SPI {

	One(10), Two(31);

	private final int start;

	private SPI(int startPort) {
		this.start = startPort;
	}

	public List<Pin> getPins() {
		List<Pin> pins = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			pins.add(new Pin(start + i));
		}
		return pins;
	}

	public int getPortNum() {
		return ordinal() + 1;
	}
}
