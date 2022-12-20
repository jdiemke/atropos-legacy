package atropos.core.model.wavefront;

import com.jogamp.opengl.util.texture.Texture;

public class Material {
	
	atropos.core.Material material = new atropos.core.Material();
	Texture texture = null;

	public Material() {
		// TODO default initialization
	}
	
	public boolean hasTexture() {
		return texture != null;
	}
	
	public void setAmbient(float red, float green, float blue) {
		material.setAmbient(red, green, blue, 1.0f);
	}

	public void setDiffuse(float red, float green, float blue) {
		material.setDiffuse(red, green, blue, 1.0f);
	}
	
	public void setSpecular(float red, float green, float blue) {
		material.setSpecular(red, green, blue, 1.0f);
	}

	public void setShininess(float s) {
		material.setShininess(s);
		
	}
	
	public void setTexture(Texture texture) {
		this.texture = texture;
	}
	
}
