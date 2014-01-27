package robot.map;

import org.opencv.core.Mat;

public abstract class MapObject {
	protected Coordinate first;
	protected Coordinate second;
	
	protected double confidence = 0.0;
	
	protected Object objectLock = new Object();
	
	public String toString(){return "not implemented";}
	
	public Coordinate[] getCoords(){return null;}
	
	public void Draw(Mat canvas){}
}
