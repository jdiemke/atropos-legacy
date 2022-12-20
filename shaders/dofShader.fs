#version 120

uniform sampler2D colorMap;
uniform sampler2D downsampledColorMap;
uniform sampler2D positionMap;

vec2 poissonDisk[8] = vec2[](vec2( 0.000000,  0.000000),
							 vec2( 0.527837, -0.085868),
							 vec2(-0.040088,  0.536087),
							 vec2(-0.670445, -0.179949),
							 vec2(-0.419418, -0.616039),
							 vec2( 0.440453, -0.639399),
							 vec2(-0.757088,  0.349334),
							 vec2( 0.574619,  0.685879));

void main() {

	vec2 coord = gl_FragCoord.xy / vec2(640,360);
	
	vec2 texelDim = vec2(1.0 / (640.0*2.0),1.0 / (360.0 *2.0)) ;

	
	float CoC = texture2D( colorMap, coord).a;
	

    
    
   
	
	
	vec4 color;
	float div;

	
	for(int i=0; i < 8; i++) {	    		
			 	
		vec4 col1= texture2D(colorMap, coord+  poissonDisk[i]*CoC*texelDim*8 );		
		vec4 col2 = texture2D(downsampledColorMap, coord +  poissonDisk[i]*CoC *texelDim*8);
			 
		vec4 mixed = mix(col1,col2,col1.a);
			
		if(col1.a <CoC) {
			color += mixed * mixed.a;
			div +=mixed.a;
			
		} else {
			color += mixed;
			div+=1.0;
		}	  	
	}
	
	color /= div;
    gl_FragData[0] =
                     color;
}

