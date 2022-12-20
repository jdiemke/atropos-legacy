package atropos.demos.shader;

import atropos.AtroposDefaultRenderer;
import atropos.AtroposWindow;

/**
 * WindowDemo.java
 * 
 * run with: -Dsun.awt.noerasebackground=true
 * 			 -Dsun.java2d.noddraw=true
 * 
 * @author Johannes Diemke
 */
public class ToonShaderDemo {

	public static void main(String[] args) {
		new AtroposWindow("Shader Demo", 640, 480, 60,
				new ToonShaderDemoRenderer());
	}

}