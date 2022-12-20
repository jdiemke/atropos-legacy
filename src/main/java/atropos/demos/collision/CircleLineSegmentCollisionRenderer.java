package atropos.demos.collision;

import java.util.Vector;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.glu.GLU;
import javax.swing.text.Position;

import com.jogamp.opengl.util.gl2.GLUT;

import atropos.AtroposDefaultRenderer;
import atropos.core.math.Vector2f;

public class CircleLineSegmentCollisionRenderer extends AtroposDefaultRenderer {

	
	Circle c1, c2,c3,c4;
	LineSegment l1, l2, l3, l4,test;
	Vector2f velocity = new Vector2f(3,1).multiply(3.5f);
	
	Vector<LineSegment> geometry;
	
	CollisionEngine engine;
	@Override
	public void init(GLAutoDrawable drawable) {
		super.init(drawable);
		GL2 gl = drawable.getGL().getGL2();

		//gl.glEnable(GL2.GL_LIGHTING);
	//	gl.glEnable(GL2.GL_LIGHT0);
		
		gl.glDisable(GL2.GL_DEPTH_TEST);
		c1 = new Circle(new Vector2f(0f,0f), 1.0f);
		c2 = new Circle(new Vector2f(0f,0f), 1.0f);
		c3 = new Circle(new Vector2f(0f,0f), 1.0f);
		c4 = new Circle(new Vector2f(0f,0f), 1.0f);
		l1 = new LineSegment(new Vector2f(1, 1).multiply(4.0f), new Vector2f(-1,1).multiply(4.0f));
		l2 = new LineSegment(new Vector2f( 1, -1).multiply(4.0f), new Vector2f(1,1).multiply(4.0f));
		l3 = new LineSegment(new Vector2f( -1, -1).multiply(4.0f), new Vector2f(1,-1).multiply(4.0f));
		l4 = new LineSegment(new Vector2f( -1, 1).multiply(4.0f), new Vector2f(-1,-1).multiply(4.0f));
		
		engine = new CollisionEngine();
		engine.addColliders(l1);
		engine.addColliders(l2);
		engine.addColliders(l3);
		engine.addColliders(l4);
		
		test = new LineSegment(new Vector2f( 0, 0), new Vector2f(5,2));
	}

	float delta = 0;
	long old = System.currentTimeMillis();
	//geometry.add(l4);
	
	
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		GLUT glut = new GLUT();

		long time = System.currentTimeMillis();
		delta = (time - old)/1000f;
		old = time;
		
		
		
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT
				| GL2.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();

		gl.glTranslatef(0.0f, 0.0f, -15.0f);
	
		engine.simulateStep(delta, gl);
		engine.draw(gl);
		
		
	
//		l2.draw(gl);
//		
//		
//		c1.draw(gl);
//		
//		
//		// draw displacement
//		Vector2f displacement = c1.position.add(velocity);
//		
//		gl.glBegin(GL2.GL_LINES);
//			gl.glColor3f(0,1,0);
//			gl.glVertex2f(c1.position.x, c1.position.y);
//			gl.glVertex2f(displacement.x, displacement.y);
//		gl.glEnd();
//		
//		Vector2f contactVector =l2.getNormal().negate().multiply(c1.radius);
//		Vector2f contactPoint = c1.position.add(contactVector);
//		Vector2f contactPointEnd = contactPoint.add(velocity);
//		
//		LineSegment ray = new LineSegment(contactPoint, contactPointEnd);
//		
//		Vector2f Intersection = ray.getIntersectionPoint(l2);
//		if(Intersection == null)
//			System.out.println("intersection is null");
//		
//		
//		Vector2f normal = l2.getNormal();
//		
//		// reflection
//		Vector2f velocity2 = normal.multiply(normal.dot(velocity.negate()) * 2).substract(velocity.negate());
//		
//		float verhaeltnis =  1-Intersection.substract(contactPoint).length() /velocity.length();
//		velocity2 = velocity2.multiply(verhaeltnis);
//		
//		Vector2f velStart = Intersection;
//		Vector2f velEnd = Intersection.add(velocity2);
//		
//		Vector2f displacement2 = Intersection.substract(contactPoint);
//		
//		// projection
//		Vector2f displacement3 = contactPointEnd.substract(Intersection);
//		Vector2f projDisp = l2.project(displacement3);
//		Vector2f projEnd = projDisp.add(Intersection);
//		
//		gl.glBegin(GL2.GL_LINES);
//		gl.glColor3f(0,1,0);
//		gl.glVertex2f(c1.position.x, c1.position.y);
//		gl.glVertex2f(displacement.x, displacement.y);
//		gl.glColor3f(1,0,1);
//		gl.glVertex2f(c1.position.x, c1.position.y);
//		gl.glVertex2f(contactPoint.x, contactPoint.y);
//		
//		gl.glColor3f(1,1,0);
//		gl.glVertex2f(contactPoint.x, contactPoint.y);
//		gl.glVertex2f(contactPointEnd.x, contactPointEnd.y);
//		
//		gl.glColor3f(1f,0.0f,0);
//		gl.glVertex2f(velStart.x, velStart.y);
//		gl.glVertex2f(velEnd.x, velEnd.y);
//		
//		gl.glColor3f(0f,1.0f,0);
//		gl.glVertex3f(Intersection.x, Intersection.y,0);
//		gl.glVertex2f(projEnd.x, projEnd.y);
//	gl.glEnd();
//	gl.glPointSize(5.0f);
//	gl.glBegin(GL2.GL_POINTS);
//		gl.glVertex3f(Intersection.x, Intersection.y,0);
//	gl.glEnd();
//		
//	c2.position = displacement;
//	c2.draw(gl);
//	c3.position = c1.position.add(displacement2);
//	c3.draw(gl);
//	c4.position = c3.position.add(projDisp);
//	c4.draw(gl);
	}
}
