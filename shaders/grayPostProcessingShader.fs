uniform sampler2D texture1;

uniform float time;

const float PI = 3.14159265;

float rand(vec2 co){

        return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);

        }


void main()
{
	vec2 coord =  gl_TexCoord[0].st;
	vec2 coord2 =  gl_TexCoord[0].st;
	coord.x = coord.x  + 0.2*sin(coord.y * 4.2 + time*0.03)  +rand(vec2(1.0,gl_TexCoord[0].t)) *0.03;
	
	//coord.y = coord.y + 0.007*sin(gl_FragCoord.x*0.09 + time * 0.01);
	

	
	vec4 colorMap = texture2D(texture1, coord);

	gl_FragColor = (colorMap);// *0.5;
}