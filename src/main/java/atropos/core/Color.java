package atropos.core;

public class Color {
	
	public final static Color ORANGE = new Color(1.0f, 0.6f, 0.2f, 1.0f); 
	
	private float[] color_ = { 0.0f, 0.0f, 0.0f, 1.0f };
	
	public Color() {
		
	}
	
	public Color(float red, float green, float blue, float alpha) {
		color_[0] = red;
		color_[1] = green;
		color_[2] = blue;
		color_[3] = alpha;
	}
	
	public float[] getColorVector() {
		return color_;
	}

}