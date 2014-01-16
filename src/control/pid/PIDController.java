package control.pid;

public class PIDController {
	private final double propCoeff;
	private final double intCoeff;
	private final double diffCoeff;
	private final PIDInputOutput io;

	private double lastValue;
	private double integral;
	private long lastTime;

	/**
	 * Create a new PIDController with the specified coefficients
	 * 
	 * Note that the controller attempts to bring the value of
	 * {@link PIDInputOutput#input()} to zero
	 * 
	 * @param propCoeff
	 *            the coefficient for the proportional term; should be
	 *            non-negative
	 * @param intCoeff
	 *            the coefficient for the integral term; should be non-negative
	 * @param diffCoeff
	 *            the coefficient for the differential term; should be
	 *            non-negative
	 * @param io
	 *            the object implementing input/output
	 */
	public PIDController(double propCoeff, double intCoeff, double diffCoeff,
			PIDInputOutput io) {
		this.propCoeff = -propCoeff;
		this.intCoeff = -intCoeff;
		this.diffCoeff = -diffCoeff;
		this.io = io;

		this.lastTime = -1;
		this.integral = 0;
	}

	/**
	 * Advance the PID state machine one step, and send output to the given
	 * {@link PIDInputOutput}
	 * 
	 * Note that this method is a no-op if less than 1 ms has elapsed from the
	 * previous call to step()
	 */
	public void step() {

		long thisTime = System.currentTimeMillis();
		long timeDiff = thisTime - lastTime;
		if (timeDiff == 0)
			return;

		double input = io.input();
		if (this.lastTime > 0) {
			double derivative = (input - lastValue) * 1000 / timeDiff;
			this.integral += (input + lastValue) / 1000 * timeDiff;
			io.output(derivative * diffCoeff + integral * intCoeff + input
					* propCoeff);
		}
		this.lastValue = input;
		this.lastTime = thisTime;
	}
}
