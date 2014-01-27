package devices.actuators;

import java.util.Arrays;
import java.util.List;

import devices.port.Pin;

public class ExtendedCytron extends Cytron implements ExtendedActuator {

	private final Pin dirPin;
	private final Pin pwmPin;

	private double maxMagnitudeOutput = 1;

	public ExtendedCytron(Pin dirPin, Pin pwmPin) {
		super(dirPin.portNum, pwmPin.portNum);
		this.dirPin = dirPin;
		this.pwmPin = pwmPin;
	}

	/**
	 * Note that besides synchronizing, this method clips the input to fall in
	 * the range [-maxMagnitudeOutput, maxMagnitudeOutput]
	 */
	@Override
	public synchronized void setSpeed(double speed) {
		super.setSpeed(Math.max(-maxMagnitudeOutput,
				Math.min(speed, maxMagnitudeOutput)));
	}

	@Override
	public synchronized byte[] generateCommandToMaple() {
		return super.generateCommandToMaple();
	}

	@Override
	public List<Pin> getPins() {
		return Arrays.asList(this.dirPin, this.pwmPin);
	}

	public synchronized void setMaxMagnitudeOutput(double maxMagnitudeOutput) {
		this.maxMagnitudeOutput = Math.min(1, Math.abs(maxMagnitudeOutput));
	}

}
