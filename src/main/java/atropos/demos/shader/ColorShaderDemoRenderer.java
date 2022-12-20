package atropos.demos.shader;

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

public class ColorShaderDemoRenderer extends AtroposDefaultRenderer {

	VertexAttrib4f colorVertexAttrib4f;
	
	@Override
	public void init(GLAutoDrawable drawable) {
		drawable.setGL(new DebugGL2(drawable.getGL().getGL2()));
		GL2 gl = drawable.getGL().getGL2();
		
		gl.setSwapInterval(1);
		
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glClearColor(0.2f, 0.2f, 0.3f, 1.0f);

		VertexShader vs = new VertexShader(gl);
		vs.setShaderSource(gl, new ShaderSource(new File("shaders/colorShader.vs")));
		vs.compileShader(gl);
		System.err.println(vs.getInfoLog(gl));

		FragmentShader fs = new FragmentShader(gl);
		fs.setShaderSource(gl, new ShaderSource(new File("shaders/colorShader.fs")));
		fs.compileShader(gl);
		System.err.println(fs.getInfoLog(gl));
		
		ShaderProgram prog = new ShaderProgram(gl);
		prog.attachShader(gl, vs);
		prog.attachShader(gl, fs);
		prog.linkProgram(gl);
		System.err.println(prog.getInfoLog(gl));
		prog.activate(gl);
		
		//colorVertexAttrib4f = prog.getVertexAttrib4f(gl, "color");
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		
		gl.glTranslatef(-1.5f, 0.0f, -6.0f);

		gl.glBegin(GL2.GL_TRIANGLES);		    
			gl.glColor4f( 1.0f, 0.0f, 0.0f, 1.0f);
			gl.glVertex3f(0.0f, 1.0f, 0.0f);
			gl.glColor4f( 0.0f, 1.0f, 0.0f, 1.0f);
			gl.glVertex3f(-1.0f, -1.0f, 0.0f);
			gl.glColor4f( 0.0f, 0.0f, 1.0f, 1.0f);
			gl.glVertex3f(1.0f, -1.0f, 0.0f);
		gl.glEnd();
		
		gl.glTranslatef(3.0f, 0.0f, 0.0f);
		
		gl.glBegin(GL2.GL_QUADS);
			gl.glColor4f( 1.0f, 0.6f, 0.0f, 1.0f);
			gl.glVertex3f(-1.0f, 1.0f, 0.0f);
			gl.glVertex3f(1.0f, 1.0f, 0.0f);
			gl.glVertex3f(1.0f, -1.0f, 0.0f);
			gl.glVertex3f(-1.0f, -1.0f, 0.0f);
		gl.glEnd();
	}

}