package atropos.demos.potentialfield;

import java.io.File;

import com.jogamp.opengl.DebugGL2;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import atropos.AtroposDefaultRenderer;
import atropos.core.math.Vector2f;
import atropos.core.shader.FragmentShader;
import atropos.core.shader.ShaderProgram;
import atropos.core.shader.ShaderSource;
import atropos.core.shader.VertexShader;
import atropos.core.shader.attrib.VertexAttrib4f;
import atropos.core.shader.uniform.Uniform1f;
import atropos.core.shader.uniform.Uniform1i;

class HarmonicPotential {
	
	float[] potentialfield;
	int width;
	int height;
	
	public HarmonicPotential(int width, int height) {
		potentialfield = new float[width*height];
		this.width = width;
		this.height = height;
	}
	
	public void set(float[] potentialfield) {
		this.potentialfield = potentialfield.clone();
	}
	
	public float getValue(int x, int y) {
		if(x > 0 && x < width && y > 0 && y < height)
			return potentialfield[x + y * width];
		else return 1.0f;
	}
	
	public Vector2f getGradient(int x, int y) {
		if(this.getValue(x, y) == 1.0f)
			return new Vector2f(0, 0).normalize().multiply(0.2f*0.5f);
		
		if(this.getValue(x, y) == 0.0f)
			return new Vector2f(0, 0).normalize().multiply(0.2f*0.5f);
		
		float value1 = this.getValue(x-1, y);
		float value2 = this.getValue(x+1, y);
		float value3 = this.getValue(x, y+1);
		float value4 = this.getValue(x, y-1);
		
		return new Vector2f(value2 - value1, value4 - value3).normalize().negate().multiply(0.2f*0.5f);
	}
	
	public void setValue(int x, int y, float value) {
		
		
		if(x > 0 && x < width && y > 0 && y < height)
			potentialfield[x + y * width] = value;
		
	}
	
	public int getWidht(){
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public HarmonicPotential getCopy() {
		HarmonicPotential temp = new HarmonicPotential(this.width, this.height);
		temp.set(this.potentialfield);
		return temp;
	}
	
	public void iteration() {
		
		HarmonicPotential copy = this.getCopy();
		
		float min = 1.0f;
		
		for(int y =0; y <  copy.getHeight(); y++)
			for(int x =0; x <  copy.getWidht(); x++) {
			
				if(this.getValue(x, y) == 1.0f) continue;
				if(this.getValue(x, y) == 0.0f) continue;
				
				float value1 = copy.getValue(x-1, y);
				float value2 = copy.getValue(x+1, y);
				float value3 = copy.getValue(x, y-1);
				float value4 = copy.getValue(x, y+1);
				
				float value = (value1 + value2 + value3 + value4)/4;
				min = Math.min(min, value);
				this.setValue(x, y, value);
			}			
		
		System.out.println("minimum: " + min);
	}
	
}

public class HarmonicPotentialfieldDemoRenderer extends AtroposDefaultRenderer {

	VertexAttrib4f colorVertexAttrib4f;
	Uniform1f time;
	Uniform1i mode;
	
	HarmonicPotential potential;
	@Override
	public void init(GLAutoDrawable drawable) {
		drawable.setGL(new DebugGL2(drawable.getGL().getGL2()));
		GL2 gl = drawable.getGL().getGL2();
		
		gl.setSwapInterval(1);
		
		potential = new HarmonicPotential(14, 11);
		potential.set(new float[]{1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,
								  1.0f,0.5f,0.5f,0.5f,1.0f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,1.0f,
								  1.0f,0.5f,0.0f,0.5f,1.0f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.0f,0.5f,1.0f,
								  1.0f,0.5f,0.5f,0.5f,1.0f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,1.0f,
								  1.0f,0.5f,0.5f,0.5f,1.0f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,1.0f,
								  1.0f,0.5f,0.5f,0.5f,1.0f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,1.0f,
								  1.0f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,1.0f,
								  1.0f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,1.0f,
								  1.0f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.0f,0.5f,1.0f,
								  1.0f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,0.5f,1.0f,
								  1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f});
		
		
		for(int i=0; i < 4000; i++)
			potential.iteration();
		
		
		
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
		
		gl.glTranslatef( -potential.getWidht()*0.2f/2.0f, potential.getHeight()*0.2f/2.0f, -4.35f);
		
		float width = 0.2f;
		float height = 0.2f;
		
		for(int y =0; y <  potential.getHeight(); y++)
			for(int x =0; x <  potential.getWidht(); x++) {
			
				float value = potential.getValue(x, y);
				
				if(value == 1.0)
					gl.glColor3f(1.0f, 0,0);
				else
					gl.glColor3f(value, value, value);
				
				gl.glBegin(GL2.GL_QUADS);
					gl.glVertex2f(x*width,y*(-height));
					gl.glVertex2f(x*width+width,y*(-height));
					gl.glVertex2f(x*width+width,y*(-height)-height);
					gl.glVertex2f(x*width,y*(-height)-height);
				gl.glEnd();
				gl.glColor3f(0,0,1);
				Vector2f grad = potential.getGradient(x, y);
				gl.glBegin(GL2.GL_LINES);
					gl.glVertex2f(x*width+width/2,y*(-height)-(height/2));
					gl.glVertex2f(x*width+width/2+grad.x,y*(-height)-(height/2)+grad.y);
				gl.glEnd();
			
				
			}
		
		
	}

}