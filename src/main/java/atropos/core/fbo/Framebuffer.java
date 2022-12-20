package atropos.core.fbo;

import com.jogamp.opengl.GL2;

import atropos.core.texture.Texture2D;

/**
 * This class implements OpenGL Frame Buffer Objects (FBOs). By using FBOs the
 * OpenGL application can redirect the rendering to the application-created
 * framebuffer. A FBO can have several logical buffers for color, depth and
 * stencil (accumulation is not supported by FBOs). These logical buffers are
 * called framebuffer-attachable images and can be further distinguished as
 * texture images or renderbuffer images. renderbuffer images are used for
 * offscreen rendering while textures images can be used for render-to-texture
 * operations.
 * 
 * @author Johannes Diemke
 *
 */
public class Framebuffer {
	
	private int fboHandle;
	
	public static boolean isSupported(GL2 gl) {
		return gl.isExtensionAvailable("GL_EXT_framebuffer_object");
	}
	
	public static int getMaxColorAttachments(GL2 gl) {
		int[] temp = new int[1];
		gl.glGetIntegerv(GL2.GL_MAX_COLOR_ATTACHMENTS, temp, 0);
		return temp[0];
	}
	
	public static int getMaxDrawBuffers(GL2 gl) {
		int[] temp = new int[1];
		gl.glGetIntegerv(GL2.GL_MAX_DRAW_BUFFERS, temp, 0);
		return temp[0];
	}
	
	public static void setDrawBuffer(GL2 gl, int drawBuffer) {
		gl.glDrawBuffer(drawBuffer);
	}
	
	public static void setDrawBuffers(GL2 gl, int[] drawBuffers) {
		gl.glDrawBuffers(drawBuffers.length, drawBuffers, 0);
	}
	
	public Framebuffer(GL2 gl) {
		int[] temp = new int[1];
		gl.glGenFramebuffers(1, temp, 0);
		fboHandle = temp[0];
	}
	
	/**
	 * The attachment point can be GL_COLOR_ATTACHMENT0_EXT to GL_COLOR_ATTACHMENT15_EXT,
	 * GL_DEPTH_ATTACHMENT_EXT and GL_STENCIL_ATTACHMENT_EXT
	 * 
	 * @param gl
	 * @param attachmentPoint
	 * @param texture
	 */
	public void attach(GL2 gl, int attachmentPoint, Texture2D texture) {
		int fbo = getCurrentlyBoundFramebuffer(gl);
		
		bindFramebuffer(gl, fboHandle);
		gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, attachmentPoint, texture.target, texture.getHandle(), 0);
		
		bindFramebuffer(gl, fbo);
	}
	
	public void detach(GL2 gl, int attachmentPoint) {
		int fbo = getCurrentlyBoundFramebuffer(gl);
		
		bindFramebuffer(gl, fboHandle);
		gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, attachmentPoint, GL2.GL_TEXTURE_2D, 0, 0);
		
		bindFramebuffer(gl, fbo);
	}
	
	public void attach(GL2 gl, int attachmentPoint, Renderbuffer renderbuffer) {
		int fbo = getCurrentlyBoundFramebuffer(gl);
		
		bindFramebuffer(gl, fboHandle);
		gl.glFramebufferRenderbuffer(GL2.GL_FRAMEBUFFER, attachmentPoint, GL2.GL_RENDERBUFFER, renderbuffer.getHandle());
		
		bindFramebuffer(gl, fbo);
	}
	
//	public void detach(GL gl, int attachmentPoint) {
//		int fbo = getCurrentlyBoundFramebuffer(gl);
//		
//		bindFramebuffer(gl, fboHandle);
//		gl.glFramebufferRenderbufferEXT(GL.GL_FRAMEBUFFER_EXT, attachmentPoint, GL.GL_RENDERBUFFER_EXT, 0);
//		
//		bindFramebuffer(gl, fbo);
//	}
	
	public void bind(GL2 gl) {
		bindFramebuffer(gl, fboHandle);
	}
	
	public void unbind(GL2 gl) {
		bindFramebuffer(gl, 0);
	}
	
	public boolean isComplete(GL2 gl) {
		int fbo = getCurrentlyBoundFramebuffer(gl);
		
		bindFramebuffer(gl, fboHandle);
		int status = gl.glCheckFramebufferStatus(GL2.GL_FRAMEBUFFER);
		
		bindFramebuffer(gl, fbo);
		
		if(status == GL2.GL_FRAMEBUFFER_COMPLETE)
			return true;
		
		return false;
	}
	
	public String getStatus(GL2 gl) {
		int fbo = getCurrentlyBoundFramebuffer(gl);
		
		bindFramebuffer(gl, fboHandle);
		int status = gl.glCheckFramebufferStatus(GL2.GL_FRAMEBUFFER);
		
		bindFramebuffer(gl, fbo);
		
		switch(status) {
			case GL2.GL_FRAMEBUFFER_COMPLETE:
				return "complete";
			case GL2.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
				return "incomplete attachment";
			case GL2.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
				return "missing attachment";
			case GL2.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS:
				return "incomplete dimension";
			case GL2.GL_FRAMEBUFFER_INCOMPLETE_FORMATS:
				return "incomplete formats";
			case GL2.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER:
				return "incomplete draw buffer";
			case GL2.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER:
				return "incomplete read buffer";
			case GL2.GL_FRAMEBUFFER_UNSUPPORTED:
				return "framebuffer objects unsupported";
			default:
				return "unknown status";
		}
	}
	
	public void delete(GL2 gl) {
		int[] temp = new int[]{fboHandle};
		gl.glDeleteFramebuffers(1, temp, 0);
	}
	
	public int getHandle() {
		return fboHandle;
	}
	
	private int getCurrentlyBoundFramebuffer(GL2 gl) {
		int[] temp = new int[1];
		gl.glGetIntegerv(GL2.GL_FRAMEBUFFER_BINDING, temp, 0);
		return temp[0];
	}
	
	private void bindFramebuffer(GL2 gl, int fboHandle) {
		gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, fboHandle);
	}

}