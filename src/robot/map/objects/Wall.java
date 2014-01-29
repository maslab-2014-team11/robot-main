package robot.map.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import robot.Constants;
import robot.map.Coordinate;
import robot.map.MapObject;

public class Wall extends MapObject{
	CopyOnWriteArraySet<Wall> container;
	
	public Wall(double x1, double y1, double x2, double y2, CopyOnWriteArraySet<Wall> container){
		this.first = new Coordinate(x1, y1);
		this.second = new Coordinate(x2, y2);
		this.container = container;
	}
	
	public boolean verifyObject(Wall other){
		synchronized(objectLock){
			Coordinate[] otherCoord = other.getCoords();
			
			Coordinate vectorThis = this.first.vectorTo(this.second);
			Coordinate vectorOther = otherCoord[0].vectorTo(otherCoord[1]);
			
			if(vectorThis.angleBetween(vectorOther) > (5.0*Math.PI)/180.0)
				if(this.first.distanceFrom(otherCoord[0]) < 100.0)
					if(this.second.distanceFrom(otherCoord[1]) < 100.0){
						if(this.confidence < Constants.mapObjectConfidenceThreshold){
							this.confidence = this.confidence - Constants.mapObjectConfidenceThreshold * 
																(0.5 * ((this.first.distanceFrom(otherCoord[0])/100.0) - 1) +
																 0.5 * ((this.second.distanceFrom(otherCoord[1])/100.0) - 1));
							if(this.confidence < 0)
								Remove();
						}
						return true;
					}
			adjustEstimate(other);
			return false;
		}
	}
	
	private void adjustEstimate(Wall other){
		if(this.confidence < Constants.mapObjectConfidenceThreshold * 2){
			Coordinate[] otherCoord = other.getCoords();
			this.confidence = this.confidence + 1;
			
			this.first = new Coordinate((this.first.x*this.confidence + otherCoord[0].x)/(this.confidence + 1.0),
					                    (this.first.y*this.confidence + otherCoord[0].y)/(this.confidence + 1.0));
			this.second = new Coordinate((this.second.x*this.confidence + otherCoord[1].x)/(this.confidence + 1.0),
	                  					 (this.second.y*this.confidence + otherCoord[1].y)/(this.confidence + 1.0));
		} else {
			if(!this.confident && !this.used)
				this.confident = true;
		}
	}
	
	public Coordinate[] getCoords(){
		synchronized(objectLock){
			return new Coordinate[]{new Coordinate(this.first), new Coordinate(this.second)};
		}
	}
	
	public void Draw(Mat canvas, int scale){
		synchronized(objectLock){
			 Core.line(canvas, new Point(this.first.x*scale, this.first.y*scale), 
					   new Point(this.second.x*scale, this.second.y*scale), new Scalar(255,0,0), 1);
		}
	}
	
	public void Remove(){
		this.container.remove(this);
	}
}
