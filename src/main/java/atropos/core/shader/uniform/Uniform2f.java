package atropos.core.shader.uniform;

import com.jogamp.opengl.GL2;

public class Uniform2f extends Uniform {

	public Uniform2f(int location) {
		super(location);
	}

	public void set(GL2 gl, float v0, float v1) {
		gl.glUniform2f(location, v0, v1);
	}
	
	public void set(GL2 gl, float[] values, int offset) {
		gl.glUniform2fv(location, 1, values, offset);
	}
	
}