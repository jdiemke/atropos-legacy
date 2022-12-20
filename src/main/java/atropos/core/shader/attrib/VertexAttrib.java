package atropos.core.shader.attrib;

import java.nio.Buffer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

public abstract class VertexAttrib {
	
	protected int location;
	
	public VertexAttrib(int location) {
		this.location = location;
	}
	
	public void setVertexAttribArray(GL2 gl, boolean enabled) {
		if(enabled) {
			gl.glEnableVertexAttribArray(location);
		} else {
			gl.glDisableVertexAttribArray(location);
		}
	}
	
	public void enableVertexAttribArray(GL2 gl) {
		gl.glEnableVertexAttribArray(location);
	}
	
	public void disableVertexAttribArray(GL2 gl) {
		gl.glDisableVertexAttribArray(location);
	}
	
	// TODO make typesafe
	public void setVertexAttribPointer(GL2 gl, int size, int type, boolean normalized, int stride, Buffer pointer) {
		gl.glVertexAttribPointer(location, size, type, normalized, stride, pointer);
	}

}