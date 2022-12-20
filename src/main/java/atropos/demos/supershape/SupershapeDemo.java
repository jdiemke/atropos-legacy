package atropos.demos.supershape;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.glu.GLU;

import com.jogamp.opengl.util.gl2.GLUT;

import atropos.AtroposDefaultRenderer;
import atropos.AtroposWindow;
import atropos.core.math.Vector3f;

/**
 * WindowDemo.java
 * 
 * run with: -Dsun.awt.noerasebackground=true -Dsun.java2d.noddraw=true
 * 
 * @author Johannes Diemke
 */
public class SupershapeDemo {

	public static void main(String[] args) {
		new AtroposWindow("Simple Teapot Demo", 640, 480, 60,
				new AtroposDefaultRenderer() {

					@Override
					public void init(GLAutoDrawable drawable) {
						super.init(drawable);
						GL2 gl = drawable.getGL().getGL2();

						gl.glEnable(GL2.GL_LIGHTING);
						gl.glEnable(GL2.GL_LIGHT0);
						gl.glEnable(GL2.GL_COLOR_MATERIAL);
					}

					float rotation = 0;

					
					double r(double o, double a, double b, double m,double n1, double n2, double n3) {
						
					
						
						return Math.pow(Math.pow(Math.abs((1/a)*Math.cos(m*o/4)),n2)+
								Math.pow(Math.abs((1/b)*Math.sin(m*o/4)),n3), -1/n1);
					}
					
					long start = System.currentTimeMillis();
					Vector3f drawSupershape( double alpha, double phi) {
						
						
						double r1 = r(Math.toRadians(alpha),1,1,7,2,(1+Math.sin(elap*0.002)/2*8),(1+Math.sin(elap*0.0005)/2*4));
						double r2 = r(Math.toRadians(phi),1,1,7,2,(1+Math.sin(elap*0.002)/2*8),(1+Math.sin(elap*0.001)/2*4));
						
						float x =(float) (r1 * Math.cos(Math.toRadians(alpha)) * r2 * Math.cos(Math.toRadians(phi)));
						float y =(float) (r1 * Math.sin(Math.toRadians(alpha)) * r2 * Math.cos(Math.toRadians(phi)));
						float z =(float)(r2 * Math.sin(Math.toRadians(phi)));
						
						return new Vector3f(x, y, z);
					}
					long elap;
					
					Vector3f sphere(float alpha, float beta) {
						
						
						
						Vector3f pos = new Vector3f((float)Math.cos(alpha)*(float)Math.sin(beta),
								(float)Math.sin(alpha)*(float)Math.sin(beta), 
								(float)Math.cos(beta));
						 
						float radius = 1+((1+ (float)Math.sin(alpha*4+elap*0.002))/2*
								(1+ (float)Math.sin(beta*4+elap*0.002))/2);
						return pos.multiply(radius);
					}
					
				
					@Override
					public void display(GLAutoDrawable drawable) {
						GL2 gl = drawable.getGL().getGL2();
						GLUT glut = new GLUT();

						gl.glClear(GL2.GL_COLOR_BUFFER_BIT
								| GL2.GL_DEPTH_BUFFER_BIT);
						gl.glLoadIdentity();
						 elap = System.currentTimeMillis() - start;
						gl.glTranslatef(0.0f, 0.0f, -4.0f);
						gl.glRotatef(rotation*0.2f, 0.0f, 0.0f, 1.0f);
						gl.glRotatef(rotation*0.2f, 0.0f, 1.0f, 0.0f);
						gl.glScalef(1f,1f,1f);
					/*	gl.glBegin(GL2.GL_QUADS);
						gl.glColor3f(0.5f,1,0.2f);
						for(float i=-360/2; i < 360/2; i+=5)
							for(float j=-360/4; j < 360/4; j+=5) {
								Vector3f p=drawSupershape(i, j);
								Vector3f p2=drawSupershape(i+5, j);
								Vector3f p3=drawSupershape(i, j+5);
								Vector3f p4=drawSupershape(i+5, j+5);
								Vector3f n=p2.substract(p).cross(
								p3.substract(p)).normalize();
								gl.glNormal3f(n.x,n.y,n.z);
								gl.glVertex3f(p.x, p.y, p.z);
								gl.glVertex3f(p3.x, p3.y, p3.z);
								gl.glVertex3f(p4.x, p4.y, p4.z);
								gl.glVertex3f(p2.x, p2.y, p2.z);
							}
						
						gl.glEnd();
						*/
						
						gl.glBegin(GL2.GL_QUADS);
						gl.glColor3f(0.5f,1,0.2f);
						int maxi= 30;
						int maxj=30;
						for(float i=0; i < maxi; i+=1)
							for(float j=0; j < maxj; j+=1) {
								
								float alpha=(float)(2*Math.PI/(maxi-1)*i);
								float beta=(float)(Math.PI/(maxj-1)*j);
								Vector3f p=sphere(alpha, beta);
								Vector3f p2=sphere(alpha+(float)(2*Math.PI/(maxi-1)), beta);
								Vector3f p3=sphere(alpha, beta+(float)(Math.PI/(maxj-1)));
								Vector3f p4=sphere(alpha+(float)(2*Math.PI/(maxi-1)), beta+(float)(Math.PI/(maxj-1)));
								Vector3f n=p2.substract(p).cross(
								p3.substract(p)).normalize().negate();
								gl.glNormal3f(n.x,n.y,n.z);
								gl.glVertex3f(p.x, p.y, p.z);
								gl.glVertex3f(p3.x, p3.y, p3.z);
								gl.glVertex3f(p4.x, p4.y, p4.z);
								gl.glVertex3f(p2.x, p2.y, p2.z);
							}
						
						gl.glEnd();

						rotation += 2.2f;
					}
				});
	}

}