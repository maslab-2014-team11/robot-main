package devices.port;

public class Pin {

	public final int portNum;

	public Pin(int portNum) {
		this.portNum = portNum;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + portNum;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pin other = (Pin) obj;
		if (portNum != other.portNum)
			return false;
		return true;
	}

	public int getPortNum() {
		return portNum;
	}

}
