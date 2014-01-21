package robot.camera.profiles;

import org.opencv.core.Scalar;
import org.opencv.core.Size;

import robot.camera.Profile;

public class VisionProfile extends Profile{
	public VisionProfile(){
		this.blurKernal = new Size(19,19);
		this.blurSigma = 4;
		
		this.redMinima = new Scalar(12, 0, 0);
		this.redMaxima = new Scalar(167, 255, 255);
		this.greenMinima = new Scalar(35, 60, 20);
		this.greenMaxima = new Scalar(120, 255, 255);
		this.blueMinima = new Scalar(0, 20, 20);
		this.blueMaxima = new Scalar(35, 230, 230);
		
		this.sobelStrength = 250;
		
		this.lineWidth = 1;
		this.lineRotationIncrements = (float) (Math.PI/720);
		this.lineThreshold = 200;
		this.lineMinLength = 100;
		this.lineMaxDisjoint = 50;
		
		this.ballResolution = 10;
		this.ballMinSeparation = 50;
		this.ballThreshold = 420;
		this.ballMinSize = 10;
		this.ballMaxSize = 300;
	}
}
