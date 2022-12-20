uniform float     time;
uniform int mode;

uniform sampler2D texCol;
float pi = 3.14159265;


void main( void )
{

	vec2 position = vec2(640/2+640/2*sin(time*2), 360/2+360/2*cos(time*3));
	vec2 position2 = vec2(640/2+640/2*sin((time+2000)*2), 360/2+360/2*cos((time+2000)*3));
	
	

float dist2 = mod(length(position2 - gl_FragCoord.xy)*0.08-(time-200)*12, 2.0);

float dist = mod(length(position - gl_FragCoord.xy)*0.08-time*12, 2.0);

float color = (1-smoothstep(1.45, 1.55, dist))*(1-smoothstep(0.55, 0.45, dist));
float color2 = (1-smoothstep(1.45, 1.55, dist2))*(1-smoothstep(0.55, 0.45, dist2));
	//if(dist < 0.5) color = 1.0;
	//else color= 0.0;
	//color +=color2;
	
	vec3 final = vec3(color,0.5,0.5) + vec3(0.5,color2,0.5);
   gl_FragColor = vec4(final, 1.0);

}