package maze.ou.cs.cg.graphics;

import java.awt.geom.Point2D;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;

import maze.ou.cs.cg.async.Director;
import maze.ou.cs.cg.async.Worker;
import maze.ou.cs.cg.driver.OGL;
import maze.ou.cs.cg.maze.Maze;
import maze.ou.cs.cg.maze.Wall;
import maze.ou.cs.cg.maze.WallPreprocessor;
import maze.ou.cs.cg.objects.LightBall;
import maze.ou.cs.cg.objects.TractorBeam;


public class Stage implements GLEventListener
{
	private Maze maze = null;
	
	private boolean newBuffer = false;
	
	// GL Global Objects
	private GLProfile profile;
	private GLCapabilities capabilities;;
	private GLCanvas canvas;
	
	//Buffer of walls and all walls
	private ArrayList<Wall> buffer = null;
	private ArrayList<Wall> draw = null;
			
	// GL Function Objects
	GL2 gl = null;
	GLU glu = new GLU();
	GLUT glut = new GLUT();
	
	// Maze Related Objects
	private Camera camera;
	private Director director;
	private WallPreprocessor wpp;
	private Stage stage;
	
	// Objects in the Maze
	private TractorBeam tractor;
	private Texture orbTexture;
	private ParticleEngine tractorEngine;
	private ArrayList<LightBall> balls;
	private ArrayList<ParticleEngine> engines;
	
	
	/////////// SCRIPT //////////////
	HashMap<String, Boolean> script = new HashMap<String, Boolean>();

	// Make a Stage object containing a Maze
	public Stage(Maze maze)
	{	
		// Hey its the maze;
		this.maze = maze;
		this.stage = this;
		
		// Initialize the variables to set the frame.
		this.director = new Director();
		this.profile = GLProfile.getDefault();
		this.capabilities = new GLCapabilities(profile);
		this.canvas = new GLCanvas(capabilities);
		
		// Configure the canvas, add KeyListener and Request Focus
		canvas.addGLEventListener(this);
		canvas.setFocusable(true);
		canvas.addKeyListener(director);
		canvas.requestFocus();
		
		// Animator stared up, runs display 60x per second.
		FPSAnimator	animator = new FPSAnimator(canvas, 60);
		animator.start();
		
		/////////// SCRIPT //////////////
		script.put("shade", false);
		script.put("lightball", true);
		script.put("beamOn", false);
		script.put("beamMoving", true);
		script.put("float", false);
		script.put("end", false);
	}

	@Override
	public void display(GLAutoDrawable glad) 
	{
		// Clear the color and depth buffers.
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
		
		// Render the Maze.
		render(glad);
	}

	@Override
	public void init(GLAutoDrawable glad) 
	{
		// Set the GL Object
		gl = glad.getGL().getGL2();
		
		try 
		{
			Field gl_object = OGL.class.getDeclaredField("gl");
			gl_object.setAccessible(true);
			
			Field modifiersField = Field.class.getDeclaredField( "modifiers" );
            modifiersField.setAccessible( true );
            modifiersField.setInt(gl_object, gl_object.getModifiers() & ~Modifier.FINAL );
			
			gl_object.set(null, gl);
			
			gl_object = OGL.class.getDeclaredField("glu");
			gl_object.setAccessible(true);
			
			modifiersField = Field.class.getDeclaredField( "modifiers" );
            modifiersField.setAccessible( true );
            modifiersField.setInt(gl_object, gl_object.getModifiers() & ~Modifier.FINAL );
			
			gl_object.set(null, glu);
		} 
		catch (NoSuchFieldException | SecurityException | IllegalAccessException e) 
		{
			e.printStackTrace();
		}
		
		//Create a tractor and it's particle engine
		tractor = new TractorBeam(0.0, 0.0);
		tractorEngine = new ParticleEngine(50, 0.01f);
		
		//Create an array of balls and an array of engines used by each ball
		engines = new ArrayList<>();
		balls = new ArrayList<>();
		
		// Initialize variables except camera which requires the GL2 object be initialized first.
		this.wpp = new WallPreprocessor(maze);
		this.camera = new Camera();
		new Worker(stage, wpp, camera, balls);
		
		// Enable the depth test code.
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glDepthFunc(GL2.GL_LESS);
		
		// Enable Antialiasing
		gl.glEnable(GL.GL_LINE_SMOOTH);
		
		//Enable transparency
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		//Add the light balls to the array of balls and add a particle engine for each of them
		balls.add(new LightBall(.00916, -.05544));
		engines.add(new ParticleEngine(25, 0.015f));
		balls.add(new LightBall(.583839, -.59799));
		engines.add(new ParticleEngine(25, 0.015f));
		balls.add(new LightBall(.15807, -.756));
		engines.add(new ParticleEngine(25, 0.015f));
		balls.add(new LightBall(-.56109, -.21425));
		engines.add(new ParticleEngine(25, 0.015f));
		balls.add(new LightBall(.3058, .3270));
		engines.add(new ParticleEngine(25, 0.015f));		
		
		//Create the tractor beam
		tractor = new TractorBeam(0.0, 0.0);
		
		orbTexture = Scenery.createTexture("ImagesOther/fire.jpg");
		
		// Initialize the scenery
		Scenery.initTextures();
		Scenery.initCamera(camera);
	}

	@Override
	public void dispose(GLAutoDrawable glad) {}
	
	@Override
	public void reshape(GLAutoDrawable glad, int x, int y, int w, int h) {}
	
	// Accessor for the canvas.
	public GLCanvas canvas()
	{
		return canvas;
	}
	
	//Render the scene on the OpenGL Canvas
	private void render(GLAutoDrawable glad)
	{	
		/////////////////////// SCRIPTING CODE ////////////////////////////
		//If a light ball has been captured, remove it
		int j = 0;
		int rem = -1;
		for(LightBall lb : balls)
		{
			if(new Point2D.Double(camera.getPosition()[0], camera.getPosition()[1]).distance(lb) < .02)
				rem = j;
			j++;
		}
		
		if(rem != -1)
		{
			balls.remove(rem);
			engines.remove(rem);
		}
		
		//If the game has ended, exit
		if(script.get("end"))
			System.exit(0);
		
		//If the z value has increased, the game has ended, so set this variable to true
		if(camera.getPosition()[2] >= 2)
			script.put("end", true);
		
		//If the size of the ball array is less than 5, one has been captured, so they should stop being displayed
		if(balls.size() < 5)
		{
			script.put("lightball", false);
		}
		
		//If the light balls are not being displayed, turn the tractor beam on and set it to move
		if(!script.get("lightball"))
		{
			script.put("beamOn", true);
			script.put("beamMoving", true);
		}
		
		//If the beam is on and the user is within it
		if(script.get("beamOn") && (camera.getPosition()[0] - tractor.getX()) < .02 && (camera.getPosition()[0] - tractor.getX()) > -.02 && (camera.getPosition()[1] - tractor.getY()) < .02 && (camera.getPosition()[1] - tractor.getY()) > -.02)
		{
			//Set movement to false
			script.put("beamMoving", false);
			//Automatically raise user up through the tunnel to exit
			this.director = new Director();
			director.remove("u:.005");
			director.add("u:.005");
			director.remove("r:.1");
			director.add("r:.1");
			newBuffer = false;
			buffer = wpp;
		}
		
		//If the beam is not moving, set this in the tractor beam class
		if(!script.get("beamMoving"))
			tractor.setMoving(false);
		
		/////////////////////// END SCRIPTING ////////////////////////////////
		
		
		//If the lightball is within a certain distance of the user, then set it to active
		for(LightBall b : balls)
		{
			if((new Point2D.Double(camera.getPosition()[0], camera.getPosition()[1]).distance(b) < .5))
				b.setActive(true);
			else
				b.setActive(false);
		}
		
		// Check for commands from the director and pass them to the camera.
		for(String s : director)
			camera.command(s);
	
		while(buffer == null)
		{
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		// Switch out the buffer if you need to.
		if(newBuffer)
		{
			newBuffer = false;	
			draw = buffer;
		}	

		//Draw the ground and sky
		Scenery.drawGround();
		Scenery.drawSky();
		
		//Draw the walls
		draw.stream().sorted((x,y) -> Integer.compare(x.getTexIndx(), y.getTexIndx())).forEach(c -> c.glDraw());
		
		//Draw the candles
		Scenery.drawCandles(glu);
		
		//Draw the orbs and their particles
		if(script.get("lightball"))
		{
			for(int i = 0; i < balls.size(); i++)
			{
				if(balls.get(i).getActive())
				{
					balls.get(i).draw(orbTexture);
					engines.get(i).update(balls.get(i), glu, camera);
				}
			}
		}
		
		//Draw light beam
		if(script.get("beamOn"))
		{
			tractorEngine.update(tractor, glu, camera);
			tractor.draw();
		}
		
		//Draw the dimmer
		//Scenery.drawDimmer(glu, camera);
	}
	
	public void setBuffer(ArrayList<Wall> al)
	{
		this.buffer = al;
		newBuffer = true;
	}

}

//////////////////////////// OLD CODE ///////////////////////////

////Randomize orb starting locations
//int numberOfOrbs = 6;
//Random rand = new Random();
//Point2D.Double userPosition = new Point2D.Double(camera.getPosition()[0], camera.getPosition()[1]);
//Point2D.Double[] orbPositions = new Point2D.Double[numberOfOrbs];
//
//for(int j=0; j<numberOfOrbs; ++j)
//{
//	double x = rand.nextDouble();
//	double y = rand.nextDouble();
//	Point2D.Double location = new Point2D.Double(x, y);
//	
//	boolean awayFromUser = false;
//	boolean insideMaze = false;
//	boolean notInWall = false;
//	while(awayFromUser != true && insideMaze != true && notInWall != true)
//	{
//		//If coordinates are within certain distance of user's initial position, find new
//		double radius = 0.1;
//		double lhs = ((x-userPosition.getX())*(x-userPosition.getX())) + ((y-userPosition.getY())*(y-userPosition.getY()));
//		double rhs = radius*radius;
//		
//		if (lhs < rhs)
//			awayFromUser = false;
//		else
//			awayFromUser = true;
//		
//		
//		//If coordinates are outside of maze, find new
//		int numRows = maze.getRows();
//		System.out.println(maze.get(0, 0).getY());
//		
//		Point2D.Double vertex1 = new Point2D.Double(0/(double)numRows, -(0-((double)numRows)/2.0)/((double)numRows/2.0));
//		Point2D.Double vertex2 = new Point2D.Double(0/(double)numRows, -(((double)numRows)-((double)numRows)/2.0)/((double)numRows/2.0));
//		Point2D.Double vertex3 = new Point2D.Double(((double) Utilities.fooCount(numRows))/(double)numRows, -(((double)numRows)-((double)numRows)/2.0)/((double)numRows/2.0));
//		
//		
//		double angle1 = Utilities.getAngle(location, vertex1)+360.0;
//		double angle2 = Utilities.getAngle(location, vertex2)+360.0;
//		double angle3 = Utilities.getAngle(location, vertex3)+360.0;
//		
//		double value = (angle1-angle3)+(angle3-angle2)+(angle2-angle1);
//		
//		if (value <= 361 || value >= 359)
//			insideMaze = true;
//		else
//			insideMaze = false;
//		
//		
//		//If coordinates are in/on a wall, find new
////		LightBall lightball = new LightBall(x, y);
////		boolean collide = LightBall.willCollide(wpp, lightball);
////		if(collide == true)
////			notInWall = false;
////		else
////			notInWall = true;
//	}
//	
//	orbPositions[j] = location;
//}
//
//for(int i = 0; i < numberOfOrbs; i++)
//{
//	balls.add(new LightBall(orbPositions[i].getX(),orbPositions[i].getY()));
//	engines.add(new ParticleEngine(25, 0.015f));
//}