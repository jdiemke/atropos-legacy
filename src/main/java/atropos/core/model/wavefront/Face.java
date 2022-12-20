package atropos.core.model.wavefront;

import java.util.ArrayList;

public class Face {
	
	ArrayList<FaceVertex> vertices = new ArrayList<FaceVertex>();
	Material material;
	public Face() {
	}
	
	public void add(FaceVertex vertex) {
		vertices.add(vertex);
	}
	public ArrayList<FaceVertex> getVertices() {
		return vertices;
	}
	
	public void setMaterial(Material material) {
		this.material = material;
	}

}
