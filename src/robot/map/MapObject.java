package robot.map;

import java.util.List;

import org.opencv.core.Mat;

public abstract class MapObject {
	protected Coordinate first;
	protected Coordinate second;
	
	public String toString(){return "not implemented";}
	
	public List<Coordinate> getCoords(){return null;}
	
	public void Draw(Mat canvas){}
}
