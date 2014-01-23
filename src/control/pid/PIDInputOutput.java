package control.pid;

/**
 * An interface that acts as a data input and output connector for a
 * {@link PIDController}
 * 
 * @author akonradi
 * 
 */
public interface PIDInputOutput {

	/**
	 * Read a double value from this data connector
	 * 
	 * @return the value read
	 */
	public double input();

	/**
	 * Write a double output to this data connector
	 * 
	 * @param value
	 *            the value to write
	 */
	public void output(double value);
}
