package atropos.core.shader.attrib;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

public class VertexAttrib4f extends VertexAttrib {
	
	public VertexAttrib4f(int location) {
		super(location);
	}
	
	public void set(GL2 gl, float v0, float v1, float v2, float v3) {
		gl.glVertexAttrib4f(location, v0, v1, v2, v3);
	}
	
	public void set(GL2 gl, float[] values, int offset) {
		gl.glVertexAttrib4fv(location, values, offset);
	}

}