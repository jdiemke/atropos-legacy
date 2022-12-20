package atropos.core.model.milkshape;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

import com.jogamp.opengl.GL2;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import atropos.core.Material;
import atropos.core.math.Matrix4f;
import atropos.core.math.Vector2f;
import atropos.core.math.Vector3f;
import atropos.core.math.Vector4f;
import atropos.core.model.md2.LittleEndianDataInputStream;
import atropos.core.model.md2.MD2Header;
import atropos.core.texture.Texture2D;

public class MilkshapeModel {
	
	// max values
	private final int MAX_VERTICES	= 65534;
	private final int MAX_TRIANGLES	= 65534;
	private final int MAX_GROUPS	= 255;
	private final int MAX_MATERIALS	= 128;
	private final int MAX_JOINTS	= 128;
	
	public enum Flag {
	    SELECTED,
	    HIDDEN,
	    SELECTED2,
	    DIRTY
	}
	
	Vector<MS3DVertex> vertices = new Vector<MS3DVertex>();
	Vector<MS3DTriangle> triangles = new Vector<MS3DTriangle>();
	Vector<MS3DGroup> groups = new Vector<MS3DGroup>();
	Vector<MS3DMaterial> materials = new Vector<MS3DMaterial>();
	Vector<MS3DJoint> joints = new Vector<MS3DJoint>();
	Vector<MS3DJoint> parentjoints = new Vector<MS3DJoint>();
	
	float animationFPS, currentAnimation,  totalFrames;
	
	public MilkshapeModel(GL2 gl, String fileName) {
		
		byte[] bytes = convertToByteArray(new File(fileName));
		
		loadHeader(gl,bytes,fileName);
		loadVertices(bytes);
	
	}
	
	
	
	private void loadVertices(byte[] bytes) {
		// TODO Auto-generated method stub
		LittleEndianDataInputStream stream = new LittleEndianDataInputStream(new ByteArrayInputStream( bytes ));

	
	}



	private void loadHeader(GL2 gl,byte[] bytes,String file) {
		
		LittleEndianDataInputStream stream = new LittleEndianDataInputStream(new ByteArrayInputStream( bytes ));
		
		try {
			
			byte[] id = new byte[10];
			stream.readFully(id, 0, 10);
			
			System.out.println("id: " + new String(id));
			
			int version =stream.readInt();
			System.out.println("version: " + version);
			
			long vertexCount = stream.readShort();
			System.out.println("vertices22: " + vertexCount);
			
			for(int i=0; i < vertexCount; i++) {
				int flags= stream.readUnsignedByte();
				float v1 = stream.readFloat();
				float v2 = stream.readFloat();
				float v3 = stream.readFloat();
				byte boneid =stream.readByte();
				int refCount = stream.readUnsignedByte();
				MS3DVertex vert = new MS3DVertex();
				vert.vertex = new Vector3f(v1, v2, v3);
				vert.boneId = boneid;
				vertices.add(vert);
			}
			
			long numTriangles = stream.readShort();
			System.out.println("triangles: " + numTriangles);
			
			for(int i=0; i < numTriangles; i++) {
				int flags= stream.readShort();
				int vertIndex1= stream.readShort();
				int vertIndex2= stream.readShort();
				int vertIndex3= stream.readShort();
				Vector3f normal1 = new Vector3f(stream.readFloat(), stream.readFloat(), stream.readFloat());
				Vector3f normal2 = new Vector3f(stream.readFloat(), stream.readFloat(), stream.readFloat());
				Vector3f normal3 = new Vector3f(stream.readFloat(), stream.readFloat(), stream.readFloat());
				
				
				float s1 = stream.readFloat();
				float s2 = stream.readFloat();
				float s3 = stream.readFloat();
				
				float t1 = stream.readFloat();
				float t2=stream.readFloat();
				float t3=stream.readFloat();
				
				stream.readUnsignedByte();
				stream.readUnsignedByte();
				
				MS3DTriangle tri = new MS3DTriangle();
				tri.a = vertices.get(vertIndex1);
				tri.b = vertices.get(vertIndex2);
				tri.c = vertices.get(vertIndex3);
				tri.normal[0] = normal1;
				tri.normal[1] = normal2;
				tri.normal[2] = normal3;
				tri.t1 = new Vector2f(s1, 1-t1);
				tri.t2 = new Vector2f(s2, 1-t2);
				tri.t3 = new Vector2f(s3, 1-t3);
				triangles.add(tri);
				
			}
			
			long numGroups = stream.readShort();
			System.out.println("groups: " + numGroups);

			for(int i=0; i <numGroups;i++) {
				MS3DGroup group = new MS3DGroup();
				stream.readUnsignedByte(); // flags
				
				byte[] name = new byte[32];
				stream.readFully(name, 0, 32);
				
				System.out.println(new String(name));
				
				int numTis = stream.readShort();
				
				for(int j=0; j < numTis; j++) {
					int triIndex = stream.readShort();
				
					//add tri to group
					
					
					group.triangles.add(triangles.get(triIndex));
				
				}
				group.material =stream.readUnsignedByte(); // material
				groups.add(group);
			}
			
			
			long numMaterials = stream.readShort();
			System.out.println("materials: " + numMaterials);
			
			for(int i=0; i < numMaterials; i++) {
				
				MS3DMaterial material = new MS3DMaterial();
				
				byte[] name = new byte[32];
				stream.readFully(name, 0, 32);
				
				System.out.println("material name: " +new String(name));
				
				material.material.setAmbient(stream.readFloat(),stream.readFloat(),stream.readFloat(),stream.readFloat());
				material.material.setDiffuse(stream.readFloat(),stream.readFloat(),stream.readFloat(),stream.readFloat());
				material.material.setSpecular(stream.readFloat(),stream.readFloat(),stream.readFloat(),stream.readFloat());
				material.material.setEmission(stream.readFloat(),stream.readFloat(),stream.readFloat(),stream.readFloat());
				material.material.setShininess(stream.readFloat());
				stream.readFloat(); // trans
				stream.readUnsignedByte(); // mode (unused)
				
				byte[] tex1 = new byte[128];
				stream.readFully(tex1, 0, 128);
				
				System.out.println("texture1: "+new String(tex1));
				System.out.println(new File(new String(tex1)).getName());
				
				byte[] tex2 = new byte[128];
				stream.readFully(tex2, 0, 128);
				
				System.out.println("texture2: "+new String(tex2));
				System.out.println(new File(new String(tex2)).getName());

				System.out.println("CRAP: " + new String(tex1).trim());

				String fileName = new File(file).getParent() + File.separator + new File(new String("beast1.png")).getName();
				System.out.println(fileName);
				Texture tex =load(gl, fileName);
				material.texture = tex;
				materials.add(material);
			}


			animationFPS = stream.readFloat(); // animation fps
			currentAnimation = stream.readFloat();// current animation frame
			totalFrames =stream.readInt(); // total frames
			int numJoints = stream.readShort(); // num joints
			
			System.out.println("anim fps: " + animationFPS);
			System.out.println("current animation: " + currentAnimation);
			System.out.println("total Frames: " + totalFrames);
			

			for(int i=0; i < numJoints; i++) {
				MS3DJoint joint = new MS3DJoint();
				stream.readUnsignedByte(); // flags
				
				byte[] name = new byte[32];
				stream.readFully(name, 0, 32);
				//System.out.println("joint name: "+new String(name).trim());
				joint.name = new String(name).trim();
				
				byte[] parentname = new byte[32];
				stream.readFully(parentname, 0, 32);
			//	System.out.println("parent joint name: "+new String(parentname).trim());
				joint.parentName = new String(parentname).trim();
				
				// local reference rotation
				float rotx=stream.readFloat();
				float roty=stream.readFloat();
				float rotz=stream.readFloat();
				
				joint.rotation = new Vector3f((float)Math.toDegrees(rotx)%360,(float)Math.toDegrees(roty)%360,(float)Math.toDegrees(rotz)%360);
				//System.out.println("rot: "+joint.rotation);
				
				// local reference position
				float posx = stream.readFloat();
				float posy = stream.readFloat();
				float posz = stream.readFloat();
				
				joint.translation = new Vector3f(posx,posy,posz);
				//System.out.println("trans: "+joint.translation);
				
				int numKeyFrameRot =stream.readShort();
				int numKeyFrameTrans =stream.readShort();
				
				for(int j=0; j < numKeyFrameRot; j++) {
					float time =stream.readFloat();
					
					float lrotx =stream.readFloat();
					float lroty =stream.readFloat();
					float lrotz =stream.readFloat();
					
					MS3DRotationKeyFrame key = new  MS3DRotationKeyFrame();
					key.rotation = new Vector3f((float)Math.toDegrees(lrotx), (float)Math.toDegrees(lroty), (float)Math.toDegrees(lrotz));
					key.time = time;
					//System.out.println("rotation time: "+key.time);
					
					joint.rotationKeyFrames.add(key);
				}
				
				System.out.println("end of joint");
				
				for(int j=0; j < numKeyFrameTrans; j++) {
					float time =stream.readFloat();
					
					float lposx =stream.readFloat();
					float lposy =stream.readFloat();
					float lposz =stream.readFloat();
					
					MS3DTranslationKeyFrame key = new MS3DTranslationKeyFrame();
					key.position = new Vector3f(lposx, lposy, lposz);
					key.time = time;
					joint.translationKeyFrames.add(key);
				}
				
				joints.add(joint);
			}
			
			// find parent joints
			for(int i =0; i < joints.size();i++) {
				MS3DJoint joint = joints.get(i);
				
				if(joint.parentName.equals(""))
					parentjoints.add(joint);
			}
			
			System.out.println("PARENT JOINTS:");
			System.out.println("--------------");
			
			for(int i=0; i < parentjoints.size();i++)
				System.out.println(parentjoints.get(i).name);
			
			// connect joints
			for(int i =0; i < joints.size();i++) {
				MS3DJoint joint = joints.get(i);
				
				for(int j =0; j < joints.size();j++) {
					MS3DJoint joint2 = joints.get(j);
					
					if(joint.parentName.equals(joint2.name)) {
						joint.parent = joint2;
						
					}
				}
			}
			
			// seems like joints are stored from hogher to lower hierachy
			
			// compute start joints
			for(int i =0; i < joints.size();i++) {
				MS3DJoint joint = joints.get(i);
				
				joint.computeInitial();
			}
			
			// compute start vertices from start joints
			for(int i =0; i < vertices.size();i++) {
				MS3DVertex vertex = vertices.get(i);
				
				vertex.computeInitial(joints);
				vertex.computeFinalVertex();
			}
			
			// compute start normals from start joints
			for(int i =0; i < triangles.size();i++) {
				MS3DTriangle tri = triangles.get(i);
				
				tri.computeInitalNormal();
			}


		} catch (IOException e) {
			
			System.out.println(e);
		}
		
		computeFrame(1.0f);
		
	}
	
	public void computeFrame(float frame) {

		// compute final bone position
		for(int i =0; i < joints.size();i++) {
			MS3DJoint joint = joints.get(i);
			
			joint.computeFrame(animationFPS, totalFrames);
		}
		
		// compute final vertices from bone positions
		for(int i =0; i < vertices.size();i++) {
			MS3DVertex vertex = vertices.get(i);
			vertex.computeFinalVertex();
		}	
		
		// compute final normals from bone orientation
		for(int i =0; i < triangles.size();i++) {
			MS3DTriangle tri = triangles.get(i);
			
			tri.computeFinalNormal();
		}
	}
	
	public void drawJoints(GL2 gl) {
		gl.glBegin(GL2.GL_LINES);
			for(int i=0; i < joints.size(); i++) {
				MS3DJoint joint = joints.get(i);
				if(joint.parent!=null) {
					
					Vector3f start = new Vector3f(joint.parent.result.multiply(new Vector4f()));
					gl.glVertex3f(start.x,start.y,start.z);
					Vector3f end = new Vector3f(joint.result.multiply(new Vector4f()));
					gl.glVertex3f(end.x,end.y,end.z);
				
				}
			}
				
		gl.glEnd();
	}
	
	
	public void drawJoints2(GL2 gl) {
		gl.glBegin(GL2.GL_LINES);
			for(int i=0; i < joints.size(); i++) {
				MS3DJoint joint = joints.get(i);
				if(joint.parent!=null) {
					
					Vector3f start = new Vector3f(joint.parent.finalResult.multiply(new Vector4f()));
					gl.glVertex3f(start.x,start.y,start.z);
					Vector3f end = new Vector3f(joint.finalResult.multiply(new Vector4f()));
					gl.glVertex3f(end.x,end.y,end.z);
				
				}
			}
				
		gl.glEnd();
	}
	public void draw(GL2 gl) {
		
		gl.glEnable(GL2.GL_TEXTURE_2D);
		
		for(int j=0; j< groups.size(); j++) {
			
			materials.get(groups.get(j).material).material.apply(gl);
			materials.get(groups.get(j).material).texture.bind(gl);
			
			gl.glBegin(GL2.GL_TRIANGLES);
			for(int i=0; i< groups.get(j).triangles.size(); i++){
			
			MS3DTriangle tri=groups.get(j).triangles.get(i);
			
			
		
			gl.glNormal3f(tri.normal[0].x,tri.normal[0].y,tri.normal[0].z);
			gl.glTexCoord2f(tri.t1.x,tri.t1.y);
			gl.glVertex3f(tri.a.vertex.x,tri.a.vertex.y,tri.a.vertex.z);
			
			gl.glNormal3f(tri.normal[1].x,tri.normal[1].y,tri.normal[1].z);
			gl.glTexCoord2f(tri.t2.x,tri.t2.y);
			gl.glVertex3f(tri.b.vertex.x,tri.b.vertex.y,tri.b.vertex.z);
			
			gl.glNormal3f(tri.normal[2].x,tri.normal[2].y,tri.normal[2].z);
			gl.glTexCoord2f(tri.t3.x,tri.t3.y);
			gl.glVertex3f(tri.c.vertex.x,tri.c.vertex.y,tri.c.vertex.z);
		}
			gl.glEnd();
		}
	
	}
	
	public void draw2(GL2 gl) {
	
		computeFrame(1.0f);
		gl.glEnable(GL2.GL_TEXTURE_2D);
		
		for(int j=0; j< groups.size(); j++) {
			
			materials.get(groups.get(j).material).material.apply(gl);
			materials.get(groups.get(j).material).texture.bind(gl);
			
			gl.glBegin(GL2.GL_TRIANGLES);
			for(int i=0; i< groups.get(j).triangles.size(); i++){
			
			MS3DTriangle tri=groups.get(j).triangles.get(i);
			
			
		
			gl.glNormal3f(tri.finalNormal[0].x,tri.finalNormal[0].y,tri.finalNormal[0].z);
			gl.glTexCoord2f(tri.t1.x,tri.t1.y);
			gl.glVertex3f(tri.a.finalVertex.x,tri.a.finalVertex.y,tri.a.finalVertex.z);
			
			gl.glNormal3f(tri.finalNormal[1].x,tri.finalNormal[1].y,tri.finalNormal[1].z);
			gl.glTexCoord2f(tri.t2.x,tri.t2.y);
			gl.glVertex3f(tri.b.finalVertex.x,tri.b.finalVertex.y,tri.b.finalVertex.z);
			
			gl.glNormal3f(tri.finalNormal[2].x,tri.finalNormal[2].y,tri.finalNormal[2].z);
			gl.glTexCoord2f(tri.t3.x,tri.t3.y);
			gl.glVertex3f(tri.c.finalVertex.x,tri.c.finalVertex.y,tri.c.finalVertex.z);
		}
			gl.glEnd();
		}
	
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
	
	
	String convertToString(byte[] array) {
		
		String string = "";
		int i=0;
		while(array[i] != 0) {
			string += Character.toString((char)array[i]);
			i++;
		}
		
		return string;
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
	
	public static void main(String[] args) {
		MilkshapeModel model = new MilkshapeModel(null, "models/ms3d/freebeast/beast.ms3d");
	}
}
