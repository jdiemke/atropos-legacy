package atropos.core.camera;

import atropos.core.math.Vector3f;

public class ControllableCamera extends BasicCamera {

	public ControllableCamera(Vector3f pos, float yaw, float pitch) {
		super(pos, yaw, pitch, 0);
	}
	
	public ControllableCamera(Vector3f pos) {
		super(pos);
	}
	
	public ControllableCamera() {

	}
	
	public void moveForward(float speed, float deltaTime) {
		float distance = speed * deltaTime;		
		position.x += distance * -Math.sin(Math.toRadians(yaw));
		position.z += distance * -Math.cos(Math.toRadians(yaw));
	}
	
	public void moveBackward(float speed, float deltaTime) {
		float distance = speed * deltaTime;		
		position.x -= distance * -Math.sin(Math.toRadians(yaw));
		position.z -= distance * -Math.cos(Math.toRadians(yaw));
	}
	
	public void turnLeft(float speed, float deltaTime) {
		float distance = speed * deltaTime;		
		yaw += Math.toDegrees(distance); 
	}
	
	public void turnRight(float speed, float deltaTime) {
		float distance = speed * deltaTime;		
		yaw -= Math.toDegrees(distance);
	}
	
	public void turnUp(float speed, float deltaTime) {
		float distance = speed * deltaTime;		
		pitch += Math.toDegrees(distance);
	}
	
	public void turnDown(float speed, float deltaTime) {
		float distance = speed * deltaTime;		
		pitch -= Math.toDegrees(distance);
	}
	
}