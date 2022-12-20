
// shadow stuff
uniform mat4 lightBPV;
varying vec3 shadowMapPos;
attribute vec3 tangent;

//point light stuff
varying vec3 normal;
varying vec3 lightDir;
varying vec3 eyeVec;
varying vec4 eyeSpaceVertex;
varying vec4 pos;

void main()
{
	// point light stuff
	normal = gl_NormalMatrix * gl_Normal;
	eyeSpaceVertex =gl_ModelViewMatrix *gl_Vertex;
	lightDir = vec3(gl_LightSource[0].position.xyz - vec3(eyeSpaceVertex));
	eyeVec = -eyeSpaceVertex;
	
	 pos =  lightBPV * gl_ModelViewMatrix * gl_Vertex;
	


	
	gl_TexCoord[0] = gl_MultiTexCoord0;
	gl_Position =  ftransform();
}
