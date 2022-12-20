package atropos.core.shader.uniform;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

public class Uniform3iv extends Uniform {

	public Uniform3iv(int location) {
		super(location);
	}
	
	public void set(GL2 gl, int count, int[] values, int offset) {
		gl.glUniform3iv(location, count, values, offset);
	}
	
}