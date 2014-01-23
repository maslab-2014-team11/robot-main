package devices;

public class ActuatorWrapper<T extends Actuator> extends Actuator {

	private final Actuator actuator;

	public ActuatorWrapper(T actuator) {
		this.actuator = actuator;
	}

	@Override
	public byte getDeviceCode() {
		return actuator.getDeviceCode();
	}

	@Override
	public byte[] getInitializationBytes() {
		return actuator.getInitializationBytes();
	}

	@Override
	public byte[] generateCommandToMaple() {
		return actuator.generateCommandToMaple();
	}

}
