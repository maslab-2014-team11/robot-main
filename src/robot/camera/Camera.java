package robot.camera;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

import robot.camera.profiles.VisionProfile;
import robot.map.Coordinate;
import robot.map.Map;

public class Camera extends Thread {	
	public enum Type{
		VISION;
	}
	public final Type type;
	
	private CameraProducer input;
	private CameraWorker output;
	private Profile Settings;
	private Map map;
	public BlockingQueue<Coordinate[]> stateData = new LinkedBlockingQueue<Coordinate[]>();
	public BlockingQueue<List<Mat>> outputData = new LinkedBlockingQueue<List<Mat>>();

	private Mat redMask = new Mat();
	private Mat greenMask = new Mat();
	private Mat blueMask = new Mat();
	private Mat rawImage = new Mat();
	private Mat processingImage = new Mat();
	
	private Mat cameraMatrix = Mat.zeros(3, 3, 6);
	private Mat distCoeffs = Mat.zeros(5, 1, 6);
	
	public Camera(Type type, VideoCapture camera, Map map){
		this.input = new CameraProducer(camera);
		this.output = new CameraWorker(map, stateData, outputData);
		this.input.start();
		this.output.start();
		
		this.map = map;
		this.type = type;
		switch(type){
			case VISION:
				this.Settings = new VisionProfile();
				break;
		}
		
		//
		cameraMatrix.put(0, 0, 320.0);
		cameraMatrix.put(1, 1, 240.0);
		
		cameraMatrix.put(0, 2, 320.0);
		cameraMatrix.put(1, 2, 240.0);
		
		cameraMatrix.put(2, 2, 1.0);
		//
		distCoeffs.put(0, 0, 0.191);
		distCoeffs.put(1, 0, -0.848);
		distCoeffs.put(2, 0, 0.0);
		distCoeffs.put(3, 0, 0.0);
		distCoeffs.put(4, 0, 1.12);
	}
	
	public void run(){
		while(true){
			this.input.getImage(rawImage);
			Coordinate[] state = map.getState();
			List<Mat> outputSets = new ArrayList<Mat>();
			
			prepImage();
			outputSets.add(rawImage);
			
			outputSets.addAll(findRedBalls(false));
			outputSets.addAll(findGreenBalls(false));
			outputSets.addAll(findBlueWalls(false));
			
			try {
				stateData.put(state);
				outputData.put(outputSets);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void prepImage(){
		startingBlur(rawImage, processingImage);
		generateMasks(processingImage);
	}
	
	private List<Mat> findRedBalls(boolean debugMode){
		Mat maskedImage = new Mat();
		Mat grayImage = new Mat();
		Mat redBalls = new Mat();
		List<Mat> output = new ArrayList<Mat>();
		
		applyMask(processingImage, redMask, maskedImage);
		createEdgeMap(maskedImage, grayImage);
		fillBallSet(grayImage, redBalls);
		redBalls = thresholdBalls(redBalls, maskedImage);
		undistortBalls(redBalls);
		if(debugMode){
			output.add(maskedImage);
			//Imgproc.cvtColor(grayImage, grayImage, Imgproc.COLOR_GRAY2BGR);
			//output.add(grayImage);
		}
		output.add(redBalls);
		return output;
	}
	
	private List<Mat> findGreenBalls(boolean debugMode){
		Mat maskedImage = new Mat();
		Mat grayImage = new Mat();
		Mat greenBalls = new Mat();
		List<Mat> output = new ArrayList<Mat>();
		
		applyMask(processingImage, greenMask, maskedImage);
		createEdgeMap(maskedImage, grayImage);
		fillBallSet(grayImage, greenBalls);
		greenBalls = thresholdBalls(greenBalls, maskedImage);
		undistortBalls(greenBalls);
		if(debugMode){
			output.add(maskedImage);
			//Imgproc.cvtColor(grayImage, grayImage, Imgproc.COLOR_GRAY2BGR);
			//output.add(grayImage);
		}
		output.add(greenBalls);
		return output;
	}
	
	private List<Mat> findBlueWalls(boolean debugMode){
		Mat maskedImage = new Mat();
		Mat grayImage = new Mat();
		Mat blueWalls = new Mat();
		List<Mat> output = new ArrayList<Mat>();
		
		applyMask(processingImage, blueMask, maskedImage);
		createEdgeMap(maskedImage, grayImage);
		fillLineSet(grayImage, blueWalls);
		blueWalls = thresholdWalls(blueWalls, maskedImage);
		undistortWalls(blueWalls);
		if(debugMode){
			output.add(maskedImage);
			//Imgproc.cvtColor(grayImage, grayImage, Imgproc.COLOR_GRAY2BGR);
			//output.add(grayImage);
		}
		output.add(blueWalls);
		return output;
	}
	
	private void startingBlur(Mat raw, Mat output){
		Imgproc.bilateralFilter(raw, output, 5, Settings.initialSigma, 1.5*Settings.initialSigma);
		Imgproc.GaussianBlur(output, output, Settings.initialKernal, Settings.initialSigma);
	}
	
	private void blurImage(Mat raw, Mat output){
		Imgproc.GaussianBlur(raw, output, Settings.blurKernal, Settings.blurSigma);
	}
	
	private void generateMasks(Mat input){
		List<Mat> HSV = new ArrayList<Mat>();
		Mat workImage = new Mat();
		
		Imgproc.cvtColor(input, workImage, Imgproc.COLOR_BGR2HSV);
		Core.split(workImage, HSV);
		
		Core.inRange(workImage, Settings.redMinima, Settings.redMaxima, redMask);
		Core.inRange(workImage, Settings.greenMinima, Settings.greenMaxima, greenMask);
		Core.inRange(workImage, Settings.blueMinima, Settings.blueMaxima, blueMask);
		
		Core.bitwise_not(greenMask, greenMask);
		Core.bitwise_not(blueMask, blueMask);
	}
	
	private void applyMask(Mat input, Mat mask, Mat output){
		Mat tempMask = new Mat();
		Core.bitwise_not(mask, tempMask);
		
		input.copyTo(output);
		output.setTo(new Scalar(255, 255, 255), mask);
		output.setTo(new Scalar(0,0,0), tempMask);
		
		blurImage(output, output);
	}
	
	private void createEdgeMap(Mat input, Mat output){
		Imgproc.cvtColor(input, output, Imgproc.COLOR_BGR2GRAY);
		Imgproc.Canny(output, output, Settings.sobelStrength/2, Settings.sobelStrength);
	}
	
	private void fillLineSet(Mat edgeMap, Mat lineSet){
		Imgproc.HoughLinesP(edgeMap, lineSet, Settings.lineWidth, Settings.lineRotationIncrements, 
				Settings.lineThreshold, Settings.lineMinLength, Settings.lineMaxDisjoint);
	}
	
	private void fillBallSet(Mat edgeMap, Mat ballSet){
		Imgproc.HoughCircles(edgeMap, ballSet, Imgproc.CV_HOUGH_GRADIENT, 
				Settings.ballResolution, Settings.ballMinSeparation, Settings.sobelStrength*2, 
				Settings.ballThreshold, Settings.ballMinSize, Settings.ballMaxSize);
	}
	
	private Mat thresholdBalls(Mat startingSet, Mat mask){
		Mat tempMask = new Mat();
		Mat ballSet = new Mat();
		Imgproc.blur(mask, tempMask, new Size(1,1));
		int newWidth = 0;
		for(int i = 0; i < startingSet.size().width; i++)
			if((tempMask.get((int) startingSet.get(0,i).clone()[1],
							 (int) startingSet.get(0,i).clone()[0])[0] < 128) &&
			   (startingSet.get(0,i).clone()[0] > 250 && startingSet.get(0,i).clone()[0] < 1920-250) &&
			   (startingSet.get(0,i).clone()[1] > 150 && startingSet.get(0,i).clone()[1] < 1080-150))
				newWidth++;
		ballSet = Mat.zeros((int) startingSet.size().height, newWidth, startingSet.type());
		int j = 0;
		for(int i = 0; i < startingSet.size().width; i++)
			if((tempMask.get((int) startingSet.get(0,i).clone()[1],
					 (int) startingSet.get(0,i).clone()[0])[0] < 128) &&
			  (startingSet.get(0,i).clone()[0] > 250 && startingSet.get(0,i).clone()[0] < 1920-250) &&
			  (startingSet.get(0,i).clone()[1] > 150 && startingSet.get(0,i).clone()[1] < 1080-150)){
				ballSet.put(0, j, startingSet.get(0, i));
				j++;
			}
		return ballSet;
	}
	
	private Mat thresholdWalls(Mat startingSet, Mat mask){
		Mat tempMask = new Mat();
		Mat wallSet = new Mat();
		Imgproc.blur(mask, tempMask, new Size(1,1));
		int newWidth = 0;
		for(int i = 0; i < startingSet.size().width; i++)
			if(tempMask.get(Math.min((int)(startingSet.get(0,i).clone()[1] + startingSet.get(0,i).clone()[3])/2 + 10, 1080),
				    		(int) (startingSet.get(0,i).clone()[0] + startingSet.get(0,i).clone()[2])/2)[0] < 128)
				newWidth++;
		wallSet = Mat.zeros((int) startingSet.size().height, newWidth, startingSet.type());
		int j = 0;
		for(int i = 0; i < startingSet.size().width; i++)
			if(tempMask.get(Math.min((int)(startingSet.get(0,i).clone()[1] + startingSet.get(0,i).clone()[3])/2 + 10, 1080),
		    				(int) (startingSet.get(0,i).clone()[0] + startingSet.get(0,i).clone()[2])/2)[0] < 128){
				wallSet.put(0, j, startingSet.get(0, i));
				j++;
			}
		return wallSet;
	}
	
	private void undistortBalls(Mat ballSet){
		List<Point> balls = new ArrayList<Point>();
		for(int i = 0; i < ballSet.size().width; i++)
			balls.add(new Point(ballSet.get(0,i).clone()[0],
								ballSet.get(0,i).clone()[1]));
		
		MatOfPoint2f ballPoints = new MatOfPoint2f();
		ballPoints.fromList(balls);
		
		if(balls.size() > 0){
			Imgproc.undistortPoints(ballPoints, ballPoints, cameraMatrix, distCoeffs);
			
			int i = 0;
			for(Iterator<Point> points = ballPoints.toList().iterator(); points.hasNext();){
				Point working = points.next();
				ballSet.put(0,i,new double[]{Map.imageWidth/2 + (working.x*Map.imageWidth/2), 
				 							 Map.imageHeight/2 + (working.y*Map.imageHeight/2), 
				 							 ballSet.get(0,i).clone()[2]});
				i++;
			}
		}
	}
	
	private void undistortWalls(Mat wallSet){
		List<Point> walls = new ArrayList<Point>();
		for(int i = 0; i < wallSet.size().width; i++){
			walls.add(new Point(wallSet.get(0,i).clone()[0],
								wallSet.get(0,i).clone()[1]));
			walls.add(new Point(wallSet.get(0,i).clone()[2],
								wallSet.get(0,i).clone()[3]));
		}
		
		MatOfPoint2f wallPoints = new MatOfPoint2f();
		MatOfPoint2f outPoints = new MatOfPoint2f();
		wallPoints.fromList(walls);
		
		if(walls.size() > 0){
			Imgproc.undistortPoints(wallPoints, outPoints, cameraMatrix, distCoeffs);
			
			int i = 0;
			for(Iterator<Point> points = outPoints.toList().iterator(); points.hasNext();){
				Point working1 = points.next();
				Point working2 = points.next();
				wallSet.put(0,i,new double[]{Map.imageWidth/2 + (working1.x*Map.imageWidth/2), 
											 Map.imageHeight/2 + (working1.y*Map.imageHeight/2), 
											 Map.imageWidth/2 + (working2.x*Map.imageWidth/2), 
											 Map.imageHeight/2 + (working2.y*Map.imageHeight/2)});
				i++;
			}
		}
	}

}
