package atropos.core.shader.uniform;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

public class UniformMatrix2fv extends Uniform {
	
	public UniformMatrix2fv(int location) {
		super(location);
	}
	
	public void set(GL2 gl, int count, boolean transpose, float[] values, int offset) {
		gl.glUniformMatrix2fv(location, count, transpose, values, offset);
	}

}