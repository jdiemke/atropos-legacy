/*	phongPointLightShader.vs
 *
 *	code:	trigger
 *	date:	2009-09-08
 *	desc:	this shader pair implements the standard phong light model for
 *			a single point light. all calculations are done in eye space per
 *			fragment.
 *
 *			see http://www.ozone3d.net/tutorials/glsl_lighting_phong.php
 */

varying vec3 eyeSpaceNormal;
varying vec3 eyeSpaceLightDirection;
varying vec3 eyeSpaceEyeVector;

varying float attenuation;

void main()
{
	vec3 eyeSpaceVertex;
	
	eyeSpaceVertex= vec3(gl_ModelViewMatrix * gl_Vertex);
	eyeSpaceNormal = gl_NormalMatrix * gl_Normal;
	eyeSpaceLightDirection = gl_LightSource[0].position.xyz - eyeSpaceVertex;
	eyeSpaceEyeVector = -eyeSpaceVertex;
	
	float distance = length(eyeSpaceLightDirection);
	
	attenuation = 1.0 / (gl_LightSource[0].constantAttenuation +
						 gl_LightSource[0].linearAttenuation * distance +
						 gl_LightSource[0].quadraticAttenuation * distance * distance);
						 
	float radius = 14.0;							 
	attenuation = 1.0;//pow(clamp(1.0 - distance / radius, 0.0, 1.0), 4.0); 						 
	
	gl_Position = ftransform();
}