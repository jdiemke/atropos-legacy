package atropos.core.shader.uniform;

import com.jogamp.opengl.GL2;

public class Uniform2fv extends Uniform {

	public Uniform2fv(int location) {
		super(location);
	}
	
	public void set(GL2 gl, int count, float[] values, int offset) {
		gl.glUniform2fv(location, count, values, offset);
	}
	
}