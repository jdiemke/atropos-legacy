package atropos.demos.collision;

import com.jogamp.opengl.GL2;

import atropos.core.math.Vector2f;

public class Circle {

	public Vector2f position;
	public float radius;
	
	public Circle(Vector2f position, float radius) {
		this.position = position;
		this.radius = radius;
	}
	
	public void draw(GL2 gl) {
		
		gl.glPushMatrix();
		gl.glTranslatef(position.x, position.y,0);
		gl.glBegin(GL2.GL_LINE_LOOP);
		
		for(int i=0; i < 20; i++) {
			float x = (float)(radius * Math.cos(2*Math.PI/20*i));
			float y = (float)(radius * Math.sin(2*Math.PI/20*i));
			
			gl.glVertex3f(x,y,0);
		}
		
		gl.glEnd();
		gl.glPopMatrix();
	}

}
