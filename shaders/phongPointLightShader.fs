/*	phongPointLightShader.fs
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
	vec4 finalColor;

	finalColor = gl_FrontLightModelProduct.sceneColor +
				 gl_FrontLightProduct[0].ambient * attenuation;

	vec3 normal =  normalize(eyeSpaceNormal);
	vec3 lightDirection = normalize(eyeSpaceLightDirection);
	
	float lambertTerm = dot(normal, lightDirection);
	
	if(lambertTerm > 0.0) {
		finalColor += gl_FrontLightProduct[0].diffuse * lambertTerm * attenuation;
					  
		vec3 eyeVector = normalize(eyeSpaceEyeVector);
		vec3 reflectionVector = reflect(-lightDirection, normal);
		
		float specular = pow(max(dot(reflectionVector, eyeVector), 0.0),
						 gl_FrontMaterial.shininess);
						 
		finalColor += gl_FrontLightProduct[0].specular * specular * attenuation;
	}
	
	gl_FragColor = finalColor;
}