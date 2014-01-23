package robot.map;

import java.util.Iterator;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import robot.map.objects.GreenBall;
import robot.map.objects.RedBall;
import robot.map.objects.Wall;

public class Visualizer extends Thread{
	
	private Map map;
	
	private int h, w;
	private Mat mapImage;
	
	public Visualizer(int h, int w, Map map){
		this.map = map;
		this.h = h;
		this.w = w;
	}
	
	public void DrawRobot(Mat canvas, Coordinate[] state){
		Point center = new Point(state[0].x, state[0].y);
		int radius = (int) Math.round(Map.botSize);
	
		Core.circle(canvas, center, 3, new Scalar(255,255,255), -1, 5, 0);
		Core.circle(canvas, center, radius, new Scalar(255,255,255), 3, 5, 0);
		
		Core.line(canvas, 
				  new Point(state[0].x, state[0].y), 
			  	  new Point(state[0].x + Math.cos(state[1].x)*Map.botSize*0.5, 
			  			  	state[0].y + Math.sin(state[1].x)*Map.botSize*0.5), 
			  	  new Scalar(255,255,255), 3);
	}
	
	public void run(){
		while(true){
			this.mapImage = Mat.zeros(this.w*Map.squareSize, 
									  this.h*Map.squareSize, 
									  CvType.CV_8UC3);
			
			Coordinate[] state = this.map.getState();
			
			for(Iterator<RedBall> balls = map.getRedBalls(); balls.hasNext();)
				balls.next().Draw(mapImage);
			
			for(Iterator<GreenBall> balls = map.getGreenBalls(); balls.hasNext();)
				balls.next().Draw(mapImage);
			
			for(Iterator<Wall> walls = map.getWalls(); walls.hasNext();)
				walls.next().Draw(mapImage);
			
			DrawRobot(this.mapImage, state);
			
		}
	}

}
