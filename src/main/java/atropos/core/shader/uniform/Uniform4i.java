package atropos.core.shader.uniform;

import com.jogamp.opengl.GL2;

public class Uniform4i extends Uniform {

	public Uniform4i(int location) {
		super(location);
	}
	
	public void set(GL2 gl, int v0, int v1, int v2, int v3) {
		gl.glUniform4i(location, v0, v1, v2, v3);
	}
	
	public void set(GL2 gl, int[] values, int offset) {
		gl.glUniform4iv(location, 1, values, offset);
	}
	
}