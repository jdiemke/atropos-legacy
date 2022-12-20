uniform sampler2D texture1;
uniform sampler2D texture2;
uniform sampler2D texture3;

varying vec3 tangentSpaceLightDirection;

varying vec3 tangentSpaceEyeVector;

void main()
{
	float s = 0.05;
	float h = -0.025;
	float Hsb = (texture2D(texture2, gl_TexCoord[0].st).r * s) + h;
	
	vec3 eyeVec = normalize(tangentSpaceEyeVector);
	
	vec2 newTexCoord = gl_TexCoord[0].st + (eyeVec.xy * Hsb);
	
	
	/////////
	vec4 colorMap = texture2D(texture1, newTexCoord);
	////////
	vec3 normal = normalize(texture2D(texture3, newTexCoord).xyz * 2.0 - 1.0);
	vec3 eyeVector = normalize(tangentSpaceEyeVector);
	
	
	vec3 lightDirection = normalize(tangentSpaceLightDirection);
	float lambert = max(dot(normal, lightDirection), 0.0);
	
	vec4 ambient = gl_LightSource[0].ambient * gl_FrontMaterial.ambient;
	vec4 diffuse = gl_LightSource[0].diffuse * gl_FrontMaterial.diffuse * lambert;
	
	vec3 reflection = reflect(-lightDirection, normal);
	float phong = pow(max(dot(reflection, eyeVector), 0.0), gl_FrontMaterial.shininess);
	
	vec4 specular = gl_LightSource[0].specular * gl_FrontMaterial.specular * phong;
	
	gl_FragColor = (ambient + diffuse) * colorMap + specular;
	
}