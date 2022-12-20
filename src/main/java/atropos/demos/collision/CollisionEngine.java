package atropos.demos.collision;

import java.util.Vector;

import com.jogamp.opengl.GL2;

import atropos.core.math.Vector2f;

public class CollisionEngine {
	
	Vector<LineSegment> colliders;
	Vector2f velocity = new Vector2f(1.4f,1).multiply(6);
	Circle player = new Circle(new Vector2f(0f,0f), 1.0f);
	
	public CollisionEngine() {
		colliders = new Vector<LineSegment>();
	}
	
	void addColliders(LineSegment collider) {
		colliders.add(collider);
	}
	
	public void simulateStep(float deltaTime,GL2 gl) {
		
		Vector2f displacement = velocity.multiply(deltaTime);
		CollisionPack result = null;
		CollisionPack firstCollision = null;
		
		// check each plane for a collision
		// not the endpoints included (right now)
		for(int i=0; i < colliders.size(); i++) {

			Vector2f normal = colliders.get(i).getNormal();
			Vector2f displacementStart = player.position.add(normal.negate());
			Vector2f displacementEnd = displacementStart.add(displacement);
			LineSegment displacementSegement = new LineSegment(displacementStart,
					displacementEnd);
			
			result = colliders.get(i).getIntersectionPoint(displacementSegement);
			if(result != null) {
			System.out.println("result " + i + ": " + result);
			System.out.println(" dist:" +result.dist);
			System.out.println(displacementStart.substract(displacementEnd).length());
			
			}
			
			gl.glColor3f(1,0,0);
			gl.glBegin(GL2.GL_LINES);
			gl.glVertex2f(displacementStart.x, displacementStart.y);
			gl.glVertex2f(displacementEnd.x, displacementEnd.y);
			gl.glEnd();
			
			if(result == null) continue;
			if(firstCollision == null) firstCollision = result;
			if(result.dist < firstCollision.dist) firstCollision = result;
		}
		
		// if no intersection stop now
		if(firstCollision == null) {
			player.position = player.position.add(displacement);
			return;
		} else  {// collision
			Vector2f collisionNormal = firstCollision.normal;
			Vector2f intersectionPoint = firstCollision.collisionPoint;
			float deltaRatio = firstCollision.dist;
			
			// reflection
			velocity = collisionNormal.multiply(collisionNormal.dot(velocity.negate()) * 2).substract(velocity.negate());
			float newDelta = deltaTime * (1-deltaRatio);
			simulateStep(newDelta, gl);
		}
		
	
	}
	
	public void draw(GL2 gl) {
		gl.glColor3f(0,1,0);
		for(int i=0; i < colliders.size(); i++)
			colliders.get(i).draw(gl);
		
		player.draw(gl);
		
	}

}
