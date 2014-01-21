package robot.map;

import java.util.List;

public abstract class MapObject {
	protected Coordinate first;
	protected Coordinate second;
	
	public String toString(){return "not implemented";}
	
	public List<Coordinate> getCoords(){return null;}
}
