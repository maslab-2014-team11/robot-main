package robot.decisionengine.states;

import java.util.List;

import robot.map.Coordinate;
import robot.pathfinder.PathFinder;

public class MoveToTarget {
	
	private PathFinder pathfinder;	
	private Coordinate target;
	
	public List<Coordinate> path;
	public double estimate;
	
	
	public MoveToTarget(Coordinate target, PathFinder pathfinder){
		this.target = target;
		this.pathfinder = pathfinder;
	}
	
	public void estimate(){
		estimate = pathfinder.map.getState()[0].distanceFrom(target);
	}
	
	public void solve(){
		path = pathfinder.findPath(target);
	}

}
