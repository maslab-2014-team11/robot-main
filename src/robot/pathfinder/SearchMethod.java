package robot.pathfinder;

import java.util.List;

import robot.map.Coordinate;
import robot.map.Map;

public abstract class SearchMethod {

	protected Map map;
	
	public List<Coordinate> search(Coordinate start, Coordinate goal){
		return null;
	}
}
