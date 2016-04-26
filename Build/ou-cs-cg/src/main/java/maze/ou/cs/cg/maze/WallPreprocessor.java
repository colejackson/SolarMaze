package maze.ou.cs.cg.maze;

import java.util.ArrayList;

// This array list processes the positions of maze walls
//Pre processed so this doesn't have to be done every render
public class WallPreprocessor extends ArrayList<Wall> 
{
	private static final long serialVersionUID = 4266007871842632293L;
	
	private final double WALL_BOTTOM = 0;
	private final double WALL_TOP = .2;

	// Store the maze that we are processing
	private Maze maze;
	
	// Start the processor
	public WallPreprocessor(Maze maze)
	{
		this.maze = maze;
		processMaze();		
	}
	
	// store all the walls in an array list
	private void processMaze()
	{	
		// For each point in the maze
		for(MazePoint mp : maze)
		{		
			// For all the walls that could be around that point
			for(int i = 0; i < 3; i++)
			{
				// If the wall exists
				if(mp.walls[i])
				{	
					this.add(buildWall(mp.getX(), mp.getY(), mp.get(i).getX(), mp.get(i).getY()));
				}
			}
		}
	}
	
	// This fairly complex method builds thickness into the walls that go into the array
	private Wall buildWall(int x_1, int y_1, int x_2, int y_2)
	{
		// Find the values in opengl coordinate for the position of walls.
		double x1 = x_1/((double)maze.getRows());
		double y1 = -(y_1-((double)maze.getRows())/2)/((double)maze.getRows()/2);
		double x2 = x_2/((double)maze.getRows());
		double y2 = -(y_2-((double)maze.getRows())/2)/((double)maze.getRows()/2);
		
		double distance = Math.sqrt(Math.pow(y1-y2, 2) + Math.pow(x1-x2, 2));
		double wallWidth = .006;
		
		Side[] wall = new Side[5];
			
		// Find an x and y offset for all the walls.
		double xo = (Math.abs(y1-y2)/distance)*(wallWidth/2);
		double yo = (Math.abs(x1-x2)/distance)*(wallWidth/2);
		
		// Add the offset for walls shaped like \ and those like /
		if((x1<x2 && y1<y2) || (x1>x2 && y1>y2))
		{
			wall[0] = new Side(new double[] {x1-xo, y1+yo, x2-xo, y2+yo, WALL_BOTTOM, WALL_TOP});
			wall[1] = new Side(new double[] {x1+xo, y1-yo, x2+xo, y2-yo, WALL_BOTTOM, WALL_TOP});
			wall[2] = new Side(new double[] {x1+xo, y1-yo, x1-xo, y1+yo, WALL_BOTTOM, WALL_TOP});
			wall[3] = new Side(new double[] {x2+xo, y2-yo, x2-xo, y2+yo, WALL_BOTTOM, WALL_TOP});
			wall[4] = new Side(new double[] {x1-xo, y1+yo, x2-xo, y2+yo, x2+xo, y2-yo, x1+xo, y1-yo, WALL_TOP});
		}
		else
		{
			wall[0] = new Side(new double[] {x1+xo, y1+yo, x2+xo, y2+yo, WALL_BOTTOM, WALL_TOP});
			wall[1] = new Side(new double[] {x1-xo, y1-yo, x2-xo, y2-yo, WALL_BOTTOM, WALL_TOP});
			wall[2] = new Side(new double[] {x1-xo, y1-yo, x1+xo, y1+yo, WALL_BOTTOM, WALL_TOP});
			wall[3] = new Side(new double[] {x2+xo, y2+yo, x2-xo, y2-yo, WALL_BOTTOM, WALL_TOP});
			wall[4] = new Side(new double[] {x1+xo, y1+yo, x2+xo, y2+yo, x2-xo, y2-yo, x1-xo, y1-yo, WALL_TOP});
		}
		
		return new Wall(wall);
	}
}