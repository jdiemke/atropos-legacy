package atropos.core;

import com.jogamp.opengl.GL2;

public class Material {

	private float[] matAmbient_ = { 0.0f, 0.0f, 0.0f, 1.0f };
	private float[] matDiffuse_ = { 0.0f, 0.0f, 0.0f, 1.0f };
	private float[] matSpecular_ = { 0.0f, 0.0f, 0.0f, 1.0f };
	private float[] matEmission_ = { 0.0f, 0.0f, 0.0f, 1.0f };

	private float matShininess_ = 0.0f;

	public Material() {

	}

	public void setAmbient(float red, float green, float blue, float alpha) {
		matAmbient_[0] = red;
		matAmbient_[1] = green;
		matAmbient_[2] = blue;
		matAmbient_[3] = alpha;
	}

	public void setDiffuse(float red, float green, float blue, float alpha) {
		matDiffuse_[0] = red;
		matDiffuse_[1] = green;
		matDiffuse_[2] = blue;
		matDiffuse_[3] = alpha;
	}

	public void setSpecular(float red, float green, float blue, float alpha) {
		matSpecular_[0] = red;
		matSpecular_[1] = green;
		matSpecular_[2] = blue;
		matSpecular_[3] = alpha;
	}

	public void setEmission(float red, float green, float blue, float alpha) {
		matEmission_[0] = red;
		matEmission_[1] = green;
		matEmission_[2] = blue;
		matEmission_[3] = alpha;
	}
	
	
	public void setAmbient(Color ambientColor) {
		matAmbient_ = ambientColor.getColorVector();
	}

	public void setDiffuse(Color diffuseColor) {
		matDiffuse_ = diffuseColor.getColorVector();
	}
	
	public void setSpecular(Color specularColor) {
		matSpecular_ = specularColor.getColorVector();
	}
	
	public void setEmission(Color emissionColor) {
		matEmission_ = emissionColor.getColorVector();
	}

	public void setShininess(float shininess) {
		// 1.0f .. 128.0f
		if(shininess>128) shininess =128;
		matShininess_ = shininess;
	}

	public void apply(GL2 gl) {
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, matAmbient_, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, matDiffuse_, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, matSpecular_, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, matEmission_, 0);
		gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, matShininess_);
	}

}