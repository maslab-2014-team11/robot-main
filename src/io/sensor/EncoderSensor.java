package io.sensor;

public class EncoderSensor extends SensorProxy {

	private final double circumference;
	private final int ticksPerRevolution;

	public EncoderSensor(double circumference, int ticksPerRevolution) {
		this.circumference = circumference;
		this.ticksPerRevolution = ticksPerRevolution;
	}

	/**
	 * Get the total distance this wheel has traveled
	 * 
	 * @return
	 */
	public synchronized double getTotalDistance() {
		// TODO implement this
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
