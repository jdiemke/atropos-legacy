package atropos.demos.shader;

import java.io.File;


import com.jogamp.opengl.DebugGL2;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;



import atropos.AtroposDefaultRenderer;
import atropos.core.Color;
import atropos.core.Material;
import atropos.core.PhotekTorusKnot;
import atropos.core.camera.BasicCamera;
import atropos.core.camera.ControllableCamera;

import atropos.core.light.DirectionalLight;
import atropos.core.light.PointLight;
import atropos.core.light.SpotLight;
import atropos.core.math.Vector3f;
import atropos.core.shader.FragmentShader;
import atropos.core.shader.ShaderProgram;
import atropos.core.shader.ShaderSource;
import atropos.core.shader.VertexShader;
import atropos.core.shader.attrib.VertexAttrib3f;
import atropos.core.shader.uniform.Uniform1i;

public class PhongShaderDemoRenderer extends AtroposDefaultRenderer {
	
	Material material;
	PointLight light;
	ControllableCamera camera;

	
	Texture texture1, texture2, texture3;
	VertexAttrib3f tangent;
	PhotekTorusKnot knot;
	
	@Override
	public void init(GLAutoDrawable drawable) {
		drawable.setGL(new DebugGL2(drawable.getGL().getGL2()));
		GL2 gl = drawable.getGL().getGL().getGL2();
		
		gl.setSwapInterval(1);
		
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glClearColor(0.f, 0.f, 0.f, 1.0f);

		VertexShader vs = new VertexShader(gl);
		vs.setShaderSource(gl, new ShaderSource(new File("shaders/phongPointLightShader.vs")));
		vs.compileShader(gl);
		System.err.println(vs.getInfoLog(gl));

		FragmentShader fs = new FragmentShader(gl);
		fs.setShaderSource(gl, new ShaderSource(new File("shaders/phongPointLightShader.fs")));
		fs.compileShader(gl);
		System.err.println(fs.getInfoLog(gl));
		
		ShaderProgram prog = new ShaderProgram(gl);
		prog.attachShader(gl, vs);
		prog.attachShader(gl, fs);
		prog.linkProgram(gl);
		System.err.println(prog.getInfoLog(gl));
		prog.activate(gl);
		
		float[] lightPosition_	= {0.3f, 0.5f, 1.0f, 0.0f};
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPosition_, 0);
		gl.glEnable(GL2.GL_LIGHT0);
		gl.glEnable(GL2.GL_LIGHTING);
		
		material = new Material();
		material.setAmbient(0.03f, 0.03f, 0.03f, 1.0f);
		material.setDiffuse(0.5f, 0.56f, 0.59f, 1.0f);
		material.setSpecular(Color.ORANGE);
		material.setShininess(4.0f);
		
		
		
		light = new PointLight(GL2.GL_LIGHT0);
		light.setAmbient(1.0f, 1.0f, 1.0f, 1.0f);
		light.setDiffuse(1.0f, 1.0f, 1.0f, 1.0f);
		light.setSpecular(1.0f, 1.0f, 1.0f, 1.0f);
		light.setPosition(0, 0, 0);
		light.setConstantAttenuation(1.0f);
		light.setLinearAttenuation(0.0f);
		light.setQuadraticAttenuation(0.5f);
	//	light.setSpotDirection(0.0f, 0.0f, -1.0f);
	//	light.setSpotCutoff(20);
		
		camera = new ControllableCamera();

		
		gl.glEnable(GL.GL_CULL_FACE);
		gl.glFrontFace(GL.GL_CW);
		
//		Uniform1i tex1 = prog.getUniform1i(gl, "texture1");
//		Uniform1i tex2 = prog.getUniform1i(gl, "texture2");
//		Uniform1i tex3 = prog.getUniform1i(gl, "texture3");
//		 tangent = prog.getVertexAttrib3f(gl, "tangent");
		
//		tex1.set(gl, 0);
//		tex2.set(gl, 1);
//		tex3.set(gl, 2);
//		tangent.set(gl, 1.0f, 0.0f, 0.0f);
		
//		texture1 = load("textures/tex1.jpg");
//		texture2 = load("textures/tex2.jpg");
		//texture1 = load("textures/collage.jpg");
		//texture2 = load("textures/collage-bump.jpg");
//		texture3 = load("textures/tex3.jpg");
		
//		knot = new PhotekTorusKnot(gl, prog);
	}
	
	  Texture load (GL gl,String filename)
	   {
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
	          //texture.setTexParameteri(GL.GL_GENERATE_MIPMAP, GL.GL_TRUE);
	          
	      }
	      catch (Exception e)
	      {
	          System.out.println ("error loading texture from "+filename);
	      }

	      return texture;
	   }

	float rotation = 0;
	long timer = 0;

	long oldticks = System.currentTimeMillis();
	
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		GLUT glut = new GLUT();

		long currentticks = System.currentTimeMillis();
		float delta = (currentticks - oldticks) * 0.001f;
		oldticks = currentticks;
		
		gl.glClear(GL.GL_COLOR_BUFFER_BIT
				| GL.GL_DEPTH_BUFFER_BIT);
		
		
//		keyboard.poll();
//		pad.poll();
	//	gl.glEnable(GL.GL_CULL_FACE);
		
//		if(keyboard.isKeyDown(Identifier.Key.W) || pad.isDirectionPadPressed(DirectionPad.UP))
//			camera.moveForward(3.0f, delta);
//		
//		if(keyboard.isKeyDown(Identifier.Key.S) || pad.isDirectionPadPressed(DirectionPad.DOWN))
//			camera.moveBackward(3.0f, delta);
//		
//		if(keyboard.isKeyDown(Identifier.Key.A) || pad.isDirectionPadPressed(DirectionPad.LEFT))
//			camera.turnLeft(1.0f, delta);
//		
//		if(keyboard.isKeyDown(Identifier.Key.D) || pad.isDirectionPadPressed(DirectionPad.RIGHT))
//			camera.turnRight(1.0f, delta);
//		
//		if(keyboard.isKeyDown(Identifier.Key.INSERT))
//			camera.turnUp(1.0f, delta);
//		
//		if(keyboard.isKeyDown(Identifier.Key.DELETE))
//			camera.turnDown(1.0f, delta);
		
		gl.glLoadIdentity();
		light.apply(gl);
		camera.applyCamera(gl);
		
		
		
		
		gl.glTranslatef(0.0f, 0.0f, -5.0f);
		gl.glRotatef(rotation, 0.0f, 0.0f, 1.0f);
		gl.glRotatef(rotation, 0.0f, 1.0f, 0.0f);
		gl.glRotatef(rotation, 1.0f, 0.0f, 0.0f);
		

		material.apply(gl);
//		gl.glActiveTexture(GL.GL_TEXTURE0);
//		texture1.bind();
//		gl.glActiveTexture(GL.GL_TEXTURE1);
//		texture2.bind();
//		gl.glActiveTexture(GL.GL_TEXTURE2);
//		texture3.bind();
		
		//gl.glScalef(4,4,4);
		gl.glBegin(GL2.GL_QUADS);
////		tangent.set(gl, 1.0f, 0.0f, 0.0f);
//		gl.glNormal3f( 0.0f, 0.0f, 1.0f);					// Normal Pointing Towards Viewer
//		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f,  1.0f);	// Point 1 (Front)
//		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f,  1.0f);	// Point 2 (Front)
//		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f,  1.0f);	// Point 3 (Front)
//		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f,  1.0f);	// Point 4 (Front)
//		// Back Face
////		tangent.set(gl, -1.0f, 0.0f, 0.0f);
//		gl.glNormal3f( 0.0f, 0.0f,-1.0f);					// Normal Pointing Away From Viewer
//		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f, -1.0f);	// Point 1 (Back)
//		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f, -1.0f);	// Point 2 (Back)
//		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f, -1.0f);	// Point 3 (Back)
//		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f, -1.0f);	// Point 4 (Back)
//		// Top Face
////		tangent.set(gl, 1.0f, 0.0f, 0.0f);
//		gl.glNormal3f( 0.0f, 1.0f, 0.0f);					// Normal Pointing Up
//		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f, -1.0f);	// Point 1 (Top)
//		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f,  1.0f,  1.0f);	// Point 2 (Top)
//		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 1.0f,  1.0f,  1.0f);	// Point 3 (Top)
//		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f, -1.0f);	// Point 4 (Top)
//		// Bottom Face
////		tangent.set(gl, -1.0f, 0.0f, 0.0f);
//		gl.glNormal3f( 0.0f,-1.0f, 0.0f);					// Normal Pointing Down
//		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1.0f, -1.0f, -1.0f);	// Point 1 (Bottom)
//		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( 1.0f, -1.0f, -1.0f);	// Point 2 (Bottom)
//		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f,  1.0f);	// Point 3 (Bottom)
//		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f,  1.0f);	// Point 4 (Bottom)
//		// Right face
////		tangent.set(gl, 0.0f, 0.0f, -1.0f);
//		gl.glNormal3f( 1.0f, 0.0f, 0.0f);					// Normal Pointing Right
//		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f, -1.0f);	// Point 1 (Right)
//		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f, -1.0f);	// Point 2 (Right)
//		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f,  1.0f);	// Point 3 (Right)
//		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f,  1.0f);	// Point 4 (Right)
//		// Left Face
////		tangent.set(gl, 0.0f, 0.0f, 1.0f);
//		gl.glNormal3f(-1.0f, 0.0f, 0.0f);					// Normal Pointing Left
//		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f, -1.0f);	// Point 1 (Left)
//		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f,  1.0f);	// Point 2 (Left)
//		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f,  1.0f);	// Point 3 (Left)
//		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f, -1.0f);	// Point 4 (Left)
		gl.glEnd();
		
//		knot.execute(gl);
		glut.glutSolidTeapot(1.0);

		rotation += 0.2f;
		timer += 9;
	}

}