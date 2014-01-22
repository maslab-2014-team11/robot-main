package robot.map;

public class Coordinate{
	    public final double x;
	    public final double y;

	    public Coordinate(double x, double y) {
	        this.x = x;
	        this.y = y;
	    }
	    
	    public double distanceFrom(Coordinate other){
	    	return Math.sqrt(Math.pow(this.x - other.x, 2.0) 
	    			       + Math.pow(this.y - other.y, 2.0));
	    }
	    
	    public Coordinate vectorTo(Coordinate dest){
	    	return new Coordinate(dest.x - this.x,
	    						  dest.y - this.y);	
	    }
	    
	    public double angleBetween(Coordinate other){
	    	return Math.acos((this.x*other.x + this.y*other.y)/
	    			          (Math.sqrt(this.x*this.x + this.y*this.y) +
	    			           Math.sqrt(other.x*other.x + other.y*other.y)));
	    }

	    @Override
	    public boolean equals(Object o) {
	        if (this == o) { // quick check
	            return true;
	        }

	        if (o == null || !(o instanceof Coordinate)) {
	            return false;
	        }

	        double otherFirst = ((Coordinate) o).x;
	        double otherSecond = ((Coordinate) o).y;
	        return this.x == otherFirst && this.y == otherSecond;
	    }
}
