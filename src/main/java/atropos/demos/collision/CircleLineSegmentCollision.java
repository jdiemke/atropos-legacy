package atropos.demos.collision;

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
public class CircleLineSegmentCollision {

	public static void main(String[] args) {
		new AtroposWindow("Torus Knot with Parallax Mapping", 640, 360, 60,
				new CircleLineSegmentCollisionRenderer());
	}

}