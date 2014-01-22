package robot.map.objects;

import java.util.ArrayList;
import java.util.List;

import robot.map.Coordinate;
import robot.map.MapObject;

public class RedBall extends MapObject{
	
	public RedBall(double x, double y, double radius){
		this.first = new Coordinate(x, y);
		this.second = new Coordinate(radius, (float) 0.0);
	}
	
	public boolean verifyObject(RedBall other){
		Coordinate otherCoord = other.getCoords().get(0);
		if(this.first.distanceFrom(otherCoord) < this.second.x)
				return true;
		adjustEstimate(other);
		return false;
	}
	
	public void adjustEstimate(RedBall other){
		Coordinate otherCoord = other.getCoords().get(0);
		this.first = new Coordinate((this.first.x + otherCoord.x)/2.0,
				                    (this.first.y + otherCoord.y)/2.0);
	}
	
	public List<Coordinate> getCoords(){
		List<Coordinate> output = new ArrayList<Coordinate>();
		output.add(this.first);
		return output;
	}

}
