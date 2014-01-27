package robot.camera;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

public abstract class Profile {
	public Size initialKernal;
	public int initialSigma;
	
	public Size blurKernal;
	public int blurSigma;
	
	public Scalar clippingMinima, clippingMaxima;
	public Scalar redMinima, redMaxima;
	public Scalar greenMinima, greenMaxima;
	public Scalar blueMinima, blueMaxima;
	
	public int sobelStrength;
	
	public int lineWidth;
	public float lineRotationIncrements;
	public int lineThreshold;
	public int lineMinLength;
	public int lineMaxDisjoint;
	
	public int ballResolution;
	public int ballMinSeparation;
	public int ballThreshold;
	public int ballMinSize;
	public int ballMaxSize;
}
