package maze.ou.cs.cg.maze;

import java.util.function.Consumer;
import com.jogamp.opengl.util.texture.Texture;

//Creates a material object used by the walls so this doesn't have to be created every single time
public class Material 
{
	private Texture texture;
	private Consumer<Object> consumer;
	
	public Material(Texture texture, Consumer<Object> consumer)
	{
		this.texture = texture;
		this.consumer = consumer;
	}
	
	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	public Consumer<Object> getConsumer() {
		return consumer;
	}

	public void setConsumer(Consumer<Object> consumer) {
		this.consumer = consumer;
	}
}
