package robot.map.objects;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import robot.map.Coordinate;
import robot.map.MapObject;

public class Wall extends MapObject{
	
	public Wall(double x1, double y1, double x2, double y2){
		this.first = new Coordinate(x1, y1);
		this.second = new Coordinate(x2, y2);
	}
	
	public boolean verifyObject(Wall other){
		synchronized(objectLock){
			Coordinate[] otherCoord = other.getCoords();
			
			Coordinate vectorThis = this.first.vectorTo(this.second);
			Coordinate vectorOther = otherCoord[0].vectorTo(otherCoord[1]);
			
			if(vectorThis.angleBetween(vectorOther) > (5.0*Math.PI)/180.0)
				if(this.first.distanceFrom(otherCoord[0]) < 100.0)
					if(this.second.distanceFrom(otherCoord[1]) < 100.0)
						return true;
			adjustEstimate(other);
			return false;
		}
	}
	
	private void adjustEstimate(Wall other){
		Coordinate[] otherCoord = other.getCoords();
		this.confidence = this.confidence + 1;
		
		this.first = new Coordinate((this.first.x*this.confidence + otherCoord[0].x)/(this.confidence + 1.0),
				                    (this.first.y*this.confidence + otherCoord[0].y)/(this.confidence + 1.0));
		this.second = new Coordinate((this.second.x*this.confidence + otherCoord[1].x)/(this.confidence + 1.0),
                  					 (this.second.y*this.confidence + otherCoord[1].y)/(this.confidence + 1.0));
	}
	
	public Coordinate[] getCoords(){
		synchronized(objectLock){
			return new Coordinate[]{new Coordinate(this.first), new Coordinate(this.second)};
		}
	}
	
	public void Draw(Mat canvas){
		synchronized(objectLock){
			 Core.line(canvas, new Point(this.first.x, this.first.y), 
					   new Point(this.second.x, this.second.y), new Scalar(255,0,0), 3);
		}
	}
	
}
