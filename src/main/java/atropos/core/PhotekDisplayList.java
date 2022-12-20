package atropos.core;

import com.jogamp.opengl.GL2;

import atropos.core.shader.ShaderProgram;

public class PhotekDisplayList {
	
	private int displayListId_;
	public ShaderProgram prog;
	
	public PhotekDisplayList(GL2 gl, ShaderProgram prog) {
		displayListId_ = gl.glGenLists(1);
		this.prog = prog;
		if(displayListId_ == 0) {
			System.out.println("display list couldnt be created!");
			return;
		}
		
		gl.glNewList(displayListId_, GL2.GL_COMPILE);
			fill(gl);
		gl.glEndList();
	}
	
	public void fill(GL2 gl) {
		
	}
	
	public void execute(GL2 gl) {
		gl.glCallList(displayListId_);
	}
	
	public void destroy(GL2 gl) {
		gl.glDeleteLists(displayListId_, 1);
	}

}
