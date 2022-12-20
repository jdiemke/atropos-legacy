package atropos.demos.input;

import java.io.File;


import com.jogamp.opengl.DebugGL2;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.glu.GLU;

import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;




import atropos.AtroposDefaultRenderer;
import atropos.core.Color;
import atropos.core.Material;
import atropos.core.PhotekTorusKnot;
import atropos.core.camera.BasicCamera;
import atropos.core.camera.ControllableCamera;
//import atropos.core.input.AtroposKeyboard;
//import atropos.core.input.Gamepad;
//import atropos.core.input.Gamepad.DirectionPad;

import atropos.core.light.DirectionalLight;
import atropos.core.light.PointLight;
import atropos.core.math.Vector3f;
import atropos.core.shader.FragmentShader;
import atropos.core.shader.ShaderProgram;
import atropos.core.shader.ShaderSource;
import atropos.core.shader.VertexShader;
import atropos.core.shader.attrib.VertexAttrib3f;
import atropos.core.shader.attrib.VertexAttrib4f;
import atropos.core.shader.uniform.Uniform1f;
import atropos.core.shader.uniform.Uniform1i;

public class KeyboardDemoRenderer extends AtroposDefaultRenderer {
	
	Material material;
	PointLight light;
	ControllableCamera camera;
	ShaderProgram prog2,prog;

//	Gamepad pad;
	VertexAttrib4f colorVertexAttrib4f;
	Uniform1f time;
	Uniform1i mode;
	
	Texture texture1, texture2, texture3;
	Texture texture;
	VertexAttrib3f tangent;
	PhotekTorusKnot knot;
	
	@Override
	public void init(GLAutoDrawable drawable) {
		drawable.setGL(new DebugGL2(drawable.getGL().getGL2()));
		GL2 gl = drawable.getGL().getGL2();
		
		gl.setSwapInterval(1);
		
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
	//	gl.glClearColor(1.2f, 1.2f, 1.2f, 1.0f);

		VertexShader vs = new VertexShader(gl);
		vs.setShaderSource(gl, new ShaderSource(new File("shaders/testParallaxShader.vs")));
		vs.compileShader(gl);
		System.err.println(vs.getInfoLog(gl));

		FragmentShader fs = new FragmentShader(gl);
		fs.setShaderSource(gl, new ShaderSource(new File("shaders/testParallaxShader.fs")));
		fs.compileShader(gl);
		System.err.println(fs.getInfoLog(gl));
		
		prog = new ShaderProgram(gl);
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
		material.setAmbient(0.0f, 0.0f, 0.0f, 1.0f);
		material.setDiffuse(0.5f, 0.56f, 0.59f, 1.0f);
		material.setSpecular(Color.ORANGE);
		material.setShininess(94.0f);
		
		
		
		light = new PointLight(GL2.GL_LIGHT0);
		light.setAmbient(1.0f, 1.0f, 1.0f, 1.0f);
		light.setDiffuse(1.0f, 1.0f, 1.0f, 1.0f);
		light.setSpecular(1.0f, 1.0f, 1.0f, 1.0f);
		light.setPosition(0,0,0);
		//light.setDirection(0.0f, 0.0f, 1.0f);
		
		camera = new ControllableCamera(new Vector3f(0,0,5));
	
//		pad = new Gamepad();
		
		Uniform1i tex2 = prog.getUniform1i(gl, "sphereMapSampler");
		//Uniform1i tex2 = prog.getUniform1i(gl, "texture2");
		Uniform1i tex3 = prog.getUniform1i(gl, "normalMapSampler");
		 tangent = prog.getVertexAttrib3f(gl, "tangent");
		
	//	tex1.set(gl, 0);
		tex2.set(gl, 1);
		tex3.set(gl, 2);
		//tangent.set(gl, 1.0f, 0.0f, 0.0f);
		
		float[] maxAniso = new float[1];
		gl.glGetFloatv(GL2.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, maxAniso,0);

		
		texture1 = load(gl,"textures/tex1.jpg");
		texture2 = load(gl,"textures/tex2.jpg");
		
		texture2 = load(gl,"textures/spheremap_grace_cathedral.jpg");
		
		texture2.setTexParameteri(gl,GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR_MIPMAP_LINEAR);
		texture2.setTexParameteri(gl,GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
		texture2.setTexParameterf(gl,GL2.GL_TEXTURE_MAX_ANISOTROPY_EXT, maxAniso[0]);
		texture2.setTexParameteri(gl,GL2.GL_GENERATE_MIPMAP, GL2.GL_TRUE);
		gl.glGenerateMipmap(GL2.GL_TEXTURE_2D);
	
	System.out.println("aniso: "+maxAniso[0]);
		
		//texture1 = load("textures/collage.jpg");
		//texture2 = load("textures/collage-bump.jpg");
		texture3 = load(gl,"textures/tex3.jpg");
		texture3.setTexParameteri(gl,GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR_MIPMAP_LINEAR);
		texture3.setTexParameteri(gl,GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
		texture3.setTexParameterf(gl,GL2.GL_TEXTURE_MAX_ANISOTROPY_EXT, maxAniso[0]);
	
		texture3.setTexParameteri(gl,GL2.GL_GENERATE_MIPMAP, GL2.GL_TRUE);
		gl.glGenerateMipmap(GL2.GL_TEXTURE_2D);
		
		
		knot = new PhotekTorusKnot(gl, prog);
		

		VertexShader vs2 = new VertexShader(gl);
		vs2.setShaderSource(gl, new ShaderSource(new File("shaders/planeDeformationShader.vs")));
		vs2.compileShader(gl);
		System.err.println(vs2.getInfoLog(gl));

		FragmentShader fs2 = new FragmentShader(gl);
		fs2.setShaderSource(gl, new ShaderSource(new File("shaders/planeDeformationShader.fs")));
		fs2.compileShader(gl);
		System.err.println(fs2.getInfoLog(gl));
		
		prog2 = new ShaderProgram(gl);
		prog2.attachShader(gl, vs2);
		prog2.attachShader(gl, fs2);
		prog2.linkProgram(gl);
		System.err.println(prog2.getInfoLog(gl));
		prog2.activate(gl);
		
		 texture = load(gl,"textures/metal.png");
		Uniform1i uni = prog2.getUniform1i(gl, "texCol");
		 time = prog2.getUniform1f(gl,"time");
		 mode = prog2.getUniform1i(gl, "mode");
		uni.set(gl, 0);
	}
	
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		GL2 gl = drawable.getGL().getGL2();
		GLU glu = new GLU();

		if (height <= 0)
			height = 1;
		
		final float h = (float) width / (float) height;
		
		gl.glViewport(0, 0, width, height);
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		
		glu.gluPerspective(45.0f, h, 0.5, 100.0);
		
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
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

	float rotation = 625578;
	long timer = 0;

	long oldticks = System.currentTimeMillis();
	long start = System.currentTimeMillis();
	private Object VertexAttrib3f;
	
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		GLUT glut = new GLUT();

		long currentticks = System.currentTimeMillis();
		float delta = (currentticks - oldticks) * 0.01f;
		oldticks = currentticks;
		
		
		
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		
		gl.glTranslatef(0.0f, 0.0f, -1.36f);
		
		float elapsed = (System.currentTimeMillis() -start) * 0.01f;
		float elapsed2 = (System.currentTimeMillis() -start) ;
		prog2.activate(gl);
		time.set(gl, elapsed);
		
		int myMode = ((int)(elapsed*0.7f))%14;
		mode.set(gl, myMode);
		gl.glActiveTexture(GL.GL_TEXTURE0);
		texture.bind(gl);
		gl.glDisable(GL.GL_CULL_FACE);
//		gl.glBegin(GL2.GL_QUADS);
//			gl.glTexCoord2f(0,1);
//			gl.glVertex3f(-1.0f, 1.0f, 0.0f);
//			gl.glTexCoord2f(1,1);
//			gl.glVertex3f(1.0f, 1.0f, 0.0f);
//			gl.glTexCoord2f(1,0);
//			gl.glVertex3f(1.0f, -1.0f, 0.0f);
//			gl.glTexCoord2f(0,0);
//			gl.glVertex3f(-1.0f, -1.0f, 0.0f);
//		gl.glEnd();
		
		gl.glClear(
				GL.GL_DEPTH_BUFFER_BIT);
		
		
	
//		pad.poll();
		gl.glEnable(GL.GL_CULL_FACE);
//		
//		if(keyboard.isKeyDown(Identifier.Key.W))// || pad.isDirectionPadPressed(DirectionPad.UP))
//			camera.moveForward(3.0f, delta);
//		
//		if(keyboard.isKeyDown(Identifier.Key.S))// || pad.isDirectionPadPressed(DirectionPad.DOWN))
//			camera.moveBackward(3.0f, delta);
//		
//		if(keyboard.isKeyDown(Identifier.Key.A))// || pad.isDirectionPadPressed(DirectionPad.LEFT))
//			camera.turnLeft(1.0f, delta);
//		
//		if(keyboard.isKeyDown(Identifier.Key.D))// || pad.isDirectionPadPressed(DirectionPad.RIGHT))
//			camera.turnRight(1.0f, delta);
//		
//		if(keyboard.isKeyDown(Identifier.Key.INSERT))
//			camera.turnUp(1.0f, delta);
//		
//		if(keyboard.isKeyDown(Identifier.Key.DELETE))
//			camera.turnDown(1.0f, delta);
//		
		gl.glLoadIdentity();
		light.apply(gl);
		camera.applyCamera(gl);
		
		
		
		
	//	gl.glTranslatef((float)(4*Math.sin(elapsed2*0.002f)), (float)(2*Math.sin(elapsed2*0.001f)), -10.2f-(float)(10*(1+Math.sin(elapsed2*0.001f))));
		gl.glTranslatef(0,0, -12.2f);
		gl.glRotatef(elapsed, 0.0f, 0.0f, 1.0f);
		gl.glRotatef(elapsed, 0.0f, 1.0f, 0.0f);
		//gl.glRotatef(rotation, 1.0f, 0.0f, 0.0f);
		
		
		material.apply(gl);
		gl.glActiveTexture(GL.GL_TEXTURE0);
		texture1.bind(gl);
		gl.glActiveTexture(GL.GL_TEXTURE1);
		texture2.bind(gl);
		gl.glActiveTexture(GL.GL_TEXTURE2);
		texture3.bind(gl);
		
	gl.glScalef(4,4,4);
//		gl.glBegin(GL2.GL_QUADS);
//		tangent.set(gl, 1.0f, 0.0f, 0.0f);
//		gl.glNormal3f( 0.0f, 0.0f, 1.0f);					// Normal Pointing Towards Viewer
//		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f,  1.0f);	// Point 1 (Front)
//		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f,  1.0f);	// Point 2 (Front)
//		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f,  1.0f);	// Point 3 (Front)
//		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f,  1.0f);	// Point 4 (Front)
//		// Back Face
//		tangent.set(gl, -1.0f, 0.0f, 0.0f);
//		gl.glNormal3f( 0.0f, 0.0f,-1.0f);					// Normal Pointing Away From Viewer
//		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f, -1.0f);	// Point 1 (Back)
//		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f, -1.0f);	// Point 2 (Back)
//		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f, -1.0f);	// Point 3 (Back)
//		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f, -1.0f);	// Point 4 (Back)
//		// Top Face
//		tangent.set(gl, 1.0f, 0.0f, 0.0f);
//		gl.glNormal3f( 0.0f, 1.0f, 0.0f);					// Normal Pointing Up
//		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f, -1.0f);	// Point 1 (Top)
//		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f,  1.0f,  1.0f);	// Point 2 (Top)
//		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 1.0f,  1.0f,  1.0f);	// Point 3 (Top)
//		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f, -1.0f);	// Point 4 (Top)
//		// Bottom Face
//		tangent.set(gl, -1.0f, 0.0f, 0.0f);
//		gl.glNormal3f( 0.0f,-1.0f, 0.0f);					// Normal Pointing Down
//		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1.0f, -1.0f, -1.0f);	// Point 1 (Bottom)
//		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( 1.0f, -1.0f, -1.0f);	// Point 2 (Bottom)
//		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f,  1.0f);	// Point 3 (Bottom)
//		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f,  1.0f);	// Point 4 (Bottom)
//		// Right face
//		tangent.set(gl, 0.0f, 0.0f, -1.0f);
//		gl.glNormal3f( 1.0f, 0.0f, 0.0f);					// Normal Pointing Right
//		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f, -1.0f);	// Point 1 (Right)
//		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f, -1.0f);	// Point 2 (Right)
//		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f,  1.0f);	// Point 3 (Right)
//		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f,  1.0f);	// Point 4 (Right)
//		// Left Face
//		tangent.set(gl, 0.0f, 0.0f, 1.0f);
//		gl.glNormal3f(-1.0f, 0.0f, 0.0f);					// Normal Pointing Left
//		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f, -1.0f);	// Point 1 (Left)
//		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f,  1.0f);	// Point 2 (Left)
//		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f,  1.0f);	// Point 3 (Left)
//		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f, -1.0f);	// Point 4 (Left)
//		gl.glEnd();
// achtung funkt nicht weil tangente nicht gesetzt wird!!		
//knot.execute(gl);
	prog.activate(gl);
		knot.fill(gl, tangent, true);
//		glut.glutSolidTeapot(1.0);

		rotation += delta;
		timer += 9;
	}

}