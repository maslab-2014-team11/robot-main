package robot.map;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import robot.map.objects.Wall;

public class Grid {

	public int height;
	public int width;
	public int resolution;
	
	private Object gridLock = new Object();
	private boolean[][] grid;
	
	public Grid(int h, int w, int r){
		this.height = h;
		this.width = w;
		this.resolution = r;
		this.grid = new boolean[width][height];
		resetGrid();
	}
	
	public void setCoord(int x, int y, boolean value){
		synchronized(gridLock){
		if( x > 0 && x < width)
			if( y > 0 && y < height)
				this.grid[x][y] = value;
		}
	}
	
	public void setCoord(Coordinate coord, boolean value){
		synchronized(gridLock){
		if( coord.x > 0 && coord.x < width)
			if( coord.y > 0 && coord.y < height)
				this.grid[(int) Math.round(coord.x)][(int) Math.round(coord.y)] = value;
		}
	}
	
	public boolean getCoord(int x, int y){
		synchronized(gridLock){
		if( x > 0 && x < width)
			if( y > 0 && y < height)
				return this.grid[x][y];
		return false;
		}
	}
	
	public boolean getCoord(Coordinate coord){
		synchronized(gridLock){
		if( coord.x > 0 && coord.x < width)
			if( coord.y > 0 && coord.y < height)
				return this.grid[(int) Math.round(coord.x)][(int) Math.round(coord.y)];
		return false;
		}
	}
	
	private void resetGrid(){
		for(int i = 0; i < width; i++)
			for(int j = 0; j < height; j++)
				setCoord(i,j,true);
	}
	
	private void addWall(Wall obstruction){
		Coordinate[] coords = obstruction.getCoords();
		double x1 = coords[0].x;
		double x2 = coords[1].x;
		double xm = Math.signum(x2-x1);
		double y1 = coords[0].y;
		double y2 = coords[1].y;
		double ym = Math.signum(y2-y1);
		for(int i = (int) x1; i*xm < x2*xm; i = (int) (i + 1*xm))
			for(int j = (int) y1; j*ym < y2*ym; j = (int) (j + 1*ym))
				for(int m = -18; m < 19; m++)
					for(int n = -18; n < 19; n++)
						setCoord(i+m,j+n,false);
	}
	
	public void addWalls(Iterator<Wall> obstructionSet){
		synchronized(gridLock){
		resetGrid();
		for(;obstructionSet.hasNext();){
			addWall(obstructionSet.next());
		}
		}
	}
	
	public List<Coordinate> giveMoves(int x, int y){
		synchronized(gridLock){
		List<Coordinate> output = new ArrayList<Coordinate>();
		for(int i = -1; i < 2; i++)
			for(int j = -1; j < 2; j++)
				if(!(i == 0 && j == 0) && getCoord(x+i,y+i))
					output.add(new Coordinate(x + i, y + j));
		return output;
		}
	}
	
	public List<Coordinate> giveMoves(Coordinate coord){
		synchronized(gridLock){
		List<Coordinate> output = new ArrayList<Coordinate>();
		for(int i = -1; i < 2; i++)
			for(int j = -1; j < 2; j++)
				if(!(i == 0 && j == 0) && getCoord((int) Math.round(coord.x+i),(int) Math.round(coord.y+i)))
					output.add(new Coordinate(Math.round(coord.x+i),Math.round(coord.y+i)));
		return output;
		}
	}
}
