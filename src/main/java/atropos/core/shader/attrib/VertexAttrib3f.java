package atropos.core.shader.attrib;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

public class VertexAttrib3f extends VertexAttrib {
	
	public VertexAttrib3f(int location) {
		super(location);
	}
	
	public void set(GL2 gl, float v0, float v1, float v2) {
		gl.glVertexAttrib3f(location, v0, v1, v2);
	}
	
	public void set(GL2 gl, float[] values, int offset) {
		gl.glVertexAttrib3fv(location, values, offset);
	}

}