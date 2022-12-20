package atropos.core;

import com.jogamp.opengl.GL2;

import atropos.core.math.PhotekVector3D;
import atropos.core.shader.ShaderProgram;
import atropos.core.shader.attrib.VertexAttrib3f;

public class PhotekTorusKnot extends PhotekDisplayList {


	
	public PhotekTorusKnot(GL2 gl, ShaderProgram program) {
		super(gl, program);
	
	}
	PhotekVector3D curve(float t, float p, float q) {
		float x = (float)(0.5 * (2.0 + Math.sin(q * t)) * Math.cos(p * t));
		float y = (float)(0.5 * (2.0 + Math.sin(q * t)) * Math.cos(q * t));
		float z = (float)(0.5 * (2.0 + Math.sin(q * t)) * Math.sin(p * t));
		
		return new PhotekVector3D(x,y,z);
	}
	
	PhotekVector3D diff1(float t, float p, float q) {
		float x_diff1 = (float)(0.5 * Math.cos(q * t) * q *
			    Math.cos(p * t) - 0.5 * (2.0 + Math.sin(q * t)) * Math.sin(p * t) * p);
		float y_diff1 = (float)(0.5 * (Math.cos(q * t) * Math.cos(q * t)) * q
				- 0.5 * (2.0 + Math.sin(q * t)) * Math.sin(q * t) * q);
		float z_diff1 = (float)(0.5 * Math.cos(q * t) * q *
				Math.sin(p * t) + 0.5 * (2.0 + Math.sin(q * t)) * Math.cos(p * t) * p);
		return new PhotekVector3D(x_diff1, y_diff1, z_diff1);

	}
	PhotekVector3D diff2(float t, float p, float q) {
	float x_diff2 = (float)(-0.5 * Math.sin(q * t) * (q * q) * Math.cos(p * t) - 1.0 * Math.cos(q * t)
			* q * Math.sin(p * t) * p -0.5 * (2.0  + Math.sin(q * t)) * Math.cos(p * t) * (p * p));
float y_diff2 = (float)(-1.5 * Math.cos(q * t) * (q * q) * Math.sin(q * t) - 0.5 *
		   (2.0  + Math.sin(q * t)) * Math.cos(q * t) * (q * q));
float z_diff2 = (float)(-0.5 * Math.sin(q * t) * (q * q) * Math.sin(p * t) + 1.0 * Math.cos(q * t)
* q * Math.cos(p * t) * p -0.5 * (2.0  + Math.sin(q * t)) * Math.sin(p * t) * (p * p));
return new PhotekVector3D(x_diff2, y_diff2, z_diff2);
	}
	
	public void fill(GL2 gl, VertexAttrib3f tangentA, boolean useTangent) {
		// 6,8
		// 5,3
		float p = 2; //2
		float q = 3;//3
		float lineSegments = 125; //320
		//******************VertexAttrib3f tangentA = prog.getVertexAttrib3f(gl, "tangent");
		
		for(int i=0; i < lineSegments; i++) {
			float t = (float)(2*Math.PI / (lineSegments-1) * i);
			PhotekVector3D curve = curve(t,p,q);
			PhotekVector3D tangent = diff1(t,p,q).getNormalized();
			//PhotekVector3D normal = tangent.cross(diff2(t,p,q)).cross(tangent).getNormalized();
			PhotekVector3D normal = tangent.cross(new PhotekVector3D(0,1,0));
			PhotekVector3D binormal = tangent.cross(normal);
			
			float t2 = (float)( 2*Math.PI/ (lineSegments-1) * (i+1));
			PhotekVector3D curve2 = curve(t2,p,q);
			PhotekVector3D tangent2 = diff1(t2,p,q).getNormalized();
			//PhotekVector3D normal2 = tangent2.cross(diff2(t2,p,q)).cross(tangent2).getNormalized();
			// dirty fix for the funking frenet frame twist problem
			// just use a vector ans normal that is never paralel
			// problem: vektor ist parallel => l�sung: einen nehmen, der nicht parallel ist

			PhotekVector3D normal2 = tangent2.cross(new PhotekVector3D(0,1,0));
			PhotekVector3D binormal2 = tangent2.cross(normal2);
			
			gl.glLineWidth(1.0f);
			gl.glBegin(GL2.GL_TRIANGLE_STRIP);
			int circleSegs =15; //30
			float radius = (float)(0.35f);//+0.08*Math.sin(32*2*Math.PI/(lineSegments-1)*i));
			float radius2 = (float)(0.35f);//+0.08*Math.sin(32*2*Math.PI/(lineSegments-1)*(i+1)));
			for(int j=0; j < circleSegs; j++) {
				
				float xx = (float)(Math.cos(2*Math.PI/(circleSegs-1)*j));
				float yy = (float)(Math.sin(2*Math.PI/(circleSegs-1)*j));
				
				float dxx = (float)(-Math.sin(2*Math.PI/(circleSegs-1)*j) * 2*Math.PI/(circleSegs-1));
				float dyy = (float)(Math.cos(2*Math.PI/(circleSegs-1)*j) * 2*Math.PI/(circleSegs-1));
				
				PhotekVector3D point2 = normal2.getRescaled(xx*radius2).add(binormal2.getRescaled(yy*radius2)).add(curve2);
				PhotekVector3D normals2 = normal2.getRescaled(xx).add(binormal2.getRescaled(yy));
				PhotekVector3D tangents2 = normal2.getRescaled(dxx).add(binormal2.getRescaled(dyy));
				gl.glNormal3f(normals2.x, normals2.y, normals2.z);
				gl.glTexCoord2f(1.0f/(circleSegs-1)*j*2, 1.0f/(lineSegments-1)*(i+1)*15);
				if(useTangent)
					tangentA.set(gl, tangents2.x,tangents2.y,tangents2.z);
				gl.glVertex4f(point2.x, point2.y, point2.z,1.f);
				
				PhotekVector3D point = normal.getRescaled(xx*radius).add(binormal.getRescaled(yy*radius)).add(curve);
				PhotekVector3D normals1 = normal.getRescaled(xx).add(binormal.getRescaled(yy));
				PhotekVector3D tangents1 = normal.getRescaled(dxx).add(binormal.getRescaled(dyy));
				gl.glNormal3f(normals1.x, normals1.y, normals1.z);
				gl.glTexCoord2f(1.0f/(circleSegs-1)*j*2, 1.0f/(lineSegments-1)*i*15);
				if(useTangent)
					tangentA.set(gl,tangents1.x,tangents1.y,tangents1.z);
				gl.glVertex4f(point.x, point.y, point.z,1.f);
				
				// sin(5*x) -> cos(5*x) *
			}
			gl.glEnd();
			
		}
	}
	
}