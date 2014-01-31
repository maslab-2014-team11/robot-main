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

public class RedBall extends MapObject{
	CopyOnWriteArraySet<RedBall> container;
	
	public RedBall(double x, double y, double radius, CopyOnWriteArraySet<RedBall> container){
		this.first = new Coordinate(x, y);
		this.second = new Coordinate(radius, (float) 0.0);
		this.container = container;
	}
	
	public boolean verifyObject(RedBall other){
		synchronized(objectLock){
			Coordinate otherCoord = other.getCoords()[0];
			if(this.first.distanceFrom(otherCoord) > this.second.x * Constants.mapObjectThreshold){
				if(this.confidence < Constants.mapObjectConfidenceThreshold){
					this.confidence = this.confidence - Constants.mapObjectConfidenceThreshold * 
														((this.first.distanceFrom(otherCoord)/
														this.second.x * Constants.mapObjectThreshold) - 1);
					if(this.confidence < 0)
						Remove();
				}
				return true;
			}
			adjustEstimate(other);
			return false;
		}
	}
	
	private void adjustEstimate(RedBall other){
		if(this.confidence < Constants.mapObjectConfidenceThreshold * 2){
			Coordinate otherCoord = other.getCoords()[0];
			this.confidence = this.confidence + 1;
			
			this.first = new Coordinate((this.first.x*this.confidence + otherCoord.x)/(this.confidence + 1.0),
					                    (this.first.y*this.confidence + otherCoord.y)/(this.confidence + 1.0));
		} else {
			if(!this.confident && !this.used)
				this.confident = true;
		}
	}
	
	public Coordinate[] getCoords(){
		synchronized(objectLock){
			return new Coordinate[]{new Coordinate(this.first)};
		}
	}

	public void Draw(Mat canvas, int scale){
		synchronized(objectLock){
			Point center = new Point(this.first.x*scale + (canvas.width()/2), 
									 this.first.y*scale + (canvas.height()/2));
			int radius = (int) Math.round(this.second.x*scale);
			
			Core.circle(canvas, center, 3, new Scalar(0,0,255), -1, 5, 0);
			Core.circle(canvas, center, radius, new Scalar(0,0,255), 1, 5, 0);
		}
	}
	
	public void Remove(){
		this.container.remove(this);
	}
}
