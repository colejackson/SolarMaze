package maze.ou.cs.cg.driver;

import maze.ou.cs.cg.swing.MazeFrame;

// Launch Point for the Maze Program
public class Driver 
{
	public static void main(String[] args) 
	{	
		// Creates a basic frame that will display the maze
		MazeFrame mf = new MazeFrame(MazeFrame.MAZE_MODE);
		
		// aaaaaaand....Launch!
		mf.setVisible(true);
	}
}
