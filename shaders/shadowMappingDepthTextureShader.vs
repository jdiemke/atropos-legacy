
uniform mat4 modelMatrix;
uniform mat4 viewMatrix;

void main()
{
	gl_Position = gl_ProjectionMatrix * viewMatrix * modelMatrix * gl_Vertex;
}