package atropos.demos.math;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.glu.GLU;

import com.jogamp.opengl.util.gl2.GLUT;


import atropos.AtroposDefaultRenderer;
import atropos.AtroposWindow;
import atropos.core.math.Matrix4f;
import atropos.core.math.Vector3f;

/**
 * WindowDemo.java
 * 
 * run with: -Dsun.awt.noerasebackground=true -Dsun.java2d.noddraw=true
 * 
 * @author Johannes Diemke
 */
public class MatrixDemo {

	public static void main(String[] args) {
		new AtroposWindow("Simple Teapot Demo", 640, 480, 60,
				new AtroposDefaultRenderer() {

					@Override
					public void init(GLAutoDrawable drawable) {
						super.init(drawable);
						GL2 gl = drawable.getGL().getGL2();

						gl.glEnable(GL2.GL_LIGHTING);
						gl.glEnable(GL2.GL_LIGHT0);
					}

					float rotation = 0;

					@Override
					public void display(GLAutoDrawable drawable) {
						GL2 gl = drawable.getGL().getGL2();
						GLUT glut = new GLUT();

						gl.glClear(GL2.GL_COLOR_BUFFER_BIT
								| GL2.GL_DEPTH_BUFFER_BIT);
						gl.glLoadIdentity();
						
						Matrix4f id = Matrix4f.constructIdentityMatrix();
						Matrix4f trans = Matrix4f.constructTranslationMatrix(0.0f, 0.0f, -5.0f);
						Matrix4f rot = Matrix4f.constructArbitraryRotationMatrix(new Vector3f(1.0f, 0.0f, 0.0f), rotation);
						//Matrix4f rot = Matrix4f.constructArbitraryScaleMatrix(new Vector3f(0.0f, 0.0f, -1.0f), 0.10f);
						
						Matrix4f result = id.multiply(trans).multiply(rot);
						
						
						Matrix4f inverseRot = new Matrix4f(result.m11, result.m21, result.m31, 0.0f,
														   result.m12, result.m22, result.m32, 0.0f,
														   result.m13, result.m23, result.m33, 0.0f,
														         0.0f,       0.0f,       0.0f, 1.0f);
						Matrix4f inverseTrans = new Matrix4f(1.0f, 0.0f, 0.0f, -result.m14,
														   0.0f, 1.0f, 0.0f, -result.m24,
														   0.0f, 0.0f, 1.0f, -result.m34,
														   0.0f, 0.0f, 0.0f, 1.0f);
						
						Matrix4f inverse = inverseRot.multiply(inverseTrans);
						
						System.out.println(inverse.multiply(result));
						
						
						glut.glutSolidTeapot(1.0);

						rotation += 0.2f;
					}
				});
	}

}