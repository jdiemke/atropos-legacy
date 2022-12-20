

varying float depth;

void main()
{
	gl_FrontColor = gl_Color;
	gl_TexCoord[0] = gl_MultiTexCoord0;
	
	vec4 eyeSpaceVertex =gl_ModelViewMatrix *gl_Vertex;
	vec4 eyeVec = -eyeSpaceVertex;
	  
	 depth =eyeVec.z;// (eyeVec.z-near)/(far-near);
	
	gl_Position = ftransform();	
}