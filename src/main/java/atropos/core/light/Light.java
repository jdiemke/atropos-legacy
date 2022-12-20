package atropos.core.light;

import com.jogamp.opengl.GL2;

public abstract class Light {
	
	protected float[] ambientLight_		= {0.0f, 0.0f, 0.0f, 1.0f};
	protected float[] diffuseLight_		= {1.0f, 1.0f, 1.0f, 1.0f};
	protected float[] specularLight_	= {1.0f, 1.0f, 1.0f, 1.0f};
	
	protected float	constantAttenuation_	= 1.0f;
	protected float	linearAttenuation_		= 0.0f;
	protected float	quadraticAttenuation_	= 0.0f;
	
	protected float[] lightPosition_	= {0.0f, 0.0f, 0.0f, 1.0f};
	
	protected float[]	spotDirection_			= {0.0f, 0.0f, -1.0f};
	protected float		spotExponent_			= 0.0f;
	protected float		spotCutoff_				= 180.0f;
	
	int light;
	
	public Light(int light) {
		this.light = light;
	}
	
	public void setAmbient(float red, float green, float blue, float alpha) {
		ambientLight_[0] = red;
		ambientLight_[1] = green;
		ambientLight_[2] = blue;
		ambientLight_[3] = alpha;
	}
	
	public void setDiffuse(float red, float green, float blue, float alpha) {
		diffuseLight_[0] = red;
		diffuseLight_[1] = green;
		diffuseLight_[2] = blue;
		diffuseLight_[3] = alpha;
	}
	
	public void setSpecular(float red, float green, float blue, float alpha) {
		specularLight_[0] = red;
		specularLight_[1] = green;
		specularLight_[2] = blue;
		specularLight_[3] = alpha;
	}
	
	public void setConstantAttenuation(float attenuation) {
		constantAttenuation_ = attenuation;
	}
	
	public void setLinearAttenuation(float attenuation) {
		linearAttenuation_ = attenuation;
	}
	
	public void setQuadraticAttenuation(float attenuation) {
		quadraticAttenuation_ = attenuation;
	}
	
	public void apply(GL2 gl) {
		gl.glLightfv(light, GL2.GL_AMBIENT, ambientLight_, 0);
		gl.glLightfv(light, GL2.GL_DIFFUSE, diffuseLight_, 0);
		gl.glLightfv(light, GL2.GL_SPECULAR, specularLight_, 0);
		
		gl.glLightf(light, GL2.GL_CONSTANT_ATTENUATION, constantAttenuation_);
		gl.glLightf(light, GL2.GL_LINEAR_ATTENUATION, linearAttenuation_);
		gl.glLightf(light, GL2.GL_QUADRATIC_ATTENUATION, quadraticAttenuation_);
		
		gl.glLightf(light, GL2.GL_SPOT_CUTOFF, spotCutoff_);
		gl.glLightf(light, GL2.GL_SPOT_EXPONENT, spotExponent_);
		gl.glLightfv(light, GL2.GL_SPOT_DIRECTION, spotDirection_, 0);
		
		gl.glLightfv(light, GL2.GL_POSITION, lightPosition_, 0);
	}
	
	public void enable(GL2 gl) {
		gl.glEnable(light);
	}
	
	public void disable(GL2 gl) {
		gl.glDisable(light);
	}
	
}