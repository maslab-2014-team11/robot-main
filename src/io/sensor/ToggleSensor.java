package io.sensor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ToggleSensor extends SensorProxy {

	private final Set<BumpListener> listeners;

	public ToggleSensor() {
		// TODO add some constructor parameters here
		this.listeners = new HashSet<>(8);
	}

	/**
	 * Check whether the bump sensor was triggered at last check
	 * 
	 * @return true if the bump sensor was triggered; otherwise false
	 */
	public synchronized boolean isBumped() {
		// TODO implement this
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public void registerListener(BumpListener listener) {
		synchronized (this.listeners) {
			this.listeners.add(listener);
		}
	}

	public interface BumpListener {
		public void onBumpStart(ToggleSensor sensor);

		public void onBumpEnd(ToggleSensor sensor);
	}
}
