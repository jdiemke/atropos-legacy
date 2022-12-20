package atropos.core.texture;

import com.jogamp.opengl.GL2;

public class Texture2D {

	private int textureHandle;
	private int width;
	private int height;
	public int target;

	public static boolean isNPOTSupported(GL2 gl) {
		return gl.isExtensionAvailable("GL_ARB_texture_non_power_of_two");
	}

	public void enable(GL2 gl) {
		gl.glEnable(target);
	}

	public void disable(GL2 gl) {
		gl.glDisable(target);
	}

	public Texture2D(GL2 gl, int target) {
		int[] temp = new int[1];
		gl.glGenTextures(1, temp, 0);
		textureHandle = temp[0];
		this.target = target;
	}

	public void bind(GL2 gl) {
		bindTexture(gl, textureHandle);
	}

	public void unbind(GL2 gl) {
		bindTexture(gl, 0);
	}

	/**
	 * storageType can be GL_RGBA, GL_DEPTH_COMPONENT, GL_LUMINANCE, GL_DEPTH_COMPONENT24_EXT,
	 * GL_STENCIL_INDEX, GL_STENCIL_INDEX8_EXT
	 *
	 * @param gl
	 * @param storageType
	 * @param width
	 * @param height
	 */
	public void setStorage(GL2 gl, int internalFormat, int storageType, int width, int height) {
		int tex = getCurrentlyBoundTexture(gl);

		bindTexture(gl, textureHandle);
		if(target == GL2.GL_TEXTURE_2D)
			gl.glTexImage2D(target, 0, internalFormat, width, height, 0, storageType, GL2.GL_UNSIGNED_BYTE, null);
		else
			gl.glTexImage2DMultisample(target, 4,internalFormat, width, height,false);
		bindTexture(gl, tex);

		this.width = width;
		this.height = height;
	}

	public void setMinFilter(GL2 gl, int minificationFilter) {
		int tex = getCurrentlyBoundTexture(gl);

		bindTexture(gl, textureHandle);
		gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, minificationFilter);
		//gl.glTexParameterf(target, GL2.GL_TEXTURE_MIN_FILTER, minificationFilter);
		bindTexture(gl, tex);
	}

	public void setMagFilter(GL2 gl, int magnificationFilter) {
		int tex = getCurrentlyBoundTexture(gl);

		bindTexture(gl, textureHandle);
		gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, magnificationFilter);
		//gl.glTexParameterf(target, GL.GL_TEXTURE_MAG_FILTER, magnificationFilter);
		bindTexture(gl, tex);
	}

	public void setWrapS(GL2 gl, int wrapMode) {
		int tex = getCurrentlyBoundTexture(gl);

		bindTexture(gl, textureHandle);
		gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, wrapMode);
		//gl.glTexParameterf(target, GL2.GL_TEXTURE_WRAP_S, wrapMode);
		bindTexture(gl, tex);
	}

	public void setWrapT(GL2 gl, int wrapMode) {
		int tex = getCurrentlyBoundTexture(gl);

		bindTexture(gl, textureHandle);
		gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, wrapMode);
		//gl.glTexParameterf(target, GL.GL_TEXTURE_WRAP_T, wrapMode);

		bindTexture(gl, tex);
	}

	/**
	 * if set to GL_TRUE, then glTex{Sub}Image2D() and glCopyTex{Sub}Image2D()
	 *  triggers mipmap generation
	 * automatically
	 *
	 * @param gl
	 * @param wrapMode
	 */
	public void setGenerateMipmap(GL2 gl, int mipmapMode) {
		int tex = getCurrentlyBoundTexture(gl);

		bindTexture(gl, textureHandle);
		gl.glTexParameteri(target, GL2.GL_GENERATE_MIPMAP, mipmapMode);

		bindTexture(gl, tex);
	}

	/**
	 * when called this methid generates the mipmap manually. its used for textures attached
	 * to an fbo. in this case mip maps should be generated manually. FBO operation does not
	 * generate its mipmaps automatically when the base level texture is modified because FBO
	 * does not call glCopyTex{Sub}Image2D() to modify the texture. Therefore,
	 * glGenerateMipmapEXT() must be explicitly called for mipmap generation.
	 *
	 * @param gl
	 */
	public void generateMipmap(GL2 gl) {
		int tex = getCurrentlyBoundTexture(gl);

		bindTexture(gl, textureHandle);
		gl.glGenerateMipmap(target);

		bindTexture(gl, tex);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void delete(GL2 gl) {
		int[] temp = new int[]{textureHandle};
		gl.glDeleteTextures(1, temp, 0);
	}

	public int getHandle() {
		return textureHandle;
	}

	private int getCurrentlyBoundTexture(GL2 gl) {
		int[] temp = new int[1];
		gl.glGetIntegerv(GL2.GL_TEXTURE_BINDING_2D, temp, 0);
		return temp[0];
	}

	private void bindTexture(GL2 gl, int textureHandle) {
		//gl.glBindTexture(GL.GL_TEXTURE_2D, textureHandle);
		gl.glBindTexture(target, textureHandle);
		
	}

}