package robot.map.objects;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import robot.map.Coordinate;
import robot.map.MapObject;

public class RedBall extends MapObject{
	
	public RedBall(double x, double y, double radius){
		this.first = new Coordinate(x, y);
		this.second = new Coordinate(radius, (float) 0.0);
	}
	
	public boolean verifyObject(RedBall other){
		synchronized(objectLock){
			Coordinate otherCoord = other.getCoords()[0];
			if(this.first.distanceFrom(otherCoord) < this.second.x)
					return true;
			adjustEstimate(other);
			return false;
		}
	}
	
	private void adjustEstimate(RedBall other){
		Coordinate otherCoord = other.getCoords()[0];
		this.confidence = this.confidence + 1;
		
		this.first = new Coordinate((this.first.x*this.confidence + otherCoord.x)/(this.confidence + 1.0),
				                    (this.first.y*this.confidence + otherCoord.y)/(this.confidence + 1.0));
	}
	
	public Coordinate[] getCoords(){
		synchronized(objectLock){
			return new Coordinate[]{new Coordinate(this.first)};
		}
	}

	public void Draw(Mat canvas){
		synchronized(objectLock){
			Point center = new Point(this.first.x, this.first.y);
			int radius = (int) Math.round(this.second.x);
			
			Core.circle(canvas, center, 3, new Scalar(0,0,255), -1, 5, 0);
			Core.circle(canvas, center, radius, new Scalar(0,0,255), 1, 5, 0);
		}
	}
}
