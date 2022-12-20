package atropos.core.shader.uniform;


import com.jogamp.opengl.GL2;


public class Uniform4fv extends Uniform {

	public Uniform4fv(int location) {
		super(location);
	}
	
	public void set(GL2 gl, int count, float[] values, int offset) {
		gl.glUniform4fv(location, count, values, offset);
	}
	
}