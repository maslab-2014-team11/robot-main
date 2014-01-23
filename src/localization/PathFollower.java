package localization;

import java.util.List;

import localization.CarrotPathFollower.Coordinate;

public interface PathFollower {

	public void setPath(List<Coordinate> newPath);

	public void addToPath(List<Coordinate> newPath);

	public void addToPath(Coordinate nextPoint);

	public Result followPath();

	public enum Result {
		Driving, Finished
	}
}