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
	private Container Container = null;
	
	public Grid discreteMap;
	public Visualizer visualizer;
	private Robot bot;
	
	private Object stateLock = new Object();
	private Coordinate position = new Coordinate(0,0);
	private Coordinate rotation = new Coordinate(0,0);
	
	public static int squareSize = 31;
	public static double botSize = 35.54;
	private static double imageHeight, imageWidth;
	private static double ballRadius, wallHeight, ballScaleFactor;
	private static double cameraHeight, cameraOffset, cameraFOVx, cameraFOVy;
	
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
		imageHeight = 1080.0;
		imageWidth = 1920.0;
		
		ballRadius = 2.2225;
		ballScaleFactor = 0.8;
		wallHeight = 13.97;
		
		cameraHeight = 33.9725;
		cameraOffset = (Math.PI/180.0)*75.0;
		cameraFOVx = (Math.PI/180.0)*60.0;
		cameraFOVy = (Math.PI/180.0)*45.0;
		
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
		return Math.atan(((i/imageHeight) - 0.5) * 2 * Math.tan(cameraFOVy));
	}
	
	private double computeTheta(double i){
		return Math.atan(((i/imageWidth) - 0.5) * 2 * Math.tan(cameraFOVx));
	}
	
	public Iterator<RedBall> getRedBalls(){ return RedBalls.iterator(); }
	
	public void addRedBall(double x, double y, double radius, Coordinate[] state){
		double facing = state[1].x;
		Coordinate origin = state[0];
		double phi = computePhi(y);
		double theta = computeTheta(x);
		
		double dist = ballRadius / Math.tan((computeTheta(x + radius*ballScaleFactor) - computeTheta(x - radius*ballScaleFactor))/2.0);
		double rad = 0.75*((cameraHeight - ballRadius)/Math.atan(cameraOffset - phi)) +
					 0.25*(Math.sqrt(Math.pow(dist, 2.0) - Math.pow(cameraHeight - ballRadius,2.0)));
		
		double depth = rad*Math.cos(theta);
		double offset = rad*Math.sin(theta);
		
		RedBall workingBall = new RedBall(origin.x + Math.cos(facing)*offset - Math.sin(facing)*depth,
                						  origin.y + Math.sin(facing)*offset + Math.cos(facing)*depth,
                						  ballRadius, RedBalls);
		Iterator<RedBall> ballSet = getRedBalls();
		boolean unique = true;
		
		while(ballSet.hasNext()){
			if(!ballSet.next().verifyObject(workingBall)){
				unique = false;
			}
		}
		if(unique)
			RedBalls.add(workingBall);
	}
	
	public Iterator<GreenBall> getGreenBalls(){ return GreenBalls.iterator(); }
	
	public void addGreenBall(double x, double y, double radius, Coordinate[] state){
		double facing = state[1].x;
		Coordinate origin = state[0];
		double phi = computePhi(y);
		double theta = computeTheta(x);
		
		double dist = ballRadius / Math.tan((computeTheta(x + radius*ballScaleFactor) - computeTheta(x - radius*ballScaleFactor))/2.0);
		double rad = 0.75*((cameraHeight - ballRadius)/Math.atan(cameraOffset - phi)) +
					 0.25*(Math.sqrt(Math.pow(dist, 2.0) - Math.pow(cameraHeight - ballRadius,2.0)));
		
		double depth = rad*Math.cos(theta);
		double offset = rad*Math.sin(theta);
		
		GreenBall workingBall = new GreenBall(origin.x + Math.cos(facing)*offset - Math.sin(facing)*depth,
                						      origin.y + Math.sin(facing)*offset + Math.cos(facing)*depth,
                						      ballRadius, GreenBalls);
		Iterator<GreenBall> ballSet = getGreenBalls();
		boolean unique = true;
		
		while(ballSet.hasNext()){
			if(!ballSet.next().verifyObject(workingBall)){
				unique = false;
			}
		}
		if(unique)
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

		double r1 = (1 / ballScaleFactor) * ((cameraHeight - wallHeight) / Math.tan(90.0 - cameraOffset + phi1));
		double r2 = (1 / ballScaleFactor) * ((cameraHeight - wallHeight) / Math.tan(90.0 - cameraOffset + phi2));
		
		double depth1 = r1*Math.cos(theta1);
		double offset1 = r1*Math.sin(theta1);
		double depth2 = r2*Math.cos(theta2);
		double offset2 = r2*Math.sin(theta2);
		
		Wall workingWall = new Wall(origin.x + Math.cos(facing)*offset1 - Math.sin(facing)*depth1,
				           			origin.y + Math.sin(facing)*offset1 + Math.cos(facing)*depth1,
				           			origin.x + Math.cos(facing)*offset2 - Math.sin(facing)*depth2,
				           			origin.y + Math.sin(facing)*offset2 + Math.cos(facing)*depth2,
				           			Walls);
		
		Iterator<Wall> wallSet = getWalls();
		boolean unique = true;
		
		while(wallSet.hasNext()){
			if(!wallSet.next().verifyObject(workingWall)){
				unique = false;
				break;
			}
		}
		if(unique)
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
		
		Silo workingSilo = new Silo(origin.x + Math.cos(facing)*offset1 - Math.sin(facing)*depth1,
       								origin.y + Math.sin(facing)*offset1 + Math.cos(facing)*depth1,
       								origin.x + Math.cos(facing)*offset2 - Math.sin(facing)*depth2,
       								origin.y + Math.sin(facing)*offset2 + Math.cos(facing)*depth2,
       								number);

		Iterator<Silo> siloSet = getSilos();
		boolean unique = true;
		
		while(siloSet.hasNext()){
			if(!siloSet.next().verifyObject(workingSilo)){
				unique = false;
				break;
			}
		}
		if(unique)
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
		
		Container workingContainer = new Container(origin.x + Math.cos(facing)*offset1 - Math.sin(facing)*depth1,
       											   origin.y + Math.sin(facing)*offset1 + Math.cos(facing)*depth1,
       											   origin.x + Math.cos(facing)*offset2 - Math.sin(facing)*depth2,
       											   origin.y + Math.sin(facing)*offset2 + Math.cos(facing)*depth2);
		if(getContainer() == null)
			Container = workingContainer;
		else
			getContainer().verifyObject(workingContainer);	
	}
}
