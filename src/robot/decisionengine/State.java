package robot.decisionengine;

import robot.map.Coordinate;
import robot.map.MapObject;

public class State {
	public enum Type{
		RED_BALL,
		RED_BALL_DUMP,
		GREEN_BALL,
		SILO,
		SILO_DEPOSIT,
		CONTAINER,
		CONTAINER_FISK,
		EXPLORE,
		RESET;
	}
	
	protected Type type;
	protected MapObject object;
	protected Coordinate target;
	protected double estimate = Double.MAX_VALUE;
	
	public State(){}
	
	public double estimate(){
		return this.estimate;
	}
	
	public Coordinate target(){
		return this.target;
	}
	
	public void remove(){}
	
	public void step(Coordinate location, double time, boolean routed){
		this.estimate = Double.MAX_VALUE;
	}
}
