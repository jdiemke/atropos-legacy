package atropos.demos.collision;

import atropos.core.math.Vector2f;

public class CollisionPack {
	
	public Vector2f normal;
	public float dist;
	public Vector2f collisionPoint;
	
	public CollisionPack(float dist, Vector2f collisionPoint, Vector2f normal) {
		this.collisionPoint = collisionPoint;
		this.dist = dist;
		this.normal = normal;
	}

}
