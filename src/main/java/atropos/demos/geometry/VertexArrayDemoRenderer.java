package atropos.demos.geometry;

import java.nio.FloatBuffer;
import java.util.Random;

import com.jogamp.opengl.DebugGL2;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import com.jogamp.common.nio.Buffers;

import atropos.AtroposDefaultRenderer;


public class VertexArrayDemoRenderer extends AtroposDefaultRenderer {
	
	FloatBuffer vertexBuffer;
	
	@Override
	public void init(GLAutoDrawable drawable) {		
		// Use debug pipeline
	    drawable.setGL(new DebugGL2(drawable.getGL().getGL2()));
		GL2 gl = drawable.getGL().getGL2();
		
		System.err.println("INIT GL IS: " + gl.getClass().getName());
	    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
	    System.err.println("GL_VENDOR: " + gl.glGetString(GL2.GL_VENDOR));
	    System.err.println("GL_RENDERER: " + gl.glGetString(GL2.GL_RENDERER));
	    System.err.println("GL_VERSION: " + gl.glGetString(GL2.GL_VERSION));
		
		// Enable VSync
		gl.setSwapInterval(1);
		
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glDepthFunc(GL2.GL_LEQUAL);
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
		
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		/// starts here
		
		
		vertexBuffer = Buffers.newDirectFloatBuffer(3 * 3); //
		vertexBuffer.put(0.0f);
		vertexBuffer.put(0.0f);
		vertexBuffer.put(0.0f);
		
		vertexBuffer.put(1.0f);
		vertexBuffer.put(0.0f);
		vertexBuffer.put(0.0f);
		
		vertexBuffer.put(0.0f);
		vertexBuffer.put(1.0f);
		vertexBuffer.put(0.0f);
		
		vertexBuffer.rewind();
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		
		gl.glTranslatef(0.0f, 0.0f, -6.0f);
		
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		
		gl.glVertexPointer(3, GL2.GL_FLOAT, 0, vertexBuffer);
		gl.glDrawArrays(GL2.GL_TRIANGLES, 0, 3);
		
		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
	}

}