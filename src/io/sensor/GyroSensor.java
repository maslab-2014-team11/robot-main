package io.sensor;

public class GyroSensor extends SensorProxy {

	/**
	 * Get the estimated position of the gyro sensor relative to the starting
	 * position.
	 * 
	 * Note that this value can wrap around (>360 || < -360), and that positive
	 * angles measure counter-clockwise rotation.
	 * 
	 * @return the angular position of the gyros in degrees
	 */
	public synchronized double getGyroPosition() {
		// TODO implement this
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
