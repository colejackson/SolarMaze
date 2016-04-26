package maze.ou.cs.cg.graphics;

import com.jogamp.opengl.glu.GLU;

import maze.ou.cs.cg.objects.Candle;
import maze.ou.cs.cg.objects.LightBall;
import maze.ou.cs.cg.objects.Particle;
import maze.ou.cs.cg.objects.TractorBeam;


//Generates the particles floating around an object, like the orbs
public class ParticleEngine
{
	//Number of particles around object
	int numParticles;
	//Decay rate
	float decay;
	//Array of particles
	Particle[] particles;
	int lastUsed = 0;
	int count = 0;
	
	//Constructor to create an engine object
	public ParticleEngine(int numParticles, float decay)
	{
		this.numParticles = numParticles;
		this.decay = decay;
		particles = new Particle[numParticles];
		
		for(int i = 0; i < numParticles; i++)
		{
			particles[i] = new Particle();
		}
	}
	
	//Updates the particle positions for a light ball object
	protected void update(LightBall ball, GLU glu, Camera camera)
	{
		int numNewParticles = 3;
		
		for(int i = 0; i < numNewParticles; i++)
		{
			int dead = firstDead(ball);
			spawn(particles[dead], ball, glu, camera);
		}
		
		for(int i = 0; i < numParticles; i++)
		{
			particles[i].setLife(decay);
			particles[i].move(count);
			particles[i].update(glu, camera, ball);
		}
	}
	
	//Updates the particle positions for a candle object
	protected void update(Candle candle, GLU glu, Camera camera)
	{
		int numNewParticles = 2;
		
		for(int i = 0; i < numNewParticles; i++)
		{
			int dead = firstDead(candle);
			if(dead != -1)
			{
				spawn(particles[dead], candle, glu, camera);
			}
		}
		
		for(int i = 0; i < numParticles; i++)
		{
			particles[i].setLife(decay);
			if(particles[i].position[2] > .011f)
				particles[i].position[2] -= 0.00001f;
			particles[i].update(glu, camera, candle);
		}
	}
	
	//Updates the particle positions for a tractor beam object
	protected void update(TractorBeam beam, GLU glu, Camera camera)
	{
		int numNewParticles = 3;
		
		for(int i = 0; i < numNewParticles; i++)
		{
			int dead = firstDead(beam);
			if(dead != -1)
			{
				spawn(particles[dead], beam, glu, camera);
			}
		}
		
		for(int i = 0; i < numParticles; i++)
		{
			particles[i].setLife(decay);
			particles[i].position[2] += 0.01f;
			particles[i].update(glu, camera, beam);
		}
	}
	
	//Returns the index of the first "dead" particle on a light ball
	private int firstDead(LightBall ball)
	{
		for(int i = lastUsed; i < numParticles; i++)
		{
			if(particles[i].getLife() <= 0.0f)
			{
				lastUsed = i;
				return i;
			}
		}
		
		for(int i = 0; i < lastUsed; i++)
		{
			if(particles[i].getLife() < 0.0f)
			{
				lastUsed = i;
				return i;
			}
		}
		
		lastUsed = 0;
		return lastUsed;
	}
	
	//Returns the index of the first "dead" particle on a candle
	private int firstDead(Candle candle)
	{
		for(int i = lastUsed; i < numParticles; i++)
		{
			if(particles[i].getLife() <= 0.0f)
			{
				lastUsed = i;
				return i;
			}
		}
		return -1;
	}
	
	//Returns the index of the first "dead" particle on a tractor beam
	private int firstDead(TractorBeam beam)
	{
		for(int i = lastUsed; i < numParticles; i++)
		{
			if(particles[i].getLife() <= 0.0f)
			{
				lastUsed = i;
				return i;
			}
		}
		
		for(int i = 0; i < lastUsed; i++)
		{
			if(particles[i].getLife() < 0.0f)
			{
				lastUsed = i;
				return i;
			}
		}
		
		lastUsed = 0;
		return lastUsed;
	}
	
	//Spawn a new particle for a light ball
	private void spawn(Particle part, LightBall ball, GLU glu, Camera camera)
	{
		part.newLife();
		for(int i = 0; i < 3; i++)
		{
			part.color[i] = ball.color[i];
		}
		
		part.color[3] = (float)Math.random() * 4.0f;
		
		//Find random position on the light ball
		float randx = (float)Math.random();
		float randy = (float)Math.random();
		float randz = (float)Math.random();
		
		if(randx > 0.5)
			part.position[0] = (float)ball.getX() + (randx * 0.01f);
		
		else
			part.position[0] = (float)ball.getX() - (randx * 0.01f);
		
		if(randy > 0.5)
			part.position[1] = (float)ball.getY() + (randy * 0.01f);
		
		else
			part.position[1] = (float)ball.getY() - (randy * 0.01f);


		if(randz > 0.5)
			part.position[2] = 0.1f + randz * 0.015f;
				
		else
			part.position[2] =0.1f - randz * 0.015f;

		//Update the particle
		part.update(glu, camera, ball);
	}
	
	//Spawn a new particle for a candle
	private void spawn(Particle part, Candle candle, GLU glu, Camera camera)
	{
		part.life = 7.0f;
		for(int i = 0; i < 4; i++)
		{
			part.color[i] = 255.0f;
		}
		
		part.color[0] = 0.965f;
		part.color[1] = 0.946f;
		part.color[2] = 0.883f;
		part.color[3] = 255.0f;
		
		//Find a random position on the candle
		float randx = (float)Math.random();
		float randy = (float)Math.random();
		
		if(randx > 0.5)
			part.position[0] = (float)candle.getX() + (float)Math.random() * 0.0005f;
		
		else
			part.position[0] = (float)candle.getX() - (float)Math.random() * 0.0005f;
		
		if(randy > 0.5)
			part.position[1] = (float)candle.getY() + (float)Math.random() * 0.0005f;
		
		else
			part.position[1] = (float)candle.getY() - (float)Math.random() * 0.0005f;
		
		part.position[2] = 0.022f;
		
		//Update the particle
		part.update(glu, camera, candle);
	}

	//Spawn a new particle for a tractor beam object
	private void spawn(Particle part, TractorBeam beam, GLU glu, Camera camera)
	{
		part.life = 0.09f;
		part.color[0] = 0.8f;
		part.color[1] = (float) (Math.random() * 0.02f) + 0.9f;
		part.color[2] = (float) (Math.random() * 0.02f) + 0.9f;
		part.color[3] = (float)Math.random() * 8.0f;
		
		//find random position
		float randx = (float)Math.random();
		float randy = (float)Math.random();
		
		if(randx > 0.5)
			part.position[0] = (float)beam.getX() + (randx * 0.02f);
		
		else
			part.position[0] = (float)beam.getX() - (randx * 0.02f);
		
		if(randy > 0.5)
			part.position[1] = (float)beam.getY() + (randy * 0.02f);
		
		else
			part.position[1] = (float)beam.getY() - (randy * 0.02f);

		part.position[2] = (float)Math.random();

		//Update the particle
		part.update(glu, camera, beam);
	}
}