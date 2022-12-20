package atropos.core.shader.attrib;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

public class VertexAttrib2f extends VertexAttrib {
	
	public VertexAttrib2f(int location) {
		super(location);
	}
	
	public void set(GL2 gl, float v0, float v1) {
		gl.glVertexAttrib2f(location, v0, v1);
	}
	
	public void set(GL2 gl, float[] values, int offset) {
		gl.glVertexAttrib2fv(location, values, offset);
	}

}