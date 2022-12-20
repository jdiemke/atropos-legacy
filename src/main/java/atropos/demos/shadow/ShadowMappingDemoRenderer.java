package atropos.demos.shadow;

import atropos.AtroposDefaultRenderer;
import atropos.core.Material;
import atropos.core.PhotekTorusKnot;
import atropos.core.camera.BasicCamera;
import atropos.core.camera.BasicCamera2;
import atropos.core.fbo.Framebuffer;
import atropos.core.fbo.Renderbuffer;
import atropos.core.light.PointLight;
import atropos.core.math.Matrix4f;
import atropos.core.math.Vector3f;
import atropos.core.model.md2.MD2Model;
import atropos.core.model.milkshape.MilkshapeModel;
import atropos.core.model.wavefront.WavefrontModel;
import atropos.core.particle.ParticleEngine;
import atropos.core.shader.FragmentShader;
import atropos.core.shader.ShaderProgram;
import atropos.core.shader.ShaderSource;
import atropos.core.shader.VertexShader;
import atropos.core.shader.attrib.VertexAttrib3f;
import atropos.core.shader.uniform.Uniform1i;
import atropos.core.shader.uniform.UniformMatrix4fv;
import atropos.core.texture.Texture2D;
import com.jogamp.opengl.DebugGL2;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

public class ShadowMappingDemoRenderer extends AtroposDefaultRenderer {
	
	final static int shadowMapWidth = 1024;
	final static int shadowMapHeight = 1024;
	
	final static int fboBufferWidth = 640 * 2;
	final static int fboBufferHeight = 360 * 2;
	
	MD2Model md2,md22;
	MilkshapeModel model2;
	Material material, material2, material3;
	PointLight light;
	BasicCamera camView;
	BasicCamera lightView;
	Framebuffer fbo, fbo2, ssaaFBO, fbo3,blurFBO, tempFBO;
	Texture2D texture2D, depthMap, normalMap, colorMap, ssaaMap,downsampledColorMap,blurMap, tempDownsample;
	ShaderProgram prog, prog2, prog3, prog4, prog5;
	UniformMatrix4fv lightMVPuniform4fv ;
	UniformMatrix4fv camMVPuniform4fv ;
	 ParticleEngine engine;
	UniformMatrix4fv modelMatrixUniform4fv;
	UniformMatrix4fv lightBPV;
	Uniform1i shadowMap;
	OBJLoader loader;
	WavefrontModel model;
	Matrix4f lightProjection;
	VertexAttrib3f tangent;
	OBJLoader obj;
	
	PhotekTorusKnot knot;
	
	Texture cementTexture;
	Texture spotlightTexture;
	Texture metalTexture;
	
	BasicCamera2 camView2, lightView2;
	

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

	          //texture.setTexParameteri (gl,GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);

	          // Use the NEAREST minification function when the pixel being
	          // textured maps to an area greater than one texel.

	          //texture.setTexParameteri (gl,GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
	          
	          float[] maxAniso = new float[1];
	  		gl.glGetFloatv(GL2.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, maxAniso,0);

	          
	          texture.setTexParameteri(gl,GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
	  			texture.setTexParameteri(gl,GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
	          
	          texture.setTexParameterf(gl,GL.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
	          texture.setTexParameterf(gl,GL.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
	          
	          texture.setTexParameterf(gl, GL2.GL_TEXTURE_MAX_ANISOTROPY_EXT, maxAniso[0]);
	          
	          //texture.setTexParameteri(gl,GL2.GL_GENERATE_MIPMAP, GL.GL_TRUE);
	          gl.glGenerateMipmap(GL2.GL_TEXTURE_2D);
	      }
	      catch (Exception e)
	      {
	          System.out.println ("error loading texture from "+filename);
	      }

	      return texture;
	   }
	
	@Override
	public void init(GLAutoDrawable drawable) {
		drawable.setGL(new DebugGL2(drawable.getGL().getGL2()));
		GL2 gl = drawable.getGL().getGL2();
		
		System.err.println("INIT GL IS: " + gl.getClass().getName());
	    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
	    System.err.println("GL_VENDOR: " + gl.glGetString(GL2.GL_VENDOR));
	    System.err.println("GL_RENDERER: " + gl.glGetString(GL2.GL_RENDERER));
	    System.err.println("GL_VERSION: " + gl.glGetString(GL2.GL_VERSION));
	    System.err.println(gl.glGetString(GL2.GL_EXTENSIONS));
		
	    gl.glEnable(GL.GL_MULTISAMPLE);
	    
		gl.setSwapInterval(1);
		
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glEnable(GL2.GL_CULL_FACE);
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glClearColor(.0f, .0f, .0f, 1.0f);

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
		Uniform1i sampler2 = prog.getUniform1i(gl, "spotMap");
		sampler.set(gl, 0);
		sampler2.set(gl, 2);
		lightBPV = prog.getUniformMatrix4v(gl, "lightBPV");
		shadowMap = prog.getUniform1i(gl, "shadowMap");
		shadowMap.set(gl, 1);
		tangent = prog.getVertexAttrib3f(gl, "tangent");
		prog.deactivate(gl);
		
		/// depth map

	///// postprocessing shader
		VertexShader vs2 = new VertexShader(gl);
		vs2.setShaderSource(gl, new ShaderSource(new File("shaders/postProcessingShader.vs")));
		vs2.compileShader(gl);
		System.err.println(vs2.getInfoLog(gl));

		FragmentShader fs2 = new FragmentShader(gl);
		fs2.setShaderSource(gl, new ShaderSource(new File("shaders/postProcessingShader.fs")));
		fs2.compileShader(gl);
		System.err.println(fs2.getInfoLog(gl));
		
		 prog2 = new ShaderProgram(gl);
		prog2.attachShader(gl, vs2);
		
		
		prog2.attachShader(gl, fs2);
		prog2.linkProgram(gl);
		System.err.println(prog2.getInfoLog(gl));
		prog2.activate(gl);
		
		Uniform1i texture1 = prog2.getUniform1i(gl, "colorMap");
		Uniform1i texture2 = prog2.getUniform1i(gl, "positionMap");
		Uniform1i texture3 = prog2.getUniform1i(gl, "normalMap");
		texture1.set(gl, 0);
		texture2.set(gl, 1);
		texture3.set(gl, 2);
		
		prog2.deactivate(gl);
		

		// dof shader
		VertexShader vs3 = new VertexShader(gl);
		vs3.setShaderSource(gl, new ShaderSource(new File("shaders/dofShader.vs")));
		vs3.compileShader(gl);
		System.err.println(vs2.getInfoLog(gl));

		FragmentShader fs3 = new FragmentShader(gl);
		fs3.setShaderSource(gl, new ShaderSource(new File("shaders/dofShader.fs")));
		fs3.compileShader(gl);
		System.err.println(fs3.getInfoLog(gl));
		
		 prog3 = new ShaderProgram(gl);
			prog3.attachShader(gl, vs3);
			
			
			prog3.attachShader(gl, fs3);
			prog3.linkProgram(gl);
			System.err.println(prog3.getInfoLog(gl));
			
			prog3.activate(gl);
			
			Uniform1i texture4 = prog3.getUniform1i(gl, "colorMap");
			Uniform1i texture6 = prog3.getUniform1i(gl, "downsampledColorMap");
			Uniform1i texture5 = prog3.getUniform1i(gl, "positionMap");
			
			
			texture4.set(gl, 0);
			texture5.set(gl, 1);
			texture6.set(gl, 2);
			
			
			prog3.deactivate(gl);
	
	 // blur shader
			
			
			VertexShader vs4 = new VertexShader(gl);
			vs4.setShaderSource(gl, new ShaderSource(new File("shaders/verticalBlurShader.vs")));
			vs4.compileShader(gl);
			System.err.println(vs4.getInfoLog(gl));

			FragmentShader fs4 = new FragmentShader(gl);
			fs4.setShaderSource(gl, new ShaderSource(new File("shaders/verticalBlurShader.fs")));
			fs4.compileShader(gl);
			System.err.println(fs4.getInfoLog(gl));
			
			 prog4 = new ShaderProgram(gl);
				prog4.attachShader(gl, vs4);
				
				
				prog4.attachShader(gl, fs4);
				prog4.linkProgram(gl);
				System.err.println(prog4.getInfoLog(gl));
				
				prog4.activate(gl);
				
				Uniform1i texture7 = prog4.getUniform1i(gl, "sceneTex");
				
				
				texture7.set(gl, 0);
				
				
				prog4.deactivate(gl);
				
				
				
				VertexShader vs5 = new VertexShader(gl);
				vs5.setShaderSource(gl, new ShaderSource(new File("shaders/horizontalBlurShader.vs")));
				vs5.compileShader(gl);
				System.err.println(vs5.getInfoLog(gl));

				FragmentShader fs5 = new FragmentShader(gl);
				fs5.setShaderSource(gl, new ShaderSource(new File("shaders/horizontalBlurShader.fs")));
				fs5.compileShader(gl);
				System.err.println(fs5.getInfoLog(gl));
				
				 prog5 = new ShaderProgram(gl);
					prog5.attachShader(gl, vs5);
					
					
					prog5.attachShader(gl, fs5);
					prog5.linkProgram(gl);
					System.err.println(prog5.getInfoLog(gl));
					
					prog5.activate(gl);
					
					Uniform1i texture8 = prog5.getUniform1i(gl, "sceneTex");
					
					
					texture8.set(gl, 0);
					
					
					prog5.deactivate(gl);
		
			
		/// renderbuffer
		
		Renderbuffer depthRenderbuffer = new Renderbuffer(gl);
		depthRenderbuffer.setStorage(gl, GL2.GL_DEPTH_COMPONENT, 1024, 1024);
		
		
		/////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////
		// create depth texture for fbo
		texture2D = new Texture2D(gl,GL2.GL_TEXTURE_2D);
		
		texture2D.setMagFilter(gl, GL.GL_LINEAR);
		texture2D.setMinFilter(gl, GL.GL_LINEAR);

		texture2D.setWrapS(gl, GL2.GL_CLAMP);
		texture2D.setWrapT(gl, GL2.GL_CLAMP);
		
		texture2D.setStorage(gl, GL2.GL_DEPTH_COMPONENT, GL2.GL_DEPTH_COMPONENT,shadowMapWidth, shadowMapHeight);
		//texture2D.setGenerateMipmap(gl, GL2.GL_TRUE);
		
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
			
			
		// post processing fbo2

			Renderbuffer depthRenderbuffer2 = new Renderbuffer(gl);
			depthRenderbuffer2.setStorage(gl, GL2.GL_DEPTH_COMPONENT, fboBufferWidth, fboBufferHeight);
			
			
			depthMap = new Texture2D(gl, GL2.GL_TEXTURE_2D);
			
			depthMap.setMagFilter(gl, GL.GL_NEAREST);
			depthMap.setMinFilter(gl,  GL2.GL_NEAREST);

			depthMap.setWrapS(gl, GL2.GL_CLAMP_TO_EDGE);
			depthMap.setWrapT(gl, GL2.GL_CLAMP_TO_EDGE);
			
			depthMap.setStorage(gl, GL2.GL_RGBA32F, GL2.GL_RGBA, fboBufferWidth, fboBufferHeight);
			
			
			normalMap = new Texture2D(gl,GL2.GL_TEXTURE_2D);
			
			normalMap.setMagFilter(gl, GL.GL_NEAREST);
			normalMap.setMinFilter(gl, GL.GL_NEAREST);

			normalMap.setWrapS(gl, GL2.GL_CLAMP_TO_EDGE);
			normalMap.setWrapT(gl, GL2.GL_CLAMP_TO_EDGE);
			
			normalMap.setStorage(gl, GL2.GL_RGBA32F, GL2.GL_RGBA, fboBufferWidth, fboBufferHeight);
			
			
			colorMap = new Texture2D(gl,GL2.GL_TEXTURE_2D);
			
			colorMap.setMagFilter(gl, GL.GL_LINEAR);
			colorMap.setMinFilter(gl, GL.GL_LINEAR);

			colorMap.setWrapS(gl, GL2.GL_REPEAT);
			colorMap.setWrapT(gl, GL2.GL_REPEAT);
			
			colorMap.setStorage(gl,GL2.GL_RGBA32F, GL2.GL_RGBA, fboBufferWidth, fboBufferHeight);
			
			
			 fbo2 = new Framebuffer(gl);
			 fbo2.attach(gl, GL2.GL_DEPTH_ATTACHMENT, depthRenderbuffer2);
			 fbo2.attach(gl, GL2.GL_COLOR_ATTACHMENT0, colorMap);
			 fbo2.attach(gl, GL2.GL_COLOR_ATTACHMENT1, depthMap);
			 fbo2.attach(gl, GL2.GL_COLOR_ATTACHMENT2, normalMap);
			 

				System.out.println(fbo2.isComplete(gl));
				System.out.println(fbo2.getStatus(gl));
				
			// super sampling fbo
				ssaaMap = new Texture2D(gl,GL2.GL_TEXTURE_2D);
				
				ssaaMap.setMagFilter(gl, GL.GL_LINEAR);
				ssaaMap.setMinFilter(gl, GL2.GL_LINEAR);
				   

				ssaaMap.setWrapS(gl, GL2.GL_CLAMP_TO_EDGE);
				ssaaMap.setWrapT(gl, GL2.GL_CLAMP_TO_EDGE);
				
				ssaaMap.setStorage(gl,GL2.GL_RGBA32F, GL2.GL_RGBA, fboBufferWidth, fboBufferHeight);
				
				 ssaaFBO = new Framebuffer(gl);
				 ssaaFBO.attach(gl, GL2.GL_COLOR_ATTACHMENT0, ssaaMap);	
			
				 System.out.println(ssaaFBO.isComplete(gl));
					System.out.println(ssaaFBO.getStatus(gl));
			
			// downsampling DOF fbo
					
					downsampledColorMap = new Texture2D(gl,GL2.GL_TEXTURE_2D);
					
					downsampledColorMap.setMagFilter(gl, GL.GL_LINEAR);
					downsampledColorMap.setMinFilter(gl, GL.GL_LINEAR);

					downsampledColorMap.setWrapS(gl, GL2.GL_CLAMP_TO_EDGE);
					downsampledColorMap.setWrapT(gl, GL2.GL_CLAMP_TO_EDGE);
					
					downsampledColorMap.setStorage(gl,GL2.GL_RGBA32F, GL2.GL_RGBA, fboBufferWidth/2, fboBufferHeight/2);
			
			
			 fbo3 = new Framebuffer(gl);
			 fbo3.attach(gl, GL2.GL_COLOR_ATTACHMENT0, downsampledColorMap);
					
			 System.out.println(fbo3.isComplete(gl));
				System.out.println(fbo3.getStatus(gl));
				
				// temp downsample
				tempDownsample = new Texture2D(gl,GL2.GL_TEXTURE_2D);
				
				tempDownsample.setMagFilter(gl, GL.GL_LINEAR);
				tempDownsample.setMinFilter(gl, GL.GL_LINEAR);

				tempDownsample.setWrapS(gl, GL2.GL_CLAMP_TO_EDGE);
				tempDownsample.setWrapT(gl, GL2.GL_CLAMP_TO_EDGE);
				
				tempDownsample.setStorage(gl,GL2.GL_RGBA32F, GL2.GL_RGBA, fboBufferWidth/4, fboBufferHeight/4);
		
		
		tempFBO = new Framebuffer(gl);
		tempFBO.attach(gl, GL2.GL_COLOR_ATTACHMENT0, tempDownsample);
				
		 System.out.println(tempFBO.isComplete(gl));
			System.out.println(tempFBO.getStatus(gl));
				
			// blur fbo	
				blurMap = new Texture2D(gl,GL2.GL_TEXTURE_2D);
				
				blurMap.setMagFilter(gl, GL.GL_LINEAR);
				blurMap.setMinFilter(gl, GL.GL_LINEAR);

				blurMap.setWrapS(gl, GL2.GL_CLAMP_TO_EDGE);
				blurMap.setWrapT(gl, GL2.GL_CLAMP_TO_EDGE);
				
				blurMap.setStorage(gl,GL2.GL_RGBA32F, GL2.GL_RGBA, fboBufferWidth/4, fboBufferHeight/4);
				
				
				blurFBO = new Framebuffer(gl);
				blurFBO.attach(gl, GL2.GL_COLOR_ATTACHMENT0, blurMap);
						
				 System.out.println(blurFBO.isComplete(gl));
					System.out.println(blurFBO.getStatus(gl));
				
		/////// create 
	//	camView = new BasicCamera(new Vector3f(0,1.6f,1.13f), 0.0f , -46.0f, 0.0f);
		//lightView = new BasicCamera(new Vector3f(0.f,3,-0.f), -45.0f , -90.0f, 0.0f);
		camView2 = new BasicCamera2();
		lightView2 = new BasicCamera2();
		lightView2.setLookAt(new Vector3f(0.0f,3,-0.f),  new Vector3f(0, 0.5f, 0), new Vector3f(1,1,0));
		gl.glEnable(GL2.GL_CULL_FACE);
		gl.glEnable(GL2.GL_DEPTH_TEST);
		//loader = new OBJLoader("models" + File.separator + "bunny.obj");
		
		// light & material setup
		float[] lightPosition_	= {0.0f, 1.4f, 0.0f, 1.0f};
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPosition_, 0);
		gl.glEnable(GL2.GL_LIGHT0);
		gl.glEnable(GL2.GL_LIGHTING);
		
		material = new Material();
		material.setAmbient(0.2f, 0.25f, 0.2f, 1.0f);
		material.setDiffuse(0.8f, 1.3f, 0.8f, 1.0f);
		material.setSpecular(1.0f, 1.0f, 1.0f, 1.0f);
		material.setShininess(24.0f);
		
		material2 = new Material();
		material2.setAmbient(0.8f, 0.8f, 0.8f, 1.0f);
		material2.setDiffuse(1.f, 1.f, 1.f, 1.0f);
		material2.setSpecular(0.5f,0.5f,0.5f,0);
		material2.setShininess(104.0f);
		
		material3 = new Material();
		material3.setAmbient(0.7f, 0.7f, 0.7f, 1.0f);
		material3.setDiffuse(1.f, 1.f, 1.f, 1.0f);
		material3.setSpecular(1.0f,1.0f,1.0f,0);
		material3.setShininess(104.0f);
		
		light = new PointLight(GL2.GL_LIGHT0);
		light.setAmbient(0.7f, 0.7f, 0.7f, 1.0f);
		light.setDiffuse(1.0f, 1.0f, 1.0f, 1.0f);
		light.setSpecular(1.0f, 1.0f, 1.0f, 1.0f);
		//Vector3f(0.04f,3,-0.f)
		light.setPosition(0.0f,3,0.f);
		light.setConstantAttenuation(1.0f);
		light.setLinearAttenuation(0.0f);
		light.setQuadraticAttenuation(0.5f);
		material.apply(gl);
		
		//model = new WavefrontModel("models/exd9xl/exd9xl_a.obj");
		//model = new WavefrontModel("models/ltv/ltv.obj");
		//model = new WavefrontModel(gl,"models/dragon_smooth.obj");
		//model = new WavefrontModel(gl,"models/HMMWV.obj");
		model = new WavefrontModel(gl,"models/lucy.obj");
		//model = new WavefrontModel(gl,"models/lost_empire.obj");
		//model = new WavefrontModel(gl,"models/untitled.obj");
		model.construct(gl);
		//"models/bunny.obj");
		
		cementTexture = load(gl,"textures/cement.png");
		spotlightTexture = load(gl,"textures/spotlight.png");
		spotlightTexture.setTexParameterf(gl,GL.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP_TO_EDGE);
		spotlightTexture.setTexParameterf(gl,GL.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP_TO_EDGE);
		metalTexture = load(gl,"textures/metal10.png");
		
		gl.glActiveTexture(GL2.GL_TEXTURE2);
		spotlightTexture.bind(gl);
		
		gl.glActiveTexture(GL2.GL_TEXTURE0);
		
		knot = new PhotekTorusKnot(gl,null);
		engine = new ParticleEngine(gl);
		
		//obj = new OBJLoader("models/dragon_smooth.obj");
		md2 = new MD2Model(gl, "models/tris.md2","models/hueteotl.png");
		long start = System.currentTimeMillis();
		md2.setStartTime(start);
		md22 = new MD2Model(gl, "models/weapon.md2","models/weapon.png");
		md22.setStartTime(start);
		
		 model2  = new MilkshapeModel(gl, "models/ms3d/freebeast/beast.ms3d");
	}
	
	
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		GL2 gl = drawable.getGL().getGL2();
		GLU glu = new GLU();

		if (height <= 0)
			height = 1;
		
		final float h = (float) width / (float) height;
		//final float h = (float) 640 / (float) 360;
		
		gl.glViewport(0, 0, width, height);
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		
		glu.gluPerspective(45.0f, h, 0.5, 100.0);
		
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		float[] pm = new float[16];
		gl.glGetFloatv(GL2.GL_PROJECTION_MATRIX,  pm, 0);
		//System.out.println(new Matrix4f(pm).transpose());
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
		
	
		//gl.glFrontFace(GL2.GL_CW);
		metalTexture.bind(gl);
		material.apply(gl);
		gl.glPushMatrix();		
			gl.glTranslatef(0.0f,0.5f,0.0f);
			gl.glRotatef(currentTime*0.02f, 1.0f, 0.0f, 0.0f);
			gl.glRotatef(currentTime*0.02f, 0.0f, 1.0f, 0.0f);
			//gl.glScalef(0.2f, 0.2f, 0.2f);
			//knot.fill(gl,tangent, false);
		gl.glPopMatrix();
		
		material2.apply(gl);
		
		gl.glFrontFace(GL2.GL_CCW);
		material3.apply(gl);
		cementTexture.bind(gl);
		gl.glBegin(GL2.GL_QUADS);
			gl.glNormal3f(0.0f, 1.0f, 0.0f);
			gl.glTexCoord2f(0.0f, 0.0f);
			gl.glVertex3f(-18.0f, 0.0f, 18.0f);
			
			gl.glTexCoord2f(4.5f, 0.0f);
			gl.glVertex3f( 18.0f, 0.0f, 18.0f);
			
			gl.glTexCoord2f(4.5f, 4.5f);
			gl.glVertex3f( 18.0f, 0.0f,-18.0f);
			
			gl.glTexCoord2f(0.0f, 4.5f);
			gl.glVertex3f(-18.0f, 0.0f,-18.0f);
		gl.glEnd();
		metalTexture.bind(gl);
		material.apply(gl);
		gl.glPushMatrix();
		//drawCube(gl, 0.0f, 0.5f, 0.0f);
		gl.glPopMatrix();
		Random randomGenerator = new Random(1464);
		
		for(int i=0; i < 10; i++) {
		gl.glPushMatrix();
		gl.glTranslatef(randomGenerator.nextFloat()*12-6.f, randomGenerator.nextFloat()*8+0.5f, randomGenerator.nextFloat()*12-6.f);
		gl.glRotatef(randomGenerator.nextFloat()*360,1,0,0);
		gl.glRotatef(randomGenerator.nextFloat()*360,0,1,0);
		float scale= randomGenerator.nextFloat()*0.4f+0.5f;
		gl.glScalef(scale,scale,scale);
		//model.draw(gl);
		drawCube(gl, randomGenerator.nextFloat()*10-5, 0.5f, randomGenerator.nextFloat()*10-5);
		gl.glPopMatrix();
		}
		gl.glPushMatrix();
		gl.glTranslatef(0.5f,3.19f,-1.4f);
		gl.glRotatef(-90,0,1,0);
		//gl.glScalef(0.05f,0.05f,0.05f);
		//gl.glScalef(0.01f,0.01f,0.01f);
		gl.glScalef(1.2f,1.2f,1.2f);
		gl.glEnable(GL2.GL_NORMALIZE);
		//obj.draw(gl);
		//model.draw(gl);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		//gl.glRotatef(0, 0,1,0);
		gl.glTranslatef(-3.6f,1.9f,2.7f);
		gl.glRotatef(90, 0,1,0);
		gl.glRotatef(-90, 1,0,0);
		
		gl.glScalef(0.08f,0.08f,0.08f);
		
		material2.apply(gl);
		gl.glFrontFace(GL2.GL_CW);
		//md2.draw(gl);
		//md22.draw(gl);
		gl.glFrontFace(GL2.GL_CCW);
		
		gl.glPopMatrix();
		gl.glTranslatef(0.0f,0.0f,0.0f);
		gl.glScalef(0.03f,0.03f,0.03f);
		gl.glRotatef(180,0,1,0);
		 model2.draw2(gl);
		
		
	}
	
	public void renderDepthTexture(GL2 gl, UniformMatrix4fv modelMatrix) {
		GLUT glut = new GLUT();
		GLU glu = new GLU();
		
		gl.glPushAttrib(GL2.GL_VIEWPORT_BIT);
		
		

		fbo.bind(gl);
		


		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		

		glu.gluPerspective(90.0f, 1.0f, 0.1, 8.0);

		
		float[] pm = new float[16];
		gl.glGetFloatv(GL2.GL_PROJECTION_MATRIX,  pm, 0);
		lightProjection = new Matrix4f(pm).transpose();

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		gl.glViewport(0, 0, shadowMapWidth, shadowMapHeight);
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		
		 gl.glCullFace(GL2.GL_FRONT);
		 gl.glShadeModel(GL2.GL_FLAT);
		 gl.glColorMask(false, false,false, false);
		
		//lightView.getViewMatrix().apply(gl);
		 lightView2.getViewMatrix().apply(gl);

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
		
		fbo2.bind(gl);
		
		
		
	gl.glDrawBuffer(GL2.GL_COLOR_ATTACHMENT0);
	gl.glClearColor(0.23f,0.23f,0.23f,0.0f);
		
		//gl.glClearColor(0,0,0,0);
		gl.glLoadIdentity();
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT | GL.GL_COLOR_BUFFER_BIT);
		
		gl.glDrawBuffer(GL2.GL_COLOR_ATTACHMENT1);
		gl.glClearColor(-255,-255,-255,0);
		gl.glLoadIdentity();
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT | GL.GL_COLOR_BUFFER_BIT);
		
		gl.glDrawBuffer(GL2.GL_COLOR_ATTACHMENT2);
		gl.glClearColor(255,0,0,0);
		gl.glLoadIdentity();
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT | GL.GL_COLOR_BUFFER_BIT);
		
		
		gl.glDrawBuffers(3,new int[]{ GL2.GL_COLOR_ATTACHMENT0, GL2.GL_COLOR_ATTACHMENT1,GL2.GL_COLOR_ATTACHMENT2 }, 0);
		
		gl.glViewport(0, 0, fboBufferWidth, fboBufferHeight);
		
		//gl.glLoadIdentity();
		//gl.glClear(GL.GL_DEPTH_BUFFER_BIT | GL.GL_COLOR_BUFFER_BIT);
		
		//camView.getViewMatrix().apply(gl);
		long currentTime = startTime - System.currentTimeMillis();
		//currentTime = 6900;
		float radius = 5.0f;//+(float)((1+Math.sin(currentTime*0.00022f))/2*6.5);//0.8+(float)((1+Math.sin(currentTime*0.0003f))/2*0.1);
		float height = 1.2f+(float)(Math.sin(currentTime*0.0002f)*1.1f);
		float up1 = (float)(0.3*Math.sin(currentTime*0.00005f));
		float up2 = (float)(0.3*Math.cos(currentTime*0.00005f));
		
		camView2.setLookAt(new Vector3f((float)(radius*Math.sin(currentTime*0.0004f)),height,(float)(radius*Math.cos(currentTime*0.0004f)))
		.add(new Vector3f(-0.0f,0.1f,0f)), new Vector3f(-0.0f,1.6f,0f), new Vector3f(up1,1,up2));
	camView2.getViewMatrix().apply(gl);

//	System.out.println("view matrix:");
//	System.out.println(camView2.getViewMatrix());

		light.apply(gl);
		
		
		Matrix4f bias = new Matrix4f(0.5f, 0.0f, 0.0f, 0.5f,
				 					 0.0f, 0.5f, 0.0f, 0.5f,
				 					 0.0f, 0.0f, 0.5f, 0.5f,
				 					 0.0f, 0.0f, 0.0f, 1.0f);
		
		//bias = bias.constructIdentityMatrix();
		
		
		//Matrix4f textureMatrix = bias.multiply(lightProjection)
		//	.multiply(lightView.getViewMatrix()).multiply(camView.getInverseViewMatrix());
		
		// TODO: invertieren der view matrix korrekt
		Matrix4f textureMatrix = bias.multiply(lightProjection)
	.multiply(lightView2.getViewMatrix()).multiply(camView2.getInverseViewMatrix());
		
		
		
//		System.out.println("cam view * inv(cam view) = id :");
	//	System.out.println(camView2.getViewMatrix().multiply(camView2.getInverseViewMatrix()));
		
		//Texture2D.enable(gl);
		//texture2D.bind(gl);
		
		prog.activate(gl);
		lightBPV.set(gl, 1, true,textureMatrix.toArray(), 0);
		render(gl);
		prog.deactivate(gl);
		
		fbo2.unbind(gl);
		
		gl.glColor3f(0,1,0);
		gl.glPointSize(3.0f);
		gl.glBegin(GL2.GL_POINTS);
			gl.glVertex3f((float)(3*Math.cos(currentTime*0.0005f)),2,-0.f);
		gl.glEnd();
		
		
		///
	
		
	}
	
	public void renderFinalComposition(GL2 gl){
		
		ssaaFBO.bind(gl);
		gl.glDrawBuffer(GL2.GL_COLOR_ATTACHMENT0);
		
		gl.glViewport(0, 0,  fboBufferWidth, fboBufferHeight);
		
		gl.glLoadIdentity();
		
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT | GL.GL_COLOR_BUFFER_BIT);
		
		colorMap.bind(gl);
		
		
		gl.glMatrixMode (GL2.GL_MODELVIEW); 
	    gl.glPushMatrix (); 
	    gl.glLoadIdentity (); 
	    gl.glMatrixMode (GL2.GL_PROJECTION); 
	    gl.glPushMatrix (); 
	    gl.glLoadIdentity ();
	    
	    gl.glColor3f(1,1,1);
	    gl.glActiveTexture(GL2.GL_TEXTURE0);
	   colorMap.bind(gl);
	   gl.glActiveTexture(GL2.GL_TEXTURE1);
	    depthMap.bind(gl);
	    gl.glActiveTexture(GL2.GL_TEXTURE2);
	    normalMap.bind(gl);
	    
	    prog2.activate(gl);
	    gl.glDepthMask(false);
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
	    gl.glPopMatrix (); 
	    gl.glMatrixMode (GL2.GL_MODELVIEW); 
	    gl.glPopMatrix ();
	    
	    prog2.deactivate(gl);
	  //  gl.glDepthMask(true);
	    //render particles
	    
		gl.glActiveTexture(GL2.GL_TEXTURE1);
	    depthMap.bind(gl);
	    gl.glActiveTexture(GL2.GL_TEXTURE0);
	    gl.glLoadIdentity();
		//gl.glClear(GL.GL_DEPTH_BUFFER_BIT | GL.GL_COLOR_BUFFER_BIT);
	    //gl.glClear(GL.GL_DEPTH_BUFFER_BIT );
		camView2.getViewMatrix().apply(gl);
		long currentTime = startTime - System.currentTimeMillis();
		gl.glTranslatef(0,0.5f,0);
		gl.glRotatef(currentTime*0.02f, 0.0f, 1.0f, 0.0f);
		gl.glRotatef(currentTime*0.02f, 1.0f, 0.0f, 0.0f);
		gl.glRotatef(currentTime*0.02f, 0.0f, 1.0f, 0.0f);
		
	//engine.draw(gl);
		
		gl.glActiveTexture(GL2.GL_TEXTURE1);
		texture2D.bind(gl);
		//texture2D.disable(gl);
		
		gl.glActiveTexture(GL2.GL_TEXTURE2);
		spotlightTexture.bind(gl);
		//spotlightTexture.disable(gl);
		 gl.glActiveTexture(GL2.GL_TEXTURE0);
		 ssaaMap.enable(gl);
		 
		 ///////////// SSAA path
		 ssaaFBO.unbind(gl);
		 
		 // downsample
		 gl.glBindFramebuffer(GL2.GL_READ_FRAMEBUFFER, ssaaFBO.getHandle());
		 gl.glBindFramebuffer(GL2.GL_DRAW_FRAMEBUFFER,  fbo3.getHandle());

		 gl.glBlitFramebuffer(0, 0, fboBufferWidth, fboBufferHeight, 0,  0, fboBufferWidth/2, fboBufferHeight/2, GL.GL_COLOR_BUFFER_BIT,  GL.GL_LINEAR);
		 
		 gl.glBindFramebuffer(GL2.GL_READ_FRAMEBUFFER, fbo3.getHandle());
		 gl.glBindFramebuffer(GL2.GL_DRAW_FRAMEBUFFER,  tempFBO.getHandle());

		 gl.glBlitFramebuffer(0, 0, fboBufferWidth/2, fboBufferHeight/2, 0,  0, fboBufferWidth/4, fboBufferHeight/4, GL.GL_COLOR_BUFFER_BIT,  GL.GL_LINEAR);

		
		 gl.glBindFramebuffer(GL2.GL_READ_FRAMEBUFFER, 0);
		 gl.glBindFramebuffer(GL2.GL_DRAW_FRAMEBUFFER,  0);
		 
		 blurFBO2(gl);
		 
		 gl.glLoadIdentity();
		 
		 gl.glViewport(0, 0,  fboBufferWidth/2, fboBufferHeight/2);
		 
			gl.glClear(GL.GL_DEPTH_BUFFER_BIT | GL.GL_COLOR_BUFFER_BIT);
			
			//ssaaMap.generateMipmap(gl);
			ssaaMap.bind(gl);
			//downsampledColorMap.bind(gl);
			gl.glActiveTexture(GL2.GL_TEXTURE1);
			//depthMap.generateMipmap(gl);
			depthMap.bind(gl);
			gl.glActiveTexture(GL2.GL_TEXTURE2);
			
			tempDownsample.bind(gl);
			//blurMap.bind(gl);
			
			prog3.activate(gl);
			
			gl.glMatrixMode (GL2.GL_MODELVIEW); 
		    gl.glPushMatrix (); 
		    gl.glLoadIdentity (); 
		    gl.glMatrixMode (GL2.GL_PROJECTION); 
		    gl.glPushMatrix (); 
		    gl.glLoadIdentity ();
		    gl.glDepthMask(false);
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
		    gl.glPopMatrix (); 
		    gl.glMatrixMode (GL2.GL_MODELVIEW); 
		    gl.glPopMatrix ();
		    
		    prog3.deactivate(gl);
		    gl.glDepthMask(true);
		    
		    gl.glActiveTexture(GL2.GL_TEXTURE1);
			texture2D.bind(gl);
			
			gl.glActiveTexture(GL2.GL_TEXTURE2);
			spotlightTexture.bind(gl);
			 gl.glActiveTexture(GL2.GL_TEXTURE0);
	}
	
	private void blurFBO2(GL2 gl) {
		
		blurFBO.bind(gl);
		gl.glDrawBuffer(GL2.GL_COLOR_ATTACHMENT0);
		
		gl.glViewport(0, 0,  fboBufferWidth/4, fboBufferHeight/4);
		
		gl.glLoadIdentity();
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT | GL.GL_COLOR_BUFFER_BIT);
		
		gl.glActiveTexture(GL2.GL_TEXTURE0);
		tempDownsample.bind(gl);
		
		prog4.activate(gl);
		// here
		gl.glMatrixMode (GL2.GL_MODELVIEW); 
	    gl.glPushMatrix (); 
	    gl.glLoadIdentity (); 
	    gl.glMatrixMode (GL2.GL_PROJECTION); 
	    gl.glPushMatrix (); 
	    gl.glLoadIdentity ();
	    gl.glDepthMask(false);
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
	    gl.glPopMatrix (); 
	    gl.glMatrixMode (GL2.GL_MODELVIEW); 
	    gl.glPopMatrix ();
		
	    prog4.deactivate(gl);
		tempDownsample.unbind(gl);
		
		blurFBO.unbind(gl);
		
		// second path
		tempFBO.bind(gl);
		gl.glDrawBuffer(GL2.GL_COLOR_ATTACHMENT0);
		
		gl.glViewport(0, 0,  fboBufferWidth/4, fboBufferHeight/4);
		
		gl.glLoadIdentity();
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT | GL.GL_COLOR_BUFFER_BIT);
		
		gl.glActiveTexture(GL2.GL_TEXTURE0);
		blurMap.bind(gl);
		
		prog5.activate(gl);
		// here
		gl.glMatrixMode (GL2.GL_MODELVIEW); 
	    gl.glPushMatrix (); 
	    gl.glLoadIdentity (); 
	    gl.glMatrixMode (GL2.GL_PROJECTION); 
	    gl.glPushMatrix (); 
	    gl.glLoadIdentity ();
	    gl.glDepthMask(false);
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
	    gl.glPopMatrix (); 
	    gl.glMatrixMode (GL2.GL_MODELVIEW); 
	    gl.glPopMatrix ();
		
	    prog5.deactivate(gl);
		blurMap.unbind(gl);
		
		tempFBO.unbind(gl);
		
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
	
		// setze lichtposition hier
long currentTime = (startTime - System.currentTimeMillis())*2+2000;
		

		float radius = 4.5f;//(float)((Math.sin(currentTime*0.0003f))*3.8);
		float height =  13.4f;//(float)(1.8+(Math.sin(currentTime*0.0003f))*0.8);
		float up1 = (float)(0.3*Math.sin(currentTime*0.00005f));
		float up2 = (float)(0.3*Math.cos(currentTime*0.00005f));

		
		//Vector3f eye = new Vector3f((float)(radius*Math.sin(currentTime*0.000001f)),height,(float)(radius*Math.cos(currentTime*0.000001f)));
		Vector3f eye = new Vector3f(-0.2f,3,-4.1f);
		Vector3f center =// new Vector3f(-.3f, 2.7f, 2.4f);
	new Vector3f(0.0f,0.0f,0.0f);
		Vector3f up = new Vector3f(0,1,0);
		
		
		
		lightView2.setLookAt(eye,  center, up);
		light.setPosition(eye.x, eye.y, eye.z);
		
		renderDepthTexture(gl,null);
		gl.glPushAttrib(GL2.GL_VIEWPORT_BIT);
		reshape(drawable, 0,0, 640, 360);
		renderShadowMappingScene(gl);
		gl.glPopAttrib();
		renderFinalComposition(gl);
		
		gl.glPointSize(4.0f);
		gl.glLineWidth(2.0f);
		gl.glColor3f(0,1,0);
		gl.glLoadIdentity();
		camView2.getViewMatrix().apply(gl);
		
		gl.glDisable(GL.GL_TEXTURE_2D);
		gl.glDisable(GL2.GL_LIGHTING);
//		try {
//			Screenshot.writeToTargaFile(new File("/home/trigger/Desktop/image.tga"),640, 360);
//		} catch (GLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		gl.glBegin(GL.GL_POINTS);
//			gl.glVertex3f(eye.x, eye.y, eye.z);
//		gl.glEnd();
//		
//		gl.glBegin(GL.GL_LINES);
//		gl.glVertex3f(eye.x, eye.y, eye.z);
//		gl.glVertex3f(center.x, center.y, center.z);
//	gl.glEnd();
	}
	


}

class TriangleIndex {
	int a, b, c;
	int n1,n2,n3;
	public TriangleIndex(int a, int b, int c, int n1, int n2, int n3) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.n1 = n1;
		this.n2 = n2;
		this.n3 = n3;
	}
}

class OBJLoader {
	
	ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();
	ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
	ArrayList<TriangleIndex> triangles = new ArrayList<TriangleIndex>();
	public OBJLoader(String filename) {
		
		try {
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(
							new File(filename))));
			System.out.println(filename);
			while(bufferedReader.ready()) {
				String line = bufferedReader.readLine();
				
				if(line == null) break;
				
				if(line.startsWith("vn"))
					parseNormalx(line);
				
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
				
				Vector3f n1 = vertices.get(tri.n1);
				Vector3f n2 = vertices.get(tri.n2);
				Vector3f n3 = vertices.get(tri.n3);
				
				
				gl.glNormal3f(n1.x, n1.y, n1.z);
				
				Vector3f norm = a.substract(b).cross(c.substract(b)).normalize().negate();
				gl.glNormal3f(norm.x, norm.y, norm.z);
				
				gl.glVertex3f(a.x, a.y, a.z);
				
				//gl.glNormal3f(n2.x, n2.y, n2.z);
				gl.glVertex3f(b.x, b.y, b.z);
				
				//gl.glNormal3f(n3.x, n3.y, n3.z);
				gl.glVertex3f(c.x, c.y, c.z);
			}
		gl.glEnd();
		
	}
	
	public void parseFace(String line) {
		StringTokenizer vertexData = new StringTokenizer(line, " ");
		
		vertexData.nextToken();
		
		//if(vertexData.countTokens() != 3) {
			System.err.println("malformed line: " + line);
//			return;
//		}
		
		StringTokenizer point1 = new StringTokenizer(vertexData.nextToken(),"/");
		StringTokenizer point2 = new StringTokenizer(vertexData.nextToken(),"/");
		StringTokenizer point3 = new StringTokenizer(vertexData.nextToken(),"/");
		
		Integer p1 = new Integer(Integer.parseInt(point1.nextToken()));
		Integer p2 = new Integer(Integer.parseInt(point2.nextToken()));
		Integer p3 = new Integer(Integer.parseInt(point3.nextToken()));
//		
//		System.err.println(point1.nextToken());
//		point2.nextToken();
//		point3.nextToken();
//		
		Integer n1 = new Integer(Integer.parseInt(point1.nextToken()));
		Integer n2 = new Integer(Integer.parseInt(point2.nextToken()));
		Integer n3 = new Integer(Integer.parseInt(point3.nextToken()));
		System.err.println(n1);
		System.err.println(n2);
		System.err.println(n3);
		
		triangles.add(new TriangleIndex(p1-1, p2-1, p3-1, n1-1, n2-1, n3-1));
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
	
	public void parseNormalx(String line) {
		StringTokenizer vertexData = new StringTokenizer(line, " ");
		
		vertexData.nextToken();
		
		if(vertexData.countTokens() != 3) {
			System.err.println("malformed line: " + line);
			return;
		}
		
		float x = Float.parseFloat(vertexData.nextToken());
		float y = Float.parseFloat(vertexData.nextToken());
		float z = Float.parseFloat(vertexData.nextToken());
		
		normals.add(new Vector3f(x, y, z));
	}
}