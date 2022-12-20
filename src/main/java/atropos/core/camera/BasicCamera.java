package atropos.core.camera;

import com.jogamp.opengl.GL2;

import atropos.core.math.Matrix4f;
import atropos.core.math.Vector3f;

public class BasicCamera {
	
	protected Vector3f	position;
	
	protected float	yaw;	//left right looking
	protected float	pitch;	// lookign up down
	protected float	roll;	// tilt your head
	
	public BasicCamera(Vector3f position, float yaw, float pitch, float roll) {
		this.position	= position;
		this.yaw		= yaw;
		this.pitch		= pitch;
		this.roll		= roll;
	}
	
	public BasicCamera(Vector3f position) {
		this(position, 0.0f, 0.0f, 0.0f);
	}
	
	public BasicCamera() {
		this(new Vector3f(0.0f, 0.0f, 0.0f));
	}
	
	public void applyCamera(GL2 gl) {
		gl.glLoadIdentity();
		
		gl.glRotatef(-this.roll, 0.0f, 0.0f, 1.0f);
		gl.glRotatef(-this.pitch, 1.0f, 0.0f, 0.0f);
		gl.glRotatef(-this.yaw, 0.0f, 1.0f, 0.0f);
		
		gl.glTranslatef(-this.position.x, -this.position.y, -this.position.z);
	}
	
	public Matrix4f getViewMatrix() {		
		Matrix4f roll  = Matrix4f.constructZRotationMatrix(-this.roll);
		Matrix4f pitch = Matrix4f.constructXRotationMatrix(-this.pitch);
		Matrix4f yaw   = Matrix4f.constructYRotationMatrix(-this.yaw);
		
		Matrix4f translation = Matrix4f.constructTranslationMatrix(-this.position.x, -this.position.y, -this.position.z);
		
		return roll.multiply(pitch).multiply(yaw).multiply(translation);
	}
	
	public Matrix4f getInverseViewMatrix() {		
		Matrix4f viewMatrix = getViewMatrix();
		
		// transpose of the 3x3 rotation submatrix
		Matrix4f inverseRotation = new Matrix4f(viewMatrix.m11,viewMatrix.m21, viewMatrix.m31, 0.0f,
												viewMatrix.m12,viewMatrix.m22, viewMatrix.m32, 0.0f,
												viewMatrix.m13,viewMatrix.m23, viewMatrix.m33, 0.0f,
												          0.0f,          0.0f,           0.0f, 1.0f);
		
		Matrix4f inverseTranslation = new Matrix4f(1.0f, 0.0f, 0.0f, this.position.x,
												   0.0f, 1.0f, 0.0f, this.position.y,
												   0.0f, 0.0f, 1.0f, this.position.z,
												   0.0f, 0.0f, 0.0f,            1.0f);		
		
		return inverseTranslation.multiply(inverseRotation);
	}
	
	public void setPosition(Vector3f position) {
		this.position = position;
	}
	
	public void setRoll(float roll) {
		this.roll = roll;
	}
	
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
	
	public void setYaw(float yaw) {
		this.yaw = yaw;
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public float getRoll() {
		return roll;
	}
	
	public float getPitch() {
		return pitch;
	}
	
	public float getYaw() {
		return yaw;
	}
	
}