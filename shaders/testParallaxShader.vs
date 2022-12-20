attribute vec3 tangent;

varying vec3 tangentSpaceEyeVector;
varying mat3 tbnMatrix;

void main() {
	vec3 eyeSpaceVertex = vec3(gl_ModelViewMatrix * gl_Vertex);
	vec3 normal = normalize(gl_NormalMatrix * gl_Normal);
	vec3 tangent= normalize(gl_NormalMatrix * tangent);
	vec3 binormal = cross(normal, tangent);
	tbnMatrix = mat3(tangent, binormal, normal);	
	tangentSpaceEyeVector = -eyeSpaceVertex * tbnMatrix;
	gl_TexCoord[0] = gl_MultiTexCoord0;
	gl_Position = ftransform();
}