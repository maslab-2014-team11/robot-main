package robot;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import robot.camera.Camera;
import robot.map.Coordinate;
import robot.map.Map;

public class Robot {
	
	public static Map WorldMap;
	public boolean running;
	
	private List<Camera> VisualSystems;
	private List<Sensor> SensorySystems;
	private BlockingQueue<Move> ActionList;
	
	private PathFinder pathingModule;
	private SearchEngine searchModule;
	
	public Coordinate getLocation(){
		return new Coordinate((float) 0.0,(float) 0.0);
	}
	
	public double getFacing(){
		return 0.0;
	}
}
