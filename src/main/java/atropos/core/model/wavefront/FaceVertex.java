package atropos.core.model.wavefront;

import atropos.core.math.Vector3f;

public class FaceVertex {
	
	public Vector3f position;
	public Vector3f normal;
	public Vector3f texCoord;
	
	public FaceVertex(Vector3f position, Vector3f texCoord,  Vector3f normal) {
		this.position = position;
		this.normal = normal;
		this.texCoord = texCoord;
	}

}
