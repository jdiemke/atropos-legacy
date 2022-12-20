varying vec3 eyeSpaceNormal;
varying vec3 eyeSpaceLightDirection;
varying vec3 eyeSpaceHalfVector;

varying vec4 diffuse;
varying vec4 ambient;

void main()
{
	vec4 color;
	color = ambient;
	
	vec3 normal = normalize(eyeSpaceNormal);
	
	float NdotL;
	NdotL = max(dot(normal, eyeSpaceLightDirection), 0.0);
	
	vec3 halfVector;
	float NdotH;
	
	if(NdotL > 0.0) {
		color += diffuse * NdotL;
		halfVector = normalize(eyeSpaceHalfVector);
		NdotH = max(dot(normal, halfVector), 0.0);
		color += gl_FrontMaterial.specular * gl_LightSource[0].specular *
					pow(NdotH, gl_FrontMaterial.shininess);
	}

	gl_FragColor = color;
}