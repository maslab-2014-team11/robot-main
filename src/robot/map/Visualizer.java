package robot.map;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import robot.map.objects.GreenBall;
import robot.map.objects.RedBall;
import robot.map.objects.Wall;

public class Visualizer extends Thread{
	
	private Map map;
	
	private int h, w;
	private Mat mapImage;
	private Mat cameraImage;
	private List<Mat> cameraImages = new ArrayList<Mat>();
	private Object cameraLock = new Object();
	
	private int width = 1920;
	private int height = 1080;
	
	JLabel cameraPane = createWindow("Camera output", width, height);
	JLabel mapPane = createWindow("Map output", width, height);
	
	private Mat2Img ImageConverter = new Mat2Img(BufferedImage.TYPE_3BYTE_BGR);

	
	public Visualizer(int h, int w, Map map){
		this.map = map;
		this.h = h;
		this.w = w;
	}
	
	public void setCameraImages(List<Mat> newImages){
		synchronized(cameraLock){
			cameraImages.clear();
			for(Iterator<Mat> set = newImages.iterator(); set.hasNext();)
				cameraImages.add(set.next().clone());
		}
	}
	
	private void DrawRobot(Mat canvas, Coordinate[] state){
		Point center = new Point(state[0].x, state[0].y);
		int radius = (int) Math.round(Map.botSize);
	
		Core.circle(canvas, center, 3, new Scalar(255,255,255), -1, 5, 0);
		Core.circle(canvas, center, radius, new Scalar(255,255,255), 3, 5, 0);
		
		Core.line(canvas, 
				  new Point(state[0].x, state[0].y), 
			  	  new Point(state[0].x + Math.cos(state[1].x)*Map.botSize*0.5, 
			  			  	state[0].y + Math.sin(state[1].x)*Map.botSize*0.5), 
			  	  new Scalar(255,255,255), 3);
	}
	
	public void run(){
		while(true){
			mapImage = Mat.zeros(w*Map.squareSize, 
								 h*Map.squareSize, 
								 CvType.CV_8UC3);
			mapImage.setTo(new Scalar(255,255,255));
			
			Coordinate[] state = map.getState();
			
			for(Iterator<RedBall> balls = map.getRedBalls(); balls.hasNext();)
				balls.next().Draw(mapImage);
			
			for(Iterator<GreenBall> balls = map.getGreenBalls(); balls.hasNext();)
				balls.next().Draw(mapImage);
			
			for(Iterator<Wall> walls = map.getWalls(); walls.hasNext();)
				walls.next().Draw(mapImage);
			
			DrawRobot(mapImage, state);
			
			//
			
			synchronized(cameraLock){
				if(!cameraImages.isEmpty()){
					cameraImage = cameraImages.get(0);
					
					Mat lineMap = cameraImages.get(3);
					for( int i = 0; i < lineMap.size().width; i++){
						  Core.line(cameraImage, new Point(lineMap.get(0, i)[0], lineMap.get(0, i)[1]), 
								  				 new Point(lineMap.get(0, i)[2], lineMap.get(0, i)[3]), new Scalar(255,0,0), 3);
					}
					
					Mat circleMap = cameraImages.get(2);
					for( int i = 0; i < circleMap.size().width; i++){
						Point center = new Point(Math.round(circleMap.get(0, i)[0]), Math.round(circleMap.get(0, i)[1]));
						int radius = (int) Math.round(circleMap.get(0, i)[2]);
						
						Core.circle(cameraImage, center, 3, new Scalar(0,255,0), -1, 5, 0);
						Core.circle(cameraImage, center, radius, new Scalar(0,255,0), 3, 5, 0);
					}
					
					circleMap = cameraImages.get(1);
					for( int i = 0; i < circleMap.size().width; i++){
						Point center = new Point(Math.round(circleMap.get(0, i)[0]), Math.round(circleMap.get(0, i)[1]));
						int radius = (int) Math.round(circleMap.get(0, i)[2]);
						
						Core.circle(cameraImage, center, 3, new Scalar(0,0,255), -1, 5, 0);
						Core.circle(cameraImage, center, radius, new Scalar(0,0,255), 3, 5, 0);
					}
				} else {
					cameraImage = Mat.zeros(w*Map.squareSize, 
							 				h*Map.squareSize, 
							 				CvType.CV_8UC3);
				}
			}
			
			//
			
			updateWindow(cameraPane, cameraImage, ImageConverter);
			updateWindow(mapPane, mapImage, ImageConverter);
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
    private static JLabel createWindow(String name, int width, int height) {    
        JFrame imageFrame = new JFrame(name);
        imageFrame.setSize(width, height);
        imageFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JLabel imagePane = new JLabel();
        imagePane.setLayout(new BorderLayout());
        imageFrame.setContentPane(imagePane);
        
        imageFrame.setVisible(true);
        return imagePane;
    }
    
    private static void updateWindow(JLabel imagePane, Mat mat, Mat2Img converter) {
    	int w = (int) (mat.size().width);
    	int h = (int) (mat.size().height);
    	if (imagePane.getWidth() != w || imagePane.getHeight() != h) {
    		imagePane.setSize(w, h);
    	}
    	BufferedImage bufferedImage = converter.getImage(mat);
    	imagePane.setIcon(new ImageIcon(bufferedImage));
    }

}
