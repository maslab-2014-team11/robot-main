package robot.camera;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.opencv.core.Core;
import org.opencv.core.Mat;
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
	}
	
	public void run(){
		while(true){
			System.out.println("Fetching Image");
			this.input.getImage(rawImage);
			System.out.println("Fetching State");
			Coordinate[] state = map.getState();
			List<Mat> outputSets = new ArrayList<Mat>();
			outputSets.add(rawImage);
			
			System.out.println("Image & State -> Data");
			
			prepImage();
			
			outputSets.addAll(findRedBalls(false));
			outputSets.addAll(findGreenBalls(false));
			outputSets.addAll(findBlueWalls(false));
			
			System.out.println("Pushing Data");
			
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
		if(debugMode){
			//output.add(maskedImage);
			Imgproc.cvtColor(grayImage, grayImage, Imgproc.COLOR_GRAY2BGR);
			output.add(grayImage);
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
		if(debugMode){
			//output.add(maskedImage);
			Imgproc.cvtColor(grayImage, grayImage, Imgproc.COLOR_GRAY2BGR);
			output.add(grayImage);
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
		if(debugMode){
			Imgproc.cvtColor(grayImage, grayImage, Imgproc.COLOR_GRAY2BGR);
			output.add(maskedImage);
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
			if(tempMask.get((int) startingSet.get(0,i).clone()[1],
							(int) startingSet.get(0,i).clone()[0])[0] < 128)
				newWidth++;
		ballSet = Mat.zeros((int) startingSet.size().height, newWidth, startingSet.type());
		int j = 0;
		for(int i = 0; i < startingSet.size().width; i++)
			if(tempMask.get((int) startingSet.get(0,i).clone()[1],
							(int) startingSet.get(0,i).clone()[0])[0] < 128){
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

}
