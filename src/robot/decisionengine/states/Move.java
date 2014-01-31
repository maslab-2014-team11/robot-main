package robot.decisionengine.states;

import robot.Constants;
import robot.decisionengine.State;
import robot.map.Coordinate;
import robot.map.MapObject;

public class Move extends State{
	
	public Move(Type type, Coordinate target, MapObject object){
		this.type = type;
		this.target = target;
		this.object = object;
	}
	
	public void remove(){
		this.object.Remove();
	}
	
	public void step(Coordinate location, double time, boolean routed){
		if(!routed)
			this.estimate = Constants.decisionDistanceWeight * location.distanceFrom(target) *
							(Constants.decisionTimeFactor * (time));
		else
			this.estimate = Constants.decisionDistanceWeight * location.distanceFrom(target) *
							(Constants.decisionTimeFactor * (time)) *
							Constants.decisionRoutedFactor;
	}

}
