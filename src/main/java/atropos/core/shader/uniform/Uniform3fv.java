package atropos.core.shader.uniform;

import com.jogamp.opengl.GL2;
public class Uniform3fv extends Uniform {

	public Uniform3fv(int location) {
		super(location);
	}
	
	public void set(GL2 gl, int count, float[] values, int offset) {
		gl.glUniform3fv(location, count, values, offset);
	}
	
}