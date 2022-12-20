package atropos.core.shader;

import com.jogamp.opengl.GL2;

import atropos.core.shader.attrib.VertexAttrib1f;
import atropos.core.shader.attrib.VertexAttrib2f;
import atropos.core.shader.attrib.VertexAttrib3f;
import atropos.core.shader.attrib.VertexAttrib4f;
import atropos.core.shader.uniform.Uniform1f;
import atropos.core.shader.uniform.Uniform1fv;
import atropos.core.shader.uniform.Uniform1i;
import atropos.core.shader.uniform.Uniform1iv;
import atropos.core.shader.uniform.Uniform2f;
import atropos.core.shader.uniform.Uniform2fv;
import atropos.core.shader.uniform.Uniform2i;
import atropos.core.shader.uniform.Uniform2iv;
import atropos.core.shader.uniform.Uniform3f;
import atropos.core.shader.uniform.Uniform3fv;
import atropos.core.shader.uniform.Uniform3i;
import atropos.core.shader.uniform.Uniform3iv;
import atropos.core.shader.uniform.Uniform4f;
import atropos.core.shader.uniform.Uniform4fv;
import atropos.core.shader.uniform.Uniform4i;
import atropos.core.shader.uniform.Uniform4iv;
import atropos.core.shader.uniform.UniformMatrix2fv;
import atropos.core.shader.uniform.UniformMatrix3fv;
import atropos.core.shader.uniform.UniformMatrix4fv;

public class ShaderProgram {
	
	private int programHandle;
	
	public ShaderProgram(GL2 gl) {
		programHandle = gl.glCreateProgram();
	}
	
	public void attachShader(GL2 gl, Shader shader) {
		gl.glAttachShader(programHandle, shader.getHandle());
	}
	
	public void detachShader(GL2 gl, Shader shader) {
		gl.glDetachShader(programHandle, shader.getHandle());
	}
	
	public void linkProgram(GL2 gl) {
		gl.glLinkProgram(programHandle);
	}
	
	public boolean isLinked(GL2 gl) {
		int[] status = new int[1];
		gl.glGetProgramiv(programHandle, GL2.GL_LINK_STATUS, status, 0);
		
		if(status[0] == GL2.GL_TRUE) {
			return true;
		}
		
		return false;
	}
	
	public String getInfoLog(GL2 gl) {	
		int[] infoLogLength = new int[1];
		gl.glGetProgramiv(programHandle, GL2.GL_INFO_LOG_LENGTH, infoLogLength, 0);
		System.out.println("I LENGTH "+infoLogLength[0]);
		System.out.println("I LENGTH "+infoLogLength[0]);
		byte[] infoLog = new byte[infoLogLength[0]];
		int[] length = new int[1];
		if (infoLogLength[0] >0 ) {
			gl.glGetProgramInfoLog(programHandle, infoLogLength[0], length, 0, infoLog, 0);

			return new String(infoLog, 0, infoLog.length - 1);
		}
		return "";

	}
	
	public void activate(GL2 gl) {
		gl.glUseProgram(programHandle);
	}
	
	public void deactivate(GL2 gl) {
		gl.glUseProgram(0);
	}
	
	public void deleteProgram(GL2 gl) {
		gl.glDeleteProgram(programHandle);
	}
	
	public Uniform1f getUniform1f(GL2 gl, String name) {
		return new Uniform1f(gl.glGetUniformLocation(programHandle, name));
	}
	
	public Uniform2f getUniform2f(GL2 gl, String name) {
		return new Uniform2f(gl.glGetUniformLocation(programHandle, name));
	}
	
	public Uniform3f getUniform3f(GL2 gl, String name) {
		return new Uniform3f(gl.glGetUniformLocation(programHandle, name));
	}
	
	public Uniform4f getUniform4f(GL2 gl, String name) {
		return new Uniform4f(gl.glGetUniformLocation(programHandle, name));
	}
	
	public Uniform1fv getUniform1fv(GL2 gl, String name) {
		return new Uniform1fv(gl.glGetUniformLocation(programHandle, name));
	}
	
	public Uniform2fv getUniform2fv(GL2 gl, String name) {
		return new Uniform2fv(gl.glGetUniformLocation(programHandle, name));
	}
	
	public Uniform3fv getUniform3fv(GL2 gl, String name) {
		return new Uniform3fv(gl.glGetUniformLocation(programHandle, name));
	}
	
	public Uniform4fv getUniform4fv(GL2 gl, String name) {
		return new Uniform4fv(gl.glGetUniformLocation(programHandle, name));
	}
	
	public Uniform1i getUniform1i(GL2 gl, String name) {
		return new Uniform1i(gl.glGetUniformLocation(programHandle, name));
	}
	
	public Uniform2i getUniform2i(GL2 gl, String name) {
		return new Uniform2i(gl.glGetUniformLocation(programHandle, name));
	}
	
	public Uniform3i getUniform3i(GL2 gl, String name) {
		return new Uniform3i(gl.glGetUniformLocation(programHandle, name));
	}
	
	public Uniform4i getUniform4i(GL2 gl, String name) {
		return new Uniform4i(gl.glGetUniformLocation(programHandle, name));
	}
	
	public Uniform1iv getUniform1iv(GL2 gl, String name) {
		return new Uniform1iv(gl.glGetUniformLocation(programHandle, name));
	}
	
	public Uniform2iv getUniform2iv(GL2 gl, String name) {
		return new Uniform2iv(gl.glGetUniformLocation(programHandle, name));
	}
	
	public Uniform3iv getUniform3iv(GL2 gl, String name) {
		return new Uniform3iv(gl.glGetUniformLocation(programHandle, name));
	}
	
	public Uniform4iv getUniform4iv(GL2 gl, String name) {
		return new Uniform4iv(gl.glGetUniformLocation(programHandle, name));
	}
	
	public UniformMatrix2fv getUniformMatrix2fv(GL2 gl, String name) {
		return new UniformMatrix2fv(gl.glGetUniformLocation(programHandle, name));
	}
	
	public UniformMatrix3fv getUniformMatrix3fv(GL2 gl, String name) {
		return new UniformMatrix3fv(gl.glGetUniformLocation(programHandle, name));
	}
	
	public UniformMatrix4fv getUniformMatrix4v(GL2 gl, String name) {
		return new UniformMatrix4fv(gl.glGetUniformLocation(programHandle, name));
	}
	
	public VertexAttrib1f getVertexAttrib1f(GL2 gl, String name) {
		return new VertexAttrib1f(gl.glGetAttribLocation(programHandle, name));
	}
	
	public VertexAttrib2f getVertexAttrib2f(GL2 gl, String name) {
		return new VertexAttrib2f(gl.glGetAttribLocation(programHandle, name));
	}
	
	public VertexAttrib3f getVertexAttrib3f(GL2 gl, String name) {
		return new VertexAttrib3f(gl.glGetAttribLocation(programHandle, name));
	}
	
	public VertexAttrib4f getVertexAttrib4f(GL2 gl, String name) {
		return new VertexAttrib4f(gl.glGetAttribLocation(programHandle, name));
	}

}