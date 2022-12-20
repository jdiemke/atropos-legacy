package atropos.core.shader.uniform;

import com.jogamp.opengl.GL2;

public class Uniform1f extends Uniform {
	
	public Uniform1f(int location) {
		super(location);
	}

	public void set(GL2 gl, float v0) {
		gl.glUniform1f(location, v0);
	}
	
	public void set(GL2 gl, float[] values, int offset) {
		gl.glUniform1fv(location, 1, values, offset);
	}
	
}