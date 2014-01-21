package robot.map;

public class Coordinate{
	    public final double x;
	    public final double y;

	    public Coordinate(double x, double y) {
	        this.x = x;
	        this.y = y;
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
