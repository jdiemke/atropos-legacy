package atropos.demos.window;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.glu.GLU;

import com.jogamp.opengl.util.gl2.GLUT;

import atropos.AtroposDefaultRenderer;
import atropos.AtroposWindow;

/**
 * WindowDemo.java
 * 
 * run with: -Dsun.awt.noerasebackground=true -Dsun.java2d.noddraw=true
 * 
 * @author Johannes Diemke
 */
public class SimpleTeapotDemo {

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

						gl.glTranslatef(0.0f, 0.0f, -5.0f);
						gl.glRotatef(rotation, 0.0f, 0.0f, 1.0f);
						gl.glRotatef(rotation, 0.0f, 1.0f, 0.0f);
						glut.glutSolidTeapot(1.0);

						rotation += 0.2f;
					}
				});
	}

}