package atropos.core.model.wavefront;

import java.util.ArrayList;

import com.jogamp.opengl.GL2;

import atropos.core.math.Vector3f;

public class MaterialGroup {
	
	Material material = new Material();
	ArrayList<Face> faces = new ArrayList<Face>();
	
	ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();
	ArrayList<Vector3f> texCoords = new ArrayList<Vector3f>();
	ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
	
	public MaterialGroup(Material material) {
		this.material = material;
	}
	
	public void add(Face face) {
		faces.add(face);
	}
	
	public void setVertices(ArrayList<Vector3f> vertices) {
		this.vertices = vertices;
	}
	
	public void setTexCoords(ArrayList<Vector3f> texCoords) {
		this.texCoords = texCoords;
	}
	
	public void setNormals(ArrayList<Vector3f> normals) {
		this.normals = normals;
	}

	public void draw(GL2 gl) {
	//	if(material.material!= null) {
			
		material.material.apply(gl);
		
		if(material.hasTexture())
			material.texture.bind(gl);
		
		//}
		
		gl.glBegin(GL2.GL_TRIANGLES);
			
		for(int i=0; i < faces.size(); i++) {
			Face face = faces.get(i);
			
			for(int j=0; j < face.vertices.size(); j++) {
				Vector3f normal = face.vertices.get(j).normal;
				Vector3f texCoord = face.vertices.get(j).texCoord;
				Vector3f position = face.vertices.get(j).position;
				
				gl.glNormal3f(normal.x, normal.y, normal.z);
				if(texCoord != null)
					gl.glTexCoord3f(texCoord.x, texCoord.y, texCoord.z);
				gl.glVertex3f(position.x, position.y, position.z);
			}
		}
		
		gl.glEnd();
	}

}
