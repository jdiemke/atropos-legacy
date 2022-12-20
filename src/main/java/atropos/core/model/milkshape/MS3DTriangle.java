package atropos.core.model.milkshape;

import atropos.core.math.Vector2f;
import atropos.core.math.Vector3f;
import atropos.core.math.Vector4f;

public class MS3DTriangle {
	
	MS3DVertex a,b,c;
	Vector3f[] normal= new Vector3f[3];
	Vector3f[] initialNormal= new Vector3f[3];
	Vector3f[] finalNormal= new Vector3f[3];
	Vector2f t1, t2, t3;
	
	public void computeInitalNormal() {
		
		initialNormal[0] =a.bone.result.inverse().multiply(normal[0]);
		initialNormal[1] =b.bone.result.inverse().multiply(normal[1]);
		initialNormal[2] =c.bone.result.inverse().multiply(normal[2]);
	}

	public void computeFinalNormal() {
		finalNormal[0] =a.bone.finalResult.multiply(initialNormal[0]);
		finalNormal[1] =b.bone.finalResult.multiply(initialNormal[1]);
		finalNormal[2] =c.bone.finalResult.multiply(initialNormal[2]);
		
	}

}
