package atropos.core.model.milkshape;

import java.util.Vector;

import atropos.core.math.Matrix4f;
import atropos.core.math.Vector3f;
import atropos.core.math.Vector4f;

public class MS3DVertex {
	
	public Vector3f vertex;
	public Vector3f initialVertex;
	public Vector3f finalVertex;
	public MS3DJoint bone;
	
	public int boneId;

	public void computeInitial(Vector<MS3DJoint> joints) {

		bone = joints.get(boneId);
		
		Matrix4f inverse = inverseMatrix(bone.result);
		
		initialVertex = new Vector3f(inverse.multiply(new Vector4f(vertex)));
	}
	
	public void computeFinalVertex() {
		
		finalVertex = new Vector3f(bone.finalResult.multiply(new Vector4f(initialVertex)));
	}
	
	public Matrix4f inverseMatrix(Matrix4f matrix) {		
		
		// transpose of the 3x3 rotation submatrix
		Matrix4f inverseRotation = new Matrix4f(matrix.m11,matrix.m21, matrix.m31, 0.0f,
												matrix.m12,matrix.m22, matrix.m32, 0.0f,
												matrix.m13,matrix.m23, matrix.m33, 0.0f,
												          0.0f,          0.0f,           0.0f, 1.0f);
		
		Matrix4f inverseTranslation = new Matrix4f(1.0f, 0.0f, 0.0f, -matrix.m14,
												   0.0f, 1.0f, 0.0f, -matrix.m24,
												   0.0f, 0.0f, 1.0f, -matrix.m34,
												   0.0f, 0.0f, 0.0f,            1.0f);		
		
		return inverseRotation.multiply(inverseTranslation);
	}

}
