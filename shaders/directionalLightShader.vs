varying vec3 eyeSpaceNormal;
varying vec3 eyeSpaceLightDirection;
varying vec3 eyeSpaceHalfVector;

varying vec4 diffuse;
varying vec4 ambient;

void main()
{
	vec4 color;
	
	eyeSpaceNormal = normalize(gl_NormalMatrix * gl_Normal);
	eyeSpaceLightDirection = normalize(gl_LightSource[0].position.xyz);
	eyeSpaceHalfVector = normalize(gl_LightSource[0].halfVector.xyz);
	
	ambient = gl_FrontMaterial.ambient * gl_LightSource[0].ambient +
			  gl_FrontMaterial.ambient * gl_LightModel.ambient;
			  
	diffuse = gl_FrontMaterial.diffuse * gl_LightSource[0].diffuse;

	gl_Position = ftransform();
}