package robot.map.objects;

import java.util.ArrayList;
import java.util.List;

import robot.map.Coordinate;
import robot.map.MapObject;

public class Silo extends MapObject{
	
	public int number;

	public Silo(double x1, double y1, double x2, double y2, int number){
		this.first = new Coordinate(x1, y1);
		this.second = new Coordinate(x2, y2);
		this.number = number;
	}
	
	public List<Coordinate> getCoords(){
		List<Coordinate> output = new ArrayList<Coordinate>();
		output.add(this.first);
		output.add(this.second);
		return output;
	}
	
}
