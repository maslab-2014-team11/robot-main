package robot.pathfinder.searchmethods;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import robot.map.Coordinate;
import robot.map.Map;
import robot.pathfinder.SearchMethod;

public class DFS extends SearchMethod{

	public DFS(Map map){
		this.map = map;
	}
	
	public List<Coordinate> search(Coordinate start, Coordinate goal){
		List<List<Coordinate>> working = new ArrayList<List<Coordinate>>();
		List<Coordinate> startList = new ArrayList<Coordinate>();
		Set<Coordinate> visited = new HashSet<Coordinate>();
		startList.add(start);
		working.add(startList);
		visited.add(start);
		
		while(!working.isEmpty()){
			List<Coordinate> current = working.get(working.size()-1);
			working.remove(working.size()-1);
			if(current.get(current.size()-1) == goal)
				return current;
			List<Coordinate> adjacents = map.discreteMap.giveMoves(current.get(current.size()-1));
			for(Coordinate e: adjacents){
				if(!visited.contains(e)){
					visited.add(e);
					List<Coordinate> next = new ArrayList<Coordinate>(current);
					next.add(e);
					working.add(next);
				}
			}
		}
		return null;
	}
}
