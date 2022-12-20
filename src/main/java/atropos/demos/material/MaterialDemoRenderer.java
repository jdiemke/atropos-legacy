package atropos.demos.material;

import java.io.File;


import com.jogamp.opengl.DebugGL2;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import com.jogamp.opengl.util.gl2.GLUT;



import atropos.AtroposDefaultRenderer;
import atropos.core.Color;
import atropos.core.Material;
import atropos.core.camera.BasicCamera;
import atropos.core.light.PointLight;
import atropos.core.math.Vector3f;
import atropos.core.shader.FragmentShader;
import atropos.core.shader.ShaderProgram;
import atropos.core.shader.ShaderSource;
import atropos.core.shader.VertexShader;

public class MaterialDemoRenderer extends AtroposDefaultRenderer {
	
	Material material;
	PointLight light;
	BasicCamera camera;
	
	@Override
	public void init(GLAutoDrawable drawable) {
		drawable.setGL(new DebugGL2(drawable.getGL().getGL2()));
		GL2 gl = drawable.getGL().getGL2();
		
		gl.setSwapInterval(1);
		
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glClearColor(0.2f, 0.2f, 0.3f, 1.0f);

		VertexShader vs = new VertexShader(gl);
		vs.setShaderSource(gl, new ShaderSource(new File("shaders/toonShader.vs")));
		vs.compileShader(gl);
		System.err.println(vs.getInfoLog(gl));

		FragmentShader fs = new FragmentShader(gl);
		fs.setShaderSource(gl, new ShaderSource(new File("shaders/toonShader.fs")));
		fs.compileShader(gl);
		System.err.println(fs.getInfoLog(gl));
		
		ShaderProgram prog = new ShaderProgram(gl);
		prog.attachShader(gl, vs);
		prog.attachShader(gl, fs);
		prog.linkProgram(gl);
		System.err.println(prog.getInfoLog(gl));
		//prog.activate(gl);
		
		float[] lightPosition_	= {0.3f, 0.5f, 1.0f, 0.0f};
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPosition_, 0);
		gl.glEnable(GL2.GL_LIGHT0);
		gl.glEnable(GL2.GL_LIGHTING);
		
		material = new Material();
		material.setAmbient(0.2f, 0.2f, 0.2f, 1.0f);
		material.setDiffuse(0.5f, 0.56f, 0.5f, 1.0f);
		material.setSpecular(Color.ORANGE);
		material.setShininess(12.0f);
		
		
		
		light = new PointLight(GL2.GL_LIGHT0);
		light.setSpecular(1.0f, 1.0f, 1.0f, 1.0f);
		
		camera = new BasicCamera(new Vector3f(0,0,0), -25.0f , 10.0f, 0.0f);
	}
	
	float rotation = 0;
	long timer = 0;

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		GLUT glut = new GLUT();

		gl.glClear(GL.GL_COLOR_BUFFER_BIT
				| GL.GL_DEPTH_BUFFER_BIT);
		
		camera.applyCamera(gl);
		
		
		light.apply(gl);
		
		gl.glTranslatef(0.0f, 0.0f, -5.0f);
		gl.glRotatef(rotation, 0.0f, 0.0f, 1.0f);
		gl.glRotatef(rotation, 0.0f, 1.0f, 0.0f);
		

		material.apply(gl);
		glut.glutSolidTeapot(1.0);

		rotation += 0.2f;
		timer += 9;
	}

}