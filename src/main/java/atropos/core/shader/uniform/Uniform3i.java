package atropos.core.shader.uniform;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

public class Uniform3i extends Uniform {

	public Uniform3i(int location) {
		super(location);
	}
	
	public void set(GL2 gl, int v0, int v1, int v2) {
		gl.glUniform3i(location, v0, v1, v2);
	}
	
	public void set(GL2 gl, int[] values, int offset) {
		gl.glUniform3iv(location, 1, values, offset);
	}
	
}