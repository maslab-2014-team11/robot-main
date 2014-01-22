package robot.pathfinder;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import robot.map.Coordinate;
import robot.map.Map;
import robot.pathfinder.searchmethods.BFS;

public class PathFinder{

	public Map map;
	
	private HashMap<Coordinate,HashMap<Coordinate,List<Coordinate>>> paths = 
			new HashMap<Coordinate,HashMap<Coordinate,List<Coordinate>>>();
	
	private BFS search;
	
	public List<Coordinate> findPath(Coordinate start, Coordinate goal){
		
		if(paths.get(start) == null)
			paths.put(start, new HashMap<Coordinate,List<Coordinate>>());
		if(paths.get(start).get(goal) == null)
			paths.get(start).put(goal, search.search(start, goal));
		
		return paths.get(start).get(goal);
	}
	
}
