package atropos.demos.spheretracing;

import java.io.File;

import com.jogamp.opengl.DebugGL2;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import atropos.AtroposDefaultRenderer;
import atropos.core.shader.FragmentShader;
import atropos.core.shader.ShaderProgram;
import atropos.core.shader.ShaderSource;
import atropos.core.shader.VertexShader;
import atropos.core.shader.attrib.VertexAttrib4f;
import atropos.core.shader.uniform.Uniform1f;

public class RevisionDemoRenderer extends AtroposDefaultRenderer {

	Uniform1f time;
	long startTime;
	
	@Override
	public void init(GLAutoDrawable drawable) {
		drawable.setGL(new DebugGL2(drawable.getGL().getGL2()));
		GL2 gl = drawable.getGL().getGL2();
		
		gl.setSwapInterval(0);
		
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glClearColor(0.2f, 0.2f, 0.3f, 1.0f);

		VertexShader vs = new VertexShader(gl);
		vs.setShaderSource(gl, new ShaderSource(new File("shaders/spheretracing/revisionDemo.vs")));
		vs.compileShader(gl);
		System.err.println(vs.getInfoLog(gl));

		FragmentShader fs = new FragmentShader(gl);
		fs.setShaderSource(gl, new ShaderSource(new File("shaders/spheretracing/revisionDemo.fs")));
		fs.compileShader(gl);
		System.err.println(fs.getInfoLog(gl));
		
		ShaderProgram prog = new ShaderProgram(gl);
		prog.attachShader(gl, vs);
		prog.attachShader(gl, fs);
		prog.linkProgram(gl);
		System.err.println(prog.getInfoLog(gl));
		prog.activate(gl);
		
		time =prog.getUniform1f(gl, "time");
		startTime = System.currentTimeMillis();
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		
		long elapsedTime = (System.currentTimeMillis() - startTime);
		time.set(gl, elapsedTime/1000.0f*0.2f);
		
	    gl.glBegin (GL2.GL_QUADS); 
	    gl.glTexCoord2f(0,0);
	    gl.glVertex3i (-1, -1, -1);
	    gl.glTexCoord2f(1,0);
	    gl.glVertex3i (1, -1, -1); 
	    gl.glTexCoord2f(1,1);
	    gl.glVertex3i (1, 1, -1);
	    gl.glTexCoord2f(0,1);
	    gl.glVertex3i (-1, 1, -1); 
	    gl.glEnd ();
	}

}