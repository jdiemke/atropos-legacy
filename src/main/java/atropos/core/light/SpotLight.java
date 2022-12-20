package atropos.core.light;

public class SpotLight extends PointLight {
	
	public SpotLight(int light) {
		super(light);
	}

	public void setSpotDirection(float x, float y, float z) {
		spotDirection_[0] = x;
		spotDirection_[1] = y;
		spotDirection_[2] = z;
	}
	
	public void setSpotExponent(float exponent) {
		spotExponent_ = exponent;
	}
	
	public void setSpotCutoff(float cutoff) {
		spotCutoff_ = cutoff;
	}
	
}