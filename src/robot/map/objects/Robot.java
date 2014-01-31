package robot.map.objects;

public class Robot {
	public static final boolean[][] groundProfile = new boolean[][]
			{};
	
	private static boolean access(int x, int y){
		if(x > 0 && x < groundProfile.length)
			if(y > 0 && y < groundProfile[0].length)
				return groundProfile[x][y];
		return groundProfile[Math.min(groundProfile.length -1, Math.max(x, 0))]
							[Math.min(groundProfile[0].length -1, Math.max(y, 0))];
	}
	
	public static boolean getValue(double x, double y){
		return (access((int)x,(int)y) ||
				access((int)x+1,(int)y) ||
				access((int)x,(int)y+1) ||
				access((int)x+1,(int)y+1));
	}
	
	public static boolean[][] getProfile(int i, int j){
		double angle = Math.atan((double) j/(double) i);
		boolean[][] out = new boolean[groundProfile.length][groundProfile[0].length];
		
		double centerX = out.length/2.0;
		double centerY = out[0].length/2.0;
		
		for(int m = 0; m < out.length;m++)
			for(int n = 0; n < out[0].length;n++)
				out[m][n] = getValue(m*Math.sin(angle) + 
									 	n*Math.cos(angle) - 
									 	(Math.sqrt(centerX*centerX + centerY*centerY)*
									 			(Math.sin(Math.atan(-centerY/centerX)) - 
									 			 Math.sin(Math.atan(-centerY/centerX) - angle))),
									 m*Math.cos(angle) + 
									 	n*Math.sin(angle) - 
									 	(Math.sqrt(centerX*centerX + centerY*centerY)*
									 			(Math.cos(Math.atan(-centerY/centerX)) - 
									 			 Math.cos(Math.atan(-centerY/centerX) - angle))));
		return out;
	}
}
