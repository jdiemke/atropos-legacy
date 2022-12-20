package atropos.demos.geometry;

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
public class VertexArrayDemo {

	public static void main(String[] args) {
		new AtroposWindow("Shader Demo", 640, 480, 60,
				new VertexArrayDemoRenderer());
	}

}