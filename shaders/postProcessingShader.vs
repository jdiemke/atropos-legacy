
varying vec4 eyeSpaceVertex;

void main()
{

	eyeSpaceVertex =gl_ModelViewMatrix *gl_Vertex;
	
	
	
	gl_FrontColor = gl_Color;
	gl_TexCoord[0] = gl_MultiTexCoord0;
	gl_Position = ftransform();
	
	
}