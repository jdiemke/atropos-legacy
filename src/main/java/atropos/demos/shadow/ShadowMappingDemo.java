package atropos.demos.shadow;

import atropos.AtroposWindow;

/**
 * WindowDemo.java
 * 
 * run with: -Dsun.awt.noerasebackground=true
 * 			 -Dsun.java2d.noddraw=true
 * 
 * @author Johannes Diemke
 */
public class ShadowMappingDemo {

	public static void main(String[] args) {
		new AtroposWindow("Shader Demo", 640, 360,
				new ShadowMappingDemoRenderer());
	}

}