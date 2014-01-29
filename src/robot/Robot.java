package robot;

import static robot.Constants.GYRO_A_OFFSET;
import static robot.Constants.GYRO_SPI_PORT;
import static robot.Constants.GYRO_SS_PIN;
import static robot.Constants.L_ENCODER_READ_PIN;
import static robot.Constants.L_ENCODER_TRIGGER_PIN;
import static robot.Constants.L_WHEEL_DIR_PIN;
import static robot.Constants.L_WHEEL_PWM_PIN;
import static robot.Constants.R_ENCODER_READ_PIN;
import static robot.Constants.R_ENCODER_TRIGGER_PIN;
import static robot.Constants.R_WHEEL_DIR_PIN;
import static robot.Constants.R_WHEEL_PWM_PIN;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import localization.CarrotPathFollower;
import localization.GyroEncoderDeadReckoner;
import localization.PathFollower.Result;
import localization.PoseSource;

import org.opencv.highgui.VideoCapture;

import control.Drive;
import control.Motor;
import devices.Maple;
import devices.actuators.ExtendedCytron;
import devices.sensor.ExtendedEncoder;
import devices.sensor.ExtendedGyroscope;
import robot.camera.Camera;
import robot.decisionengine.DecisionEngine;
import robot.map.Coordinate;
import robot.map.Map;

public class Robot{
	
	public Map map;
	public boolean running;
	private VideoCapture visionCamera;
	private Camera vision;
	
	private long time;
	
	private final Maple maple;
	private CarrotPathFollower controller;
	private List<Coordinate> controllerPath = Arrays.asList(Coordinate.ORIGIN,
			new Coordinate(9.2029, 10.0000), new Coordinate(14.2754,
					30.2899), new Coordinate(22.6812, 52.0290),
			new Coordinate(37.3188, 65.2174), new Coordinate(58.7681,
					76.5217), new Coordinate(82.5362, 86.5217),
			new Coordinate(97.0290, 92.0290));
	private Object pathLock = new Object();
	
	private final ExtendedCytron leftMotor;
	private final ExtendedCytron rightMotor;
	private final ExtendedEncoder leftEnc;
	private final ExtendedEncoder rightEnc;
	private final Motor left;
	private final Motor right;
	private final ExtendedGyroscope gyro;
	
	private DecisionEngine decisionEngine;
	
	public Robot() throws FileNotFoundException {
		this.leftMotor = new ExtendedCytron(L_WHEEL_DIR_PIN, L_WHEEL_PWM_PIN);
		this.rightMotor = new ExtendedCytron(R_WHEEL_DIR_PIN, R_WHEEL_PWM_PIN);

		this.leftEnc = new ExtendedEncoder(L_ENCODER_TRIGGER_PIN,
				L_ENCODER_READ_PIN);
		this.rightEnc = new ExtendedEncoder(R_ENCODER_TRIGGER_PIN,
				R_ENCODER_READ_PIN);

		this.left = new Motor(leftMotor, leftEnc);
		this.right = new Motor(rightMotor, rightEnc);
		this.gyro = new ExtendedGyroscope(GYRO_SPI_PORT, GYRO_SS_PIN);
		
		this.maple = Maple.getMaple();

		this.visionCamera = new VideoCapture();
		this.visionCamera.open(0);
		this.time = System.currentTimeMillis();
	}
	
	public void init(){
		map = new Map(10, 10, this, true);
		vision = new Camera(Camera.Type.VISION, this.visionCamera, this.map);
		decisionEngine = new DecisionEngine(this, this.map);
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		vision.start();
		decisionEngine.start();
		
		maple.registerDevice(leftMotor);
		maple.registerDevice(rightMotor);
		maple.registerDevice(leftEnc);
		maple.registerDevice(rightEnc);
		maple.registerDevice(gyro);

		gyro.setOffset(GYRO_A_OFFSET);
		gyro.setTotalAngleDeg(0);

		leftMotor.setMaxMagnitudeOutput(0.2);
		rightMotor.setMaxMagnitudeOutput(0.2);
		maple.start();
		
		PoseSource posSource = new GyroEncoderDeadReckoner(rightEnc, leftEnc, gyro);
		Drive drive = new Drive(this.right, this.left, posSource);
		posSource.setCurrentPose(Coordinate.ORIGIN, 0);
		controller = new CarrotPathFollower(posSource, drive, 30);
	}
	
	public long getTime(){
		return System.currentTimeMillis() - time;
	}
	
	public List<Coordinate> getPath(){
		synchronized(pathLock){
			return new ArrayList<Coordinate>(this.controllerPath);
		}
	}
	
	public void setPath(List<Coordinate> path){
		synchronized(pathLock){
			this.controllerPath = new ArrayList<Coordinate>(path);
		}
	}
	
	public void start(){
		while(true){
			if(!controller.checkPath(getPath()))
				controller.setPath(getPath());
			if(controller.followPath() == Result.Finished)
				decisionEngine.hasFinished();
		}
	}
}
