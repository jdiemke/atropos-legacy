uniform float     time;
uniform int mode;

uniform sampler2D texCol;
float pi = 3.14159265;


float sdSphere(vec2 pos, float size) {
	return length(pos) - size;
}

vec4 bobs () {

  vec2 newPos = gl_FragCoord.xy / vec2(640,360);

newPos = newPos - vec2(1)/2;

vec4 final = vec4(0,0,0,0);
 for(int i=0; i < 10; i++) {
 	float x = cos(6*time+i*0.2);
 	float y = sin(4*time+i*0.2);
 	final += vec4(0.2,0,0,1.0) * (1-step(0.0, sdSphere(newPos-vec2(0.4*x,0.3*y),0.06)));
 	final.a = min(final.a, 1); 
 }
 
 return final;
}

vec4 effect1() {

  vec2 newPos = gl_FragCoord.xy / vec2(640,360);
   
   
   newPos = (newPos -0.5);
   
   float k= 1.8;
   float rd = length(newPos);
   float ru = rd*(1+ k*rd*rd);
   
   newPos = newPos/length(newPos) * ru +0.5;
   
   vec2 oldPos = newPos;
   float angle = pi/4;
   newPos = vec2(oldPos.x *cos(angle) - oldPos.y *sin(angle),
   					  oldPos.y *cos(angle) + oldPos.x *sin(angle));
   
    vec2 coords=newPos*vec2(0.15,-1)*3.1+vec2(time*1.3,time*1.3);
   vec4 final = texture2D(texCol,coords);
  
	
   vec2 oldPos2 = (gl_FragCoord.xy/vec2(640, 360) );
	final = final *0.7  +(0.3*30.0*oldPos2.x*oldPos2.y*(1.0-oldPos2.x)*(1.0-oldPos2.y));
	   
//final = final *0.7  +(0.3*30.0*oldPos2.x*oldPos2.y*(1.0-oldPos2.x)*(1.0-oldPos2.y));
	  // rasters

   float size = 0.4;
   vec4 temp2 = vec4(0,0,0,0);
   for(int i=0; i < 10; i++) {
      float pos2 = +360/2+sin(time*10+0.16*i) *60;
   float col2 = (cos((gl_FragCoord.y-pos2)*size) +1)/2;
   col2 = pow(col2+0.1,3.0);
   //float al2 = (1-smoothstep(pi-0.01,  pi, (gl_FragCoord.y-pos2)*size))*(1-smoothstep(-pi+0.01,  -pi, (gl_FragCoord.y-pos2)*size));
  float al2 =(1-smoothstep(pi-0.9,  pi-0.89, (gl_FragCoord.y-pos2)*size))*(1-smoothstep(-pi+0.9,  -pi+0.89, (gl_FragCoord.y-pos2)*size));
  float red = (cos(pi*i/10/0.5+time*3)+1.0)/2.0;
	float green = (sin(pi*i/10/0.5+time*3)+1.0)/2.0;
	float blue = (sin(+time*3)+1.0)/2.0;
	float luma = red*0.3 + green*0.59+ blue*0.11;
	float alpha = 0.6f;
	red = red*(1-alpha) + luma*(alpha);
	green = green*(1-alpha) + luma*(alpha);
	blue = blue*(1-alpha) + luma*(alpha);
  
   final =final*(1-al2)+ al2* vec4(col2*red,col2*green, col2*blue,1);
   }
  return vec4(final.xyz, 1.0);
}

vec4 rasters (vec4 color) {
  // rasters

	vec4 final = color;
   float size = 0.4;
   vec4 temp2 = vec4(0,0,0,0);
   for(int i=0; i < 10; i++) {
      float pos2 = +360/2+sin(time*10+0.16*i) *60;
   float col2 = (cos((gl_FragCoord.y-pos2)*size) +1)/2;
   col2 = pow(col2+0.2,2.0);
   //float al2 = (1-smoothstep(pi-0.07,  pi-0.05, (gl_FragCoord.y-pos2)*size))*(1-smoothstep(-pi+0.07,  -pi+0.05, (gl_FragCoord.y-pos2)*size));
  float al2 =(1-smoothstep(pi-0.9,  pi-0.89, (gl_FragCoord.y-pos2)*size))*(1-smoothstep(-pi+0.9,  -pi+0.89, (gl_FragCoord.y-pos2)*size));
  float red = (cos(pi*i/10/0.5+time*3)+1.0)/2.0;
	float green = (sin(pi*i/10/0.5+time*3)+1.0)/2.0;
	float blue = (sin(+time*3)+1.0)/2.0;
	
		float luma = red*0.3 + green*0.59+ blue*0.11;
	float alpha = 0.6f;
	red = red*(1-alpha) + luma*(alpha);
	green = green*(1-alpha) + luma*(alpha);
	blue = blue*(1-alpha) + luma*(alpha);
  
  
   final =final*(1-al2)+ al2* vec4(col2*red,col2*green, col2*blue,1);
   }
   return final;
}

vec4 effect2() {

	vec2 position = vec2(640/2+640/2*sin(time*2), 360/2+360/2*cos(time*3));
	vec2 position2 = vec2(640/2+640/2*sin((time+2000)*2), 360/2+360/2*cos((time+2000)*3));
	
	
	vec2 offset = vec2(640/2, 360/2) ;
	vec2 offset2 = vec2(6*sin(time*1.1), 3*cos(time*1.1));
   
   vec2 oldPos = (gl_FragCoord.xy-offset);
   
   float angle = time*2;
   
   vec2 newPos = vec2(oldPos.x *cos(angle) - oldPos.y *sin(angle),
   					  oldPos.y *cos(angle) + oldPos.x *sin(angle));
   
        
        newPos = (newPos)*(0.0044+0.004*sin(time*3.0))-offset2;
        vec2 temp = newPos;
       // newPos.x = temp.x + 0.4*sin(temp.y*2+time*8);
       // newPos.y = (-temp.y + 0.4*sin(temp.x*2+time*8));
   vec4 final = texture2D(texCol,newPos*vec2(0.15,-1));
	//final = texture2D(texCol,gl_FragCoord.xy*vec2(1.0/640, -1.0/360));
	  
	  
	   vec2 oldPos2 = (gl_FragCoord.xy/vec2(640, 360) );
	final = final *0.7  +(0.3*30.0*oldPos2.x*oldPos2.y*(1.0-oldPos2.x)*(1.0-oldPos2.y));
	  // rasters

   float size = 0.4;
   vec4 temp2 = vec4(0,0,0,0);
   for(int i=0; i < 10; i++) {
      float pos2 = +360/2+sin(time*10+0.16*i) *60;
   float col2 = (cos((gl_FragCoord.y-pos2)*size) +1)/2;
   col2 = pow(col2+0.2,2.0);
   //float al2 = (1-smoothstep(pi-0.07,  pi-0.05, (gl_FragCoord.y-pos2)*size))*(1-smoothstep(-pi+0.07,  -pi+0.05, (gl_FragCoord.y-pos2)*size));
  float al2 =(1-smoothstep(pi-0.9,  pi-0.89, (gl_FragCoord.y-pos2)*size))*(1-smoothstep(-pi+0.9,  -pi+0.89, (gl_FragCoord.y-pos2)*size));
  float red = (cos(pi*i/10/0.5+time*3)+1.0)/2.0;
	float green = (sin(pi*i/10/0.5+time*3)+1.0)/2.0;
	float blue = (sin(+time*3)+1.0)/2.0;
	
		float luma = red*0.3 + green*0.59+ blue*0.11;
	float alpha = 0.6f;
	red = red*(1-alpha) + luma*(alpha);
	green = green*(1-alpha) + luma*(alpha);
	blue = blue*(1-alpha) + luma*(alpha);
  
  
   final =final*(1-al2)+ al2* vec4(col2*red,col2*green, col2*blue,1);
   }
   //float alp = temp2.a;//* max(min(0.5+10*sin(time*1.2),1),0);
   //final =  final *(1-alp) +(alp)*temp2;
	
 return vec4(final.xyz, 1.0);
   }
  vec3 mod289(vec3 x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }
vec2 mod289(vec2 x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }
vec3 permute(vec3 x) { return mod289(((x*34.0)+1.0)*x); }
float snoise (vec2 v)
{
    const vec4 C = vec4(0.211324865405187,  // (3.0-sqrt(3.0))/6.0
                        0.366025403784439,  // 0.5*(sqrt(3.0)-1.0)
                        -0.577350269189626, // -1.0 + 2.0 * C.x
                        0.024390243902439); // 1.0 / 41.0

    // First corner
    vec2 i  = floor(v + dot(v, C.yy) );
    vec2 x0 = v -   i + dot(i, C.xx);

    // Other corners
    vec2 i1;
    i1 = (x0.x > x0.y) ? vec2(1.0, 0.0) : vec2(0.0, 1.0);
    vec4 x12 = x0.xyxy + C.xxzz;
    x12.xy -= i1;

    // Permutations
    i = mod289(i); // Avoid truncation effects in permutation
    vec3 p = permute( permute( i.y + vec3(0.0, i1.y, 1.0 ))
        + i.x + vec3(0.0, i1.x, 1.0 ));

    vec3 m = max(0.5 - vec3(dot(x0,x0), dot(x12.xy,x12.xy), dot(x12.zw,x12.zw)), 0.0);
    m = m*m ;
    m = m*m ;

    // Gradients: 41 points uniformly over a line, mapped onto a diamond.
    // The ring size 17*17 = 289 is close to a multiple of 41 (41*7 = 287)

    vec3 x = 2.0 * fract(p * C.www) - 1.0;
    vec3 h = abs(x) - 0.5;
    vec3 ox = floor(x + 0.5);
    vec3 a0 = x - ox;

    // Normalise gradients implicitly by scaling m
    // Approximation of: m *= inversesqrt( a0*a0 + h*h );
    m *= 1.79284291400159 - 0.85373472095314 * ( a0*a0 + h*h );

    // Compute final noise value at P
    vec3 g;
    g.x  = a0.x  * x0.x  + h.x  * x0.y;
    g.yz = a0.yz * x12.xz + h.yz * x12.yw;
    return 130.0 * dot(m, g);
}
      
      vec4  texture2DB(sampler2D s,vec2 pos) {
      vec4 final = texture2D(texCol,pos)*0.5;
      	 final.r += 0.4*texture2D(texCol,pos +0.5*vec2(+0.04, 0)).r;
	  final.g += 0.4*texture2D(texCol,pos +0.5*vec2(+0.02, 0)).g;
	   final.b += 0.4*texture2D(texCol,pos +0.5*vec2(+0.0, 0)).b;
	   
return final;
      }  
void main( void )
{

	vec4 e1 = effect1();
	vec4 e2 = effect2();
	
		vec2 offset = vec2(640, 360) ;

   
   vec2 oldPos = (gl_FragCoord.xy/offset);
 
 float a = min(max((5*sin(time+(gl_FragCoord.x+gl_FragCoord.y)*0.0005)+0.5),0),1);
 vec4 final =
  (e1*a + e2*(1-a));
  //final = final * 0.7
  
   final = final*0.8 + final *0.3*(1+snoise(oldPos*100+vec2(time*1312.2,time*1333.2)))/2;
   


   
    vec2 oldPos2 = (gl_FragCoord.xy/vec2(640, 360) );
    vec2 oldPos3 = (gl_FragCoord.xy/vec2(640, 360) );
   // oldPos2 = oldPos2*vec2(2,2);
    float x = cos(6*time*0.8);
 	float y = sin(4*time*0.8);
    
    vec2 pos = vec2(0.5+0.3*x,0.5+ 0.2*y);
    //  final = texture2D(texCol,(oldPos2+vec2(time*0.1,time*0.1))*10);
    float radius = 0.35;//0.35
   //final= vec4(0);
    float offx=0;
    float offy=0;
    float MAG= 0.35*(1/radius);//0.35*(1/radius);
      
 //  final=texture2D(texCol,vec2((oldPos.x+offx),(oldPos.y+offy))*vec2(0.7,-1.)*vec2(1.6,0.8)+vec2(sin(8*time*.1),cos(4*time*.1)))
   ;//*(1-smoothstep(radius-0.001, radius, length(oldPos2)));
  
// final = final *0.5  +(0.3*30.0*oldPos3.x*oldPos3.y*(1.0-oldPos3.x)*(1.0-oldPos3.y))
// -vec4(0.8,0.8,0.5,0)*0.1;
 
 //final = vec4(0);
	for(int i=0; i < 2; i++ ) {
   
       float x = cos(1*time*2+i*pi);
 	float y = sin(1*time*2+i*pi);
    
    vec2 pos = vec2(0.5+0.32*x,0.5+ 0.36*y);
  //  oldPos2 = oldPos2*vec2(0.8,0.8);
    if((oldPos2.x-pos.x)*(oldPos2.x-pos.x)*1.7*1.7 + (oldPos2.y-pos.y)*(oldPos2.y-pos.y) < radius *radius) {
   
   vec2 posi;
    float z =1-1/sqrt( -(oldPos2.x-pos.x)*1.7*1.7*(oldPos2.x-pos.x) - (oldPos2.y-pos.y)*(oldPos2.y-pos.y)+radius*radius );
    	posi.x=(oldPos.x-pos.x)/z*MAG+(oldPos.x);
    	posi.y=(oldPos.y-pos.y)/z*MAG+(oldPos.y);

 
 		float alpha = 0.0
 		+(1-smoothstep(radius-0.003,radius,sqrt((oldPos2.x-pos.x)*1.7*1.7*(oldPos2.x-pos.x) + (oldPos2.y-pos.y)*(oldPos2.y-pos.y))))
 		;
 		float exp = (sqrt((oldPos2.x-pos.x)*1.7*1.7*(oldPos2.x-pos.x) + (oldPos2.y-pos.y)*(oldPos2.y-pos.y)) / sqrt(radius *radius));
 		float exp2 =(sqrt((oldPos2.x-pos.x+radius*0.3)*1.7*1.7*(oldPos2.x-pos.x+radius*0.3) + (oldPos2.y-pos.y-radius*0.3)*(oldPos2.y-pos.y-radius*0.3)) / sqrt(radius*1.24 *radius*1.24));

 		
    	vec4 color=((
    	vec4(0.8,1.0,0.8,0)*(0.2+0.9*(1-exp*exp*exp)*texture2D(texCol,vec2((posi.x),(posi.y))*vec2(.7,-1.)*vec2(1.6,0.8)+vec2(sin(8*time*.1),cos(4*time*.1)))))
    	
    	)-0.1*(exp)*(1+snoise(18*vec2((oldPos.x-pos.x)*1.7/z*MAG, (oldPos.y-pos.y)/z*MAG)))/2;
    	 float a= alpha;
    	alpha *=1.0;
    	//final = final *(1-alpha) + alpha* color+a*vec4(0.9,0.9,1.0,0)*1.00*(1-exp2)*(1-exp2);
    	
   
    	
    } 
    
   }
   //final = final *0.5  +(0.3*30.0*oldPos3.x*oldPos3.y*(1.0-oldPos3.x)*(1.0-oldPos3.y));
    //final = final *0.5  +(0.3*30.0*oldPos3.x*oldPos3.y*(1.0-oldPos3.x)*(1.0-oldPos3.y));
//  final = rasters(final);
   
    // final = final*0.8 + final *0.3*(1+snoise(oldPos*100+vec2(time*1312.2,time*1333.2)))/2;
    
    	//vec4 col = bobs();
  //final = final*(1-col.a) + col.a * col;

  gl_FragColor = final;
}



/*
void main( void )
{

	vec2 position = vec2(640/2+640/2*sin(time*2), 360/2+360/2*cos(time*3));
	vec2 position2 = vec2(640/2+640/2*sin((time+2000)*2), 360/2+360/2*cos((time+2000)*3));
	
	
	vec2 offset = vec2(640/2, 360/2) ;
	vec2 offset2 = vec2(6*sin(time*1.1), 3*cos(time*1.1));
   
   vec2 oldPos = (gl_FragCoord.xy-offset);
   
   float angle = time*2;
   
   vec2 newPos = vec2(oldPos.x *cos(angle) - oldPos.y *sin(angle),
   					  oldPos.y *cos(angle) + oldPos.x *sin(angle));
   
        
        newPos = (newPos)*(0.0044+0.004*sin(time*3.0))-offset2;
        vec2 temp = newPos;
        newPos.x = temp.x + 0.4*sin(temp.y*2+time*8);
        newPos.y = (-temp.y + 0.4*sin(temp.x*2+time*8));
   vec4 final = texture2D(texCol,newPos);
	//final = texture2D(texCol,gl_FragCoord.xy*vec2(1.0/640, -1.0/360));
   gl_FragColor = vec4(final.xyz, 1.0);

}*/