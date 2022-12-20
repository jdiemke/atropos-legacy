
varying vec3 tangentSpaceLightDirection;
varying vec3 tangentSpaceEyeVector;

attribute vec3 tangent;

void main()
{

	vec3 eyeSpaceVertex = vec3(gl_ModelViewMatrix * gl_Vertex);
	
	vec3 normal = normalize(gl_NormalMatrix * gl_Normal);
	vec3 tangent = normalize(gl_NormalMatrix * tangent);
	vec3 binormal = cross(normal, tangent);

	vec3 eyeSpaceEyeVector = -eyeSpaceVertex;

	tangentSpaceEyeVector.x = dot(eyeSpaceEyeVector, tangent);
	tangentSpaceEyeVector.y = dot(eyeSpaceEyeVector, binormal);
	tangentSpaceEyeVector.z = dot(eyeSpaceEyeVector, normal);
	
	vec3 eyeSpaceLightDirection = gl_LightSource[0].position.xyz - eyeSpaceVertex;
	
	tangentSpaceLightDirection.x = dot(eyeSpaceLightDirection, tangent);
	tangentSpaceLightDirection.y = dot(eyeSpaceLightDirection, binormal);
	tangentSpaceLightDirection.z = dot(eyeSpaceLightDirection, normal);
	
	gl_TexCoord[0] = gl_MultiTexCoord0;
	gl_Position = ftransform();
}