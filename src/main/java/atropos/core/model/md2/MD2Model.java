package atropos.core.model.md2;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Vector;

import com.jogamp.opengl.GL2;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import atropos.core.math.Vector3f;
import atropos.core.shader.attrib.VertexAttrib3f;

/**
 * This class implements an MD2 Model Loader
 * Take care and keep in mind that MD2 files are stored in
 * little-endian (x86) format.
 * @see http://tfc.duke.free.fr/coding/md2-specs-en.html
 * @author trigger
 */
public class MD2Model {
	
	// max values
	private final int MAX_TRIANGLES = 4096;
	private final int MAX_VERTICES  = 2048;
	private final int MAX_TEXCOORDS = 2048;
	private final int MAX_FRAMES    =  512;
	private final int MAX_SKINS     =   32;
	private final int MAX_NORMALS   =  162;
	
	// magic number "IDP2" or 844121161
	private final int MD2_IDENT 	= 844121161;
	private final int MD2_VERSION	= 8;
	
	MD2Header md2Header = null;
	
	Vector<Vector3f> vertices = new Vector<Vector3f>();
	Vector<MD2Triangle> triangles = new Vector<MD2Triangle>();
	Vector<MD3TexCoord> texCoords = new Vector<MD3TexCoord>();
	Vector<MD2Frame> frames = new Vector<MD2Frame>();
	Texture tex;
	
	
	public MD2Model(GL2 gl, String fileName, String texture) {
		
		byte[] bytes = convertToByteArray(new File(fileName));
		
		md2Header = loadHeader(bytes);
		System.out.println(md2Header);
		
		// verify its an MD2 file
		if(md2Header.ident != MD2_IDENT || md2Header.version != MD2_VERSION) {
			System.err.println("Not an MD2 file or wrong version!");
			return;
		}
		
		loadSkins(bytes);
		loadTexCoords(bytes);
		loadFrame(bytes);
		loadTriangles(bytes);

		tex = load(gl, texture);
		//tex = load(gl, "models/weapon.png");

	}
	
	 Texture load (GL2 gl,String filename)
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

	          
	          texture.setTexParameteri(gl,GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST_MIPMAP_LINEAR );
	  			texture.setTexParameteri(gl,GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
	          
	          texture.setTexParameterf(gl,GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
	          texture.setTexParameterf(gl,GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
	          
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
	
	public void loadTriangles(byte[] bytes) {
		LittleEndianDataInputStream stream = new LittleEndianDataInputStream(new ByteArrayInputStream( bytes ));
		
		int max = 0;
		try {
			stream.skip(md2Header.offsetTriangles);
			for(int i=0; i < md2Header.numTriangles; i++) {
			int tri1 = stream.readShort();
			int tri2 = stream.readShort();
			int tri3 = stream.readShort();
			
			int tex1 = stream.readShort();
			int tex2 = stream.readShort();
			int tex3 = stream.readShort();
			
			max = Math.max(max, tri1);
			max = Math.max(max, tri2);
			max = Math.max(max, tri3);
			//System.out.println(tri1 +" "+ tri2 + " "+tri3);
			triangles.add(new MD2Triangle(tri1, tri2, tri3,tex1, tex2,tex3));
			}
			
		} catch (IOException e) {
		}
		
		System.out.println("max tri:" + max);
	}
	
	private void loadFrame(byte[] bytes) {
		LittleEndianDataInputStream stream = new LittleEndianDataInputStream(new ByteArrayInputStream( bytes ));
		
		try {
			stream.skip(md2Header.offsetFrames);
			for(int j=0; j < md2Header.numFrames; j++) {
				
				MD2Frame frame = new MD2Frame();
			float scalex=stream.readFloat(); // scale
			float scaley=stream.readFloat(); // scale
			float scalez=stream.readFloat(); // scale
			float transx =stream.readFloat(); //trans
			float transy =stream.readFloat(); //trans
			float transz =stream.readFloat(); //trans
			
			
			byte[] name = new byte[16];
			stream.readFully(name, 0, 16);
			
			System.out.println(j+" name: " + convertToString(name));
			frame.name =  convertToString(name);
			for(int i=0; i < md2Header.numVertices; i++) {
			int x = stream.readUnsignedByte();
			int y = stream.readUnsignedByte();
			int z = stream.readUnsignedByte();
			
			
			
			int norm = stream.readUnsignedByte();
			MD2Vertex vertex = new MD2Vertex();
			vertex.vertex = new Vector3f(x*scalex+transx, y*scaley+transy, z*scalez+transz);
			vertex.normalIndex = norm;
			
			frame.vertices.add(vertex);
			
			}
			frames.add(frame);
			}
			
		} catch (IOException e) {
		}
	}
	
	int frame = 0;
	long start = System.currentTimeMillis();
	
	public void setStartTime(long time) {
		start = time;
	}
	public void draw(GL2 gl) {
		gl.glPointSize(3.0f);
		gl.glColor3f(1,1,1);
	//	Systems.out.println("md");
		tex.enable(gl);
		tex.bind(gl);
		long now =System.currentTimeMillis();
		MD2AnimList anim= MD2AnimList.POINT;
		
		int animLength =anim.lastFrame() - anim.firstFrame() + 1;  
		
		long elapsed = (now - start);
		float realFrame = (elapsed *0.2f*  anim.fps())/1000.0f;
		float frac = realFrame % 1.0f;
		int frame =  anim.firstFrame() + (((int)realFrame)%animLength);
		int frame2 = anim.firstFrame() + (((int)realFrame+1)%animLength);
		
//		float realFrame = (now - start)*0.008f;
//		float frac = realFrame % 1.0f;
//		int frame =  ((int)realFrame)%md2Header.numFrames;
//		int frame2 =  ((int)realFrame+1)%md2Header.numFrames;
	//	System.out.println(realFrame +" " +frame +" "+ frame2);
	//	System.out.println(frames.get(frame).name);
		gl.glBegin(GL2.GL_TRIANGLES);
			for(int i=0; i < md2Header.numTriangles; i++) {
				
				
				gl.glTexCoord2f(texCoords.get(triangles.get(i).tex1).u,texCoords.get(triangles.get(i).tex1).v);
				
				float n1 = MD2NormalTable.normals[frames.get(frame).vertices.get(triangles.get(i).a).normalIndex][0] + frac *
						   (MD2NormalTable.normals[frames.get(frame2).vertices.get(triangles.get(i).a).normalIndex][0]-MD2NormalTable.normals[frames.get(frame).vertices.get(triangles.get(i).a).normalIndex][0]);
				
				float n2 = MD2NormalTable.normals[frames.get(frame).vertices.get(triangles.get(i).a).normalIndex][1] + frac *
				   (MD2NormalTable.normals[frames.get(frame2).vertices.get(triangles.get(i).a).normalIndex][1]-MD2NormalTable.normals[frames.get(frame).vertices.get(triangles.get(i).a).normalIndex][1]);
				
				float n3 = MD2NormalTable.normals[frames.get(frame).vertices.get(triangles.get(i).a).normalIndex][2] + frac *
				   (MD2NormalTable.normals[frames.get(frame2).vertices.get(triangles.get(i).a).normalIndex][2]-MD2NormalTable.normals[frames.get(frame).vertices.get(triangles.get(i).a).normalIndex][2]);
				
				Vector3f norm = new Vector3f(n1,n2,n3);
				norm.normalize();
				gl.glNormal3f(norm.x,norm.y,norm.z);
				
				float x1 = frames.get(frame).vertices.get(triangles.get(i).a).vertex.x + frac *
						   (frames.get(frame2).vertices.get(triangles.get(i).a).vertex.x-frames.get(frame).vertices.get(triangles.get(i).a).vertex.x);
				
				float x2 = (frames.get(frame).vertices.get(triangles.get(i).a).vertex.y) + frac *
						   (frames.get(frame2).vertices.get(triangles.get(i).a).vertex.y-frames.get(frame).vertices.get(triangles.get(i).a).vertex.y);
				
				float x3 = (frames.get(frame).vertices.get(triangles.get(i).a).vertex.z) + frac *
						   (frames.get(frame2).vertices.get(triangles.get(i).a).vertex.z-frames.get(frame).vertices.get(triangles.get(i).a).vertex.z);
				
				gl.glVertex3f(x1,x2,x3);
				
				gl.glTexCoord2f(texCoords.get(triangles.get(i).tex2).u,texCoords.get(triangles.get(i).tex2).v);
				
				 n1 = MD2NormalTable.normals[frames.get(frame).vertices.get(triangles.get(i).b).normalIndex][0] + frac *
				   (MD2NormalTable.normals[frames.get(frame2).vertices.get(triangles.get(i).b).normalIndex][0]-MD2NormalTable.normals[frames.get(frame).vertices.get(triangles.get(i).b).normalIndex][0]);
		
	 n2 = MD2NormalTable.normals[frames.get(frame).vertices.get(triangles.get(i).b).normalIndex][1] + frac *
		   (MD2NormalTable.normals[frames.get(frame2).vertices.get(triangles.get(i).b).normalIndex][1]-MD2NormalTable.normals[frames.get(frame).vertices.get(triangles.get(i).b).normalIndex][1]);
		
		 n3 = MD2NormalTable.normals[frames.get(frame).vertices.get(triangles.get(i).b).normalIndex][2] + frac *
		   (MD2NormalTable.normals[frames.get(frame2).vertices.get(triangles.get(i).b).normalIndex][2]-MD2NormalTable.normals[frames.get(frame).vertices.get(triangles.get(i).b).normalIndex][2]);
		
		 norm = new Vector3f(n1,n2,n3);
			norm.normalize();
			gl.glNormal3f(norm.x,norm.y,norm.z);
				x1 = frames.get(frame).vertices.get(triangles.get(i).b).vertex.x + frac *
				   (frames.get(frame2).vertices.get(triangles.get(i).b).vertex.x-frames.get(frame).vertices.get(triangles.get(i).b).vertex.x);
		
				x2 = (frames.get(frame).vertices.get(triangles.get(i).b).vertex.y) + frac *
				   (frames.get(frame2).vertices.get(triangles.get(i).b).vertex.y-frames.get(frame).vertices.get(triangles.get(i).b).vertex.y);
		
				x3 = (frames.get(frame).vertices.get(triangles.get(i).b).vertex.z) + frac *
				   (frames.get(frame2).vertices.get(triangles.get(i).b).vertex.z-frames.get(frame).vertices.get(triangles.get(i).b).vertex.z);
				
				gl.glVertex3f(x1,x2,x3);
				
				gl.glTexCoord2f(texCoords.get(triangles.get(i).tex3).u,texCoords.get(triangles.get(i).tex3).v);
				
				 n1 = MD2NormalTable.normals[frames.get(frame).vertices.get(triangles.get(i).c).normalIndex][0] + frac *
				   (MD2NormalTable.normals[frames.get(frame2).vertices.get(triangles.get(i).c).normalIndex][0]-MD2NormalTable.normals[frames.get(frame).vertices.get(triangles.get(i).c).normalIndex][0]);
		
	 n2 = MD2NormalTable.normals[frames.get(frame).vertices.get(triangles.get(i).c).normalIndex][1] + frac *
		   (MD2NormalTable.normals[frames.get(frame2).vertices.get(triangles.get(i).c).normalIndex][1]-MD2NormalTable.normals[frames.get(frame).vertices.get(triangles.get(i).c).normalIndex][1]);
		
		 n3 = MD2NormalTable.normals[frames.get(frame).vertices.get(triangles.get(i).c).normalIndex][2] + frac *
		   (MD2NormalTable.normals[frames.get(frame2).vertices.get(triangles.get(i).c).normalIndex][2]-MD2NormalTable.normals[frames.get(frame).vertices.get(triangles.get(i).c).normalIndex][2]);
		
	 norm = new Vector3f(n1,n2,n3);
			norm.normalize();
			gl.glNormal3f(norm.x,norm.y,norm.z);
				
				x1 = frames.get(frame).vertices.get(triangles.get(i).c).vertex.x + frac *
				   (frames.get(frame2).vertices.get(triangles.get(i).c).vertex.x-frames.get(frame).vertices.get(triangles.get(i).c).vertex.x);
		
				x2 = (frames.get(frame).vertices.get(triangles.get(i).c).vertex.y) + frac *
				   (frames.get(frame2).vertices.get(triangles.get(i).c).vertex.y-frames.get(frame).vertices.get(triangles.get(i).c).vertex.y);
		
				x3 = (frames.get(frame).vertices.get(triangles.get(i).c).vertex.z) + frac *
				   (frames.get(frame2).vertices.get(triangles.get(i).c).vertex.z-frames.get(frame).vertices.get(triangles.get(i).c).vertex.z);
				
				gl.glVertex3f(x1,x2,x3);
			}
		gl.glEnd();
	}
	
//	public void draw(GL2 gl) {
//		gl.glPointSize(3.0f);
//		gl.glColor3f(1,1,1);
//	//	Systems.out.println("md");
//		tex.enable(gl);
//		tex.bind(gl);
//		long now = System.currentTimeMillis();
//		frame = (int) ((now - start)*0.004%md2Header.numFrames);
//		System.out.println(frames.get(frame).name);
//		gl.glBegin(GL2.GL_TRIANGLES);
//			for(int i=0; i < md2Header.numTriangles; i++) {
//				
//				
//				gl.glTexCoord2f(texCoords.get(triangles.get(i).tex1).u,texCoords.get(triangles.get(i).tex1).v);
//				gl.glNormal3f(MD2NormalTable.normals[frames.get(frame).vertices.get(triangles.get(i).a).normalIndex][0],
//							  MD2NormalTable.normals[frames.get(frame).vertices.get(triangles.get(i).a).normalIndex][1],
//						      MD2NormalTable.normals[frames.get(frame).vertices.get(triangles.get(i).a).normalIndex][2]);
//				gl.glVertex3f(frames.get(frame).vertices.get(triangles.get(i).a).vertex.x, frames.get(frame).vertices.get(triangles.get(i).a).vertex.y, frames.get(frame).vertices.get(triangles.get(i).a).vertex.z);
//				
//				gl.glTexCoord2f(texCoords.get(triangles.get(i).tex2).u,texCoords.get(triangles.get(i).tex2).v);
//				gl.glNormal3f(MD2NormalTable.normals[frames.get(frame).vertices.get(triangles.get(i).b).normalIndex][0],
//						  	  MD2NormalTable.normals[frames.get(frame).vertices.get(triangles.get(i).b).normalIndex][1],
//						  	  MD2NormalTable.normals[frames.get(frame).vertices.get(triangles.get(i).b).normalIndex][2]);
//				gl.glVertex3f(frames.get(frame).vertices.get(triangles.get(i).b).vertex.x, frames.get(frame).vertices.get(triangles.get(i).b).vertex.y, frames.get(frame).vertices.get(triangles.get(i).b).vertex.z);
//				
//				gl.glTexCoord2f(texCoords.get(triangles.get(i).tex3).u,texCoords.get(triangles.get(i).tex3).v);
//				gl.glNormal3f(MD2NormalTable.normals[frames.get(frame).vertices.get(triangles.get(i).c).normalIndex][0],
//					  	  MD2NormalTable.normals[frames.get(frame).vertices.get(triangles.get(i).c).normalIndex][1],
//					  	  MD2NormalTable.normals[frames.get(frame).vertices.get(triangles.get(i).c).normalIndex][2]);
//				gl.glVertex3f(frames.get(frame).vertices.get(triangles.get(i).c).vertex.x, frames.get(frame).vertices.get(triangles.get(i).c).vertex.y, frames.get(frame).vertices.get(triangles.get(i).c).vertex.z);
//			}
//		gl.glEnd();
//	}
	
	private void loadTexCoords(byte[] bytes) {
		LittleEndianDataInputStream stream = new LittleEndianDataInputStream(new ByteArrayInputStream( bytes ));
		
		try {
			stream.skip(md2Header.offsetTexCoords);
			for(int i=0; i <md2Header.numTexCoords;i++) {
			int u = stream.readShort();
			int v = stream.readShort();
			texCoords.add(new MD3TexCoord(u/(float)md2Header.skinWidth, 1-v/(float)md2Header.skinHeight));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void loadSkins(byte[] bytes) {
		LittleEndianDataInputStream stream = new LittleEndianDataInputStream(new ByteArrayInputStream( bytes ));
		
		
		try {
			
			stream.skip(md2Header.offsetSkins);
			for(int i =0; i < md2Header.numSkins; i++){
			byte[] name = new byte[64];
			stream.readFully(name, 0, 64);
			System.out.println("skin name: " + convertToString(name));
		}

		} catch (IOException e) {
		}
		
	}
	
	String convertToString(byte[] array) {
		
		String string = "";
		int i=0;
		while(array[i] != 0) {
			string += Character.toString((char)array[i]);
			i++;
		}
		
		return string;
	}
	
	private MD2Header loadHeader(byte[] bytes) {
		MD2Header header = new MD2Header();
		LittleEndianDataInputStream stream = new LittleEndianDataInputStream(new ByteArrayInputStream( bytes ));
		
		try {
			header.ident = stream.readInt();
			header.version = stream.readInt();
			header.skinWidth = stream.readInt();
			header.skinHeight=stream.readInt();
			header.frameSize =stream.readInt();
			header.numSkins = stream.readInt();
			header.numVertices= stream.readInt();
			header.numTexCoords=stream.readInt();
			header.numTriangles =stream.readInt();
			header.numGLCommands =stream.readInt();
			header.numFrames =stream.readInt();
			
			header.offsetSkins =  stream.readInt();
			header.offsetTexCoords =stream.readInt();
			header.offsetTriangles = stream.readInt();
			header.offsetFrames =stream.readInt();
			header.offsetGLCommands =stream.readInt();
			header.offsetEnd =stream.readInt();		
		} catch (IOException e) {
		}
		
		return header;
	}
	
	private byte[] convertToByteArray(File file) {
		
		DataInputStream stream = null;
		
		try {
			stream = new DataInputStream(new FileInputStream(file));
		} catch (FileNotFoundException e) {
		}
		
		byte[] bytes = new  byte[(int) file.length()];

		try {
			stream.read(bytes);
		} catch (IOException e) {
			
		}
		
		return bytes;
	}

}
