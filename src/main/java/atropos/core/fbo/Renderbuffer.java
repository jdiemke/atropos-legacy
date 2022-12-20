package atropos.core.fbo;


import com.jogamp.opengl.GL2;

public class Renderbuffer {
	
	private int renderbufferHandle;
	private int width;
	private int height;
	
	public static boolean isSupported(GL2 gl) {
		return gl.isExtensionAvailable("GL_EXT_framebuffer_object");
	}
	
	public static int getMaxRenderbufferSize(GL2 gl) {
		int[] temp = new int[1];
		gl.glGetIntegerv(GL2.GL_MAX_RENDERBUFFER_SIZE, temp, 0);
		return temp[0];
	}
	
	public Renderbuffer(GL2 gl) {
		int[] temp = new int[1];
		gl.glGenRenderbuffers(1, temp, 0);
		renderbufferHandle = temp[0];
	}
	
	public void bind(GL2 gl) {
		bindRenderbuffer(gl, renderbufferHandle);
	}
	
	public void setStorage(GL2 gl, int storageType, int width, int height) {
		int rb = getCurrentlyBoundRenderbuffer(gl);
		
		bindRenderbuffer(gl, renderbufferHandle);
		gl.glRenderbufferStorage(GL2.GL_RENDERBUFFER, storageType, width, height);
		//gl.glRenderbufferStorageMultisample(GL2.GL_RENDERBUFFER, 4,storageType, width, height);
		bindRenderbuffer(gl, rb);
		
		this.width = width;
		this.height = height;
	}
	
	public void delete(GL2 gl) {
		int[] temp = new int[]{renderbufferHandle};
		gl.glDeleteRenderbuffers(1, temp, 0);
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}

	public int getHandle() {
		return renderbufferHandle;
	}
	
	private int getCurrentlyBoundRenderbuffer(GL2 gl) {
		int[] temp = new int[1];
		gl.glGetIntegerv(GL2.GL_RENDERBUFFER_BINDING, temp, 0);
		return temp[0];
	}
	
	private void bindRenderbuffer(GL2 gl, int renderbufferHandle) {
		gl.glBindRenderbuffer(GL2.GL_RENDERBUFFER, renderbufferHandle);
	}

}