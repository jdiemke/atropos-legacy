package atropos.demos.robotsoccer;

import atropos.AtroposWindow;

/**
 * WindowDemo.java
 * 
 * run with: -Dsun.awt.noerasebackground=true
 * 			 -Dsun.java2d.noddraw=true
 * 
 * @author Johannes Diemke
 */
public class HarmonicPathPlanningDemo {

	public static void main(String[] args) {
		new AtroposWindow("HPF Path Planning Test Environment", 640, 360, 
				new  HarmonicPathPlanningDemoRenderer());
	}

}