package robot.pathfinder;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import robot.map.Coordinate;
import robot.map.Map;

public class PathFinder extends Thread{

	public Map map;
	public SearchMethod method;
	public BlockingQueue<List<Coordinate>> input;
	
	private HashMap<Coordinate,HashMap<Coordinate,List<Coordinate>>> paths;
	
}
