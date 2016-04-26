package maze.ou.cs.cg.objects;

import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;

import maze.ou.cs.cg.driver.OGL;
import maze.ou.cs.cg.graphics.Camera;


public class Particle
{
	public float[] position = new float[3];
	float velocityx = 0.001f;
	float velocityy = 0.000f;
	float radius;
	public float[] color = new float[4];
	boolean left;
	
	//How long the particle exists
	public float life;
	
	public Particle()
	{
		position[0] = 0.0f;
		position[1] = 0.0f;
		
		for(int i = 0; i < 4; i++)
		{
			color[i] = 1.0f;
		}
		
		life = 0.0f;
		
		radius = (float)Math.random() * 0.001f;
		
		if(Math.random() >= 0.5)
		{
			left = true;
		}
	}

	//Update the light ball particle position
	public void update(GLU glu, Camera camera, LightBall ball)
	{
		GLUquadric quad = glu.gluNewQuadric();
		glu.gluQuadricNormals(quad, GLU.GLU_SMOOTH);   // Create Smooth Normals ( NEW )
		glu.gluQuadricTexture(quad, false); 
		
		//OGL.gl.glColor4f(color[0], color[1], color[2], color[3]);
		OGL.gl.glColor4f((float)(245.0/255.0), (float)(175.0/255.0), (float)(47.0/255.0), color[3]);
		OGL.gl.glPushMatrix();
		
		OGL.gl.glTranslatef(position[0], position[1], position[2]);
		OGL.gl.glScalef(1, 1, 2);
		glu.gluSphere(quad, radius, 10, 10);
		
		OGL.gl.glPopMatrix();
		glu.gluDeleteQuadric(quad);
	}
	
	//Update the candle particles
	public void update(GLU glu, Camera camera, Candle candle)
	{
		GLUquadric quad = glu.gluNewQuadric();
		glu.gluQuadricNormals(quad, GLU.GLU_SMOOTH);   // Create Smooth Normals ( NEW )
		glu.gluQuadricTexture(quad, false); 
		
		OGL.gl.glColor4f(color[0], color[1], color[2], color[3]);
		OGL.gl.glPushMatrix();
		
		OGL.gl.glTranslatef(position[0], position[1], position[2]);
		OGL.gl.glScalef(1, 1, 3);
		glu.gluSphere(quad, 0.00115f, 10, 10);
		
		OGL.gl.glPopMatrix();
		glu.gluDeleteQuadric(quad);
	}
	
	//Updates particle positions in tractor beam
	public void update(GLU glu, Camera camera, TractorBeam beam){
		GLUquadric quad = glu.gluNewQuadric();
		glu.gluQuadricNormals(quad, GLU.GLU_SMOOTH);   // Create Smooth Normals ( NEW )
		glu.gluQuadricTexture(quad, false); 
		
		OGL.gl.glColor4f(color[0], color[1], color[2], color[3]);
		OGL.gl.glPushMatrix();
		
		OGL.gl.glTranslatef(position[0], position[1], position[2]);
		OGL.gl.glScalef(1, 1, 2);
		glu.gluSphere(quad, 0.0005f, 10, 10);
		
		OGL.gl.glPopMatrix();
		glu.gluDeleteQuadric(quad);
	}
	
	//Set the life of a particle based on a decay rate
	public void setLife(float decay)
	{
		life = life - decay;
		color[3] = color[3] - 3 * decay;
	}
	
	public void newLife()
	{
		// Random life makes it trail off much nicer
		life = (float)Math.random();
	}
	
	public float getLife()
	{
		return life;
	}
	
	public void move(int count)
	{
		if(left)
		{
			//position[0] += velocityy * Math.random() * 0.0005f;
			position[1] += velocityx * Math.random() * 0.0005f;
		}

		//else{
			//position[0] -= velocityy * Math.random() * 0.0005f;
			//position[1] -= velocityx * Math.random() * 0.0005f;
		//}
		
		position[2] += Math.random() * 0.001;
	}
}