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
	private VideoCapture visionCamera = new VideoCapture();
	private Camera vision;
	
	//private PathFinder pathingModule;
	//private DecisionEngine searchModule;
	
	public Robot(){
		visionCamera = new VideoCapture();
		visionCamera.open(0);
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
}
