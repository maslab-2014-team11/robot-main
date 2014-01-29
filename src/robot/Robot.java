package robot;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.opencv.highgui.VideoCapture;

import robot.camera.Camera;
import robot.map.Coordinate;
import robot.map.Map;

public class Robot {
	
	public Map map;
	public boolean running;
	private VideoCapture visionCamera;
	private Camera vision;
	
	private long time;
	
	//private PathFinder pathingModule;
	//private DecisionEngine searchModule;
	
	public Robot(){
		visionCamera = new VideoCapture();
		visionCamera.open(0);
		time = System.currentTimeMillis();
	}
	
	public void init(){
		map = new Map(10, 10, this, true);
		vision = new Camera(Camera.Type.VISION, visionCamera, map);
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		vision.start();
	}
	
	public long getTime(){
		return System.currentTimeMillis() - time;
	}
}
