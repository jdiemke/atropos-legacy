#version 120


uniform float time;
uniform int mode;

uniform sampler2D texCol;
float pi = 3.14159265;

float clamp(float a) {

	return min(max(a,0.0),1.0);
}

void main( void )
{
    float x = -1.0 + 2.0 * gl_TexCoord[0].s;
    float y = -1.0 + 2.0 * gl_TexCoord[0].t;
    
    float r = sqrt(x*x + y*y);
    float a = atan(y,x);
    
    float u, v, w;
    u=v=0.0;
    w= 1.0;
    // bad: 8, 9, 12,14,17

  	switch(mode) {
		case 0:
			u = x*cos(2.0*r) - y*sin(2.0*r);
			v = y*cos(2.0*r) + x*sin(2.0*r);
			w = 1.0;
			break;
		case 1:
			u = 0.3/(r+0.5*x);
			v = 3.0*a/pi;
			w = 1.0-((0.2 + 0.25/(0.09+r+0.5*x))-1.0);
			break;
		case 2:
			u = pow(r, 0.1);
			v = (1.0*a/pi)+r;
			w = 1.0;
			break;
		case 3:
			u = cos(a)/r;
			v = sin(a)/r;
			w = 1.0-((1.0/(pow(r,0.5)))-1.0);
			break;
		case 4:
			u = 0.02*y+0.03*cos(a*3.0)/r;
			v = 0.02*x+0.03*sin(a*3.0)/r;
			w = 0.9 + 0.5*pow(1.15-r,36.0);
			break;
		case 5:
			u = 0.1*(x)/(0.11+r*0.5);
			v = 0.1*(y)/(0.11+r*0.5);
			w = 1.0;
			break;
		case 6:
			u = 0.5*a/pi + 0.1*r;
			v = pow(r,0.1);
			w = 1.0;
			break;
		case 7:
			u = 0.5*a/pi;
			v = sin(7.0*r); 
			w = 0.7+0.3*sin(7.0*r);
			break;

		case 8:
			u = (-0.4/r)+.1*sin(8.0*a);
			v = 0.5 + 0.5*a/pi;
			w = clamp(r*r*r*(32.0-2.0*clamp(22.0*r)));
			break;
		case 9:
			u = x*sqrt(pow(r,2.0)+.4);
			v = y*sqrt(pow(r,2.0)+.4);
			w = (1.0-0.4*r)+ 0.2*(pow((1.15-r),5.0));
			break;

		case 10:
			u = 0.2/abs(y);
			v = 0.2*x/abs(y);
			w = abs(y);
			break;

 		case 11:
 			u = x;
			v = 0.3/(x*x-y);
			w = 1.0-0.1*(abs(x) + abs(1.0/(x*x-y)));
			break;
		case 12:
			u = sin(a+cos(3.0*r))/(pow(r,0.2));
			v = cos(a+cos(3.0*r))/(pow(r,0.2));
			w = 1.0+ 0.2/r;
			break;
		/*case 17:
			u = 1/(    pow((pow(x,32)+pow((y-0.5),32)),(1/32))     );
			v =3*atan((y-0.5)/x)/pi;
			w = pow(pow(x,32)+pow((y-.5),32.0),(1/32));
			break;*/

		case 13:
			u = r;
			v = r;
			w = 0.2 + 0.8*(1.2+ .6*sin(13.0*a))/r;
			break;
  	}
   
    vec4 co = vec4(w * vec3(texture2D( texCol, vec2(u,v) +vec2(time))), 1.0);
    gl_FragColor = co;
}