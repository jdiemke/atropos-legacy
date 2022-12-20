package atropos.demos.collision;

import com.jogamp.opengl.GL2;

import atropos.core.math.Vector2f;

public class LineSegment implements Collidable {
	
	Vector2f start;
	Vector2f end;
	
	public LineSegment(Vector2f start, Vector2f end) {
		this.start = start;
		this.end = end;
	}
	
	public Vector2f project(Vector2f vec) {
		Vector2f segment = end.substract(start);
		
		return segment.multiply(segment.dot(vec)).divide(segment.length()*segment.length());
	}
	
	public CollisionPack getIntersectionPoint(LineSegment lineSegment) {
		Vector2f P3 = this.start;
		Vector2f P4 = this.end;
		Vector2f P1 = lineSegment.start;
		Vector2f P2 = lineSegment.end;
		
		float denom = (P4.y - P3.y)*(P2.x -P1.x) - (P4.x - P3.x)*(P2.y - P1.y);
		
		float ua = (P4.x - P3.x)*(P1.y - P3.y) - (P4.y - P3.y)*(P1.x - P3.x);
		
		System.out.println("denom: " + denom);
		float result = ua/denom;
		
		if (denom != 0.0f  && ua!= 0.0f && result >= 0.0f && result <=1.0f)
			return new CollisionPack(result, P1.add(P2.substract(P1).multiply(result)), getNormal()); 
			
		return null;
	}
	
	public Vector2f getNormal() {
		
		return end.substract(start).getNormal().normalize();

	}

	public void draw(GL2 gl) {
		
		Vector2f temp = start.add(end).multiply(0.5f);
		Vector2f temp2 = start.add(end).multiply(0.5f).add(getNormal());
		
		gl.glBegin(GL2.GL_LINES);	
			gl.glVertex3f(start.x,start.y,0);
			gl.glVertex3f(end.x,end.y,0);
			gl.glVertex3f(temp.x,temp.y,0);
			gl.glVertex3f(temp2.x,temp2.y,0);
		gl.glEnd();
	}

	@Override
	public CollisionPack intersect(LineSegment lineSegement) {
		// TODO Auto-generated method stub
		return null;
	}
}
