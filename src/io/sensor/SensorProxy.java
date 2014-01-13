package io.sensor;

/**
 * An abstract class for representing a sensor input source.
 * 
 * All subclasses must be thread-safe, and must support synchronizing on an
 * instance variable to allow calling methods to retrieve multiple values from
 * the same time step.
 */
public abstract class SensorProxy {

	private long lastUpdateTime;

	/**
	 * Get the timestamp of the last time this sensor proxy's value was updated.
	 * 
	 * @return the timestamp of the last update
	 */
	public synchronized long getLastUpdateTime() {
		return lastUpdateTime;
	}

	/**
	 * Reset the given sensor input to it's "zero-value", whatever that means
	 */
	public void reset() {
		// Default implementation does nothing
	}

}
