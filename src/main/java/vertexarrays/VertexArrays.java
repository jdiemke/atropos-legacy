package vertexarrays;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.Animator;

public class VertexArrays implements GLEventListener {

	FloatBuffer vertexBuffer;
	FloatBuffer colorBuffer;
	IntBuffer indexBuffer;
	
	public static void main(String[] args) {
		GLProfile.initSingleton();

		GLProfile glp = GLProfile.get(GLProfile.GL2);
		GLCapabilities caps = new GLCapabilities(glp);
		GLCanvas canvas = new GLCanvas(caps);
		canvas.addGLEventListener(new VertexArrays());

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
		
		
//		gl.glColor3f(1.0f, 0.0f, 0.0f);
//		gl.glVertex3f(-1.0f, -1.0f, 0.0f);
//		
//		gl.glColor3f(0.0f, 1.0f, 0.0f);
//		gl.glVertex3f( 1.0f, -1.0f, 0.0f);
//		
//		gl.glColor3f(0.0f, 0.0f, 1.0f);
//		gl.glVertex3f( 0.0f,  1.0f, 0.0f);
		
		// vertex arrays stuff
		vertexBuffer = Buffers.newDirectFloatBuffer(3*3);
		colorBuffer = Buffers.newDirectFloatBuffer(3*3);
		indexBuffer = Buffers.newDirectIntBuffer(3);
		
		addColor(1.0f, 0.0f, 0.0f);
		addVertex(-1.0f, -1.0f, 0.0f);
		addIndex(0);
		
		addColor(0.0f, 1.0f, 0.0f);
		addVertex( 1.0f, -1.0f, 0.0f);
		addIndex(1);
		
		addColor(0.0f, 0.0f, 1.0f);
		addVertex( 0.0f,  1.0f, 0.0f);
		addIndex(2);
		
		indexBuffer.rewind();
		vertexBuffer.rewind();
		colorBuffer.rewind();
	}
	
	void addVertex(float a, float b, float c) {
		vertexBuffer.put(a);
		vertexBuffer.put(b);
		vertexBuffer.put(c);
	}
	
	void addColor(float a, float b, float c) {
		colorBuffer.put(a);
		colorBuffer.put(b);
		colorBuffer.put(c);
	}
	
	void addIndex(int a) {
		indexBuffer.put(a);
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

	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		gl.glTranslatef(0.0f, 0.0f, -5.0f);


		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
		
		gl.glVertexPointer(3, GL2.GL_FLOAT,0, vertexBuffer);
		gl.glColorPointer(3, GL2.GL_FLOAT,0, colorBuffer);
		
		//gl.glDrawElements(GL2.GL_TRIANGLES, 3, GL2.GL_UNSIGNED_INT, indexBuffer);
		gl.glDrawArrays(GL2.GL_TRIANGLES,0, 3);
		
		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL2.GL_COLOR_ARRAY);
		
		
	}

	public void dispose(GLAutoDrawable drawable) {
	}
}