package robot.decisionengine.states;

import java.util.List;

import robot.map.Coordinate;
import robot.map.MapObject;
import robot.pathfinder.PathFinder;

public class MoveToObject {
	
	private PathFinder pathfinder;	
	private MapObject target;
	
	public List<Coordinate> path;
	public double estimate;
	
	
	public MoveToObject(MapObject target, PathFinder pathfinder){
		this.target = target;
		this.pathfinder = pathfinder;
	}
	
	public void estimate(){
		estimate = pathfinder.map.getState()[0].distanceFrom(target.getCoords()[0]);
	}
	
	public void solve(){
		path = pathfinder.findPath(target.getCoords()[0]);
	}

}
