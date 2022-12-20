package atropos.demos.springsystem;

import atropos.core.math.Vector3f;

public class Particle {
	
	Vector3f position = new Vector3f();
	Vector3f oldPosition = new Vector3f();
	Vector3f acceleration = new Vector3f();
	float mass = 1.0f;
	float damping = 0.5f;
	boolean movable = true;
	
	public Particle() {
	}
	
	void addForce(Vector3f force) {
		acceleration.add(force.divide(mass));
	}
	
	void timeStep() {
		float timeStepSize2 = 1.0f;
		if(movable) {
			Vector3f temp = position;
			position = position.add(position.substract(oldPosition).multiply(1.0f-damping)).add(acceleration.multiply(timeStepSize2));
			oldPosition = temp;
			acceleration = new Vector3f();
		}
	}

}
