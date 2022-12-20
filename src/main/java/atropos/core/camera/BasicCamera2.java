package atropos.core.camera;

import atropos.core.math.Matrix4f;
import atropos.core.math.Vector3f;

public class BasicCamera2 {
	
	Matrix4f cameraMatrix = new Matrix4f();
	
	public BasicCamera2() {
		
	}
	
	public void setLookAt(Vector3f eye, Vector3f center, Vector3f up) {
		
		// construct camera frame
		Vector3f eyePosition = eye;
		
		Vector3f lookVector = center.substract(eye).normalize();
		Vector3f rightVector = lookVector.cross(up).normalize();
		Vector3f upVector = rightVector.cross(lookVector).normalize();
		
		// setup camera matrix
		cameraMatrix.m11 = rightVector.x;
		cameraMatrix.m12 = rightVector.y;
		cameraMatrix.m13 = rightVector.z;
		
		cameraMatrix.m21 = upVector.x;
		cameraMatrix.m22 = upVector.y;
		cameraMatrix.m23 = upVector.z;
		
		cameraMatrix.m31 = -lookVector.x;
		cameraMatrix.m32 = -lookVector.y;
		cameraMatrix.m33 = -lookVector.z;
		
		cameraMatrix.m14 = rightVector.dot(eyePosition.negate());
		cameraMatrix.m24 = upVector.dot(eyePosition.negate());
		cameraMatrix.m34 = lookVector.dot(eyePosition);
	}
	
	public Matrix4f getInverseViewMatrix() {		
		Matrix4f viewMatrix = getViewMatrix();
		
		// transpose of the 3x3 rotation submatrix
		Matrix4f inverseRotation = new Matrix4f(viewMatrix.m11,viewMatrix.m21, viewMatrix.m31, 0.0f,
												viewMatrix.m12,viewMatrix.m22, viewMatrix.m32, 0.0f,
												viewMatrix.m13,viewMatrix.m23, viewMatrix.m33, 0.0f,
												          0.0f,          0.0f,           0.0f, 1.0f);
		
		Matrix4f inverseTranslation = new Matrix4f(1.0f, 0.0f, 0.0f, -viewMatrix.m14,
												   0.0f, 1.0f, 0.0f, -viewMatrix.m24,
												   0.0f, 0.0f, 1.0f, -viewMatrix.m34,
												   0.0f, 0.0f, 0.0f,            1.0f);		
		
		return inverseRotation.multiply(inverseTranslation);
	}
	
	public Matrix4f getViewMatrix() {
		return cameraMatrix;
	}

}
