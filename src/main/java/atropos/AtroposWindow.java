package atropos;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;

import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;



@SuppressWarnings("serial")
public class AtroposWindow extends Frame{
	
	private GLCanvas canvas;
	private AnimatorBase animator;
	private AtroposDefaultRenderer renderer;
	
	static {
		GLProfile.initSingleton();
	}
	
	private AtroposWindow(String caption, int width, int height, Integer fps, AtroposDefaultRenderer renderer) {
		setTitle(caption);
		setLocation(81, 276);
		
		GLProfile glp = GLProfile.get(GLProfile.GL2);
		GLCapabilities caps = new GLCapabilities(glp);
		
		
		
	
	    caps.setNumSamples(4);
	    caps.setAlphaBits(16);
	   // caps.setSampleBuffers(true);
		caps.setDoubleBuffered(true);
		caps.setHardwareAccelerated(true);
		
		
		
		this.renderer = renderer;
		if(renderer == null) {
			this.renderer = new AtroposDefaultRenderer();
		}
		
		canvas = new GLCanvas(caps);
		canvas.addGLEventListener(renderer);
		canvas.setPreferredSize(new Dimension(width, height));
		
		canvas.addMouseListener(renderer);
		canvas.addKeyListener(renderer);
		canvas.setFocusable(true);
		canvas.requestFocus();
		
		this.add(canvas);
		this.pack();	
		
		if(fps == null) {
			animator = new Animator(canvas);
		} else {
			animator = new FPSAnimator(canvas, fps);
		}
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// Run this on another thread than the AWT event queue to
				// make sure the call to Animator.stop() completes before
				// exiting
				new Thread(new Runnable() {
					public void run() {
						animator.stop();
		                System.exit(0);
					}
				}).start();
			}
		});
		
		this.setVisible(true);
		animator.start();
		
		canvas.addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				// TODO Auto-generated method stub
				AtroposWindow.this.renderer.mousex = e.getX();
				AtroposWindow.this.renderer.mousey = e.getY();
				
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public AtroposWindow(String caption, int width, int height, int fps, AtroposDefaultRenderer renderer) {
		this(caption, width, height, (Integer)fps, renderer);
	}
	
	public AtroposWindow(String caption, int width, int height, int fps) {
		this(caption, width, height, (Integer)fps, null);
	}
	
	public AtroposWindow(String caption, int width, int height, AtroposDefaultRenderer renderer) {
		this(caption, width, height, (Integer)null, renderer);
	}
	
	public AtroposWindow(String caption, int width, int height) {
		this(caption, width, height, (Integer)null, null);
	}

}