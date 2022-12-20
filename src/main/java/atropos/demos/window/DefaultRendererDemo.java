package atropos.demos.window;

import atropos.AtroposWindow;

/**
 * DefaultRendererDemo.java
 * 
 * run with: -Dsun.awt.noerasebackground=true
 * 			 -Dsun.java2d.noddraw=true
 * 			 -Dsun.java2d.opengl=true
 * 
 * @author Johannes Diemke
 */
public class DefaultRendererDemo {

	public static void main(String[] args) {
		new AtroposWindow("DefaultRenderer Demo", 640, 480, 60);
	}

}