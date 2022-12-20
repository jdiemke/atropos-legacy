package atropos.core.shader.uniform;

import com.jogamp.opengl.GL2;

public class UniformMatrix4fv extends Uniform {
	
	public UniformMatrix4fv(int location) {
		super(location);
	}
	
	public void set(GL2 gl, int count, boolean transpose, float[] values, int offset) {
		gl.glUniformMatrix4fv(location, count, transpose, values, offset);
	}

}