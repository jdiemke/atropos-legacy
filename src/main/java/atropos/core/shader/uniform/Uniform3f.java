package atropos.core.shader.uniform;

import com.jogamp.opengl.GL2;

public class Uniform3f extends Uniform {
	
	public Uniform3f(int location) {
		super(location);
	}

	public void set(GL2 gl, float v0, float v1, float v2) {
		gl.glUniform3f(location, v0, v1, v2);
	}
	
	public void set(GL2 gl, float[] values, int offset) {
		gl.glUniform3fv(location, 1, values, offset);
	}
	
}