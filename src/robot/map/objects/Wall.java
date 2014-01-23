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
		Coordinate otherOne = other.getCoords().get(0);
		Coordinate otherTwo = other.getCoords().get(1);
		
		Coordinate vectorThis = this.first.vectorTo(this.second);
		Coordinate vectorOther = otherOne.vectorTo(otherTwo);
		
		if(vectorThis.angleBetween(vectorOther) > (5.0*Math.PI)/180.0)
			if(this.first.distanceFrom(otherOne) < 100.0)
				if(this.second.distanceFrom(otherTwo) < 100.0)
					return true;
		adjustEstimate(other);
		return false;
	}
	
	public void adjustEstimate(Wall other){
		Coordinate otherOne = other.getCoords().get(0);
		Coordinate otherTwo = other.getCoords().get(1);
		
		this.first = new Coordinate((this.first.x + otherOne.x)/2.0,
				                    (this.first.y + otherOne.y)/2.0);
		this.second = new Coordinate((this.second.x + otherTwo.x)/2.0,
                  					 (this.second.y + otherTwo.y)/2.0);
	}
	
	public List<Coordinate> getCoords(){
		List<Coordinate> output = new ArrayList<Coordinate>();
		output.add(this.first);
		output.add(this.second);
		return output;
	}
	
	public void Draw(Mat canvas){
		  Core.line(canvas, new Point(this.first.x, this.first.y), 
				  	new Point(this.second.x, this.second.y), new Scalar(255,0,0), 3);
	}
	
}
