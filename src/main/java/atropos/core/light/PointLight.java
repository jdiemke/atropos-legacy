package atropos.core.light;

public class PointLight extends Light {

	public PointLight(int light) {
		super(light);
	}
	
	public void setPosition(float x, float y, float z) {
		lightPosition_[0] = x;
		lightPosition_[1] = y;
		lightPosition_[2] = z;
		lightPosition_[3] = 1;
	}

}
