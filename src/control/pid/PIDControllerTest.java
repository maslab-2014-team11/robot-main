package control.pid;

import org.junit.Test;

/**
 * Manual test for P, I, and D components of the PID controller
 * 
 * Note that the output is such that you can run `gnuplot -e
 * "plot for [IDX=0:1] 'file.dat'  i IDX u 1:2 w lines title columnheader(1);"`
 * (on one line) from the command line to graph the output.
 * 
 * @author akonradi
 * 
 */
public class PIDControllerTest {

	private class DummyInputOutput implements PIDInputOutput {

		private double value;
		private double coeff;
		private double angle;
		private double lastOutput;

		public DummyInputOutput(double value, double coeff) {
			this(value, coeff, 0);
		}

		public DummyInputOutput(double value, double coeff, double angle) {
			this.value = value;
			this.coeff = coeff;
			this.angle = angle;
		}

		@Override
		public double input() {
			return this.value;
		}

		@Override
		public void output(double value) {
			this.lastOutput = value;
			this.value += coeff * Math.sin(this.angle);
			this.angle = Math.max(Math.min(Math.PI / 2, this.angle + value),
					-Math.PI / 2);
		}

		public double getValue() {
			return value;
		}

		public double getLastOutput() {
			return lastOutput;
		}

	}

	@Test
	public void testProportionalOnly() {
		DummyInputOutput io = new DummyInputOutput(10, 1);
		PIDController controller = new PIDController(10, 0, 0, io);
		System.out.println("\"proportional\"");
		for (int i = 0; i < 1000; i++) {
			controller.step();
			System.out.print(i);
			System.out.print(" ");
			System.out.println(io.getValue());
		}
		System.out.println("\n");
	}

	@Test
	public void testIntegralOnly() {
		DummyInputOutput io = new DummyInputOutput(10, 1);
		PIDController controller = new PIDController(0, 10, 0, io);
		System.out.println("\"integral\"");
		for (int i = 0; i < 1000; i++) {
			controller.step();
			System.out.print(i);
			System.out.print(" ");
			System.out.println(io.getValue());
		}
		System.out.println("\n");
	}

	@Test
	public void testDerivativeOnly() {
		DummyInputOutput io = new DummyInputOutput(10, 1, -Math.PI / 4);
		PIDController controller = new PIDController(0, 0, 1, io);
		System.out.println("\"derivative\"");
		for (int i = 0; i < 1000; i++) {
			controller.step();
			System.out.print(i);
			System.out.print(" ");
			System.out.println(io.getValue());
		}
		System.out.println("\n");
	}

}
