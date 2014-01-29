package robot.decisionengine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import robot.Robot;
import robot.decisionengine.State.Type;
import robot.decisionengine.states.Explore;
import robot.decisionengine.states.Move;
import robot.map.Coordinate;
import robot.map.Map;
import robot.map.MapObject;
import robot.map.objects.GreenBall;
import robot.map.objects.RedBall;
import robot.pathfinder.PathFinder;
import robot.decisionengine.State;

public class DecisionEngine extends Thread{
	
	private Robot bot;
	private Map map;
	private PathFinder pathfinder;
	
	private boolean routed;
	private boolean reached;
	private int time;
	
	private List<Coordinate> path = new ArrayList<Coordinate>();
	
	private List<robot.decisionengine.State> stateSet = new ArrayList<robot.decisionengine.State>();
	private robot.decisionengine.State activeState = new robot.decisionengine.State();
	
	public DecisionEngine(Robot bot, Map map){
		this.bot = bot;
		this.map = map;
		this.pathfinder = new PathFinder();
		this.routed = false;
		this.reached = false;
		this.time = (int) bot.getTime();
	}
	
	public void hasFinished(){
		this.reached = true;
	}
	
	public void run(){
		while(true){
			establishStates();
			
			if(stateSet.isEmpty())
				stateSet.add(new Explore(new Coordinate(0,5)));
			
			if(reached){
				activeState.remove();
				activeState = new robot.decisionengine.State();
			}
			
			Coordinate location = map.getState()[0];
			double time = bot.getTime();
			
			stepStates(location, time, routed);
			activeState.step(location, time, false);
			
			if(stepSystem()){
				path = pathfinder.findPath(activeState.target());
				System.out.println("==Start==");
				for(Iterator<Coordinate> coord = path.iterator(); coord.hasNext();)
					System.out.println(coord.next().toString());
				System.out.println("===End===");
				//bot.setPath(path);
				reached = false;
			}
		}
	}
	
	private void establishStates(){
		for(Iterator<RedBall> balls = map.getRedBalls(); balls.hasNext();){
			RedBall object = balls.next();
			if(object.confident && !object.used){
				stateSet.add(new Move(Type.RED_BALL, object.getCoords()[0], object));
				object.used = true;
				System.out.println("Add Red");
			}
		}
		
		for(Iterator<GreenBall> balls = map.getGreenBalls(); balls.hasNext();){
			GreenBall object = balls.next();
			if(object.confident && !object.used){
				stateSet.add(new Move(Type.GREEN_BALL, object.getCoords()[0], object));
				object.used = true;
				System.out.println("Add Green");
			}
		}
	}
	
	private void stepStates(Coordinate location, double time, boolean routed){
		for(robot.decisionengine.State state: stateSet)
			state.step(location, time, routed);
	}
	
	private boolean stepSystem(){
		boolean changedMove = false;
		for(robot.decisionengine.State state: stateSet)
			if(state.estimate() < activeState.estimate()){
				stateSet.add(activeState);
				activeState = state;
				stateSet.remove(state);
				changedMove = true;
			}
		return changedMove;
	}

}
