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
public class PhongShaderDemo {

	public static void main(String[] args) {
		new AtroposWindow("Torus Knot with Parallax Mapping", 800, 600, 60,
				new PhongShaderDemoRenderer());
	}

}