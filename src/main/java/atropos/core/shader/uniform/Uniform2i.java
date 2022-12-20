package atropos.core.shader.uniform;

import com.jogamp.opengl.GL2;

public class Uniform2i extends Uniform {

	public Uniform2i(int location) {
		super(location);
	}
	

	public void set(GL2 gl, int v0, int v1) {
		gl.glUniform2i(location, v0, v1);
	}
	
	public void set(GL2 gl, int[] values, int offset) {
		gl.glUniform2iv(location, 1, values, offset);
	}
	
}