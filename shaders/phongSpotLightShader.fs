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

void main()
{
	vec4 finalColor;

	finalColor = gl_FrontLightModelProduct.sceneColor +
				 gl_FrontLightProduct[0].ambient;

	vec3 normal =  normalize(eyeSpaceNormal);
	vec3 lightDirection = normalize(eyeSpaceLightDirection);
	vec3 spotDirection = normalize(gl_LightSource[0].spotDirection);
	
	float lambertTerm = dot(normal, lightDirection);
	
	float innerAngle = 7.0; // put one or both of them into a uniform to
	float outerAngle = 8.0; // controll from the application
	float currentAngle = degrees(acos(dot(-lightDirection, spotDirection)));
	float spot = pow(clamp(1.0 - (currentAngle - innerAngle) / (outerAngle - innerAngle), 0.0, 1.0), 8.0);
	
	if(lambertTerm > 0.0) {
		finalColor += gl_FrontLightProduct[0].diffuse * lambertTerm * spot;
					  
		vec3 eyeVector = normalize(eyeSpaceEyeVector);
		vec3 reflectionVector = reflect(-lightDirection, normal);
		
		float specular = pow(max(dot(reflectionVector, eyeVector), 0.0),
						 gl_FrontMaterial.shininess);
						 
		finalColor += gl_FrontLightProduct[0].specular * specular * spot;
	}
	
	gl_FragColor = finalColor;
}