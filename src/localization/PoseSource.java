package localization;

import robot.map.Coordinate;

public interface PoseSource {

	/**
	 * Get the robot's position on the Cartesian coordinate plane
	 * 
	 * @return the robot's position
	 */
	public Coordinate getPosition();

	/**
	 * Get the robot's heading in degrees on the Cartesian coordinate plane
	 * 
	 * @return the robot's heading in degrees in the range (-180, 180]
	 */
	public double getHeading();

	/**
	 * Perform another step of computation in the position model
	 */
	public void step();

	/**
	 * Set the position and heading of the robot
	 * 
	 * @param position
	 *            the position of the robot
	 * @param heading
	 *            the heading of the robot, in degrees
	 */
	public void setCurrentPose(Coordinate position, double heading);
}
