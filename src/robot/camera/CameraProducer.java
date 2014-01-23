package robot.camera;

import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;

public class CameraProducer extends Thread{

	private VideoCapture input;
	private Mat image;
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
			synchronized(imageLock){
				while (!input.read(image)) {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
}
