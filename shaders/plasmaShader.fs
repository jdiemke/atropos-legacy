uniform float     time;
uniform int mode;

uniform sampler2D texCol;
float pi = 3.14159265;


void main( void )
{

	vec4 color1, color2, color;
	
	color1 = vec4(vec3((sin(dot(gl_FragCoord.xy,vec2(sin(time*3),cos(time*3)))*0.02+time*3))+1.0)/2.0, 1.0);
	
	vec2 center = vec2(640.0/2.0, 360.0/2.0) + vec2(640.0/2.0*sin(-time*3),360.0/2.0*cos(-time*3));
	
	color2 = vec4(vec3((cos(length(gl_FragCoord.xy - center)*0.03))+1.0)/2.0, 1.0);
	
	color = (color1+ color2)/2.0;
	float time = -89.9;
	float red = (cos(pi*color/0.5+time*3)+1.0)/2.0;
	float green = (sin(pi*color/0.5+time*3)+1.0)/2.0;
	float blue = (sin(+time*3)+1.0)/2.0;
	
    gl_FragColor = vec4(red*0.5,green*0.5,blue*0.5, 1.0);
}