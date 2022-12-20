void main() {

	float scale = 1.0;
	
	mat4 shear= mat4(1.0, scale, 0.0, 0.0,
                     0.0, 1.0, 0.0, 0.0,
                     0.0, 0.0, 1.0, 0.0,
                     0.0, 0.0, 0.0, 1.0);
                
	gl_Position = gl_ProjectionMatrix * gl_ModelViewMatrix * shear * gl_Vertex;
}