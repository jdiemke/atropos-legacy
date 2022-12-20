package atropos.demos.robotsoccer;

import atropos.AtroposDefaultRenderer;
import atropos.core.fbo.Framebuffer;
import atropos.core.math.Vector2d;
import atropos.core.math.Vector2f;
import atropos.core.shader.FragmentShader;
import atropos.core.shader.ShaderProgram;
import atropos.core.shader.ShaderSource;
import atropos.core.shader.VertexShader;
import atropos.core.shader.uniform.Uniform1f;
import atropos.core.shader.uniform.Uniform2f;
import atropos.core.texture.Texture2D;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.DebugGL2;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.awt.TextRenderer;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.util.Vector;

public class HarmonicPathPlanningDemoRenderer extends AtroposDefaultRenderer {

	ShaderProgram obstacleRegionShader;
	ShaderProgram laplaceSolverShader, displayShader;
	Texture2D obstacleRegionTexture, solution2,solution1;
	Framebuffer obstacleRegionFBO,solutionFBO1,solutionFBO2;
	
	Uniform2f dimension ,targetslot;
	Uniform1f time, radiusSlot;
	Uniform2f dimensionSolver;
	int vbo=0;
	int obstacleTextureWidth = 70;//107;
	int obstacleTextureHeight = 40;// 70;
	int iterations = 1000;
	float simulationElapsed;
	
	Vector2d playerPos = new Vector2d(-30.0f, 20.0f);
	Vector2d target = new Vector2d(29.0f, 13.0f);
	boolean updateTarget = false;
	
	FloatBuffer vertexBuffer;
	
	final float FIELD_MIN_X =	-52.5f;
	final float FIELD_MAX_X =	 52.5f;
	final float FIELD_MIN_Y =	-34.0f;
	final float FIELD_MAX_Y =	 34.0f;
	final float GOAL_MIN_Y 	=	 -7.01f;
	final float GOAL_MAX_Y 	=	  7.01f;
	final float BOUNDARY_MIN_Y =	-39.0f;
	final float BOUNDARY_MAX_Y =	 39.0f;
	final float BOUNDARY_MIN_X =	-57.5f;
	final float BOUNDARY_MAX_X =	 57.5f;
	final float PENALTY_MIN_X =	-36.0f;
	final float PENALTY_MAX_X	= 36.0f;
	final float PENALTY_MIN_Y=	-20.16f;
	final float PENALTY_MAX_Y=	 20.16f;
	
	@Override
	public void init(GLAutoDrawable drawable) {
		drawable.setGL(new DebugGL2(drawable.getGL().getGL2()));
		GL2 gl = drawable.getGL().getGL2();
		
		gl.setSwapInterval(0);
		
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glClearColor(0.2f, 0.2f, 0.3f, 1.0f);

		setupObstacleRegionShader(gl);
		setupLaplaceSolverShader(gl);
		
		setupObstacleRegionFBO(gl);
		setupLaplaceSolverFBOs(gl);
		
		setupDisplayShader(gl);
		
		vertexBuffer = Buffers.newDirectFloatBuffer(6*3);
		
		addVertex(-1.0f,-1.0f,-1.0f);
		addVertex( 1.0f,-1.0f,-1.0f);
		addVertex(-1.0f, 1.0f,-1.0f);
		addVertex(  1.0f,-1.0f,-1.0f);
		addVertex(  1.0f, 1.0f,-1.0f);
		addVertex( -1.0f, 1.0f,-1.0f);
		vertexBuffer.rewind();
	
		// new
		int[] handle = new int[1];
		gl.glGenBuffers(1, handle, 0);
		 vbo = handle[0];
		
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, 6*4 , vertexBuffer, GL.GL_STATIC_DRAW);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER,0);
		
		//gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo);
	gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
	gl.glVertexPointer(3, GL2.GL_FLOAT,0, vertexBuffer);
	gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo);
	
	Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
	}
	

	private void setupDisplayShader(GL2 gl) {
		
		VertexShader vs = new VertexShader(gl);
		vs.setShaderSource(gl, new ShaderSource(new File("shaders/harmonic/displayData.vs")));
		vs.compileShader(gl);
		System.err.println(vs.getInfoLog(gl));

		FragmentShader fs = new FragmentShader(gl);
		fs.setShaderSource(gl, new ShaderSource(new File("shaders/harmonic/displayData.fs")));
		fs.compileShader(gl);
		System.err.println(fs.getInfoLog(gl));
		
		displayShader = new ShaderProgram(gl);
		displayShader.attachShader(gl, vs);
		displayShader.attachShader(gl, fs);
		displayShader.linkProgram(gl);
		System.err.println(displayShader.getInfoLog(gl));
		
		displayShader.activate(gl);
		
		 displayShader.deactivate(gl);
	}


	void addVertex(float a, float b, float c) {
		vertexBuffer.put(a);
		vertexBuffer.put(b);
		vertexBuffer.put(c);
	}
	
	void setupObstacleRegionFBO(GL2 gl) {
		
		obstacleRegionTexture = new Texture2D(gl,GL2.GL_TEXTURE_2D);
		
		obstacleRegionTexture.setMagFilter(gl, GL.GL_NEAREST);
		obstacleRegionTexture.setMinFilter(gl, GL.GL_NEAREST);

		obstacleRegionTexture.setWrapS(gl, GL2.GL_REPEAT);
		obstacleRegionTexture.setWrapT(gl, GL2.GL_REPEAT);
		
		obstacleRegionTexture.setStorage(gl,GL2.GL_RGBA32F, GL2.GL_RGBA, obstacleTextureWidth, obstacleTextureHeight);
		
		
		obstacleRegionFBO = new Framebuffer(gl);
		obstacleRegionFBO.attach(gl, GL2.GL_COLOR_ATTACHMENT0, obstacleRegionTexture);

		System.out.println(obstacleRegionFBO.isComplete(gl));
		System.out.println(obstacleRegionFBO.getStatus(gl));
	}
	
	
	FloatBuffer buffer = Buffers.newDirectFloatBuffer(obstacleTextureHeight*obstacleTextureWidth*4);
	FloatBuffer buffer2 = Buffers.newDirectFloatBuffer(obstacleTextureHeight*obstacleTextureWidth*4);
	
	DoubleBuffer dBuffer = Buffers.newDirectDoubleBuffer(obstacleTextureHeight*obstacleTextureWidth*8);
	private void readTextureData(GL2 gl) {
		
		
		solution1.bind(gl);
		
	
		
				//FloatBuffer.allocate(obstacleTextureHeight*obstacleTextureHeight);
		
		gl.glGetTexImage(GL2.GL_TEXTURE_2D, 0,GL2.GL_RED, GL2.GL_FLOAT,buffer );
		gl.glGetTexImage(GL2.GL_TEXTURE_2D, 0,GL2.GL_GREEN, GL2.GL_FLOAT,buffer2 );
		buffer.rewind();
		buffer2.rewind();
		
		for(int i=0; i < obstacleTextureHeight*obstacleTextureWidth; i++) {
			double fp1 = buffer.get(i);
			double  fp2 = buffer2.get(i);
			
			dBuffer.put(fp1 + fp2);
			//System.out.println("fp1: "+ fp1);
			//System.out.println("fp2: "+ fp2);
		}
		dBuffer.rewind();

		

	
	computePath2(dBuffer, playerPos.x,playerPos.y);
	computePath(dBuffer, playerPos.x,playerPos.y);

	}
	
	int mousex, mousey;
	public void mouseClicked(MouseEvent arg0) {
		
		mousex = arg0.getX();
		 mousey = arg0.getY();
		
		updateTarget = true;
	
	}
	
	void drawPath(GL2 gl) {
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(0.0, 1.0, 0.0, 1.0, -1.0, 1.0);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPointSize(4.0f);
		gl.glColor3f(1,0,0);
		gl.glBegin(GL2.GL_POINTS);
			for(int i=0; i <path2.size(); i++) {
				float x= (float)path2.get(i).x/obstacleTextureWidth+0.5f/obstacleTextureWidth;
				float y =(float)path2.get(i).y/obstacleTextureHeight+0.5f/obstacleTextureHeight;
				gl.glVertex2f(x,y);
			}
		gl.glEnd();
	}
	
	void drawPath2(GL2 gl) {
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(0.0, 1.0, 0.0, 1.0, -1.0, 1.0);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPointSize(4.0f);
		gl.glLineWidth(2.0f);
		

		// TextRenderer renderer = new TextRenderer(new Font("Arial", Font.BOLD, 12),true,false);
		
//		  renderer.beginRendering(640, 360);
//		    // optionally set the color
//		    renderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);
//		 
//		    renderer.draw("start", -40+(int)((path.get(0).x/ 107.0f+0.5f)*640), (int)((path.get(0).y/ 70.0f+0.5f)*360)+10);
//		    renderer.draw("goal", +10+(int)((path.get(path.size()-1).x/ 107.0f+0.5f)*640), (int)((path.get(path.size()-1).y/ 70.0f+0.5f)*360)+10);
//		    // ... more draw commands, color changes, etc.
//		    renderer.endRendering();
	

		    gl.glColor3f(1,1,0.2f);
		gl.glBegin(GL2.GL_LINE_STRIP);
			for(int i=0; i <path.size(); i++) {
				float x= (float)path.get(i).x / 107.0f+0.5f;
				float y =(float)path.get(i).y/ 70.0f+0.5f;
				gl.glVertex2f(x,y);
			}
		gl.glEnd();
	}
	
	
	boolean showInfo = false;
	boolean showConfigurationSpace = false;
	boolean showWorkspace = true;
	boolean showGradientDescent = false;
	boolean showLowestNeighbour = false;
	boolean showPotentialField = false;
	boolean printScreen = false;
	boolean showDebug = true;
	float securityRadius = 0.0f;
	
	@Override
	public void keyPressed(KeyEvent arg0) {
	
		int code =  arg0.getKeyCode();
		
		switch(code) {
			case KeyEvent.VK_F1: 
				showInfo = !showInfo;
				if(showInfo)
					showDebug = false;
				break;
			case KeyEvent.VK_F3: 
				showConfigurationSpace = !showConfigurationSpace;
				break;
			case KeyEvent.VK_F4: 
				showPotentialField = !showPotentialField;
				break;
			case KeyEvent.VK_F2: 
				showWorkspace = !showWorkspace;
				break;
			case KeyEvent.VK_F5: 
				showGradientDescent = !showGradientDescent;
				break;
			case KeyEvent.VK_F6: 
				showLowestNeighbour = !showLowestNeighbour;
				break;
			case KeyEvent.VK_F11: 
				showDebug = !showDebug;
				if(showDebug)
					showInfo = false;
				break;
				
			case KeyEvent.VK_PLUS: 
				securityRadius += 0.5f;
				break;
				
			case KeyEvent.VK_MINUS: 
				if(securityRadius>0.0f) securityRadius -= 0.5f;
				break;
			case KeyEvent.VK_F12:
				printScreen = true;
				break;
		}
	}
	
	void printDebug(GL2 gl) {
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(0, 640,0,360, -1.0, 1.0);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	gl.glDisable(GL2.GL_DEPTH_TEST);
	gl.glEnable(GL2.GL_BLEND);
	gl.glBlendFunc (GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
	gl.glDisable(GL.GL_TEXTURE_2D);
	gl.glColor4f(0,0,0,0.7f);
	    gl.glBegin (GL2.GL_QUADS); 
    
    	gl.glVertex3i (0, 0+360-12*3, 0);
    	
    	gl.glVertex3i (640, 0+360-12*3, 0); 
    	
    	gl.glVertex3i (640, 360, 0);
    
    	gl.glVertex3i (0, 360, 0); 
    	gl.glEnd ();
    	gl.glDisable(GL2.GL_BLEND);
		
		 TextRenderer renderer = new TextRenderer(new Font("monospaced", Font.PLAIN, 12),true,false);
		 renderer.setSmoothing(false); 
		 renderer.beginRendering(640, 360);
		  
		    // optionally set the color
		    renderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		 
		    DecimalFormat newFormat = new DecimalFormat("#.##");
		    double twoDecimal =  Double.valueOf(newFormat.format(securityRadius));
		    double twoDecimal1 =  Double.valueOf(newFormat.format(value));
		    
		    renderer.draw("Path Length: " + (path.size()-1),12, 360 -12*2);
		    renderer.draw("Iterations: " + iterations*2,12*12, 360 -12*2);
		    renderer.draw("Clearance: " + twoDecimal,12*23, 360 -12*2);
		    renderer.draw("Computation Time (ms): " + twoDecimal1,12*33, 360 -12*2);
		   

		    // ... more draw commands, color changes, etc.
		    renderer.endRendering();
		   
	}
	
	void printInfo(GL2 gl) {
		
		gl.glDisable(GL2.GL_DEPTH_TEST);
		
	    
	    gl.glMatrixMode (GL2.GL_PROJECTION);  
	    gl.glLoadIdentity ();
	    gl.glMatrixMode (GL2.GL_MODELVIEW); 
	    gl.glLoadIdentity (); 
	    
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc (GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glDisable(GL2.GL_TEXTURE_2D);
		gl.glDisable(GL2.GL_DEPTH_TEST);
		gl.glColor4f(0.0f,0.0f,0.0f,0.70f);
	    gl.glBegin (GL2.GL_QUADS); 
    
    	gl.glVertex3i (-1, -1, -1);
    	
    	gl.glVertex3i (1, -1, -1); 
    	
    	gl.glVertex3i (1, 1, -1);
    
    	gl.glVertex3i (-1, 1, -1); 
    	gl.glEnd ();
    	gl.glDisable(GL2.GL_BLEND);
		
		 TextRenderer renderer = new TextRenderer(new Font("monospaced", Font.PLAIN, 12),true,false);
		 renderer.setSmoothing(false); 
		 renderer.beginRendering(640, 360);
		  
		    // optionally set the color
		    renderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		 
		    
		    renderer.draw("User Interface Help",12, 360-12*2);
		    renderer.draw("-------------------",12, 360-12*3);
		    renderer.draw("F1  - Switch On/Off Help",12, 360-12*5);
		    renderer.draw("F2  - Switch On/Off Workspace",12, 360-12*6);
		    renderer.draw("F3  - Switch On/Off Configuration Space",12, 360-12*7);
		    renderer.draw("F4  - Switch On/Off Harmonic Potential Field",12, 360-12*8);
		    renderer.draw("F5  - Switch On/Off Gradient Descent Path",12, 360-12*9);
		    renderer.draw("F6  - Switch On/Off Lowest Neighbour Path",12, 360-12*10);
		    renderer.draw("F11 - Switch On/Off Debug Information",12, 360-12*11);
		    renderer.draw("+   - Increase Security Radius",12, 360-12*13);
		    renderer.draw("-   - Decrease Security Radius",12, 360-12*14);
		    renderer.draw("LMB - Select A New Goal Configuration",12, 360-12*16);

		    // ... more draw commands, color changes, etc.
		    renderer.endRendering();
		   
	}
	
	Vector<Vector2d> path;
	Vector<Vector2d> path2;
	
	class vec2 {
		int x;
		int y;
		
		public vec2(int x, int y) {
			this.x=x; this.y=y;
		}
	}
	
	vec2 Field2Array(double x, double y) {
		// umrechnen in array position
		int xpos = (int)((x + 53.5f)/107.0*obstacleTextureWidth);
		int ypos = (int)((y + 35.0f)/70.0*obstacleTextureHeight);
		return new vec2(xpos,ypos);
	}
	
	// x,y sind spielfeld position
	void computePath2(DoubleBuffer buffer,double x, double y) {
		path = new Vector<Vector2d>();
		
		vec2 pos = Field2Array(x,y);
		
		double value = getValue(buffer, pos.x, pos.y);
		
		Vector2d position = new Vector2d(x,y);
		
		
		
		double oldvalue;
		
		path.add(new Vector2d(position));
		
		while(value > 0.0f) {
			oldvalue = value;
			
			Vector2d grad = computeGradient(buffer, pos.x, pos.y).normalize().negate();
			
		//	System.out.println("gradient("+pos.x+", " + pos.y+" :"+ grad);
			if(Double.isNaN(grad.x) || Double.isNaN(grad.y)) return;
			grad = grad.multiply(0.5f);
			if(grad.length()<=0.0f)  return;
			
			position = position.add(grad);
			path.add(new Vector2d(position));
			
			 pos = Field2Array(position.x,position.y);
			 
			 if(pos.x < 1|| pos.y < 1) return;
			value = getValue(buffer, pos.x, pos.y);
			
			//System.out.println("position x:"+ position.x);
			//System.out.println("position y:"+ position.y);
		
			
		}
		// added for reverse pathplanning
		//Vector2f target = new Vector2f(playerPos);
		
		Vector2d vector = target.substract(position);
		int length = (int)(vector.length()/0.5f);
		for(int i=0; i< length; i++)path.add(position.add(vector.normalize().multiply(0.5f)));
		path.add(target);
		System.out.println("path: " + path.size());
	}
	
	void computePath(DoubleBuffer buffer, double x, double y) {
		path2 = new Vector<Vector2d>();
		
		vec2 pos = Field2Array(x,y);
		
		double currentValue = getValue(buffer, pos.x, pos.y);
		
		//int offset = x + y * obstacleTextureWidth;
		//float currentValue = buffer.get(offset);
		
		//path.add(new Vector2f(x,y));
		double oldValue;
		path2.add(new Vector2d(pos.x,pos.y));
		int xx = pos.x;
		int yy=pos.y;
		while(currentValue > 0.0f) {
			 
			 oldValue= currentValue;
			
			//System.out.println("current: " + currentValue);
			int tempx=0;
			int tempy=0;
			if(getValue(buffer,xx-1,yy) < currentValue) {
				tempx=xx-1;
				tempy=yy;
				currentValue =getValue(buffer,tempx,tempy);
				
			}
			if(getValue(buffer,xx-1,yy+1) < currentValue) {
				tempx=xx-1;
				tempy=yy+1;
				currentValue =getValue(buffer,tempx,tempy);
				
			}
			if(getValue(buffer,xx-1,yy-1) < currentValue) {
				tempx=xx-1;
				tempy=yy-1;
				currentValue =getValue(buffer,tempx,tempy);
				
			}
			
			if(getValue(buffer,xx+1,yy) < currentValue) {
				tempx=xx+1;
				tempy=yy;
				currentValue =getValue(buffer,tempx,tempy);
				
			}
			if(getValue(buffer,xx+1,yy-1) < currentValue) {
				tempx=xx+1;
				tempy=yy-1;
				currentValue =getValue(buffer,tempx,tempy);
				
			}
			if(getValue(buffer,xx+1,yy+1) < currentValue) {
				tempx=xx+1;
				tempy=yy+1;
				currentValue =getValue(buffer,tempx,tempy);
				
			}
			if(getValue(buffer,xx,yy-1) < currentValue) {
				tempx=xx;
				tempy=yy-1;
				currentValue =getValue(buffer,tempx,tempy);
			
			}
			
			if(getValue(buffer,xx,yy+1) < currentValue) {
				tempx=xx;
				tempy=yy+1;
				currentValue =getValue(buffer,tempx,tempy);
				
			}
			if(oldValue == currentValue) {
				System.err.println("local minima!!!");
				break;
			}
			xx=tempx;
			yy=tempy;
			path2.add(new Vector2d(xx,yy));
			
		}
		//System.out.println("path: " + path2.size());
	}
	
	double getValue(DoubleBuffer buffer, int x, int y) {
		int offset = x + y * obstacleTextureWidth;
		return buffer.get(offset);
	}
	
	Vector2d computeGradient(DoubleBuffer buffer, int x, int y) {
		
		double xx = (getValue(buffer,x+1,y) - getValue(buffer,x-1,y))/2.0f;
		double yy = (getValue(buffer,x,y+1) - getValue(buffer,x,y-1))/2.0f;
		
		return new Vector2d(xx, yy);
	}
	

	
	void setupLaplaceSolverFBOs(GL2 gl) {
		
		// FBO1
		 solution1 = new Texture2D(gl,GL2.GL_TEXTURE_2D);
		
		solution1 = new Texture2D(gl,GL2.GL_TEXTURE_2D);
		
		solution1.setMagFilter(gl, GL.GL_NEAREST);
		solution1.setMinFilter(gl, GL.GL_NEAREST);

		solution1.setWrapS(gl, GL2.GL_REPEAT);
		solution1.setWrapT(gl, GL2.GL_REPEAT);
		
		solution1.setStorage(gl,GL2.GL_RGBA32F, GL2.GL_RGBA, obstacleTextureWidth, obstacleTextureHeight);
		
		
		solutionFBO1 = new Framebuffer(gl);
		solutionFBO1.attach(gl, GL2.GL_COLOR_ATTACHMENT0, solution1);

		System.out.println(solutionFBO1.isComplete(gl));
		System.out.println(solutionFBO1.getStatus(gl));
		
		// FBO2
		 solution2 = new Texture2D(gl,GL2.GL_TEXTURE_2D);
		
		solution2 = new Texture2D(gl,GL2.GL_TEXTURE_2D);
		
		solution2.setMagFilter(gl, GL.GL_NEAREST);
		solution2.setMinFilter(gl, GL.GL_NEAREST);

		solution2.setWrapS(gl, GL2.GL_REPEAT);
		solution2.setWrapT(gl, GL2.GL_REPEAT);
		
		solution2.setStorage(gl,GL2.GL_RGBA32F, GL2.GL_RGBA, obstacleTextureWidth, obstacleTextureHeight);
		
		
		solutionFBO2 = new Framebuffer(gl);
		solutionFBO2.attach(gl, GL2.GL_COLOR_ATTACHMENT0, solution2);

		System.out.println(solutionFBO2.isComplete(gl));
		System.out.println(solutionFBO2.getStatus(gl));
	}
	
	
	
	void setupObstacleRegionShader(GL2 gl) {
		VertexShader vs = new VertexShader(gl);
		vs.setShaderSource(gl, new ShaderSource(new File("shaders/harmonic/configurationSpace.vs")));
		vs.compileShader(gl);
		System.err.println(vs.getInfoLog(gl));

		FragmentShader fs = new FragmentShader(gl);
		fs.setShaderSource(gl, new ShaderSource(new File("shaders/harmonic/configurationSpace.fs")));
		fs.compileShader(gl);
		System.err.println(fs.getInfoLog(gl));
		
		obstacleRegionShader = new ShaderProgram(gl);
		obstacleRegionShader.attachShader(gl, vs);
		obstacleRegionShader.attachShader(gl, fs);
		obstacleRegionShader.linkProgram(gl);
		System.err.println(obstacleRegionShader.getInfoLog(gl));
		
		obstacleRegionShader.activate(gl);
		 dimension = obstacleRegionShader.getUniform2f(gl, "discretizationResolution");
		 time = obstacleRegionShader.getUniform1f(gl, "time");
		 targetslot = obstacleRegionShader.getUniform2f(gl, "targetPosition");
		 radiusSlot = obstacleRegionShader.getUniform1f(gl, "clearanceZone");
		obstacleRegionShader.deactivate(gl);
	}
	
	void setupLaplaceSolverShader(GL2 gl) {
		VertexShader vs = new VertexShader(gl);
		vs.setShaderSource(gl, new ShaderSource(new File("shaders/harmonic/laplaceSolver.vs")));
		vs.compileShader(gl);
		System.err.println(vs.getInfoLog(gl));

		FragmentShader fs = new FragmentShader(gl);
		fs.setShaderSource(gl, new ShaderSource(new File("shaders/harmonic/laplaceSolver.fs")));
		fs.compileShader(gl);
		System.err.println(fs.getInfoLog(gl));
		
		laplaceSolverShader = new ShaderProgram(gl);
		laplaceSolverShader.attachShader(gl, vs);
		laplaceSolverShader.attachShader(gl, fs);
		laplaceSolverShader.linkProgram(gl);
		System.err.println(laplaceSolverShader.getInfoLog(gl));
		
		laplaceSolverShader.activate(gl);
		 dimensionSolver = laplaceSolverShader.getUniform2f(gl, "discretizationResolution");
		 
		laplaceSolverShader.deactivate(gl);
	}
	
	long start = System.currentTimeMillis();
	
	
	void computeObstacleRegion(GL2 gl) {
		
		
	obstacleRegionFBO.bind(gl);
	gl.glDrawBuffer(GL2.GL_COLOR_ATTACHMENT0);
		gl.glViewport(0, 0,  obstacleTextureWidth, obstacleTextureHeight);
		
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		
		obstacleRegionShader.activate(gl);
		dimension.set(gl, obstacleTextureWidth, obstacleTextureHeight);
		time.set(gl, simulationElapsed);
		
		targetslot.set(gl, (float)target.x, (float)target.y);
	//	targetslot.set(gl, playerPos.x, playerPos.y);
		radiusSlot.set(gl, securityRadius);
		
		
		gl.glDrawArrays(GL2.GL_TRIANGLES,0, 6);
	    
	    obstacleRegionShader.deactivate(gl);
	    
		obstacleRegionFBO.unbind(gl);
	}
	
	private void computeLaplaceSolution(GL2 gl) {
		
		//copy DB to first texture
		 gl.glBindFramebuffer(GL2.GL_READ_FRAMEBUFFER, obstacleRegionFBO.getHandle());
		 gl.glBindFramebuffer(GL2.GL_DRAW_FRAMEBUFFER, solutionFBO1.getHandle());

		 gl.glBlitFramebuffer(0, 0, obstacleTextureWidth, obstacleTextureHeight, 0,  0, obstacleTextureWidth, obstacleTextureHeight, GL.GL_COLOR_BUFFER_BIT,  GL.GL_LINEAR);

		
		 gl.glBindFramebuffer(GL2.GL_READ_FRAMEBUFFER, 0);
		 gl.glBindFramebuffer(GL2.GL_DRAW_FRAMEBUFFER,  0);
		 
		// setup done here
		// now solve
		 
		 solutionFBO2.bind(gl);
		 gl.glDrawBuffer(GL2.GL_COLOR_ATTACHMENT0);
		gl.glViewport(0, 0,  obstacleTextureWidth, obstacleTextureHeight);
		
			
			//obstacleRegionShader.activate(gl);
			//dimension.set(gl, obstacleTextureWidth, obstacleTextureHeight);
		laplaceSolverShader.activate(gl);
		dimensionSolver.set(gl, obstacleTextureWidth,obstacleTextureHeight);
		solution1.bind(gl);
		solutionFBO2.bind(gl);
		 for(int i=0; i < iterations; i++) { // in wirklichkeit mal 2 weil 2 iterationen je schleife
			 
			 solutionFBO2.attach(gl, GL2.GL_COLOR_ATTACHMENT0, solution2);
			 solution1.bind(gl);	 
			 gl.glDrawArrays(GL2.GL_TRIANGLES,0, 6);
		
	    	 solutionFBO2.attach(gl, GL2.GL_COLOR_ATTACHMENT0, solution1);
	 		 solution2.bind(gl);
	 		 gl.glDrawArrays(GL2.GL_TRIANGLES,0, 6);
	 	}
		
		laplaceSolverShader.deactivate(gl);
		solutionFBO2.unbind(gl);
	}
	
	// x: -53,5 .. 53,5
	// y: -35 .. 35
	
	
	void drawGrass(GL2 gl) {
		
		gl.glPushMatrix();
		gl.glColor3f(0.3f * 0.8f, 0.4f * 0.8f, 0.3f * 0.8f);

		float diff = FIELD_MAX_X - FIELD_MIN_X;
		int stripes = 7;
		float width = diff / (stripes * 2);

		float size = width;
		for (int i = 0 - 5; i < stripes + 5; i++)
			drawThickLine(gl, new Vector2f(FIELD_MIN_X + size * 2 * i, FIELD_MAX_Y + 40), new Vector2f(FIELD_MIN_X + size * 2 * i,
					FIELD_MIN_Y - 40), size);

		gl.glPopMatrix();
	}
	
	void drawPlayfield(GL2 gl) {
		gl.glPushMatrix();
		gl.glColor3f(0.9f, 0.9f, 0.9f);

		// playfield borders
		drawThickLine(gl,new Vector2f(FIELD_MIN_X, FIELD_MAX_Y), new Vector2f(FIELD_MAX_X, FIELD_MAX_Y), 1.0f);

		drawThickLine(gl, new Vector2f(FIELD_MIN_X, FIELD_MIN_Y),  new Vector2f(FIELD_MAX_X, FIELD_MIN_Y), 1.0f);

		drawThickLine(gl, new Vector2f(FIELD_MIN_X, FIELD_MIN_Y),  new Vector2f(FIELD_MIN_X, FIELD_MAX_Y), 1.0f);

		drawThickLine(gl, new Vector2f(FIELD_MAX_X, FIELD_MIN_Y),  new Vector2f(FIELD_MAX_X, FIELD_MAX_Y), 1.0f);

		// mittellinie
		drawThickLine(gl, new Vector2f(0.0f, FIELD_MIN_Y),  new Vector2f(0.0f, FIELD_MAX_Y), 1.0f);

		// penalty left
		drawThickLine(gl, new Vector2f(FIELD_MIN_X, PENALTY_MIN_Y),  new Vector2f(PENALTY_MIN_X, PENALTY_MIN_Y), 1.0f);

		drawThickLine(gl, new Vector2f(PENALTY_MIN_X, PENALTY_MIN_Y),  new Vector2f(PENALTY_MIN_X, PENALTY_MAX_Y), 1.0f);

		drawThickLine(gl, new Vector2f(PENALTY_MIN_X, PENALTY_MAX_Y),  new Vector2f(FIELD_MIN_X, PENALTY_MAX_Y), 1.0f);

		// penalty right
		drawThickLine(gl, new Vector2f(FIELD_MAX_X, PENALTY_MIN_Y),  new Vector2f(PENALTY_MAX_X, PENALTY_MIN_Y), 1.0f);

		drawThickLine(gl, new Vector2f(PENALTY_MAX_X, PENALTY_MIN_Y),  new Vector2f(PENALTY_MAX_X, PENALTY_MAX_Y), 1.0f);

		drawThickLine(gl, new Vector2f(PENALTY_MAX_X, PENALTY_MAX_Y),  new Vector2f(FIELD_MAX_X, PENALTY_MAX_Y), 1.0f);

		// penaltiy  ???

		drawThickLine(gl, new Vector2f(FIELD_MAX_X, PENALTY_MIN_Y + 11.0f),  new Vector2f(PENALTY_MAX_X + 11.0f, PENALTY_MIN_Y + 11.0f),
				1.0f);

		drawThickLine(gl, new Vector2f(PENALTY_MAX_X + 11.0f, PENALTY_MIN_Y + 11.0f),  new Vector2f(PENALTY_MAX_X + 11.0f,PENALTY_MAX_Y - 11.0f), 1.0f);

		drawThickLine(gl, new Vector2f(PENALTY_MAX_X + 11.0f, PENALTY_MAX_Y - 11.0f),  new Vector2f(FIELD_MAX_X, PENALTY_MAX_Y - 11.0f),1.0f);

		// second pen ??

		drawThickLine(gl, new Vector2f(FIELD_MIN_X, PENALTY_MIN_Y + 11.0f),  new Vector2f(PENALTY_MIN_X - 11.0f, PENALTY_MIN_Y + 11.0f),
				1.0f);

		drawThickLine(gl, new Vector2f(PENALTY_MIN_X - 11.0f, PENALTY_MIN_Y + 11.0f),  new Vector2f(PENALTY_MIN_X - 11.0f,
				PENALTY_MAX_Y - 11.0f), 1.0f);

		drawThickLine(gl, new Vector2f(PENALTY_MIN_X - 11.0f, PENALTY_MAX_Y - 11.0f),  new Vector2f(FIELD_MIN_X, PENALTY_MAX_Y - 11.0f),
				1.0f);

		for (int a = -53 + 4; a <= 53 - 4; a += 2) {
			float f = (float) Math.toRadians(a);
			float f2 = (float) Math.toRadians(a + 2);
			//glVertex3f(FIELD_MIN_X + 11.0 + cos(f) * 9.15f, sin(f) * 9.15f, 0.0f);
			drawThickLine(gl, new Vector2f(FIELD_MIN_X + 11.0f + (float)Math.cos(f) * 9.15f, (float)Math.sin(f) * 9.15f),  new Vector2f(FIELD_MIN_X + 11.0f + (float)Math.cos(f2)
					* 9.15f, (float)Math.sin(f2) * 9.15f), 1.0f);

		}

		for (int a = 127 + 4; a <= 233 - 4; a += 2) {
			float f = (float) Math.toRadians(a);
			float f2 = (float) Math.toRadians(a + 2);
			drawThickLine(gl,new Vector2f(FIELD_MAX_X - 11.0f + (float)Math.cos(f) * 9.15f, (float)Math.sin(f) * 9.15f), new Vector2f(FIELD_MAX_X - 11.0f + (float)Math.cos(f2)
					* 9.15f, (float)Math.sin(f2) * 9.15f), 1.0f);

		}

		for (int a = 0; a < 360; a += 5) {
			float f = (float) Math.toRadians(a);
			float f2 = (float) Math.toRadians(a + 5);

			drawThickLine(gl,new Vector2f((float)Math.cos(f) * 9.15f, (float)Math.sin(f) * 9.15f), new Vector2f((float)Math.cos(f2) * 9.15f, (float)Math.sin(f2) * 9.15f), 1.0f);

		}

		gl.glPopMatrix();

	}
	
	void drawGoal(GL2 gl) {
		gl.glPushMatrix();

		gl.glColor4f(0, 0, 0, 1.0f);

		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(FIELD_MIN_X, GOAL_MIN_Y, 0.0f);
		gl.glVertex3f(FIELD_MIN_X, GOAL_MAX_Y, 0.0f);
		gl.glVertex3f(FIELD_MIN_X - 3, GOAL_MAX_Y, 0.0f);
		gl.glVertex3f(FIELD_MIN_X - 3, GOAL_MIN_Y, 0.0f);

		gl.glVertex3f(FIELD_MAX_X, GOAL_MIN_Y, 0.0f);
		gl.glVertex3f(FIELD_MAX_X + 3, GOAL_MIN_Y, 0.0f);
		gl.glVertex3f(FIELD_MAX_X + 3, GOAL_MAX_Y, 0.0f);
		gl.glVertex3f(FIELD_MAX_X, GOAL_MAX_Y, 0.0f);
		gl.glEnd();

		gl.glPopMatrix();
	}
	
	void drawGoalPosition(GL2 gl,Vector2f position) {
		
		Vector2f end = position.add(new Vector2f(0.0f, 3.5f));
		Vector2f l1 = end.add(new Vector2f(4.0f, -1.0f));
		Vector2f l2 = l1.add(new Vector2f(-4.0f, -1.0f));
		
		gl.glColor4f(0, 1, 0, 1.0f);
		gl.glBegin(GL2.GL_TRIANGLES);
			gl.glVertex2f(l2.x, l2.y);
			gl.glVertex2f(l1.x, l1.y);
			gl.glVertex2f(end.x, end.y);
		gl.glEnd();
		gl.glColor4f(0, 0, 0, 1.0f);
		drawThickLine(gl, position, end,0.5f);
		drawThickLine(gl, end, l1,0.5f);
		drawThickLine(gl, l1, l2,0.5f);
	}
	
	void drawField(GL2 gl) {
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(-53.5f, 53.5f,-35.0f, 35.0f, -1.0, 1.0);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glDisable(GL2.GL_DEPTH_TEST);
		drawGrass(gl);
		drawPlayfield(gl);
		drawGoal(gl);
		
//		float dist = sdCircle(pos-vec2(0.0,0.5+10*sin(time)), 0.3);
//		dist = min(dist, sdCircle(pos-vec2(45.0,20.5), 0.3));
//		dist = min(dist, sdCircle(pos-vec2(-10.0,2.5+20), 0.3));
//		dist = min(dist, sdCircle(pos-vec2(25.0+10*sin(time),5.5), 0.3));
//		dist = min(dist, sdCircle(pos-vec2(-35.0,-10.5), 0.3));
		
	}
	
	void drawAgents(GL2 gl) {
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(-53.5f, 53.5f,-35.0f, 35.0f, -1.0, 1.0);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glDisable(GL2.GL_DEPTH_TEST);
		
		float time = simulationElapsed;
		drawAgent(gl, new Vector2f(45.0f,20.5f), 1.0f, 1.0f, 0.0f);
		drawAgent(gl, new Vector2f(-10.0f,2.5f+20f), 1.0f, 1.0f, 0.0f);
		drawAgent(gl, new Vector2f(25.0f+10f*(float)Math.sin(time),5.5f),1.0f, 1.0f, 0.0f);
		drawAgent(gl, new Vector2f(-30.0f,-10.5f+10f*(float)Math.sin(time)), 1.0f, 1.0f, 0.0f);
		drawAgent(gl, new Vector2f(0.0f+10f*(float)Math.cos(time),0.5f+10f*(float)Math.sin(time)),  1.0f, 1.0f, 0.0f);
		
	
		drawAgent(gl, new Vector2f(-30.0f, 0.5f), 1.0f, 1.0f, 0.0f);
		drawAgent(gl, new Vector2f(-40.0f,-10.5f), 1.0f, 1.0f, 0.0f);
		drawAgent(gl, new Vector2f(-20.0f,-20.5f), 1.0f, 1.0f, 0.0f);
		drawAgent(gl, new Vector2f(-10.0f,-30.5f), 1.0f, 1.0f, 0.0f);
		drawAgent(gl, new Vector2f(  0.0f, 40.5f), 1.0f, 1.0f, 0.0f);
		drawAgent(gl, new Vector2f(10.0f, 20.5f), 1.0f, 1.0f, 0.0f);
		
		drawAgent(gl, new Vector2f( 50.0f, 0.5f), 0.0f, 1.0f, 0.0f);
		drawAgent(gl, new Vector2f( 45.0f,-10.5f), 0.0f, 1.0f, 0.0f);
		drawAgent(gl, new Vector2f(42.0f,  14.5f), 0.0f, 1.0f, 0.0f);
		drawAgent(gl, new Vector2f(40.0f,  20.5f), 0.0f, 1.0f, 0.0f);
		drawAgent(gl, new Vector2f(38.0f,  -30.5f), 0.0f, 1.0f, 0.0f);
		
		
		drawAgent(gl, new Vector2f( 27.0f,-30.5f), 0.0f, 1.0f, 0.0f);
		drawAgent(gl, new Vector2f(24.0f,  19.5f), 0.0f, 1.0f, 0.0f);
		drawAgent(gl, new Vector2f(20.0f,  10.5f), 0.0f, 1.0f, 0.0f);
		drawAgent(gl, new Vector2f(23.0f,  -13.5f), 0.0f, 1.0f, 0.0f);
		
		drawAgent(gl, new Vector2f(1.0f,   13.5f), 0.0f, 1.0f, 0.0f);
		drawAgent(gl, new Vector2f(-20.0f,  -5.5f), 0.0f, 1.0f, 0.0f);
//		drawAgent(gl, new Vector2f(-10.0f,-30.5f), 0.0f, 1.0f, 0.0f);
//		drawAgent(gl, new Vector2f(  0.0f, 40.5f), 0.0f, 1.0f, 0.0f);
//		drawAgent(gl, new Vector2f(10.0f, 20.5f), 0.0f, 1.0f, 0.0f);
//		drawAgent(gl, new Vector2f(-30.0f, 0.5f), 0.0f, 1.0f, 0.0f);
//		drawAgent(gl, new Vector2f(-40.0f,-10.5f), 0.0f, 1.0f, 0.0f);
//		drawAgent(gl, new Vector2f(-20.0f,-20.5f), 0.0f, 1.0f, 0.0f);
//		drawAgent(gl, new Vector2f(-10.0f,-30.5f), 0.0f, 1.0f, 0.0f);
//		drawAgent(gl, new Vector2f(  0.0f, 40.5f), 0.0f, 1.0f, 0.0f);
		
		
		// own player
		drawAgent(gl, new Vector2f((float)playerPos.x, (float)playerPos.y), 0.9f, 0.5f, 0.1f);
	}
	
	void drawFlag(GL2 gl) {
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(-53.5f, 53.5f,-35.0f, 35.0f, -1.0, 1.0);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		drawGoalPosition(gl,new Vector2f((float)target.x,(float)target.y));
	}
	

	void drawAgent(GL2 gl, Vector2f position, float r, float g, float b) {
		gl.glPushMatrix();
	gl.glLineWidth(1.3f);
gl.glDisable(GL2.GL_DEPTH_TEST);
	float margin;
	
		margin = 0.7f;
	

	float radius;
	
		radius = 0.3f;

	gl.glTranslatef(position.x, position.y, 0.0f);
	
	gl.glBegin(GL2.GL_TRIANGLE_FAN);
	gl.glColor4f(r, g, b, 1.0f);

	gl.glVertex3f(0, 0, 0);
	for (int i = 0; i <= 50; i++) {
		float xx = (radius + margin) * (float)Math.sin((2 * Math.PI) / 50 * i);
		float yy = (radius + margin) * (float)Math.cos((2 * Math.PI) / 50 * i);

		gl.glVertex3f(xx, yy, 0);
	}
	gl.glEnd();
	gl.glDisable(GL2.GL_BLEND);

	// draw player size outline
	gl.glBegin(GL2.GL_LINE_LOOP);
	gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
	for (int i = 0; i <= 50; i++) {
		float xx = (radius) * (float)Math.sin((2 * Math.PI) / 50 * i);
		float yy = (radius) * (float)Math.cos((2 * Math.PI) / 50 * i);
		gl.glVertex3f(xx, yy, 0);
	}
	gl.glEnd();

	// draw player size + kickable margin outline
	gl.glBegin(GL2.GL_LINE_LOOP);
	gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
	for (int i = 0; i <= 50; i++) {
		float xx = (radius + margin) * (float)Math.sin((2 * Math.PI) / 50 * i);
		float yy = (radius + margin) * (float)Math.cos((2 * Math.PI) / 50 * i);
		gl.glVertex3f(xx, yy, 0);
	}
	gl.glEnd();

	
gl.glPopMatrix();


}

	
	void drawThickLine(GL2 gl,Vector2f start, Vector2f end, float width) {

		// get line direction
		Vector2f direction = start.substract(end);
		direction = direction.normalize();
		start = start.add(direction.multiply(width * 0.5f));
		end = end.substract( direction.multiply(width * 0.5f));

		// get orthogonal vector to line direction
		Vector2f orthogonal = new Vector2f(direction.y, -direction.x);

		// construct thick line
		float scale = width * 0.5f;
		Vector2f a = start.add(orthogonal.multiply(scale));
		Vector2f b = end.add(orthogonal.multiply(scale));
		Vector2f c = end.substract(orthogonal.multiply(scale));
		Vector2f d = start.substract(orthogonal.multiply(scale));

		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(a.x, a.y, 0.0f);
		gl.glVertex3f(b.x, b.y, 0.0f);
		gl.glVertex3f(c.x, c.y, 0.0f);
		gl.glVertex3f(d.x, d.y, 0.0f);
		gl.glEnd();
	}
	
	
	
	int min = 10000;
	int max = 0;
	float value = 0;
	
	void drawPotential(GL2 gl) {
		gl.glMatrixMode (GL2.GL_MODELVIEW); 
		gl.glLoadIdentity (); 
    
		gl.glMatrixMode (GL2.GL_PROJECTION);  
		gl.glLoadIdentity ();
		
		gl.glEnable(GL2.GL_TEXTURE_2D);
		solution2.bind(gl);
		
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc (GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
	//	gl.glBlendFunc (GL.GL_ZERO,GL.GL_SRC_COLOR);
		gl.glColor4f(1.0f,1.0f,1.0f,0.5f);
		
		//gl.glColor4f(1.0f,1.0f,1.0f,0.5f);
		displayShader.activate(gl);
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
		 displayShader.deactivate(gl);
		gl.glDisable(GL2.GL_BLEND);	
		gl.glColor4f(1.0f,1.0f,1.0f,1.5f);gl.glDisable(GL2.GL_TEXTURE_2D);
	}
	
	void drawCellDecomposition(GL2 gl) {
		gl.glMatrixMode (GL2.GL_MODELVIEW); 
		gl.glLoadIdentity (); 
    
		gl.glMatrixMode (GL2.GL_PROJECTION);  
		gl.glLoadIdentity ();
		
		gl.glEnable(GL2.GL_TEXTURE_2D);
		obstacleRegionTexture.bind(gl);
		
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc (GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
	//	gl.glBlendFunc (GL.GL_ZERO,GL.GL_SRC_COLOR);
		gl.glColor4f(1.0f,1.0f,1.0f,0.5f);
		
		//gl.glColor4f(1.0f,1.0f,1.0f,0.5f);
		displayShader.activate(gl);
		
		
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
		 displayShader.deactivate(gl);
		gl.glDisable(GL2.GL_BLEND);	gl.glDisable(GL2.GL_TEXTURE_2D);
	}
	
//	void inversePath() {
//		
//		Vector<Vector2f> reversePath = new Vector<Vector2f>();
//		Vector<Vector2f> reversePath2 = new Vector<Vector2f>();
//		
//		for(int i = 0; i < path.size(); i++)
//			reversePath.add(path.get(path.size()-1-i));
//		
//		for(int i = 0; i < path2.size(); i++)
//			reversePath2.add(path2.get(path2.size()-1-i));
//		
//		path = reversePath;
//		path2 = reversePath2;
//	}
	
	
	long simStart = System.currentTimeMillis();
	int screenshotCounter = 0;

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		
		if(updateTarget) {
			
			
			target.x =(mousex/(float)drawable.getSurfaceWidth()-0.5f)*107.0f;
			target.y =-(mousey/(float)drawable.getSurfaceHeight()-0.5f)* 70.0f;
			updateTarget = !updateTarget;
		}
		
		
		simulationElapsed = (System.currentTimeMillis() -start)*0.0002f;
		computeObstacleRegion(gl);
		
		long startTime = System.currentTimeMillis();
		computeLaplaceSolution(gl);
		long endTime = System.currentTimeMillis()-startTime;
		
		readTextureData(gl);
		
		
		min  = (int) Math.min(min,  endTime);
		max  = (int) Math.max(max,  endTime);
	//	System.out.println("min: " +min);
		//System.out.println("max: " +max);
	
		value = (value +endTime)/2.0f;
		System.out.println("value: " +value);
		gl.glViewport(0, 0,  drawable.getSurfaceWidth()
				,drawable.getSurfaceHeight());
		gl.glColor3f(1.0f,1.0f,1.0f);
	
		//solution1.bind(gl);
		gl.glClearColor(0.3f, 0.4f, 0.3f, 1.0f);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		
		 gl.glDisable(GL2.GL_DEPTH_TEST);
		
	//	inversePath();
		 
		    long countTime =  System.currentTimeMillis();
		    long elapsed =(countTime -simStart);
		    
		    if(elapsed >= 50) {
		  	  int simSteps = (int)(elapsed / 50);
		  	  simStart = countTime;
		  	  
		  	  for(int i = 0; i< simSteps; i++) {
		  		  if(path.size()> 1) {
		  			  playerPos = new Vector2d(path.get(1));
		  			  path.remove(0);
		  		  }
		  	  }
		    }
		 
		if(showWorkspace) drawField(gl);
		if(showPotentialField) drawPotential(gl);
		if(showConfigurationSpace) drawCellDecomposition(gl);//
		if(showGradientDescent) drawPath2(gl);
		if(showLowestNeighbour) drawPath(gl);
		
		drawAgents(gl);
		drawFlag(gl);
    
		if(showInfo) printInfo(gl);
		if(showDebug) printDebug(gl);

		/*
		if(printScreen) {
			printScreen = false;
			BufferedImage tScreenshot = Screenshot.readToBufferedImage(0,0, drawable.getSurfaceWidth(), drawable.getSurfaceHeight(), false);
			File tScreenCaptureImageFile = new File("screenshot"+ screenshotCounter +".png");
			screenshotCounter++;
			try {

				ImageIO.write(tScreenshot, "png", tScreenCaptureImageFile);
			} catch (IOException e) {} 
		}*/
    
    gl.glEnable(GL2.GL_DEPTH_TEST);
    
//    	if(System.currentTimeMillis() - countTime > 900) {
//    		if(path.size()> 1)
//    		playerPos = new Vector2f(path.get(1));
//    	}
    
//    if(System.currentTimeMillis() - countTime > 10) {
//    	countTime = System.currentTimeMillis();
//    	
//		if(path.size()> 1)
//		playerPos = new Vector2d(path.get(1));
//	}
   

		  
  }
	
}
    		
    
	


