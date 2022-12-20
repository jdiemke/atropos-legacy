package atropos.core.shader;

import com.jogamp.opengl.GL2;

public class VertexShader extends Shader {
	
	public VertexShader(GL2 gl) {
		shaderHandle = gl.glCreateShader(GL2.GL_VERTEX_SHADER);
	}
	
}