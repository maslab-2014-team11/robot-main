package devices.actuators;

import java.util.Arrays;
import java.util.List;

import devices.port.Pin;

public class ExtendedCytron extends Cytron implements ExtendedActuator{

	private final Pin dirPin;
	private final Pin pwmPin;

	public ExtendedCytron(Pin dirPin, Pin pwmPin) {
		super(dirPin.portNum, pwmPin.portNum);
		this.dirPin = dirPin;
		this.pwmPin = pwmPin;
	}

	/**
	 * Note that besides synchronizing, this method clips the input to fall in
	 * the range [-1,1]
	 */
	@Override
	public synchronized void setSpeed(double speed) {
		super.setSpeed(Math.max(-1, Math.min(1, speed)));
	}

	@Override
	public synchronized byte[] generateCommandToMaple() {
		return super.generateCommandToMaple();
	}

	@Override
	public List<Pin> getPins() {
		return Arrays.asList(this.dirPin, this.pwmPin);
	}

}
