package atropos.core.shader.attrib;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

public class VertexAttrib1f extends VertexAttrib {
	
	public VertexAttrib1f(int location) {
		super(location);
	}
	
	public void set(GL2 gl, float v0) {
		gl.glVertexAttrib1f(location, v0);
	}
	
	public void set(GL2 gl, float[] values, int offset) {
		gl.glVertexAttrib1fv(location, values, offset);
	}

}