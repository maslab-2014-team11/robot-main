package control;

import java.util.Arrays;

import devices.actuators.ExtendedCytron;
import devices.sensor.ExtendedAnalogInput;

public class FrontMotorControl implements Runnable {

	private final ExtendedCytron motor;
	private final ExtendedAnalogInput opticalEncoder;

	private final float[] encoderValues;
	private int nextValueIndex;

	private long maybeStuckTime;
	private long stuckTime;
	private long lastTime;

	private static final float NOT_STUCK_THRESHOLD = 5000;
	private static final double UNSTICK_MOTOR_POWER = -.2;
	private static final double NORMAL_MOTOR_POWER = .3;
	private static final long MAYBE_STUCK_TIMEOUT = 800;

	private enum State {
		Stopped, Turning, Stuck, MaybeStuck
	}

	private volatile State state;
	private final Thread thread;

	public FrontMotorControl(ExtendedCytron motor,
			ExtendedAnalogInput opticalEncoder) {
		this.motor = motor;
		this.opticalEncoder = opticalEncoder;
		this.encoderValues = new float[100];
		this.nextValueIndex = 0;
		this.state = State.Stopped;

		this.thread = new Thread(this);
	}

	@Override
	public void run() {
		while (true) {
			System.out.println(this.state);
			switch (this.state) {
			case Stopped:
				motor.setSpeed(0);
				stepAndGetVariance();
				Thread.yield();
				break;
			case MaybeStuck:
			case Stuck:
			case Turning:
				checkMotorIsTurning();
				try {
					Thread.sleep(2);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}
	}

	public void start() {
		this.thread.start();
	}

	private void checkMotorIsTurning() {
		long now = System.currentTimeMillis();
		long delta = now - lastTime;
		float variance = stepAndGetVariance();
		System.out.println("variance: " + variance);
		lastTime = now;

		if (now - delta == 0) {
			return;
		}

		switch (this.state) {
		case Stuck:
			motor.setSpeed(UNSTICK_MOTOR_POWER);
			stuckTime += delta;
			if (stuckTime > 1200) {
				motor.setSpeed(NORMAL_MOTOR_POWER);
				this.state = State.MaybeStuck;
				maybeStuckTime = 0;
			}
			break;
		case MaybeStuck:
			if (variance > NOT_STUCK_THRESHOLD) {
				this.state = State.Turning;
				maybeStuckTime = 0;
			} else {
				maybeStuckTime += delta;
				if (maybeStuckTime > MAYBE_STUCK_TIMEOUT) {
					this.state = State.Stuck;
					motor.setSpeed(UNSTICK_MOTOR_POWER);
					stuckTime = 0;
				}
			}
			break;
		case Turning:
			motor.setSpeed(NORMAL_MOTOR_POWER);
			if (variance < NOT_STUCK_THRESHOLD) {
				this.state = State.MaybeStuck;
			}
		case Stopped:
			break;
		}

	}

	private float stepAndGetVariance() {
		encoderValues[nextValueIndex++] = opticalEncoder.getValue();

		if (nextValueIndex >= encoderValues.length) {
			nextValueIndex = 0;
		}
		return getVariance(encoderValues);
	}

	private static float getVariance(float[] measurements) {

		float sum = 0;

		for (float f : measurements) {
			sum += f;
		}

		float average = sum / measurements.length;
		float variance = 0;

		for (float f : measurements) {
			float delta = average - f;
			variance += delta * delta;
		}

		return variance / measurements.length;
	}

	public void startMotor() {
		if (this.state == State.Stopped)
			this.state = State.Turning;
	}
}
