package robot;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import robot.camera.Camera;
import robot.map.Coordinate;
import robot.map.Map;

public class Robot {
	
	public Map map;
	public boolean running;
	private Camera vision;
	
	private PathFinder pathingModule;
	private DecisionEngine searchModule;
}
