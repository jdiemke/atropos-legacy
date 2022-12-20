package atropos.demos.window;

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
public class WindowDemo {

	public static void main(String[] args) {
		new AtroposWindow("Window Demo", 640, 480, 60,
				new AtroposDefaultRenderer());
	}

}