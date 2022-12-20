#version 120

varying vec3 eyeSpaceNormal;
varying vec3 pos;

void main()
{
	eyeSpaceNormal = gl_NormalMatrix * gl_Normal;
	gl_Position = ftransform();
	pos = gl_Vertex.xyz*15.0;
}