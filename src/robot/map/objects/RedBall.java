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
	
	public List<Coordinate> getCoords(){
		List<Coordinate> output = new ArrayList<Coordinate>();
		output.add(this.first);
		return output;
	}

}
