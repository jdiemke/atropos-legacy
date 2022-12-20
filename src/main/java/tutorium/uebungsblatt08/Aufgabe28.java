package tutorium.uebungsblatt08;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;

import atropos.core.math.Vector3f;
import atropos.core.model.wavefront.WavefrontModel;
import atropos.core.shader.FragmentShader;
import atropos.core.shader.ShaderProgram;
import atropos.core.shader.ShaderSource;
import atropos.core.shader.VertexShader;

import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.gl2.GLUT;

public class Aufgabe28 implements GLEventListener {

	OBJLoader loader;
	
	ShaderProgram prog;
	WavefrontModel model;
	
	public static void main(String[] args) {
		GLProfile.initSingleton();

		GLProfile glp = GLProfile.get(GLProfile.GL2);
		GLCapabilities caps = new GLCapabilities(glp);
		caps.setSampleBuffers(true);
		caps.setNumSamples(4);
		GLCanvas canvas = new GLCanvas(caps);
		canvas.addGLEventListener(new Aufgabe28());

		Frame frame = new Frame("JOGL2Basecode");
		frame.add(canvas);
		canvas.setSize(640, 360);
		frame.pack();	

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
		 drawable.setGL(new DebugGL2(drawable.getGL().getGL2()));

		GL2 gl = drawable.getGL().getGL2();

		System.err.println("Chosen GLCapabilities: "
				+ drawable.getChosenGLCapabilities());
		System.err.println("INIT GL IS: " + gl.getClass().getName());
		System.err.println("GL_VENDOR: " + gl.glGetString(GL2.GL_VENDOR));
		System.err.println("GL_RENDERER: " + gl.glGetString(GL2.GL_RENDERER));
		System.err.println("GL_VERSION: " + gl.glGetString(GL2.GL_VERSION));

		gl.setSwapInterval(1);

		gl.glClearColor(0.3f, 0.3f, 0.3f, 1.0f);
		//gl.glEnable(GL2.GL_CULL_FACE);
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glClearDepth(1.0f);
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glDepthFunc(GL2.GL_LEQUAL);
		gl.glEnable(GL2.GL_CULL_FACE);
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
		
		loader = new OBJLoader("models" + File.separator + "bunny.obj");
	
		VertexShader vs = new VertexShader(gl);
		vs.setShaderSource(gl, new ShaderSource(new File("./shaders/toonShader.vs")));
		vs.compileShader(gl);
		System.err.println(vs.getInfoLog(gl));

		FragmentShader fs = new FragmentShader(gl);
		fs.setShaderSource(gl, new ShaderSource(new File("./shaders/toonShader.fs")));
		fs.compileShader(gl);
		System.err.println(fs.getInfoLog(gl));
		
		 prog = new ShaderProgram(gl);
		prog.attachShader(gl, vs);
		prog.attachShader(gl, fs);
		prog.linkProgram(gl);
		System.err.println(prog.getInfoLog(gl));
		
	 model = new WavefrontModel(gl,"models/bunny.obj");
	 model.construct(gl);
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

		glu.gluPerspective(45.0f, aspect, 1.0f, 100.0f);

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	float rotation = 0;
	long timer = 0;

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		GLUT glut = new GLUT();

		gl.glClear(GL.GL_COLOR_BUFFER_BIT
				| GL.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		
		float[] lightPosition_	= {-0.8f, 0.8f, 1.0f, 0.0f};
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPosition_, 0);
		
		gl.glTranslatef(0.0f, 0.0f, -3.5f);
		gl.glRotatef(rotation, 0.0f, 0.0f, 1.0f);
		gl.glRotatef(rotation, 0.0f, 1.0f, 0.0f);
		gl.glRotatef(rotation, 1.0f, 0.0f, 0.0f);
		
		
//		gl.glEnable(GL2.GL_LINE_SMOOTH);
//		gl.glEnable(GL2.GL_BLEND);
//		gl.glEnable (GL2.GL_BLEND);
//		gl.glBlendFunc (GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		////
		
		// setup
		gl.glDisable(GL2.GL_CULL_FACE);
		
		// first renderpass
		gl.glLineWidth(3.0f);
		gl.glColor3f(0,0,0);
		
		gl.glFrontFace(GL2.GL_CCW);
		gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_LINE);
		
	//	glut.glutSolidTeapot(1.0);
		
		// second renderpass
		
		gl.glFrontFace(GL2.GL_CW);
		gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
		
		prog.activate(gl);
		gl.glScalef(13,13,13);
		//gl.glScalef(0.3f,0.3f,0.3f);
		model.draw(gl);
		//glut.glutSolidTeapot(1.0);
		//loader.draw(gl);
		
		
		prog.deactivate(gl);
		
		
		
		rotation += 0.1f;
		timer += 9;
	}

	public void dispose(GLAutoDrawable drawable) {
	}
}

class TriangleIndex {
	int a, b, c;
	public TriangleIndex(int a, int b, int c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}
}

class OBJLoader {
	
	ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();
	ArrayList<TriangleIndex> triangles = new ArrayList<TriangleIndex>();
	public OBJLoader(String filename) {
		
		try {
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(
							new File(filename))));
			
			while(bufferedReader.ready()) {
				String line = bufferedReader.readLine();
				
				if(line == null) break;
				
				if(line.startsWith("vt"))
					continue; // handle later
				
				if(line.startsWith("v"))
					parseVertex(line);
				
				if(line.startsWith("f")) {
					parseFace(line);
				}
				
				//System.out.println(line);
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void draw(GL2 gl) {
		gl.glBegin(GL2.GL_TRIANGLES);
			for(TriangleIndex tri : triangles) {
				Vector3f a = vertices.get(tri.a);
				Vector3f b = vertices.get(tri.b);
				Vector3f c = vertices.get(tri.c);				
				
				
				gl.glVertex3f(a.x, a.y, a.z);
				
				gl.glVertex3f(b.x, b.y, b.z);
				
				gl.glVertex3f(c.x, c.y, c.z);
			}
		gl.glEnd();
		
	}
	
	public void parseFace(String line) {
		StringTokenizer vertexData = new StringTokenizer(line, " ");
		
		vertexData.nextToken();
		
		if(vertexData.countTokens() != 3) {
			System.err.println("malformed line: " + line);
			return;
		}
		
		StringTokenizer point1 = new StringTokenizer(vertexData.nextToken(),"/");
		StringTokenizer point2 = new StringTokenizer(vertexData.nextToken(),"/");
		StringTokenizer point3 = new StringTokenizer(vertexData.nextToken(),"/");
		
		Integer p1 = new Integer(Integer.parseInt(point1.nextToken()));
		Integer p2 = new Integer(Integer.parseInt(point2.nextToken()));
		Integer p3 = new Integer(Integer.parseInt(point3.nextToken()));
		
		triangles.add(new TriangleIndex(p1-1, p2-1, p3-1));
		//System.out.println(line);
		//System.out.println(p1 + " " +p2 +" " + p3);
	}
	
	public void parseVertex(String line) {
		StringTokenizer vertexData = new StringTokenizer(line, " ");
		
		vertexData.nextToken();
		
		if(vertexData.countTokens() != 3) {
			System.err.println("malformed line: " + line);
			return;
		}
		
		float x = Float.parseFloat(vertexData.nextToken());
		float y = Float.parseFloat(vertexData.nextToken());
		float z = Float.parseFloat(vertexData.nextToken());
		
		vertices.add(new Vector3f(x, y, z));
	}
}