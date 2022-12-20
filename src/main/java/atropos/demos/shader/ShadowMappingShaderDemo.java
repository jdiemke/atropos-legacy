package atropos.demos.shader;

import atropos.AtroposWindow;

/**
 * WindowDemo.java
 * 
 * run with: -Dsun.awt.noerasebackground=true
 * 			 -Dsun.java2d.noddraw=true
 * 
 * @author Johannes Diemke
 */
public class ShadowMappingShaderDemo {

	public static void main(String[] args) {
		new AtroposWindow("Shader Demo", 640*2, 360*2, 60,
				new ShadowMappingShaderDemoRenderer());
	}

}