package atropos.demos.math;

import atropos.core.math.Matrix4f;
import atropos.core.math.Vector3f;
import atropos.core.math.Vector4f;

/**
 * MathDemo.java
 * 
 * run with: -Dsun.awt.noerasebackground=true -Dsun.java2d.noddraw=true
 * 
 * @author Johannes Diemke
 */
public class MathDemo {

	public static void main(String[] args) {
		Vector3f zero = new Vector3f(0.0f, 0.0f, 0.0f);
		Vector3f vec1 = new Vector3f(4.0f, 3.0f, 0.0f);
		Vector3f vec2 = vec1.negate();

		Matrix4f identityMatrix = Matrix4f.constructIdentityMatrix();

		System.out.println(zero);
		System.out.println(vec1);
		System.out.println(vec2);
		System.out.println(vec1.length());
		System.out.println(new Vector3f(12, -5, 0).normalize());

		Matrix4f matrix1 = new Matrix4f(1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);

		float[] matArray = new float[] { 2.0f, 0.0f, 0.0f, 0.0f, 0.0f, 2.0f,
				0.0f, 0.0f, 0.0f, 0.0f, 2.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f };

		Matrix4f matrix2 = new Matrix4f(matArray).transpose();

		System.out.println(matrix1);
		System.out.println(matrix2);

		Vector3f vec = new Vector3f(1.0f, 2.0f, 3.0f);
		System.out.println(vec);
		System.out.println(new Vector4f(vec));
		System.out.println(matrix2.multiply(matrix2).multiply(
				vec.toHomogenousCoordinate()));

		Matrix4f.constructTranslationMatrix(5.0f, 5.0f, 5.0f);

		System.out.println(Matrix4f.constructZRotationMatrix(90.0f).multiply(
				new Vector3f(1.0f, 0.0f, 0.0f).toHomogenousCoordinate()));

		// extract basis vectors after transformation
		Vector3f p = Matrix4f.constructZRotationMatrix(90.0f).multiply(
				new Vector3f(1.0f, 0.0f, 0.0f).toHomogenousCoordinate())
				.toCartesianCoordinate();
		Vector3f q = Matrix4f.constructZRotationMatrix(90.0f).multiply(
				new Vector3f(0.0f, 1.0f, 0.0f).toHomogenousCoordinate())
				.toCartesianCoordinate();
		Vector3f r = Matrix4f.constructZRotationMatrix(90.0f).multiply(
				new Vector3f(0.0f, 0.0f, 1.0f).toHomogenousCoordinate())
				.toCartesianCoordinate();

	}

}