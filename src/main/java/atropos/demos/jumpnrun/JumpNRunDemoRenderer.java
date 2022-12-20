package atropos.demos.jumpnrun;

import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.FloatBuffer;

import com.jogamp.opengl.DebugGL2;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.glu.GLU;



import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import atropos.AtroposDefaultRenderer;
import atropos.core.Color;
import atropos.core.fbo.Framebuffer;
import atropos.core.math.Vector2f;
import atropos.core.shader.FragmentShader;
import atropos.core.shader.ShaderProgram;
import atropos.core.shader.ShaderSource;
import atropos.core.shader.VertexShader;
import atropos.core.shader.attrib.VertexAttrib4f;
import atropos.core.shader.uniform.Uniform1f;
import atropos.core.shader.uniform.Uniform1i;
import atropos.core.texture.Texture2D;

public class JumpNRunDemoRenderer extends AtroposDefaultRenderer {

	long startTime;
	Texture rick ,sprites;
	int[][] level;
	Player player = new Player(new Vector2f(8*18,8*23-21));
	Texture2D renderTarget;
	Framebuffer renderTargetFBO;
	ShaderProgram prog;
	Uniform1f time;
	
	@Override
	public void init(GLAutoDrawable drawable) {
		drawable.setGL(new DebugGL2(drawable.getGL().getGL2()));
		GL2 gl = drawable.getGL().getGL2();
		
		gl.setSwapInterval(0);
	
		startTime = System.currentTimeMillis();
		
		level = new int[][] {{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
							 {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
							 {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
							 {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
							 {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
							 {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
							 {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
							 {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
							 {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
							 {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
							 {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
							 {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
							 {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
							 {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
							 {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
							 {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
							 {1,0,0,0,0,0,0,369,370,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
							 {1,0,0,0,0,369,369,370,369,370,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
							 {1,0,0,0,0,0,0,369,370,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
							 {1,0,0,0,0,0,0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
							 {1,0,0,0,0,0,0,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
							 {1,0,0,0,0,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
							 {1,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
							 {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}};
		
		rick = load(gl, "textures/rick/rick1tiles.png");
		sprites = load(gl, "textures/rick/sprites.png");
		setupFBO(gl);
		
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
	
	setupCRTShader(gl);
	}
	
	void setupCRTShader(GL2 gl) {
		VertexShader vs = new VertexShader(gl);
		vs.setShaderSource(gl, new ShaderSource(new File("shaders/crtShader.vs")));
		vs.compileShader(gl);
		System.err.println(vs.getInfoLog(gl));

		FragmentShader fs = new FragmentShader(gl);
		fs.setShaderSource(gl, new ShaderSource(new File("shaders/crtShader.fs")));
		fs.compileShader(gl);
		System.err.println(fs.getInfoLog(gl));
		
	 prog = new ShaderProgram(gl);
		prog.attachShader(gl, vs);
		prog.attachShader(gl, fs);
		prog.linkProgram(gl);
		
prog.activate(gl);
		
		
		Uniform1i uni = prog.getUniform1i(gl, "texCol");
		uni.set(gl, 0);
		 time = prog.getUniform1f(gl,"time");
		prog.deactivate(gl);
	}
	
	FloatBuffer vertexBuffer;
	int vbo=0;
	
	void addVertex(float a, float b, float c) {
		vertexBuffer.put(a);
		vertexBuffer.put(b);
		vertexBuffer.put(c);
	}
	
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		GL2 gl = drawable.getGL().getGL2();
		GLU glu = new GLU();

		if (height <= 0)
			height = 1;
		
		final float h = (float) width / (float) height;
		
		// @see http://www.opengl.org/archives/resources/faq/technical/transformations.
		gl.glViewport(0, 0, width, height);
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		
		gl.glOrtho(0, 256, 200, 0, -1, 1);
		
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glTranslatef (0.375f, 0.375f, 0.0f);
	}
	

	void drawPlayer(GL2 gl, int posx, int posy) {
		
	gl.glDisable(GL2.GL_TEXTURE_2D);
	gl.glBegin(GL2.GL_QUADS);
		
		gl.glVertex2f(posx+0,posy+0);
		
		
		gl.glVertex2f(posx+16,posy+0);
		
		
		gl.glVertex2f(posx+16,posy+16);
		
		
		gl.glVertex2f(posx+0,posy+16);
	gl.glEnd();
	
	gl.glEnable(GL2.GL_TEXTURE_2D);
	}
	
	void drawSprite(GL2 gl, int number, int posx, int posy, boolean lines) {
		
		
		int width= 320;
		int height = 1400;
		float blockWidth = 32;
		float blockHeight = 21;
		
		int xPos = number % 8;
		int yPos = number / 8;
		double xOffset = xPos*(blockWidth/width);//(xPos * 9.0f) / 315.0f;
		double yOffset = yPos*(blockHeight/height);//1.0f-((yPos * 9.0f) / 206.0f);
		
		
		
		
		
		
		
		
		double tileWidth = blockWidth/width;
		double tileHeight = blockHeight/height;
		
		
		gl.glEnable (GL2.GL_BLEND);
		gl.glBlendFunc (GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glColor3f(1,1,1);
		sprites.bind(gl);
		gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2d(xOffset,yOffset);
			gl.glVertex2f(posx+0,posy+0);
			
			gl.glTexCoord2d(xOffset+tileWidth,yOffset);
			gl.glVertex2f(posx+32,posy+0);
			
			gl.glTexCoord2d(xOffset+tileWidth,yOffset+tileHeight);
			gl.glVertex2f(posx+32,posy+21);
			
			gl.glTexCoord2d(xOffset,yOffset +tileHeight);
			gl.glVertex2f(posx+0,posy+21);
		gl.glEnd();
		
		if(!lines) return;
		gl.glLineWidth(0.1f);
		gl.glColor3f(1.0f,0.0f,0.0f);
		gl.glDisable(GL2.GL_TEXTURE_2D);
		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
		gl.glDisable(GL2.GL_BLEND);
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2d(xOffset,yOffset);
		gl.glVertex2f(posx+0,posy+0);
		
		gl.glTexCoord2d(xOffset+tileWidth,yOffset);
		gl.glVertex2f(posx+31,posy+0);
		
		gl.glTexCoord2d(xOffset+tileWidth,yOffset+tileHeight);
		gl.glVertex2f(posx+31,posy+20);
		
		gl.glTexCoord2d(xOffset,yOffset +tileHeight);
		gl.glVertex2f(posx+0,posy+20);
	gl.glEnd();
	
	gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
	gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glDisable (GL2.GL_BLEND);
		gl.glColor3f(1,1,1);
	}
	
	void drawTile(GL2 gl, int number, int posx, int posy) {
		
		
		int width= 315;
		int height = 206;
		int blockWidth = 8;
		int blockHeight = 8;
		
		int xPos = number % 35;
		int yPos = number / 35;
		double xOffset = xPos*(9.0f/315f);//(xPos * 9.0f) / 315.0f;
		double yOffset = yPos*(8.0f/206f);//1.0f-((yPos * 9.0f) / 206.0f);
		
		
		
		//gl.glColor3f(1,0,0);
		
		
		//gl.glDisable(GL2.GL_TEXTURE_2D);
		
		double tileWidth = 8/315f;
		double tileHeight = 8/206f;
		
		
		rick.bind(gl);
		gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2d(xOffset,yOffset);
			gl.glVertex2f(posx+0,posy+0);
			
			gl.glTexCoord2d(xOffset+tileWidth,yOffset);
			gl.glVertex2f(posx+8,posy+0);
			
			gl.glTexCoord2d(xOffset+tileWidth,yOffset+tileHeight);
			gl.glVertex2f(posx+8,posy+8);
			
			gl.glTexCoord2d(xOffset,yOffset +tileHeight);
			gl.glVertex2f(posx+0,posy+8);
		gl.glEnd();
		
		gl.glColor3f(0.0f,1.0f,0.0f);
		gl.glDisable(GL2.GL_TEXTURE_2D);
		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
//		gl.glBegin(GL2.GL_QUADS);
//		gl.glTexCoord2d(xOffset,yOffset);
//		gl.glVertex2f(posx+0,posy+0);
//		
//		gl.glTexCoord2d(xOffset+tileWidth,yOffset);
//		gl.glVertex2f(posx+8,posy+0);
//		
//		gl.glTexCoord2d(xOffset+tileWidth,yOffset+tileHeight);
//		gl.glVertex2f(posx+8,posy+8);
//		
//		gl.glTexCoord2d(xOffset,yOffset +tileHeight);
//		gl.glVertex2f(posx+0,posy+8);
//	gl.glEnd();
		
	gl.glEnable(GL2.GL_TEXTURE_2D);
	gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
		gl.glColor3f(1,1,1);
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

	          //texture.setTexParameteri (gl,GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);

	          // Use the NEAREST minification function when the pixel being
	          // textured maps to an area greater than one texel.

	          //texture.setTexParameteri (gl,GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
	          
	          float[] maxAniso = new float[1];
	  		gl.glGetFloatv(GL2.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, maxAniso,0);

	          
	          texture.setTexParameteri(gl,GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
	  			texture.setTexParameteri(gl,GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
	          
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
	 
	 void drawText(GL2 gl, String text) {
		 int number = 2460;
		 
		 String points = String.format("%06d", number);
		 
		 int pos= 0;
		 for(int i=0; i < points.length(); i++) {
			 drawTile(gl, 48 +(points.charAt(i)-'0'), pos,0);
			 pos+=8;
		 }
		 
		 pos= 6*8+3*8;
		 for(int i=0; i < 6; i++) {
			 drawTile(gl, 1, pos,0);
			 pos+=8;
		 }
		 
		 pos= 6*8+3*8+6*8+2*8;
		 for(int i=0; i < 6; i++) {
			 drawTile(gl, 2, pos,0);
			 pos+=8;
		 }
		 
		 pos= 6*8+3*8+6*8+2*8+6*8+3*8;
		 for(int i=0; i < 6; i++) {
			 drawTile(gl, 3, pos,0);
			 pos+=8;
		 }
		 
		 for(int y=0; y < level.length; y ++)
			 for(int x=0; x < level[y].length; x ++) {
				 if(level[y][x]== 1)
					 drawTile(gl,22*35+10, x*8, y*8+8);
				 else
					 drawTile(gl,level[y][x], x*8, y*8+8);
			 }
	 }
	 
	 void setupFBO(GL2 gl) {
			
			renderTarget = new Texture2D(gl,GL2.GL_TEXTURE_2D);
			
			renderTarget.setMagFilter(gl, GL.GL_LINEAR);
			renderTarget.setMinFilter(gl, GL.GL_LINEAR);

			renderTarget.setWrapS(gl, GL2.GL_CLAMP);
			renderTarget.setWrapT(gl, GL2.GL_CLAMP);
			
			renderTarget.setStorage(gl,GL2.GL_RGBA, GL2.GL_RGBA, 256, 200);
			
			
			renderTargetFBO = new Framebuffer(gl);
			renderTargetFBO.attach(gl, GL2.GL_COLOR_ATTACHMENT0, renderTarget);

			System.out.println(renderTargetFBO.isComplete(gl));
			System.out.println(renderTargetFBO.getStatus(gl));
		}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		
		
		renderTargetFBO.bind(gl);
		gl.glDrawBuffer(GL2.GL_COLOR_ATTACHMENT0);
		gl.glViewport(0, 0,  256, 200);
		gl.glLoadIdentity();
		gl.glTranslatef (0.375f, 0.375f, 0.0f);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		
		// processInput();
		// ProcessControll
		// processMovement
		// processCollision
		// render
		// render OVerlay
		
		long elapsedTime = (System.currentTimeMillis() - startTime);
		gl.glEnable(GL2.GL_TEXTURE_2D);
		rick.bind(gl);

		handleInput();
		drawText(gl, "");
		
		
		//long elapsed =(long)((System.currentTimeMillis() -startTime)*0.006f);  
		
		//drawSprite(gl, 8*12-2+(int)(elapsed%3), (int)playerx-24/2,(int)playery+8-20);
		gl.glTranslatef(0,8,0);
		drawSprite(gl,43, 8*20,8*23-21,false);
		player.draw(gl);
		
		
		//drawMarker(gl,new Vector2f(player.position), new Color(1.0f, 0.0f, 1.0f, 1.0f));

		gl.glColor4f(1.0f,1.0f,1.0f,1.0f);
		renderTargetFBO.unbind(gl);
		
		renderTarget.bind(gl);
		gl.glViewport(0, 0,  640, 360);
		gl.glEnable(GL2.GL_TEXTURE_2D);
	
		gl.glMatrixMode (GL2.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glLoadIdentity (); 
		gl.glMatrixMode (GL2.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity ();
		
		prog.activate(gl);
		
		time.set(gl, elapsedTime);
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
		prog.deactivate(gl);
		
		gl.glMatrixMode (GL2.GL_PROJECTION);
		gl.glPopMatrix();
		
		gl.glMatrixMode (GL2.GL_MODELVIEW);
		gl.glPopMatrix();
		
	}
	
	void drawMarker(GL2 gl, Vector2f position, Color color) {
		gl.glColor4f(color.getColorVector()[0],color.getColorVector()[1],color.getColorVector()[2],color.getColorVector()[3]);
		gl.glDisable(GL2.GL_BLEND);
		gl.glDisable(GL2.GL_TEXTURE_2D);
		gl.glLineWidth(1.0f);
		gl.glBegin(GL2.GL_LINES);
			gl.glVertex2f(position.x-2, position.y);
			gl.glVertex2f(position.x+3, position.y);
			gl.glVertex2f(position.x, position.y-2);
			gl.glVertex2f(position.x, position.y+3);
		gl.glEnd();
		gl.glEnable(GL2.GL_TEXTURE_2D);
	}
	
	class Player {
		
		Vector2f position;
		
		public Player(Vector2f position) {
			this.position = new Vector2f(position);
		}
		
		void draw(GL2 gl) {
			long elapsed =(long)((System.currentTimeMillis() -startTime)*0.006f);  
			
			
			
			Vector2f[] cont = getContactPoints();
			for(int i =0; i < cont.length; i++) {
				//drawMarker(gl,new Vector2f((int)cont[i].x, (int)cont[i].y), new Color(1.0f, 0.0f, 1.0f, 1.0f));
			
			}
			drawSprite(gl, 8*12-2+(int)(elapsed%3), (int)position.x,(int)position.y, false);
		}
		
		Vector2f getPosition() {
			return position;
		}
		
		Vector2f[] getContactPoints() {
			return new Vector2f[]{player.position.add(new Vector2f(0, 0)),
								  player.position.add(new Vector2f(21-1, 0)),
								  player.position.add(new Vector2f(0,21-1)),
								  player.position.add(new Vector2f(21-1,21-1)),
								  player.position.add(new Vector2f(0,8-1)),
								  player.position.add(new Vector2f(0,16-1)),
								  player.position.add(new Vector2f(21-1,8-1)),
								  player.position.add(new Vector2f(21-1,16-1)),
								  player.position.add(new Vector2f(8-1,0)),
								  player.position.add(new Vector2f(16-1,0)),
								  player.position.add(new Vector2f(8-1,21-1)),
								  player.position.add(new Vector2f(16-1,21-1))};
		}
		
		
	}
	
	boolean isSolid(Vector2f[] contactPoints, Vector2f vel) {
		for(int i=0; i < contactPoints.length; i++) {
			if(level[(int)(contactPoints[i].y+vel.y)/8][(int)(contactPoints[i].x+vel.x)/8]!=0)
				return true;
		}
	 return false;
	}
	
	final float STEP_SIZE= 1.5f; 
	
	void handleInput() {
		if(right) {
			
				boolean solid =isSolid(player.getContactPoints(), new Vector2f(STEP_SIZE,0));
			
				if(!solid){ 
					player.getPosition().x+= STEP_SIZE;
		} else {
			player.position.x = (int)(((int)(player.position.x+21+STEP_SIZE))/8)*8-21; 
		}
			//right =false;
		}
		
		if(left) {
			boolean solid =isSolid(player.getContactPoints(), new Vector2f(-STEP_SIZE,0));
			
			if(!solid) {
				player.getPosition().x-= STEP_SIZE;
			} else {
				player.position.x = (int)(((int)(player.position.x-STEP_SIZE))/8+1)*8; 
			}
			
			
			//left =false;
		}
		
		if(jump && !jumping) {
			jumping=true;
			jumpSpeed=6;
			jump= false;
		}
		
		boolean solid2 =isSolid(player.getContactPoints(), new Vector2f(0,1));
		
		if(!jumping && !solid2) {
			jumping =true;
			jumpSpeed = 0;
			jump = false;
		}
	
		
		if(jumping) {
			
			 
			
				boolean solid =isSolid(player.getContactPoints(), new Vector2f(0,-jumpSpeed));
			if(solid)
			{
				if(-jumpSpeed > 0)
				//player.position.y = (int)(((int)(player.position.y+21-jumpSpeed))/8)*8-21;
					player.position.y = (int)(((int)(player.position.y+21-jumpSpeed))/8)*8-21;
				else
					player.position.y = (int)(((int)(player.position.y-jumpSpeed))/8+1)*8;
				jumping= false;
				
			} else {
				player.getPosition().y-=jumpSpeed;
			jumpSpeed -= 0.5f;
			if(jumpSpeed < -8) jumpSpeed = -8;
			}
		}
	}
	float jumpSpeed=0;
	int playerx=8*18;
	int playery=8*23-1;
	boolean right=false, left=false, jump=false, jumping=false;
	
	@Override
	public void keyReleased(KeyEvent arg0) {
		int code =arg0.getKeyCode();
		switch(code) {
		case KeyEvent.VK_RIGHT:
			right = false;
			break;
		
	case KeyEvent.VK_LEFT:
		left = false;
		break;
	case KeyEvent.VK_SPACE:
		jump = false;
		}	
	}
	
	@Override
	public void keyPressed(KeyEvent arg0) {
		

		int code =arg0.getKeyCode();
		switch(code) {
		case KeyEvent.VK_RIGHT:
			right = true;
			break;
		
	case KeyEvent.VK_LEFT:
		left = true;
		break;
	case KeyEvent.VK_SPACE:
		jump = true;
		}	
	}
	}
