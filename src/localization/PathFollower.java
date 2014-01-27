package localization;

import java.util.List;

import robot.map.Coordinate;

public interface PathFollower {

	public void setPath(List<Coordinate> newPath);

	public Result followPath();

	public enum Result {
		Driving, Finished
	}
}