package atropos.demos.cellular;

import java.util.Random;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import atropos.AtroposDefaultRenderer;


public class CellularEffectDemoRenderer extends AtroposDefaultRenderer {
	
	long start = System.currentTimeMillis();
	
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		
		renderCellularEffect(gl, (start - System.currentTimeMillis())*0.0004f);
	}
	
	void renderCellularEffect(GL2 gl, float time )
	{
	    // set 2d mode
	    gl.glMatrixMode(GL2.GL_PROJECTION );
	    gl.glLoadIdentity();
	    gl.glMatrixMode(GL2.GL_MODELVIEW );
	    gl.glLoadIdentity();

	    // clear the buffers
	    gl.glClear( GL.GL_COLOR_BUFFER_BIT|GL.GL_DEPTH_BUFFER_BIT );
	    gl.glEnable( GL.GL_DEPTH_TEST );

	    // render the cells
	    int numCells =200;
	    int sem = 134;
	    Random rand = new Random(12);
	    for( int j=0; j < numCells; j++ )
	    {
	        // move the cell on the screen
	        float x = (float)Math.cos( rand.nextDouble()*3.14f + rand.nextDouble()*time );
	        float y = (float)Math.cos( rand.nextDouble()*3.14f + rand.nextDouble()*time );

	        // render cone (can be optimized of course)
	        gl.glBegin( GL.GL_TRIANGLE_FAN );
	        
	        gl.glColor4d( rand.nextDouble(), rand.nextDouble(), rand.nextDouble(), 1.0f );
	        gl.glVertex3f( x, y, -1.0f );
	       // gl.glColor4f(0.002f-1, 0.010f-1, 0.005f-1, 1.0f );
	       // gl.glColor4d( rand.nextDouble(), rand.nextDouble(), rand.nextDouble(), 1.0f );
	        for(int  i=0; i < 45; i++ )
	        {
	            float an = (6.28318f/44.0f)*(float)i;
	            gl.glVertex3f( x+(float)Math.cos(an)*0.5f, y+	(float)Math.sin(an)*0.5f,1.0f );
	        }
	        gl.glEnd();
	    }
	}


}