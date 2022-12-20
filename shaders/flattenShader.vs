uniform float time;

void main()
{
	vec4 vertex = gl_Vertex;
	vertex.z += sin(vertex.x * 5.0 + time * 0.01) * 0.07;
	
	gl_Position = gl_ModelViewProjectionMatrix * vertex;
}