package atropos.demos.shader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.jogamp.opengl.DebugGL2;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.glu.GLU;


import com.jogamp.opengl.util.gl2.GLUT;

import atropos.AtroposDefaultRenderer;
import atropos.core.Color;
import atropos.core.Material;
import atropos.core.camera.BasicCamera;
import atropos.core.fbo.Framebuffer;
import atropos.core.fbo.Renderbuffer;
import atropos.core.light.PointLight;
import atropos.core.math.Matrix4f;
import atropos.core.math.Vector3f;
import atropos.core.model.wavefront.WavefrontModel;
import atropos.core.shader.FragmentShader;
import atropos.core.shader.ShaderProgram;
import atropos.core.shader.ShaderSource;
import atropos.core.shader.VertexShader;
import atropos.core.shader.uniform.Uniform1i;
import atropos.core.shader.uniform.Uniform4fv;
import atropos.core.shader.uniform.UniformMatrix4fv;
import atropos.core.texture.Texture2D;

public class ShadowMappingShaderDemoRenderer extends AtroposDefaultRenderer {

	Material material, material2;
	PointLight light;
	BasicCamera camView;
	BasicCamera lightView;
	Framebuffer fbo;
	Texture2D texture2D;
	ShaderProgram prog, prog2;
	UniformMatrix4fv lightMVPuniform4fv ;
	UniformMatrix4fv camMVPuniform4fv ;
	
	UniformMatrix4fv modelMatrixUniform4fv;
	UniformMatrix4fv lightBPV;
	Uniform1i shadowMap;
	OBJLoader loader;
	WavefrontModel model;
	Matrix4f lightProjection;
	
	@Override
	public void init(GLAutoDrawable drawable) {
		drawable.setGL(new DebugGL2(drawable.getGL().getGL2()));
		GL2 gl = drawable.getGL().getGL2();
		
		gl.setSwapInterval(1);
		
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glEnable(GL2.GL_CULL_FACE);
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

		///// shadow map
		VertexShader vs = new VertexShader(gl);
		vs.setShaderSource(gl, new ShaderSource(new File("shaders/shadowMapping.vs")));
		vs.compileShader(gl);
		System.err.println(vs.getInfoLog(gl));

		FragmentShader fs = new FragmentShader(gl);
		fs.setShaderSource(gl, new ShaderSource(new File("shaders/shadowMapping.fs")));
		fs.compileShader(gl);
		System.err.println(fs.getInfoLog(gl));
		
		 prog = new ShaderProgram(gl);
		prog.attachShader(gl, vs);
		prog.attachShader(gl, fs);
		prog.linkProgram(gl);
		System.err.println(prog.getInfoLog(gl));
		
		prog.activate(gl);
		lightMVPuniform4fv =  prog.getUniformMatrix4v(gl, "lightMVP");
		camMVPuniform4fv =  prog.getUniformMatrix4v(gl, "camMVP");
		modelMatrixUniform4fv = prog.getUniformMatrix4v(gl, "modelMatrix");
		Uniform1i sampler = prog.getUniform1i(gl, "colorMap");
		sampler.set(gl, 0);
		lightBPV = prog.getUniformMatrix4v(gl, "lightBPV");
		shadowMap = prog.getUniform1i(gl, "shadowMap");
		shadowMap.set(gl, 1);
		prog.deactivate(gl);
		
		/// depth map
		
		VertexShader vs2 = new VertexShader(gl);
		vs.setShaderSource(gl, new ShaderSource(new File("shaders/shadowMapping.vs")));
		vs.compileShader(gl);
		System.err.println(vs.getInfoLog(gl));

		FragmentShader fs2 = new FragmentShader(gl);
		fs.setShaderSource(gl, new ShaderSource(new File("shaders/shadowMapping.fs")));
		fs.compileShader(gl);
		System.err.println(fs.getInfoLog(gl));
		
		 prog2 = new ShaderProgram(gl);
		prog2.attachShader(gl, vs);
		prog2.attachShader(gl, fs);
		prog2.linkProgram(gl);
		System.err.println(prog2.getInfoLog(gl));
		
		prog2.activate(gl);

		
		prog2.deactivate(gl);
		
		

	
		/// renderbuffer
		
		Renderbuffer depthRenderbuffer = new Renderbuffer(gl);
		depthRenderbuffer.setStorage(gl, GL2.GL_DEPTH_COMPONENT, 32, 32);
		
		
		/////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////
		// create depth texture for fbo
		texture2D = new Texture2D(gl,GL2.GL_TEXTURE_2D);
		
		texture2D.setMagFilter(gl, GL.GL_NEAREST);
		texture2D.setMinFilter(gl, GL.GL_NEAREST);

		texture2D.setWrapS(gl, GL2.GL_CLAMP);
		texture2D.setWrapT(gl, GL2.GL_CLAMP);
		
		texture2D.setStorage(gl, GL2.GL_DEPTH_COMPONENT, GL2.GL_DEPTH_COMPONENT, 32, 32);
		
		gl.glActiveTexture(GL2.GL_TEXTURE1);
		texture2D.bind(gl);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_COMPARE_MODE, GL2.GL_COMPARE_R_TO_TEXTURE);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_DEPTH_TEXTURE_MODE, GL2.GL_INTENSITY);
		gl.glActiveTexture(GL2.GL_TEXTURE0);
		
		/////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////
		// create fbo
		
		 if(Framebuffer.isSupported(gl))
			 System.err.println("FBO support available.");
		 
		 fbo = new Framebuffer(gl);
		 fbo.attach(gl, GL2.GL_DEPTH_ATTACHMENT, texture2D);
		// fbo.attach(gl, GL2.GL_DEPTH_ATTACHMENT, depthRenderbuffer);
		 fbo.bind(gl);
			gl.glDrawBuffer(GL2.GL_NONE);
			gl.glReadBuffer(GL2.GL_NONE);
			fbo.unbind(gl);
			
			System.out.println(fbo.isComplete(gl));
			System.out.println(fbo.getStatus(gl));
			
		/////// create 
		camView = new BasicCamera(new Vector3f(0,-.8f,1.65f), 0.0f , -30.0f, 0.0f);
		lightView = new BasicCamera(new Vector3f(0,4,0), 0.0f , -90.0f, 0.0f);
		gl.glEnable(GL2.GL_CULL_FACE);
		gl.glEnable(GL2.GL_DEPTH_TEST);
		loader = new OBJLoader("models" + File.separator + "bunny.obj");
		
		// light & material setup
		float[] lightPosition_	= {0.0f, 1.4f, 0.0f, 1.0f};
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPosition_, 0);
		gl.glEnable(GL2.GL_LIGHT0);
		gl.glEnable(GL2.GL_LIGHTING);
		
		material = new Material();
		material.setAmbient(0.1f, 0.1f, 0.1f, 1.0f);
		material.setDiffuse(0.5f, 0.56f, 0.59f, 1.0f);
		material.setSpecular(Color.ORANGE);
		material.setShininess(104.0f);
		
		material2 = new Material();
		material2.setAmbient(0.1f, 0.1f, 0.1f, 1.0f);
		material2.setDiffuse(0.7f, 0.56f, 0.59f, 1.0f);
		material2.setSpecular(Color.ORANGE);
		material2.setShininess(104.0f);
		
		
		
		light = new PointLight(GL2.GL_LIGHT0);
		light.setAmbient(0.2f, 0.2f, 0.2f, 1.0f);
		light.setDiffuse(1.0f, 1.0f, 1.0f, 1.0f);
		light.setSpecular(1.0f, 1.0f, 1.0f, 1.0f);
		light.setPosition(0, 4, 0);
		light.setConstantAttenuation(1.0f);
		light.setLinearAttenuation(0.0f);
		light.setQuadraticAttenuation(0.5f);
		material.apply(gl);
		
		model = new WavefrontModel(gl, "models/exd9xl/exd9xl_a.obj");
		//model = new WavefrontModel("models/Apple_Iphone/iphone.obj");
		//model = new WavefrontModel("models/ltv/ltv.obj");
		//model = new WavefrontModel("models/Cobra fighter/cobra fighter.obj");
		model.construct(gl);
				//"models/pumpkin_tall_10k.obj");
		//"models/bunny.obj");
	}
	
	float rotation = 0;
	long timer = 0;
	
	
//	
//	Matrix4f bias = new Matrix4f(0.5f, 0.0f, 0.0f, 0.5f,
//			 					 0.0f, 0.5f, 0.0f, 0.5f,
//			 					 0.0f, 0.0f, 0.5f, 0.5f,
//			 					 0.0f, 0.0f, 0.0f, 1.0f);
//	
//	float[] mv = new float[16];
//	
//	gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX,  mv, 0);
//	Matrix4f MVmatrix = new Matrix4f(mv).transpose();
//	
//	gl.glGetFloatv(GL2.GL_PROJECTION_MATRIX,  mv, 0);
//	Matrix4f MVProj = new Matrix4f(mv).transpose();
//	
//	Matrix4f lightMat = bias.multiply(MVProj).multiply(MVmatrix);
//	//gl.glLoadIdentity();
//
//	gl.glTranslatef(0.0f, 0.0f, -3.0f);
//	gl.glRotatef(rotation, 0.0f, 0.0f, 1.0f);
//	gl.glRotatef(rotation, 0.0f, 1.0f, 0.0f);
//	
//	glut.glutSolidTeapot(1.0);
//	

	public void drawCube(GL2 gl, float x, float y, float z) {
		gl.glTranslatef(x, y, z);
		gl.glScalef(0.2f,0.2f,0.2f);
		gl.glRotatef(45, 1.0f,0.0f,0.0f);
		gl.glTranslatef(0.0f, (float)(1*Math.sin(rotation*0.03f)),0.0f);
		gl.glRotatef(rotation, 0.0f,0.0f,1.0f);
		gl.glRotatef(rotation, 0.0f,1.0f,0.0f);
		gl.glBegin(GL2.GL_QUADS);
			// front
			gl.glColor3f(1.0f, 0.0f, 0.0f);
			gl.glNormal3f(0.0f, 0.0f, 1.0f);
			gl.glVertex3f(-1.0f, -1.0f, 1.0f);
			gl.glVertex3f( 1.0f, -1.0f, 1.0f);
			gl.glVertex3f( 1.0f,  1.0f, 1.0f);
			gl.glVertex3f(-1.0f,  1.0f, 1.0f);
			
			// right
			gl.glColor3f(0.0f, 1.0f, 0.0f);
			gl.glNormal3f(1.0f, 0.0f, 0.0f);
			gl.glVertex3f(1.0f, -1.0f,  1.0f);
			gl.glVertex3f(1.0f, -1.0f, -1.0f);
			gl.glVertex3f(1.0f,  1.0f, -1.0f);
			gl.glVertex3f(1.0f,  1.0f,  1.0f);
			
			// back
			gl.glColor3f(0.0f, 0.0f, 1.0f);
			gl.glNormal3f(0.0f, 0.0f, -1.0f);
			gl.glVertex3f( 1.0f, -1.0f, -1.0f);
			gl.glVertex3f(-1.0f, -1.0f, -1.0f);
			gl.glVertex3f(-1.0f,  1.0f, -1.0f);
			gl.glVertex3f( 1.0f,  1.0f, -1.0f);
			
			// left
			gl.glColor3f(1.0f, 0.0f, 1.0f);
			gl.glNormal3f(-1.0f, 0.0f, 1.0f);
			gl.glVertex3f(-1.0f, -1.0f, -1.0f);
			gl.glVertex3f(-1.0f, -1.0f,  1.0f);
			gl.glVertex3f(-1.0f,  1.0f,  1.0f);
			gl.glVertex3f(-1.0f,  1.0f, -1.0f);
			
			//top
			gl.glColor3f(0.0f, 1.0f, 1.0f);
			gl.glNormal3f(0.0f, 1.0f, 0.0f);
			gl.glVertex3f(-1.0f, 1.0f, 1.0f);
			gl.glVertex3f( 1.0f, 1.0f, 1.0f);
			gl.glVertex3f( 1.0f, 1.0f,-1.0f);
			gl.glVertex3f(-1.0f, 1.0f,-1.0f);
		
			// bottom
			gl.glNormal3f(0.0f, -1.0f, 0.0f);
			gl.glVertex3f(-1.0f,-1.0f,-1.0f);
			gl.glVertex3f( 1.0f,-1.0f,-1.0f);
			gl.glVertex3f( 1.0f,-1.0f, 1.0f);
			gl.glVertex3f(-1.0f,-1.0f, 1.0f);
		gl.glEnd();
	}
	
	
	long startTime = System.currentTimeMillis();
	public void render(GL2 gl) {
		GLUT glut = new GLUT();
		
		Matrix4f mat1 = Matrix4f.constructZRotationMatrix(rotation);
		Matrix4f mat2 = Matrix4f.constructXRotationMatrix(rotation);
		
		Matrix4f composition = mat2.multiply(mat1);
		
		long currentTime = startTime - System.currentTimeMillis();
		
		gl.glTranslatef(0.0f, -0.42f, 0.0f);
		gl.glRotatef(currentTime*0.05f, 0,1,0);
		//composition.apply(gl);
		
		
		gl.glPushMatrix();
		//
		
		//loader.draw(gl);
		//gl.glFrontFace(GL2.GL_CCW);
		//gl.glDisable(GL2.GL_CULL_FACE);
		
		
		
		gl.glTranslatef(0.0f, -1.32f, 0.0f);
		gl.glRotatef(190,0,1,1);
		gl.glScalef(0.3f,0.3f,0.3f);
		model.draw(gl);
		//glut.glutSolidTeapot(0.3);
		gl.glPopMatrix();
		material.apply(gl);
		gl.glFrontFace(GL2.GL_CW);
		gl.glTranslatef(0.0f, -1.2f, 0.f);
		gl.glScalef(1.1f,0.1f,1.1f);
//		gl.glBegin(GL2.GL_QUADS);
//			gl.glTexCoord2f(0.5f, 0.5f);
//			gl.glNormal3f(0.0f, 1.0f, 0.0f);
//			gl.glVertex3f(-1.0f,-1.3f,-1.0f);
//			gl.glVertex3f(1.0f,-1.3f,-1.0f);
//			gl.glVertex3f(1.0f,-1.3f,1.0f);
//			gl.glVertex3f(-1.0f,-1.3f,1.0f);
//		gl.glEnd();
		gl.glFrontFace(GL2.GL_CCW);
//		drawCube(gl, 0.5f, -0.8f, -0.3f);
	}
	
	public void renderDepthTexture(GL2 gl, UniformMatrix4fv modelMatrix) {
		GLUT glut = new GLUT();
		GLU glu = new GLU();
		
		gl.glPushAttrib(GL2.GL_VIEWPORT_BIT);
		
		

		fbo.bind(gl);


		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		
		glu.gluPerspective(35.0f, 1.0f, 1.0, 10.0);
		
		float[] pm = new float[16];
		gl.glGetFloatv(GL2.GL_PROJECTION_MATRIX,  pm, 0);
		lightProjection = new Matrix4f(pm).transpose();

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		gl.glViewport(0, 0, 1024, 1024);
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		
		 gl.glCullFace(GL2.GL_FRONT);
		 gl.glShadeModel(GL2.GL_FLAT);
		 gl.glColorMask(false, false,false, false);
		
		lightView.getViewMatrix().apply(gl);

		render(gl);

		fbo.unbind(gl);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		
		gl.glPopAttrib();
		
		gl.glCullFace(GL2.GL_BACK);
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glColorMask(true, true, true, true);
	}
	
	public void renderShadowMappingScene(GL2 gl) {
		gl.glLoadIdentity();
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT | GL.GL_COLOR_BUFFER_BIT);
		
		camView.getViewMatrix().apply(gl);
		light.apply(gl);
		
		Matrix4f bias = new Matrix4f(0.5f, 0.0f, 0.0f, 0.5f,
				 					 0.0f, 0.5f, 0.0f, 0.5f,
				 					 0.0f, 0.0f, 0.5f, 0.5f,
				 					 0.0f, 0.0f, 0.0f, 1.0f);
		
		//bias = bias.constructIdentityMatrix();
		
		
		Matrix4f textureMatrix = bias.multiply(lightProjection)
			.multiply(lightView.getViewMatrix()).multiply(camView.getInverseViewMatrix());

		texture2D.enable(gl);
		texture2D.bind(gl);
		
		prog.activate(gl);
		lightBPV.set(gl, 1, true,textureMatrix.toArray(), 0);
		render(gl);
		prog.deactivate(gl);
		
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
	
	
	renderDepthTexture(gl,null);

		gl.glLoadIdentity();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glTranslatef(0.0f, 0.0f, -3.0f);

		texture2D.enable(gl);
		gl.glDisable(GL2.GL_LIGHTING);
		texture2D.bind(gl);
		gl.glBegin(GL2.GL_QUADS);
			gl.glColor3f(1.0f, 1.0f,1.0f);
			gl.glTexCoord2f(0.0f, 0.0f);
			gl.glVertex3f(-1.0f,-1.0f, 0.0f);
			
			gl.glTexCoord2f(1.0f, 0.0f);
			gl.glVertex3f( 1.0f,-1.0f, 0.0f);
			
			gl.glTexCoord2f(1.0f, 1.0f);
			gl.glVertex3f( 1.0f, 1.0f, 0.0f);
			
			gl.glTexCoord2f(0.0f, 1.0f);
			gl.glVertex3f(-1.0f, 1.0f, 0.0f);
		gl.glEnd();
		
		gl.glLoadIdentity();
		
		renderShadowMappingScene(gl);
		
		rotation += 0.8f;
	}
	

//	@Override
//	public void display(GLAutoDrawable drawable) {
//		GL2 gl = drawable.getGL().getGL2();
//		GLUT glut = new GLUT();
//		
//		// render in fbo for light
//		fbo.bind(gl);
//		
//		gl.glPushAttrib(GL2.GL_VIEWPORT_BIT);
//		gl.glViewport(0, 0, 512, 512);
//		gl.glClearColor(0.3f, 0.4f, 0.2f, 1.0f);
//		gl.glClear(GL.GL_COLOR_BUFFER_BIT
//				| GL.GL_DEPTH_BUFFER_BIT);
//		//gl.glClear(GL2.GL_DEPTH_BUFFER_BIT);
//		gl.glLoadIdentity();
//		lightCamera.applyCamera(gl);		
//		
//		
//		
//		Matrix4f bias = new Matrix4f(0.5f, 0.0f, 0.0f, 0.5f,
//				 0.0f, 0.5f, 0.0f, 0.5f,
//				 0.0f, 0.0f, 0.5f, 0.5f,
//				 0.0f, 0.0f, 0.0f, 1.0f);
//		
//		float[] mv = new float[16];
//		
//		gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX,  mv, 0);
//		Matrix4f MVmatrix = new Matrix4f(mv).transpose();
//		
//		gl.glGetFloatv(GL2.GL_PROJECTION_MATRIX,  mv, 0);
//		Matrix4f MVProj = new Matrix4f(mv).transpose();
//		
//		Matrix4f lightMat = bias.multiply(MVProj).multiply(MVmatrix);
//		//gl.glLoadIdentity();
//	
//		gl.glTranslatef(0.0f, 0.0f, -3.0f);
//		gl.glRotatef(rotation, 0.0f, 0.0f, 1.0f);
//		gl.glRotatef(rotation, 0.0f, 1.0f, 0.0f);
//		
//		glut.glutSolidTeapot(1.0);
//		
//		gl.glPopAttrib();
//		gl.glViewport(0, 0, 640, 480);
//		fbo.unbind(gl);
//		
//
//		gl.glLoadIdentity();
//		gl.glClear(GL.GL_COLOR_BUFFER_BIT
//				| GL.GL_DEPTH_BUFFER_BIT);
//		
//	viewCamera.applyCamera(gl);
//		
//	gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX,  mv, 0);
// MVmatrix = new Matrix4f(mv).transpose();
//	
//	gl.glGetFloatv(GL2.GL_PROJECTION_MATRIX,  mv, 0);
//	 MVProj = new Matrix4f(mv).transpose();
//	
//	 Matrix4f camMat = MVProj.multiply(MVmatrix);	
//	
//	 gl.glLoadIdentity();
//		light.apply(gl);
//		
//		gl.glTranslatef(0.0f, 0.0f, -3.0f);
//		Texture2D.enable(gl);
//		gl.glDisable(GL2.GL_LIGHTING);
//		texture2D.bind(gl);
//		gl.glBegin(GL2.GL_QUADS);
//		gl.glColor3f(1.0f, 1.0f,1.0f);
//			gl.glTexCoord2f(0.0f, 0.0f);
//			gl.glVertex3f(-1.0f+1f,-1.0f, 0.0f);
//			
//			gl.glTexCoord2f(1.0f, 0.0f);
//			gl.glVertex3f( 1.0f+1f,-1.0f, 0.0f);
//			
//			gl.glTexCoord2f(1.0f, 1.0f);
//			gl.glVertex3f( 1.0f+1f, 1.0f, 0.0f);
//			
//			gl.glTexCoord2f(0.0f, 1.0f);
//			gl.glVertex3f(-1.0f+1f, 1.0f, 0.0f);
//		gl.glEnd();
//		
//		gl.glRotatef(rotation, 0.0f, 0.0f, 1.0f);
//		gl.glRotatef(rotation, 0.0f, 1.0f, 0.0f);
//		
//
//		material.apply(gl);
//		prog.activate(gl);
//		
//		lightMVPuniform4fv.set(gl, 1, true, lightMat.toArray() , 0);
//		camMVPuniform4fv.set(gl, 1, true, camMat.toArray() , 0);
//
//		gl.glActiveTexture(GL2.GL_TEXTURE0);
//		texture2D.bind(gl);
//		
//		gl.glColor3f(1.0f, 0.0f, 1.0f);
//		glut.glutSolidTeapot(1.0);
//	
//		prog.deactivate(gl);
//
//		rotation += 0.2f;
//		timer += 9;
//		
//	}

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
				
			//	System.out.println(line);
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