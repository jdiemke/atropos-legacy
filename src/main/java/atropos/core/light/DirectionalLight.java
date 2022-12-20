package atropos.core.light;

public class DirectionalLight extends Light {
	
	public DirectionalLight(int light) {
		super(light);
	}
	
	public void setDirection(float x, float y, float z) {
		lightPosition_[0] = x;
		lightPosition_[1] = y;
		lightPosition_[2] = z;
		lightPosition_[3] = 0;
	}

}