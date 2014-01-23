package robot.map.objects;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import robot.map.Coordinate;
import robot.map.MapObject;

public class GreenBall extends MapObject{

	public GreenBall(double x, double y, double radius){
		this.first = new Coordinate(x, y);
		this.second = new Coordinate(radius, (float) 0.0);
	}
	
	public boolean verifyObject(GreenBall other){
		Coordinate otherCoord = other.getCoords().get(0);
		if(this.first.distanceFrom(otherCoord) < this.second.x)
				return true;
		adjustEstimate(other);
		return false;
	}
	
	public void adjustEstimate(GreenBall other){
		Coordinate otherCoord = other.getCoords().get(0);
		this.first = new Coordinate((this.first.x + otherCoord.x)/2.0,
				                    (this.first.y + otherCoord.y)/2.0);
	}
	
	public List<Coordinate> getCoords(){
		List<Coordinate> output = new ArrayList<Coordinate>();
		output.add(this.first);
		return output;
	}
	
	public void Draw(Mat canvas){
		Point center = new Point(this.first.x, this.first.y);
		int radius = (int) Math.round(this.second.x);
		
		Core.circle(canvas, center, 3, new Scalar(0,255,0), -1, 5, 0);
		Core.circle(canvas, center, radius, new Scalar(0,255,0), 3, 5, 0);
	}
}
