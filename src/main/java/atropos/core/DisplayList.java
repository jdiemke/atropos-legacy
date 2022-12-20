package atropos.core;

import com.jogamp.opengl.GL2;

public class DisplayList {
	
	private int dlHandle;
	
	public DisplayList(GL2 gl) {
		dlHandle = gl.glGenLists(1);
		
		
	}

	public void begin(GL2 gl) {
		gl.glNewList(getHandle(), GL2.GL_COMPILE);
	}
	
	public void end(GL2 gl) {
		gl.glEndList();
	}
	
	public void delete(GL2 gl) {
		
	}
	
	public void construct(GL2 gl) {
		
	}
	
	public void draw(GL2 gl) {
		gl.glCallList(dlHandle);
	}
	
	public int getHandle() {
		return dlHandle;
	}
}
