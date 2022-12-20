package atropos.demos.shader;

import java.io.File;

import com.jogamp.opengl.DebugGL2;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.glu.GLU;

import com.jogamp.opengl.util.gl2.GLUT;

import atropos.AtroposDefaultRenderer;
import atropos.core.Color;
import atropos.core.Material;
import atropos.core.fbo.Framebuffer;
import atropos.core.fbo.Renderbuffer;
import atropos.core.light.PointLight;
import atropos.core.shader.FragmentShader;
import atropos.core.shader.ShaderProgram;
import atropos.core.shader.ShaderSource;
import atropos.core.shader.VertexShader;
import atropos.core.shader.attrib.VertexAttrib4f;
import atropos.core.shader.uniform.Uniform;
import atropos.core.shader.uniform.Uniform1f;
import atropos.core.shader.uniform.Uniform4f;
import atropos.core.shader.uniform.Uniform4fv;
import atropos.core.texture.Texture2D;

public class FrameBufferObjectDemoRenderer extends AtroposDefaultRenderer {

	VertexAttrib4f colorVertexAttrib4f;
	Framebuffer fbo;
	Texture2D texture2D;
	
	ShaderProgram prog, prog2;
	Uniform1f time;
	
	@Override
	public void init(GLAutoDrawable drawable) {
		drawable.setGL(new DebugGL2(drawable.getGL().getGL2()));
		GL2 gl = drawable.getGL().getGL2();
		System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
		System.err.println(gl.glGetString(GL2.GL_EXTENSIONS));
		gl.setSwapInterval(1);
		
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glClearColor(0.2f, 0.2f, 0.3f, 1.0f);

		VertexShader vs = new VertexShader(gl);
		vs.setShaderSource(gl, new ShaderSource(new File("shaders/phongPointLightShader.vs")));
		vs.compileShader(gl);
		System.err.println(vs.getInfoLog(gl));

		FragmentShader fs = new FragmentShader(gl);
		fs.setShaderSource(gl, new ShaderSource(new File("shaders/phongPointLightShader.fs")));
		fs.compileShader(gl);
		System.err.println(fs.getInfoLog(gl));
		
		prog = new ShaderProgram(gl);
		prog.attachShader(gl, vs);
		prog.attachShader(gl, fs);
		prog.linkProgram(gl);
		System.err.println(prog.getInfoLog(gl));
		prog.activate(gl);
		
		VertexShader vs2 = new VertexShader(gl);
		vs2.setShaderSource(gl, new ShaderSource(new File("shaders/grayPostProcessingShader.vs")));
		vs2.compileShader(gl);
		
		FragmentShader fs2 = new FragmentShader(gl);
		fs2.setShaderSource(gl, new ShaderSource(new File("shaders/grayPostProcessingShader.fs")));
		fs2.compileShader(gl);
		
		prog2 = new ShaderProgram(gl);
		prog2.attachShader(gl, vs2);
		prog2.attachShader(gl, fs2);
		prog2.linkProgram(gl);
		prog2.activate(gl);
		
		time = prog2.getUniform1f(gl, "time");
		
		
		Renderbuffer depthRenderbuffer = new Renderbuffer(gl);
		depthRenderbuffer.setStorage(gl, GL2.GL_DEPTH_COMPONENT, 640, 480);
		
		if(Texture2D.isNPOTSupported(gl))
			System.err.println("non power of two supported!");
		
		texture2D = new Texture2D(gl,GL.GL_TEXTURE_2D);
		texture2D.setStorage(gl,  GL2.GL_RGB8, GL.GL_RGBA, 640, 480);
		texture2D.setMagFilter(gl, GL.GL_LINEAR);
		texture2D.setMinFilter(gl, GL.GL_LINEAR);
		//texture2D.generateMipmap(gl);
		// repeat its not supported on radeon 9600 for NPOTS!!
		texture2D.setWrapS(gl, GL2.GL_CLAMP);
		texture2D.setWrapT(gl, GL2.GL_CLAMP);
		
		 if(Framebuffer.isSupported(gl))
			 System.err.println("FBO support available.");
		 
		 System.err.println("max color attachments: " + Framebuffer.getMaxColorAttachments(gl));
		 System.err.println("max color draw buffers: " + Framebuffer.getMaxDrawBuffers(gl));
		 
		fbo = new Framebuffer(gl);
		fbo.attach(gl, GL2.GL_COLOR_ATTACHMENT0, texture2D);
		fbo.attach(gl, GL2.GL_DEPTH_ATTACHMENT, depthRenderbuffer);
		
		System.out.println(fbo.isComplete(gl));
		
		PointLight light = new PointLight(GL2.GL_LIGHT0);
		light.setPosition(0,1,3);
		light.setSpecular(1,1,1,1);
		light.setDiffuse(1.0f, 1.0f,1.0f,1.0f);
		light.setAmbient(0.1f,0.1f,0.1f,1);
		light.apply(gl);
		
		Material mat = new Material();
		mat.setAmbient(Color.ORANGE);
		mat.setDiffuse(Color.ORANGE);
		mat.setSpecular(Color.ORANGE);
		mat.setShininess(23);
		mat.apply(gl);
		//gl.glEnable(GL.GL_CULL_FACE);
		//gl.glFrontFace(GL.GL_CW);
		//newTexture2D = new Texture2D(gl);
	}
	
	float rotation = 0;
	
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		GLUT glut = new GLUT();
		GLU glu = new GLU();
		gl.glPushAttrib(GL2.GL_VIEWPORT_BIT);
		prog.activate(gl);
		fbo.bind(gl);
				gl.glClearColor(.2f, .2f,.3f,1.0f);
		gl.glViewport(0,0 , 640, 480);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		
		//gl.glClear(GL.GL_COLOR_BUFFER_BIT );
		gl.glLoadIdentity();
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glEnable(GL2.GL_LIGHT0);
		gl.glTranslatef(0.0f, 0.0f, -3.0f);
		gl.glRotatef(rotation, 0.0f, 0.0f, 1.0f);
		gl.glRotatef(rotation, 0.0f, 1.0f, 0.0f);
		gl.glRotatef(rotation, 1.0f, 0.0f, 0.0f);
		
		glut.glutSolidTeapot(1.0);

		
		fbo.unbind(gl);
		prog.deactivate(gl);
		gl.glPopAttrib();
		gl.glDisable(GL2.GL_LIGHTING);
		//gl.glEnable(GL.GL_LIGHT0);
	
		gl.glClearColor(1, 1,1,1.0f);
	gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		
		gl.glLoadIdentity();
		
		
		
		prog.deactivate(gl);
		
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		gl.glEnable(GL2.GL_TEXTURE_2D);
		texture2D.bind(gl);
		//texture2D.generateMipmap(gl);
		gl.glDisable(GL2.GL_LIGHTING);
		
		
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(-1, 1, -1, 1, 1.0, -1.0);
		gl.glMatrixMode(GL2.GL_MODELVIEW);

		
		prog2.activate(gl);
		time.set(gl, rotation);
		gl.glBegin(GL2.GL_QUADS);
		//tangent.set(gl, 1.0f, 0.0f, 0.0f);
		gl.glNormal3f( 0.0f, 0.0f, 1.0f);					// Normal Pointing Towards Viewer
		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f,  -0.5f);	// Point 1 (Front)
		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f,  -0.5f);	// Point 2 (Front)
		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f,  -0.5f);	// Point 3 (Front)
		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f,  -0.5f);	// Point 4 (Front)
		
		gl.glEnd();
		prog2.deactivate(gl);
	//prog.activate(gl);
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		
		glu.gluPerspective(45.0f, 640/480.f, 1.0, 100.0);
		
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		
		gl.glDisable(GL.GL_TEXTURE_2D);
		rotation += 0.6f;
	}

}