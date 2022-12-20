package atropos.core.shader;

import com.jogamp.opengl.GL2;

public abstract class Shader {
	
	protected int shaderHandle;
	
	public void setShaderSource(GL2 gl, ShaderSource shaderSource) {
		String[] source = shaderSource.getShaderSource();
		gl.glShaderSource(shaderHandle, source.length, source, null);
	}
	
	public void compileShader(GL2 gl) {
		gl.glCompileShader(shaderHandle);
	}

	public void deleteShader(GL2 gl) {
		gl.glDeleteShader(shaderHandle);
	}
	
	public int getHandle() {
		return shaderHandle;
	}
	
	public boolean isCompiled(GL2 gl) {
		int[] status = new int[1];
		gl.glGetShaderiv(shaderHandle, GL2.GL_COMPILE_STATUS, status, 0);
		
		if(status[0] == GL2.GL_TRUE) {
			return true;
		}
		
		return false;
	}
	
	public String getInfoLog(GL2 gl) {
		int[] infoLogLength = new int[1];
		gl.glGetShaderiv(shaderHandle, GL2.GL_INFO_LOG_LENGTH, infoLogLength, 0);
	
		byte[] infoLog = new byte[infoLogLength[0]];
		int[] length = new int[1];

		if (infoLogLength[0]> 0) {
			gl.glGetShaderInfoLog(shaderHandle, infoLogLength[0], length, 0, infoLog, 0);

			return new String(infoLog, 0, infoLog.length - 1);
		}

		return "";
	}

}