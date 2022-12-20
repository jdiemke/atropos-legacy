package atropos.core.shader.uniform;

import com.jogamp.opengl.GL2;

public class Uniform1i extends Uniform {

	public Uniform1i(int location) {
		super(location);
	}
	
	public void set(GL2 gl, int v0) {
		gl.glUniform1i(location, v0);
	}
	
	public void set(GL2 gl, int[] values, int offset) {
		gl.glUniform1iv(location, 1, values, offset);
	}
	
}