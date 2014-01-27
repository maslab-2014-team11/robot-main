package robot.camera;

import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;

public class CameraProducer extends Thread{

	private VideoCapture input;
	private Mat image = new Mat();
	private Object imageLock = new Object();
	
	public CameraProducer(VideoCapture input){
		this.input = input;
	}
	
	public void getImage(Mat dest){
		synchronized(imageLock){
			image.copyTo(dest);
		}
	}
	
	public void run(){
		while(true){
			try{
				synchronized(imageLock){
					while (!input.read(image)) {
							Thread.sleep(1);
					}
				}
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
