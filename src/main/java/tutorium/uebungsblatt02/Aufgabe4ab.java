package tutorium.uebungsblatt02;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;

import com.jogamp.opengl.util.Animator;

public class Aufgabe4ab implements GLEventListener {

	public static void main(String[] args) {
		GLProfile.initSingleton();

		GLProfile glp = GLProfile.get(GLProfile.GL2);
		GLCapabilities caps = new GLCapabilities(glp);
		GLCanvas canvas = new GLCanvas(caps);
		canvas.addGLEventListener(new Aufgabe4ab());

		Frame frame = new Frame("JOGL2Basecode");
		frame.add(canvas);
		frame.setSize(640, 480);

		final Animator animator = new Animator(canvas);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// Run this on another thread than the AWT event queue to
				// make sure the call to Animator.stop() completes before
				// exiting
				new Thread(new Runnable() {
					public void run() {
						animator.stop();
						System.exit(0);
					}
				}).start();
			}
		});

		frame.setVisible(true);
		animator.start();
	}

	public void init(GLAutoDrawable drawable) {
		System.err.println("GLBasecode Init: " + drawable);
		// Use debug pipeline
		// drawable.setGL(new DebugGL(drawable.getGL()));

		GL2 gl = drawable.getGL().getGL2();

		System.err.println("Chosen GLCapabilities: "
				+ drawable.getChosenGLCapabilities());
		System.err.println("INIT GL IS: " + gl.getClass().getName());
		System.err.println("GL_VENDOR: " + gl.glGetString(GL2.GL_VENDOR));
		System.err.println("GL_RENDERER: " + gl.glGetString(GL2.GL_RENDERER));
		System.err.println("GL_VERSION: " + gl.glGetString(GL2.GL_VERSION));

		gl.setSwapInterval(1);

		gl.glClearColor(0.3f, 0.3f, 0.3f, 1.0f);
		gl.glDisable(GL2.GL_CULL_FACE);
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glClearDepth(1.0f);
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glDepthFunc(GL2.GL_LEQUAL);
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		GL2 gl = drawable.getGL().getGL2();
		GLU glu = new GLU();

		if (height == 0) {
			height = 1;
		}
		
		float aspect = (float) width / (float) height;

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		
		glu.gluPerspective(45.0f, aspect, 0.1f, 100.0f);

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	public void drawCube(GL2 gl, float x, float y, float z) {
		gl.glTranslatef(x, y, z);
		gl.glScalef(0.2f,0.2f,0.2f);
		gl.glBegin(GL2.GL_QUADS);
			// front
			gl.glColor3f(1.0f, 0.0f, 0.0f);
			gl.glVertex3f(-1.0f, -1.0f, 1.0f);
			gl.glVertex3f( 1.0f, -1.0f, 1.0f);
			gl.glVertex3f( 1.0f,  1.0f, 1.0f);
			gl.glVertex3f(-1.0f,  1.0f, 1.0f);
			
			// right
			gl.glColor3f(0.0f, 1.0f, 0.0f);
			gl.glVertex3f(1.0f, -1.0f,  1.0f);
			gl.glVertex3f(1.0f, -1.0f, -1.0f);
			gl.glVertex3f(1.0f,  1.0f, -1.0f);
			gl.glVertex3f(1.0f,  1.0f,  1.0f);
			
			// back
			gl.glColor3f(0.0f, 0.0f, 1.0f);
			gl.glVertex3f( 1.0f, -1.0f, -1.0f);
			gl.glVertex3f(-1.0f, -1.0f, -1.0f);
			gl.glVertex3f(-1.0f,  1.0f, -1.0f);
			gl.glVertex3f( 1.0f,  1.0f, -1.0f);
			
			// left
			gl.glColor3f(1.0f, 0.0f, 1.0f);
			gl.glVertex3f(-1.0f, -1.0f, -1.0f);
			gl.glVertex3f(-1.0f, -1.0f,  1.0f);
			gl.glVertex3f(-1.0f,  1.0f,  1.0f);
			gl.glVertex3f(-1.0f,  1.0f, -1.0f);
			
			//top
			gl.glColor3f(0.0f, 1.0f, 1.0f);
			gl.glVertex3f(-1.0f, 1.0f, 1.0f);
			gl.glVertex3f( 1.0f, 1.0f, 1.0f);
			gl.glVertex3f( 1.0f, 1.0f,-1.0f);
			gl.glVertex3f(-1.0f, 1.0f,-1.0f);
		
			// bottom
			
			gl.glVertex3f(-1.0f,-1.0f,-1.0f);
			gl.glVertex3f( 1.0f,-1.0f,-1.0f);
			gl.glVertex3f( 1.0f,-1.0f, 1.0f);
			gl.glVertex3f(-1.0f,-1.0f, 1.0f);
		gl.glEnd();
	}
	
	float rot = 0;
	
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		gl.glTranslatef(0.0f, 0.0f, -10.0f);
		gl.glRotatef(rot, 1.0f, 0.0f, 0.0f);
		gl.glRotatef(rot, 0.0f, 1.0f, 0.0f);
		gl.glRotatef(rot, 0.0f, 0.0f, 1.0f);

		gl.glColor3f(0.65f, 0.85f, 0.65f);

		gl.glBegin(GL2.GL_LINE_LOOP);
		
		for(int i=0; i < 100; i++) {
			float phi = (float)(2*Math.PI / 100 * i);
			
			float x = (float)((Math.cos(3*phi) +2) * Math.cos(2*phi));
			float y = (float)((Math.cos(3*phi) +2) * Math.sin(2*phi));
			float z = (float)(Math.sin(3*phi));
			
			gl.glColor3f(x,y,z);
			gl.glVertex3f(x, y, z);
		}
		
		gl.glEnd();
		
		float phi = rot*0.037f;
		
		float x = (float)((Math.cos(3*phi) +2) * Math.cos(2*phi));
		float y = (float)((Math.cos(3*phi) +2) * Math.sin(2*phi));
		float z = (float)(Math.sin(3*phi));
		
		drawCube(gl, x, y, z);
		
		rot+= 0.2f;
	}

	public void dispose(GLAutoDrawable drawable) {
	}
}