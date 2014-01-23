package robot.map;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;

import robot.Robot;
import robot.map.objects.GreenBall;
import robot.map.objects.RedBall;
import robot.map.objects.Silo;
import robot.map.objects.Wall;
import robot.map.objects.Container;

public class Map{

	private CopyOnWriteArraySet<RedBall> RedBalls = new CopyOnWriteArraySet<RedBall>();
	private CopyOnWriteArraySet<GreenBall> GreenBalls = new CopyOnWriteArraySet<GreenBall>();
	private CopyOnWriteArraySet<Wall> Walls = new CopyOnWriteArraySet<Wall>();
	private CopyOnWriteArraySet<Silo> Silos = new CopyOnWriteArraySet<Silo>();
	private Container Container;
	
	public Grid discreteMap;
	public Visualizer visualizer;
	private Robot bot;
	
	private Object stateLock = new Object();
	private Coordinate position = new Coordinate(0,0);
	private Coordinate rotation = new Coordinate(0,0);
	
	public static int squareSize = 31;
	public static double botSize = 35.54;
	private static double imageHeight, imageWidth;
	private static double ballRadius, wallHeight;
	private static double cameraOffset, cameraFOVx, cameraFOVy;
	
	public Map(int h, int w, Robot bot, boolean DebugMode){
		this.discreteMap = new Grid(h*squareSize, w*squareSize, 1);
		this.bot = bot;
		if(DebugMode){
			this.visualizer = new Visualizer(h, w, this);
			this.visualizer.start();
		} else {
			this.visualizer = null;
		}
		initialize();
	}
	
	public void initialize(){
		imageHeight = 1080;
		imageWidth = 1920;
		
		ballRadius = 4.445;
		wallHeight = 15.24;
		
		cameraOffset = Math.PI/6.0;
		cameraFOVx = (2*Math.PI)/3.0;
		cameraFOVy = (Math.PI)/2.0;
		
	}
	
	public Coordinate[] getState(){
		synchronized(stateLock){
			return new Coordinate[]{new Coordinate(position), 
									new Coordinate(rotation)};
		}
	}
	
	public void setState(Coordinate[] newState){
		synchronized(stateLock){
			position = new Coordinate(newState[0]);
			rotation = new Coordinate(newState[1]);
		}
	}
	
 	private double computePhi(double i){
		return Math.atan(i/imageHeight - 0.5) * 2 * cameraFOVy;
	}
	
	private double computeTheta(double i){
		return Math.atan(i/imageWidth - 0.5) * 2 * cameraFOVx;
	}
	
	public Iterator<RedBall> getRedBalls(){ return RedBalls.iterator(); }
	
	public void addRedBall(double x, double y, double radius, Coordinate[] state){
		double facing = state[1].x;
		Coordinate origin = state[0];
		double phi = computePhi(y);
		double theta = computeTheta(x);
		
		double dist = (radius/2.0) * Math.atan((computePhi(x - (radius/2.0)) - computePhi(x + (radius/2.0))/2.0));
		double rad = 0.75*(imageHeight - ballRadius*Math.tan(cameraOffset-phi)) +
				     0.25*(Math.sqrt(Math.pow(dist, 2.0) - Math.pow(imageHeight - ballRadius,2.0)));
		
		double depth = rad*Math.cos(theta);
		double offset = rad*Math.sin(theta);
		
		RedBall workingBall = new RedBall(origin.x + Math.cos(facing)*depth - Math.sin(facing)*offset,
                						  origin.y + Math.sin(facing)*depth + Math.cos(facing)*offset,
                						  ballRadius);
		Iterator<RedBall> ballSet = getRedBalls();
		
		while(ballSet.hasNext()){
			if(!ballSet.next().verifyObject(workingBall))
				break;
		}
		RedBalls.add(workingBall);
	}
	
	public Iterator<GreenBall> getGreenBalls(){ return GreenBalls.iterator(); }
	
	public void addGreenBall(double x, double y, double radius, Coordinate[] state){
		double facing = state[1].x;
		Coordinate origin = state[0];
		double phi = computePhi(y);
		double theta = computeTheta(x);
		
		double dist = (radius/2.0) * Math.atan((computePhi(x - (radius/2.0)) - computePhi(x + (radius/2.0))/2.0));
		double rad = 0.75*(imageHeight - ballRadius*Math.tan(cameraOffset-phi)) +
				     0.25*(Math.sqrt(Math.pow(dist, 2.0) - Math.pow(imageHeight - ballRadius,2.0)));
		
		double depth = rad*Math.cos(theta);
		double offset = rad*Math.sin(theta);
		
		GreenBall workingBall = new GreenBall(origin.x + Math.cos(facing)*depth - Math.sin(facing)*offset,
				  							origin.y + Math.sin(facing)*depth + Math.cos(facing)*offset,
				  							ballRadius);
		Iterator<GreenBall> ballSet = getGreenBalls();
		
		while(ballSet.hasNext()){
			if(!ballSet.next().verifyObject(workingBall))
				break;
		}
		GreenBalls.add(workingBall);
	}
	
	public Iterator<Wall> getWalls(){ return Walls.iterator(); }
	
	public void addWall(double x1, double y1, double x2, double y2, Coordinate[] state){
		double facing = state[1].x;
		Coordinate origin = state[0];
		double phi1 = computePhi(y1);
		double theta1 = computeTheta(x1);
		double phi2 = computePhi(y2);
		double theta2 = computeTheta(x2);
		
		double r1 = imageHeight - wallHeight * Math.tan(cameraOffset - phi1);
		double r2 = imageHeight - wallHeight * Math.tan(cameraOffset - phi2);
		
		double depth1 = r1*Math.cos(theta1);
		double offset1 = r1*Math.sin(theta1);
		double depth2 = r2*Math.cos(theta2);
		double offset2 = r2*Math.sin(theta2);
		
		Wall workingWall = new Wall(origin.x + Math.cos(facing)*depth1 - Math.sin(facing)*offset1,
				           			origin.y + Math.sin(facing)*depth1 + Math.cos(facing)*offset1,
				           			origin.x + Math.cos(facing)*depth2 - Math.sin(facing)*offset2,
				           			origin.y + Math.sin(facing)*depth2 + Math.cos(facing)*offset2);
		
		Iterator<Wall> wallSet = getWalls();
		
		while(wallSet.hasNext()){
			if(!wallSet.next().verifyObject(workingWall))
				break;
		}
		Walls.add(workingWall);
				           			
		discreteMap.addWalls(getWalls());
	}
	
	public Iterator<Silo> getSilos(){ return Silos.iterator(); }
	
	public void addSilo(double x1, double y1, double x2, double y2, int number, Coordinate[] state){
		double facing = state[1].x;
		Coordinate origin = state[0];
		double phi1 = computePhi(y1);
		double theta1 = computeTheta(x1);
		double phi2 = computePhi(y2);
		double theta2 = computeTheta(x2);
		
		double r1 = imageHeight - wallHeight * Math.tan(cameraOffset - phi1);
		double r2 = imageHeight - wallHeight * Math.tan(cameraOffset - phi2);
		
		double depth1 = r1*Math.cos(theta1);
		double offset1 = r1*Math.sin(theta1);
		double depth2 = r2*Math.cos(theta2);
		double offset2 = r2*Math.sin(theta2);
		
		Silo workingSilo = new Silo(origin.x + Math.cos(facing)*depth1 - Math.sin(facing)*offset1,
       								origin.y + Math.sin(facing)*depth1 + Math.cos(facing)*offset1,
       								origin.x + Math.cos(facing)*depth2 - Math.sin(facing)*offset2,
       								origin.y + Math.sin(facing)*depth2 + Math.cos(facing)*offset2,
       								number);

		Iterator<Silo> siloSet = getSilos();
		
		while(siloSet.hasNext()){
			if(!siloSet.next().verifyObject(workingSilo))
				break;
		}
		Silos.add(workingSilo);
	}
	
	public Container getContainer(){ return Container; }
	
	public void addContainer(double x1, double y1, double x2, double y2, Coordinate[] state){
		double facing = state[1].x;
		Coordinate origin = state[0];
		double phi1 = computePhi(y1);
		double theta1 = computeTheta(x1);
		double phi2 = computePhi(y2);
		double theta2 = computeTheta(x2);
		
		double r1 = imageHeight - wallHeight * Math.tan(cameraOffset - phi1);
		double r2 = imageHeight - wallHeight * Math.tan(cameraOffset - phi2);
		
		double depth1 = r1*Math.cos(theta1);
		double offset1 = r1*Math.sin(theta1);
		double depth2 = r2*Math.cos(theta2);
		double offset2 = r2*Math.sin(theta2);
		
		Container workingContainer = new Container(origin.x + Math.cos(facing)*depth1 - Math.sin(facing)*offset1,
				                  				   origin.y + Math.sin(facing)*depth1 + Math.cos(facing)*offset1,
				                  				   origin.x + Math.cos(facing)*depth2 - Math.sin(facing)*offset2,
				                  				   origin.y + Math.sin(facing)*depth2 + Math.cos(facing)*offset2);
		
		getContainer().verifyObject(workingContainer);	
	}
}
