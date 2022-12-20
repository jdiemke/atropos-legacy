package atropos.core.shader.uniform;

import com.jogamp.opengl.GL2;

public class Uniform4f extends Uniform {
	
	public Uniform4f(int location) {
		super(location);
	}

	public void set(GL2 gl, float v0, float v1, float v2, float v3) {
		gl.glUniform4f(location, v0, v1, v2, v3);
	}
	
	public void set(GL2 gl, float[] values, int offset) {
		gl.glUniform4fv(location, 1, values, offset);
	}
	
}