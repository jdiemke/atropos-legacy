package tutorium.uebungsblatt01;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;

import atropos.core.Color;
import atropos.core.Material;
import atropos.core.light.Light;
import atropos.core.light.PointLight;
import atropos.core.math.Quaternion;
import atropos.core.math.Vector3f;
import atropos.core.model.md2.MD2Model;
import atropos.core.model.milkshape.MilkshapeModel;

import com.jogamp.opengl.util.Animator;

public class Aufgabe2ab implements GLEventListener {

	MD2Model model, weapon;
	Material material;
	MilkshapeModel model2;
	PointLight light;
	
	public static void main(String[] args) {
		GLProfile.initSingleton();

		GLProfile glp = GLProfile.get(GLProfile.GL2);
		GLCapabilities caps = new GLCapabilities(glp);
		
		caps.setNumSamples(4);
	    caps.setAlphaBits(16);
	    caps.setSampleBuffers(true);
		caps.setDoubleBuffered(true);
		caps.setHardwareAccelerated(true);
		
		GLCanvas canvas = new GLCanvas(caps);
		canvas.addGLEventListener(new Aufgabe2ab());

		Frame frame = new Frame("JOGL2Basecode");
		canvas.setSize(640, 360);
		frame.add(canvas);
		

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

		frame.pack();
		frame.setVisible(true);
		animator.start();
	}

	public void init(GLAutoDrawable drawable) {
		System.err.println("GLBasecode Init: " + drawable);
		// Use debug pipeline
		drawable.setGL(new DebugGL2((GL2) drawable.getGL()));

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
		
		//model = new MD2Model(gl, "models/tris.md2","models/hueteotl.tga");
		//weapon = new MD2Model(gl, "models/weapon.md2","models/weapon.png");
		
//		model = new MD2Model(gl, "models/md2/tris.md2","models/md2/dragon_green.png");
//		weapon = new MD2Model(gl, "models/md2/weapon.md2","models/md2/knight.png");
		model = new MD2Model(gl, "./models/md2/tris.MD2","./models/md2/arboshak.png");
		//weapon = new MD2Model(gl, "models/md2/weapon.md2","models/md2/knight.png");
		long start = System.currentTimeMillis();
		model.setStartTime(start);
	//	weapon.setStartTime(start);
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glEnable(GL2.GL_LIGHT0);
		
		material = new Material();
		material.setAmbient(0.0f, 0.0f, 0.0f, 1.0f);
		material.setDiffuse(0.0f, 0.0f, 0.0f, 0.5f);
		material.setSpecular(0.0f,0.0f,0.0f,1.0f);
		material.setShininess(112.0f);
		
		
light = new PointLight(GL2.GL_LIGHT0);
light.setAmbient(0.2f,0.2f,0.2f,1);
light.setDiffuse(1,1,1,1);
light.setSpecular(1,1,1,1);
light.setPosition(0,0,0);
		
		gl.glLightModeli(GL2.GL_LIGHT_MODEL_COLOR_CONTROL,GL2.GL_SEPARATE_SPECULAR_COLOR);
		
		// model2  = new MilkshapeModel(gl, "models/ms3d/dwarf2.ms3d");
		 model2  = new MilkshapeModel(gl, "./models/ms3d/freebeast/beast.ms3d");
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

	long start = System.currentTimeMillis();
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		light.apply(gl);
		gl.glTranslatef(0.0f, -15.0f, -70.0f);
		long elap = System.currentTimeMillis() - start;
		gl.glRotatef(-90+elap*0.02f, 0,1,0);
		//Quaternion quat = new Quaternion(new Vector3f(0.0f, 1.0f, 0.0f), -90);
		//Quaternion quat2 = new Quaternion(new Vector3f(0.0f, 0.0f, 1.0f), -90);
		//quat.multiply(quat2).toMatrix().apply(gl);
		//Vector3f axis = quat2.multiply(quat).getAxisOfRotation();
		//gl.glRotatef(quat.getAngleOfRotation(), axis.x, axis.y, axis.z);
	//	gl.glRotatef(-90, 1,0,0);
		
		gl.glScalef(0.36f,0.36f,0.36f);
		
		material.apply(gl);
		model.draw(gl);
		gl.glEnable(GL2.GL_LIGHTING);
		model2.draw2(gl);
		gl.glClear( GL2.GL_DEPTH_BUFFER_BIT);
		gl.glDisable(GL2.GL_LIGHTING);
		gl.glDisable(GL2.GL_TEXTURE_2D);
		gl.glColor3f(1,0,0);
		gl.glLineWidth(1.0f);
		//model2.drawJoints2(gl);
		//weapon.draw(gl);

	}

	public void dispose(GLAutoDrawable drawable) {
	}
}