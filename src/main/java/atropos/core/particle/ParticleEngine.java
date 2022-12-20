package atropos.core.particle;

import java.io.File;
import java.util.Vector;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import atropos.core.math.Vector3f;
import atropos.core.shader.FragmentShader;
import atropos.core.shader.ShaderProgram;
import atropos.core.shader.ShaderSource;
import atropos.core.shader.VertexShader;
import atropos.core.shader.uniform.Uniform1i;
import atropos.core.texture.Texture2D;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class ParticleEngine {
	
	Texture texture;
	ShaderProgram prog;
	Vector<Vector3f> particles = new Vector<Vector3f>();
	
	public ParticleEngine(GL2 gl) {
		texture = load(gl,"textures/Blender3D_CuttingThroughSteel_Smoke.jpg"); 
		
	///// shadow map
		VertexShader vs = new VertexShader(gl);
		vs.setShaderSource(gl, new ShaderSource(new File("shaders/SoftParticleShader.vs")));
		vs.compileShader(gl);
		System.err.println(vs.getInfoLog(gl));

		FragmentShader fs = new FragmentShader(gl);
		fs.setShaderSource(gl, new ShaderSource(new File("shaders/softParticleShader.fs")));
		fs.compileShader(gl);
		System.err.println(fs.getInfoLog(gl));
		
		 prog = new ShaderProgram(gl);
		prog.attachShader(gl, vs);
		prog.attachShader(gl, fs);
		prog.linkProgram(gl);
		System.err.println(prog.getInfoLog(gl));
		
		prog.activate(gl);
		
		Uniform1i sampler = prog.getUniform1i(gl, "colorMap");
		Uniform1i sampler2 = prog.getUniform1i(gl, "depthMap");
		sampler.set(gl, 0);
		sampler2.set(gl, 1);
		prog.deactivate(gl);
		
		
		for(int i=0; i < 500; i++)
			particles.add(new Vector3f((float)Math.random()*40-20,(float)Math.random()*40-20,(float) Math.random()*40-20).normalize().multiply(0.0f+8.5f*(float)Math.random()));
	}
	
	public void draw(GL2 gl) {
		float[] matrix = new float[16];
		
		gl.glGetFloatv(GL2.GL_TRANSPOSE_MODELVIEW_MATRIX, matrix, 0);
		Vector3f right = new Vector3f(matrix[0], matrix[1], matrix[2]);
		Vector3f up = new Vector3f(matrix[4], matrix[5], matrix[6]);
		Vector3f point = new Vector3f(0,0.75f,0);
		
		gl.glDisable(GL2.GL_LIGHTING);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc (GL.GL_SRC_ALPHA,GL.GL_ONE_MINUS_SRC_ALPHA );
		gl.glDepthMask(false);
		float size =0.18f;// 0.05f;
		
		//Texture2D.enable(gl);
		gl.glColor3f(0.5f,0.6f,0.5f);
		
		prog.activate(gl);
		gl.glActiveTexture(GL2.GL_TEXTURE0);
		texture.bind(gl);
		gl.glBegin(GL2.GL_QUADS);
		
		for(int i =0; i < 500; i++){
		Vector3f A = right.add(up).multiply(-size).add(particles.get(i));
		Vector3f B = right.substract(up).multiply(size).add(particles.get(i));
		Vector3f C = right.add(up).multiply(size).add(particles.get(i));
		Vector3f D = up.substract(right).multiply(size).add(particles.get(i));
		
		

		
//		gl.glMultiTexCoord2f(GL.GL_TEXTURE0,0.0f, 0.0f); gl.glVertex3f(A.x,A.y,A.z);
//		gl.glMultiTexCoord2f(GL.GL_TEXTURE0,1.0f, 0.0f); gl.glVertex3f(B.x,B.y,B.z);
//		gl.glMultiTexCoord2f(GL.GL_TEXTURE0,1.0f, 1.0f); gl.glVertex3f(C.x,C.y,C.z);
//		gl.glMultiTexCoord2f(GL.GL_TEXTURE0,0.0f, 1.0f); gl.glVertex3f(D.x,D.y,D.z);
		
	
		
		gl.glMultiTexCoord2f(GL.GL_TEXTURE0,0.0f, 1.0f); gl.glVertex3f(A.x,A.y,A.z);
		gl.glMultiTexCoord2f(GL.GL_TEXTURE0,1.0f, 1.0f);gl.glVertex3f(B.x,B.y,B.z);
			gl.glMultiTexCoord2f(GL.GL_TEXTURE0,1.0f, 0.0f); gl.glVertex3f(C.x,C.y,C.z);
			gl.glMultiTexCoord2f(GL.GL_TEXTURE0,0.0f, 0.0f); gl.glVertex3f(D.x,D.y,D.z);
		
		
		
		}
		
		gl.glEnd();
		prog.deactivate(gl);
		gl.glDisable(GL.GL_BLEND);
		gl.glDepthMask(true);
		gl.glEnable(GL2.GL_LIGHTING);
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
	          //texture.setTexParameteri(GL.GL_GENERATE_MIPMAP, GL.GL_TRUE);
	          
	      }
	      catch (Exception e)
	      {
	          System.out.println ("error loading texture from "+filename);
	      }

	      return texture;
	   }
	
	

}
