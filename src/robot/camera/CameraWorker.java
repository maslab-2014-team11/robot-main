package robot.camera;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.opencv.core.Mat;

import robot.map.Coordinate;
import robot.map.Map;

public class CameraWorker extends Thread{
	
	private Map map;
	
	private BlockingQueue<Coordinate[]> stateData;
	private BlockingQueue<List<Mat>> outputData;
	
	public CameraWorker(Map map, BlockingQueue<Coordinate[]> stateData,
			BlockingQueue<List<Mat>> outputData){
		this.map = map;
		this.stateData = stateData;
		this.outputData = outputData;
	}
	
	public void run(){
		while(true){
			try {
				List<Mat> outputs = outputData.take();
				Coordinate[] state = stateData.take();
			
				Mat redBalls = outputs.get(1);
				for( int i = 0; i < redBalls.size().width; i++){
					map.addRedBall(redBalls.get(0,i)[0], 
								   redBalls.get(0,i)[1], 
								   redBalls.get(0,i)[2], 
								   state);
				}
				
				Mat greenBalls = outputs.get(2);
				for( int i = 0; i < greenBalls.size().width; i++){
					map.addGreenBall(greenBalls.get(0,i)[0], 
									 greenBalls.get(0,i)[1], 
									 greenBalls.get(0,i)[2], 
									 state);
				}
				
				Mat walls = outputs.get(3);
				for( int i = 0; i < walls.size().width; i++){
					map.addWall(walls.get(0,i)[0],
								walls.get(0,i)[1],
								walls.get(0,i)[2],
								walls.get(0,i)[3], 
								state);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}

}
