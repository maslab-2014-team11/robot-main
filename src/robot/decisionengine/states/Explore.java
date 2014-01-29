package robot.decisionengine.states;

import robot.Constants;
import robot.decisionengine.State;
import robot.decisionengine.State.Type;
import robot.map.Coordinate;
import robot.map.MapObject;

public class Explore extends State{
	
	public Explore(Coordinate target){
		this.type = State.Type.EXPLORE;
		this.target = target;
		this.object = null;
	}
	
	public void remove(){}
	
	public void step(Coordinate location, double time, boolean routed){
		if(!routed)
			this.estimate = Constants.decisionDistanceWeight * location.distanceFrom(target) *
							(Constants.decisionTimeFactor * (time)) * 
							Constants.decisionExploreCost;
		else
			this.estimate = Constants.decisionDistanceWeight * location.distanceFrom(target) *
							(Constants.decisionTimeFactor * (time)) *
							Constants.decisionRoutedFactor  * 
							Constants.decisionExploreCost;
	}
}
