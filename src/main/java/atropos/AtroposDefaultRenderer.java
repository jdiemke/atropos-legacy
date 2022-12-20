package atropos;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.jogamp.opengl.DebugGL2;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.GL2;

public class AtroposDefaultRenderer implements GLEventListener, MouseListener, KeyListener {

	public int mousex, mousey;
	
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
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		
		gl.glTranslatef(-1.5f, 0.0f, -6.0f);

		gl.glBegin(GL2.GL_TRIANGLES);		    
			gl.glColor3f(1.0f, 0.0f, 0.0f);
			gl.glVertex3f(0.0f, 1.0f, 0.0f);
			gl.glColor3f(0.0f, 1.0f, 0.0f);
			gl.glVertex3f(-1.0f, -1.0f, 0.0f);
			gl.glColor3f(0.0f, 0.0f, 1.0f);
			gl.glVertex3f(1.0f, -1.0f, 0.0f);
		gl.glEnd();
		
		gl.glTranslatef(3.0f, 0.0f, 0.0f);
		
		gl.glBegin(GL2.GL_QUADS);
			gl.glColor3f(0.5f, 0.5f, 1.0f);
			gl.glVertex3f(-1.0f, 1.0f, 0.0f);
			gl.glVertex3f(1.0f, 1.0f, 0.0f);
			gl.glVertex3f(1.0f, -1.0f, 0.0f);
			gl.glVertex3f(-1.0f, -1.0f, 0.0f);
		gl.glEnd();
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		GL2 gl = drawable.getGL().getGL2();
		GLU glu = new GLU();

		if (height <= 0)
			height = 1;
		
		final float h = (float) width / (float) height;
		
		gl.glViewport(0, 0, width, height);
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		
		glu.gluPerspective(45.0f, h, 1.0, 100.0);
		
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {	
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}