package robot.camera.profiles;

import org.opencv.core.Scalar;
import org.opencv.core.Size;

import robot.camera.Profile;

public class VisionProfile extends Profile{
	public VisionProfile(){
		this.initialKernal = new Size(25,25);
		this.initialSigma = 100;
		
		this.blurKernal = new Size(19,19);
		this.blurSigma = 5;
		
		this.clippingMinima = new Scalar(0, 0, 0);
		this.clippingMaxima = new Scalar(255, 100, 100);
		
		this.redMinima = new Scalar(12, 0, 0);
		this.redMaxima = new Scalar(167, 255, 255);
		this.greenMinima = new Scalar(50, 100, 30);//35,60,20//50,100,30
		this.greenMaxima = new Scalar(170, 255, 160);//120,255,255//170,255,160
		this.blueMinima = new Scalar(90, 120, 120);//0,20,20
		this.blueMaxima = new Scalar(140, 250, 250);//35,230,230
		
		this.sobelStrength = 250;
		
		this.lineWidth = 1;
		this.lineRotationIncrements = (float) (Math.PI/720);
		this.lineThreshold = 100;//200
		this.lineMinLength = 100;
		this.lineMaxDisjoint = 100;//50
		
		this.ballResolution = 5;//10//15//1
		this.ballMinSeparation = 75;
		this.ballThreshold = 1;//420//250//20
		this.ballMinSize = 10;//10//1
		this.ballMaxSize = 100;
	}
}
