package atropos.core.shader;

import com.jogamp.opengl.GL2;

public class FragmentShader extends Shader {
	
	public FragmentShader(GL2 gl) {
		shaderHandle = gl.glCreateShader(GL2.GL_FRAGMENT_SHADER);
	}

}