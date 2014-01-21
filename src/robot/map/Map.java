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
	private Robot bot;
	
	private static double imageHeight, imageWidth;
	private static double ballRadius, wallHeight;
	private static double cameraOffset, cameraFOVx, cameraFOVy;
	
	public Map(int h, int w, Robot bot){
		this.discreteMap = new Grid(h, w, 1);
		this.bot = bot;
		initialize();
	}
	
	public void initialize(){
		imageHeight = 1080;
		imageWidth = 1920;
		
		ballRadius = 1.75;
		wallHeight = 4;
		
		cameraOffset = Math.PI/6.0;
		cameraFOVx = (2*Math.PI)/3.0;
		cameraFOVy = (Math.PI)/2.0;
		
	}
	
 	private double computePhi(double i){
		return Math.atan(i/imageHeight - 0.5) * 2 * cameraFOVy;
	}
	
	private double computeTheta(double i){
		return Math.atan(i/imageWidth - 0.5) * 2 * cameraFOVx;
	}
	
	public Iterator<RedBall> getRedBalls(){ return RedBalls.iterator(); }
	
	public void addRedBall(double x, double y, double radius){
		double facing = bot.getFacing();
		Coordinate origin = bot.getLocation();
		double phi = computePhi(y);
		double theta = computeTheta(x);
		
		double dist = (radius/2.0) * Math.atan((computePhi(x - (radius/2.0)) - computePhi(x + (radius/2.0))/2.0));
		double rad = 0.75*(imageHeight - ballRadius*Math.tan(cameraOffset-phi)) +
				     0.25*(Math.sqrt(Math.pow(dist, 2.0) - Math.pow(imageHeight - ballRadius,2.0)));
		
		double depth = rad*Math.cos(theta);
		double offset = rad*Math.sin(theta);
		
		RedBalls.add(new RedBall(origin.x + Math.cos(facing)*depth - Math.sin(facing)*offset,
				                 origin.y + Math.sin(facing)*depth + Math.cos(facing)*offset,
				                 ballRadius));
	}
	
	public Iterator<GreenBall> getGreenBalls(){ return GreenBalls.iterator(); }
	
	public void addGreenBall(float x, float y, float radius){
		double facing = bot.getFacing();
		Coordinate origin = bot.getLocation();
		double phi = computePhi(y);
		double theta = computeTheta(x);
		
		double dist = (radius/2.0) * Math.atan((computePhi(x - (radius/2.0)) - computePhi(x + (radius/2.0))/2.0));
		double rad = 0.75*(imageHeight - ballRadius*Math.tan(cameraOffset-phi)) +
				     0.25*(Math.sqrt(Math.pow(dist, 2.0) - Math.pow(imageHeight - ballRadius,2.0)));
		
		double depth = rad*Math.cos(theta);
		double offset = rad*Math.sin(theta);
		
		GreenBalls.add(new GreenBall(origin.x + Math.cos(facing)*depth - Math.sin(facing)*offset,
				                     origin.y + Math.sin(facing)*depth + Math.cos(facing)*offset,
				                     ballRadius));
	}
	
	public Iterator<Wall> getWalls(){ return Walls.iterator(); }
	
	public void addWall(float x1, float y1, float x2, float y2){
		double facing = bot.getFacing();
		Coordinate origin = bot.getLocation();
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
		
		Walls.add(new Wall(origin.x + Math.cos(facing)*depth1 - Math.sin(facing)*offset1,
				           origin.y + Math.sin(facing)*depth1 + Math.cos(facing)*offset1,
				           origin.x + Math.cos(facing)*depth2 - Math.sin(facing)*offset2,
				           origin.y + Math.sin(facing)*depth2 + Math.cos(facing)*offset2));
		
		discreteMap.addWalls(getWalls());
	}
	
	public Iterator<Silo> getSilos(){ return Silos.iterator(); }
	
	public void addSilo(float x1, float y1, float x2, float y2, int number){
		double facing = bot.getFacing();
		Coordinate origin = bot.getLocation();
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
		
		Silos.add(new Silo(origin.x + Math.cos(facing)*depth1 - Math.sin(facing)*offset1,
				           origin.y + Math.sin(facing)*depth1 + Math.cos(facing)*offset1,
				           origin.x + Math.cos(facing)*depth2 - Math.sin(facing)*offset2,
				           origin.y + Math.sin(facing)*depth2 + Math.cos(facing)*offset2,
				           number));
	}
	
	public Container getContainer(){ return Container; }
	
	public void addContainer(float x1, float y1, float x2, float y2){
		double facing = bot.getFacing();
		Coordinate origin = bot.getLocation();
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
		
		Container = new Container(origin.x + Math.cos(facing)*depth1 - Math.sin(facing)*offset1,
				                  origin.y + Math.sin(facing)*depth1 + Math.cos(facing)*offset1,
				                  origin.x + Math.cos(facing)*depth2 - Math.sin(facing)*offset2,
				                  origin.y + Math.sin(facing)*depth2 + Math.cos(facing)*offset2);
	}
}
