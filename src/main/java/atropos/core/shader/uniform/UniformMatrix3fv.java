package atropos.core.shader.uniform;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

public class UniformMatrix3fv extends Uniform {
	
	public UniformMatrix3fv(int location) {
		super(location);
	}
	
	public void set(GL2 gl, int count, boolean transpose, float[] values, int offset) {
		gl.glUniformMatrix3fv(location, count, transpose, values, offset);
	}

}