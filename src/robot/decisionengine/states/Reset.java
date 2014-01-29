package robot.decisionengine.states;

import robot.Constants;
import robot.decisionengine.State;
import robot.map.Coordinate;

public class Reset extends State{
	
	public Reset(){
		this.type = State.Type.RESET;
		this.target = new Coordinate(0.0, 0.0);
		this.object = null;
	}
	
	public void remove(){}
	
	public void step(Coordinate location, double time, boolean routed){
		this.estimate = 0;
	}
}
