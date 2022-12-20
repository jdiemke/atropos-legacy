package atropos.core.shader.uniform;

import com.jogamp.opengl.GL2;

public class Uniform1fv extends Uniform {

	public Uniform1fv(int location) {
		super(location);
	}
	
	public void set(GL2 gl, int count, float[] values, int offset) {
		gl.glUniform1fv(location, count, values, offset);
	}
	
}