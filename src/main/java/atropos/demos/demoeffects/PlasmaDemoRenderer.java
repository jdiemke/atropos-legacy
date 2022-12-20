package atropos.demos.demoeffects;

import ibxm.Channel;

import java.io.File;
import java.io.IOException;

import com.jogamp.opengl.DebugGL2;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import atropos.AtroposDefaultRenderer;
import atropos.core.IBXMPlayer;
import atropos.core.shader.FragmentShader;
import atropos.core.shader.ShaderProgram;
import atropos.core.shader.ShaderSource;
import atropos.core.shader.VertexShader;
import atropos.core.shader.attrib.VertexAttrib4f;
import atropos.core.shader.uniform.Uniform1f;
import atropos.core.shader.uniform.Uniform1i;

public class PlasmaDemoRenderer extends AtroposDefaultRenderer {

	VertexAttrib4f colorVertexAttrib4f;
	Uniform1f time;
	Uniform1i mode;
	Texture rick ,logo;
	ShaderProgram prog;
	IBXMPlayer player;
	long start;
	@Override
	public void init(GLAutoDrawable drawable) {
		drawable.setGL(new DebugGL2(drawable.getGL().getGL2()));
		GL2 gl = drawable.getGL().getGL2();
		
		gl.setSwapInterval(1);
		
		gl.glDisable(GL.GL_DEPTH_TEST);
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glClearColor(0.2f, 0.2f, 0.3f, 1.0f);

//		VertexShader vs = new VertexShader(gl);
//		vs.setShaderSource(gl, new ShaderSource(new File("shaders/plasmaShader.vs")));
//		vs.compileShader(gl);
//		System.err.println(vs.getInfoLog(gl));
//
//		FragmentShader fs = new FragmentShader(gl);
//		fs.setShaderSource(gl, new ShaderSource(new File("shaders/plasmaShader.fs")));
//		fs.compileShader(gl);
//		System.err.println(fs.getInfoLog(gl));
		
		
		VertexShader vs = new VertexShader(gl);
		vs.setShaderSource(gl, new ShaderSource(new File("shaders/spheretracing/revisionDemo.vs")));
		vs.compileShader(gl);
		System.err.println(vs.getInfoLog(gl));

		FragmentShader fs = new FragmentShader(gl);
		fs.setShaderSource(gl, new ShaderSource(new File("shaders/spheretracing/revisionDemo.fs")));
		fs.compileShader(gl);
		System.err.println(fs.getInfoLog(gl));
		
	 prog = new ShaderProgram(gl);
		prog.attachShader(gl, vs);
		prog.attachShader(gl, fs);
		prog.linkProgram(gl);
		System.err.println(prog.getInfoLog(gl));
		prog.activate(gl);
		
		Texture texture = load(gl,"textures/bow.tga");
		Uniform1i uni = prog.getUniform1i(gl, "texCol");
		 time = prog.getUniform1f(gl,"time");
		 mode = prog.getUniform1i(gl, "mode");
		uni.set(gl, 0);
		
		
		//colorVertexAttrib4f = prog.getVertexAttrib4f(gl, "color");
		rick = load(gl, "textures/rick/font.png");
		logo = load(gl, "textures/rick/hlm_matrix.png");
		 player = new IBXMPlayer();
		
		 
		 
		try {
			player.loadModule(new File("music/xm/Dubmood_ft_Zabutom_-_St_style.xm"));
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		
		player.setInterpolation(Channel.LINEAR);
		player.play();
		start =  System.currentTimeMillis();
		
	}
	
	@Override
	public void dispose(GLAutoDrawable drawable) {
		player.stop();
	}
	
	void drawImage(GL2  gl) {
		
		logo.bind(gl);

		gl.glEnable (GL2.GL_BLEND);
		gl.glBlendFunc (GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_COLOR);
		gl.glColor3f(1,1,1);
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2d(0,0);
		gl.glVertex2f(0,0);
		
		gl.glTexCoord2d(1,0);
		gl.glVertex2f(640,0);
		
		gl.glTexCoord2d(1,1);
		gl.glVertex2f(640,253);
		
		gl.glTexCoord2d(0,1);
		gl.glVertex2f(0,253);
	gl.glEnd();
	}
	
	void drawTile(GL2 gl, int number, float posx, float posy, float scale) {
		
		int width= 256;
		int height = 16;
		int blockWidth = 8;
		int blockHeight = 8;
		
		int xPos = number % (width/blockWidth);
		int yPos = number / (width/blockWidth);
		double xOffset = (xPos*(double)blockWidth/width);// +((double)0.25/width);//(xPos * 9.0f) / 315.0f;
		double yOffset = (yPos*(double)blockHeight/height);//-((double)0.25/height);//1.0f-((yPos * 9.0f) / 206.0f);
		
		double tileWidth = (blockWidth)/(double)width;
		double tileHeight =(blockHeight)/(double)height;
		
scale = 2.0f;
		
		rick.bind(gl);
		

	 
		gl.glColor3f(1,1,1);
		
		gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2d(xOffset,yOffset);
			gl.glVertex2f(posx+0,posy+0);
			
			gl.glTexCoord2d(xOffset+tileWidth,yOffset);
			gl.glVertex2f(posx+blockWidth*scale,posy+0);
			
			gl.glTexCoord2d(xOffset+tileWidth,yOffset+tileHeight);
			gl.glVertex2f(posx+blockWidth*scale,posy+blockHeight*scale);
			
			gl.glTexCoord2d(xOffset,yOffset +tileHeight);
			gl.glVertex2f(posx+0,posy+blockHeight*scale);
		gl.glEnd();
		
		//gl.glColor3f(0.0f,1.0f,0.0f);
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
	//	gl.glColor3f(1,1,1);
	
	}
	
	String [][] text = {{"HATRED",
					     "DEPROTECTED ON 17-04-2007",
			             "",
			             "STAR WARS EMPIRE AT WAR:",
			             "FORCES OF CORRUPTION % LUCASARTS"},
			             
			             {"CRACKTRO CREDITS:",
			              
			              "",
			            	 "CODE:...................TRIGGER",
						  "GFX:........................H2O",
				         },
			             
			             {"WE GREET RESPECTABLE GROUPS LIKE:",
						     "",
				             "GENESIS - RELOADED - DEVIANCE",
				             "HOODLUM - RAZOR1911 - IMMERSION"
				             }
				         
	, {
	     "STAY TUNED FOR MORE",
         "QUALITY RELEASES!",
         }};
	
	
	double CosineInterpolate(
			   double start,double end,
			   double time)
			{
			   if(time < start) return 0.0;
			   else if(time > end) return 1.0;
			   else return (1-Math.cos(((time-start)/(start - end))*Math.PI))/2;
			}
	
	void textWriter(GL2 gl) {
		gl.glEnable (GL2.GL_BLEND);
		gl.glBlendFunc (GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		
		int elapsedMillis =(int) (System.currentTimeMillis() - start);
		int time = elapsedMillis % (12000*text.length);
		int localTime = time % 12000;
		
		String[] page = text[((int)(time/12000))%text.length];
		
		int posy = 360/2-(int)(page.length/(float)2*8*2);
		int pos=0;
		for(int column=0; column < page.length; column++) {
			 int width = page[column].length() *8*2;
			pos=640/2-(width/2) +(int)((640)*(1-CosineInterpolate(0+column*300, 2000+column*300, localTime)))
			- (int)((640)*(CosineInterpolate(0+( column)*300+(12000-2000-300*(page.length-1)), 2000+(column)*300+(12000-2000-300*(page.length-1)), localTime)));
		for(int i=0; i < page[column].length(); i++) {
	
			
			 drawTile(gl,(int)(page[column].charAt(i)-(int)' '),(int)( pos),(int)(posy+8*2*column), 2.0f);
			
			 pos+=8*2;
		}
		}
		
		gl.glDisable(GL2.GL_BLEND);
	}
	
	 void drawText(GL2 gl, String text,int posx, int posy) {
		
			gl.glEnable (GL2.GL_BLEND);
			gl.glBlendFunc (GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
			
		 int pos= posx;
		 System.out.println("---");
		 float elapsed = (System.currentTimeMillis() -start) * 0.003f*2;
		 
			
			
			
		
		 for(int i=0; i < text.length(); i++) {
			 drawTile(gl,(int)(text.charAt(i)-(int)' '),(int)( pos),(int)(posy+(float)(20*Math.cos(pos*0.01f+elapsed))), 1.0f);
			
			 pos+=8*2;
		 }
		 gl.glDisable(GL2.GL_BLEND);
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

	          texture.setTexParameteri (gl,GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);

	          // Use the NEAREST minification function when the pixel being
	          // textured maps to an area greater than one texel.

	          texture.setTexParameteri (gl,GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
	          texture.setTexParameterf(gl,GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
	          texture.setTexParameterf(gl,GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
	        //  texture.setTexParameteri(gl,GL2.GL_GENERATE_MIPMAP, GL.GL_TRUE);
	          
	      }
	      catch (Exception e)
	      {
	          System.out.println ("error loading texture from "+filename);
	      }

	      return texture;
	   }

	 
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glClearColor(0, 0, 0, 1);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		
gl.glLoadIdentity();

float elapsed = (System.currentTimeMillis() -start) * 0.0001f*2;



		
		//gl.glTranslatef(0.0f, 0.0f, -1.35f);
	
		
	gl.glDisable(GL2.GL_BLEND);
	
	gl.glMatrixMode(GL2.GL_PROJECTION);
	gl.glPushMatrix();
	gl.glLoadIdentity();
	gl.glOrtho(0, 640, 360, 0, -1, 1);
		
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glTranslatef (0.375f, 0.375f, 0.0f);
		int myMode = ((int)(elapsed*1.5f))%14;
		prog.activate(gl);
		mode.set(gl, myMode);
		time.set(gl, elapsed);

		gl.glBegin(GL2.GL_QUADS);

		gl.glVertex2f(0,50);


		gl.glVertex2f(640,50);

		gl.glVertex2f(640,360-50);

		gl.glVertex2f(0,360-50);
		gl.glEnd();
		prog.deactivate(gl);

		

	
		//drawImage(gl);
		//drawText(gl, "BELLI BACK IN TOWN! WATCH OUT!", 75,175+130);
		//drawText(gl, "BELLI BACK IN TOWN! WATCH OUT!", 75,175+8*2+130);
		textWriter(gl);
		
		
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		

		
	}

}