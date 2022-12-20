package atropos.demos.collision;

import com.jogamp.opengl.GL2;

public interface Collidable {
	
	public CollisionPack intersect(LineSegment lineSegement);
	public void draw(GL2 gl);

}
