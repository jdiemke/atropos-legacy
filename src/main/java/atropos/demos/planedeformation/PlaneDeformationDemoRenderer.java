package atropos.demos.planedeformation;

import java.io.File;

import com.jogamp.opengl.DebugGL2;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import atropos.AtroposDefaultRenderer;
import atropos.core.shader.FragmentShader;
import atropos.core.shader.ShaderProgram;
import atropos.core.shader.ShaderSource;
import atropos.core.shader.VertexShader;
import atropos.core.shader.attrib.VertexAttrib4f;
import atropos.core.shader.uniform.Uniform1f;
import atropos.core.shader.uniform.Uniform1i;

public class PlaneDeformationDemoRenderer extends AtroposDefaultRenderer {

	VertexAttrib4f colorVertexAttrib4f;
	Uniform1f time;
	Uniform1i mode;
	@Override
	public void init(GLAutoDrawable drawable) {
		drawable.setGL(new DebugGL2(drawable.getGL().getGL2()));
		GL2 gl = drawable.getGL().getGL2();
		
		gl.setSwapInterval(1);
		
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glClearColor(0.2f, 0.2f, 0.3f, 1.0f);

		VertexShader vs = new VertexShader(gl);
		vs.setShaderSource(gl, new ShaderSource(new File("./shaders/planeDeformationShader.vs")));
		vs.compileShader(gl);
		System.err.println(vs.getInfoLog(gl));

		FragmentShader fs = new FragmentShader(gl);
		fs.setShaderSource(gl, new ShaderSource(new File("./shaders/planeDeformationShader.fs")));
		fs.compileShader(gl);
		System.err.println(fs.getInfoLog(gl));
		
		ShaderProgram prog = new ShaderProgram(gl);
		prog.attachShader(gl, vs);
		prog.attachShader(gl, fs);
		prog.linkProgram(gl);
		System.err.println(prog.getInfoLog(gl));
		prog.activate(gl);
		
		Texture texture = load(gl,"./textures/bow.tga");
		Uniform1i uni = prog.getUniform1i(gl, "texCol");
		 time = prog.getUniform1f(gl,"time");
		 mode = prog.getUniform1i(gl, "mode");
		uni.set(gl, 0);
		
		
		//colorVertexAttrib4f = prog.getVertexAttrib4f(gl, "color");
	}
	
	 Texture load (GL gl,String filename)
	   {
		 System.out.println(filename);
	      Texture texture = null;

	      try
	      {
	          // Create an OpenGL texture from the specified file. Do not create
	          // mipmaps.

	          texture = TextureIO.newTexture (new File (filename), false);

	          // Use the NEAREST magnification function when the pixel being
	          // textured maps to an area less than or equal to one texture
	          // element (texel).

	          texture.setTexParameteri (gl,GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);

	          // Use the NEAREST minification function when the pixel being
	          // textured maps to an area greater than one texel.

	          texture.setTexParameteri (gl,GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
	          texture.setTexParameterf(gl,GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
	          texture.setTexParameterf(gl,GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
	          texture.setTexParameteri(gl,GL2.GL_GENERATE_MIPMAP, GL.GL_TRUE);
	          
	      }
	      catch (Exception e)
	      {
	          System.out.println ("error loading texture from "+filename);
	      }

	      return texture;
	   }
	long start =  System.currentTimeMillis();
	 
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		
		gl.glTranslatef(0.0f, 0.0f, -1.35f);
		
		float elapsed = (System.currentTimeMillis() -start) * 0.0001f;
		time.set(gl, elapsed);
		
		int myMode = ((int)(elapsed*1.5f))%14;
		mode.set(gl, myMode);
	
	
		gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2f(0,1);
			gl.glVertex3f(-1.0f, 1.0f, 0.0f);
			gl.glTexCoord2f(1,1);
			gl.glVertex3f(1.0f, 1.0f, 0.0f);
			gl.glTexCoord2f(1,0);
			gl.glVertex3f(1.0f, -1.0f, 0.0f);
			gl.glTexCoord2f(0,0);
			gl.glVertex3f(-1.0f, -1.0f, 0.0f);
		gl.glEnd();
		
	}

}