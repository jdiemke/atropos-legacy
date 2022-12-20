package atropos.demos.demoeffects;

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
public class RotoZoomDemo {

	public static void main(String[] args) {
		new AtroposWindow("Shader Demo", 640, 360, 60,
				new  RotoZoomDemoRenderer());
	}

}