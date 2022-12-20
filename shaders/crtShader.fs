  /*   uniform float     time;
uniform int mode;

uniform sampler2D texCol;

        
       vec2 rubyInputSize=vec2(256,200);
      vec2 rubyOutputSize=vec2(256*2,200*2);
       vec2 rubyTextureSize=vec2(256,200);

        // Abbreviations
		//#define TEX2D(c) texture2D(texCol, (c))
        #define TEX2D(c) pow(texture2D(texCol, (c)), vec4(inputGamma))        		
		#define FIX(c)   max(abs(c), 1e-6);
        #define PI 3.141592653589

        // Adjusts the vertical position of scanlines. Useful if the output
        // pixel size is large compared to the scanline width (so, scale
        // factors less than 4x or so). Ranges from 0.0 to 1.0.
        #define phase 0.5

        // Assume NTSC 2.2 Gamma for linear blending
        #define inputGamma 2.2

        // Simulate a CRT gamma of 2.5
        #define outputGamma 2.5

        // Controls the intensity of the barrel distortion used to emulate the
        // curvature of a CRT. 0.0 is perfectly flat, 1.0 is annoyingly
        // distorted, higher values are increasingly ridiculous.
        #define distortion 0.1

        // Apply radial distortion to the given coordinate.
        vec2 radialDistortion(vec2 coord) {
				coord *= rubyTextureSize / rubyInputSize;
                vec2 cc = coord - 0.5;
                float dist = dot(cc, cc) * distortion;				
                return (coord + cc * (1.0 + dist) * dist) * rubyInputSize / rubyTextureSize;
        }
				
        // Calculate the influence of a scanline on the current pixel.
        //
        // 'distance' is the distance in texture coordinates from the current
        // pixel to the scanline in question.
        // 'color' is the colour of the scanline at the horizontal location of
        // the current pixel.
        vec4 scanlineWeights(float distance, vec4 color)
        {
                // The "width" of the scanline beam is set as 2*(1 + x^4) for
                // each RGB channel.
                vec4 wid = 2.0 + 2.0 * pow(color, vec4(4.0));

                // The "weights" lines basically specify the formula that gives
                // you the profile of the beam, i.e. the intensity as
                // a function of distance from the vertical center of the
                // scanline. In this case, it is gaussian if width=2, and
                // becomes nongaussian for larger widths. Ideally this should
                // be normalized so that the integral across the beam is
                // independent of its width. That is, for a narrower beam
                // "weights" should have a higher peak at the center of the
                // scanline than for a wider beam.
                vec4 weights = vec4(distance * 3.333333);                
                return 0.51 * exp(-pow(weights * sqrt(2.0 / wid), wid)) / (0.18 + 0.06 * wid);
        }
				
        void main()
        {
       
                // Here's a helpful diagram to keep in mind while trying to
                // understand the code:
                //
                //  |      |      |      |      |
                // -------------------------------
                //  |      |      |      |      |
                //  |  01  |  11  |  21  |  31  | <-- current scanline
                //  |      | @    |      |      |
                // -------------------------------
                //  |      |      |      |      |
                //  |  02  |  12  |  22  |  32  | <-- next scanline
                //  |      |      |      |      |
                // -------------------------------
                //  |      |      |      |      |
                //
                // Each character-cell represents a pixel on the output
                // surface, "@" represents the current pixel (always somewhere
                // in the bottom half of the current scan-line, or the top-half
                // of the next scanline). The grid of lines represents the
                // edges of the texels of the underlying texture.

                // The size of one texel, in texture-coordinates.
                vec2 one = 1.0 / rubyTextureSize;
				
				vec2 texCoord = gl_FragCoord.xy/vec2(256*2,200*2);

                // Texture coordinates of the texel containing the active pixel				
                vec2 xy = radialDistortion(texCoord);
                
                

                // Of all the pixels that are mapped onto the texel we are
                // currently rendering, which pixel are we currently rendering?
                vec2 uv_ratio = fract(xy * rubyTextureSize) - vec2(0.5);

                // Snap to the center of the underlying texel.                
				xy = (floor(xy * rubyTextureSize) + vec2(0.5)) / rubyTextureSize;
                
                // Calculate Lanczos scaling coefficients describing the effect
                // of various neighbour texels in a scanline on the current
                // pixel.				
                vec4 coeffs = PI * vec4(1.0 + uv_ratio.x, uv_ratio.x, 1.0 - uv_ratio.x, 2.0 - uv_ratio.x);                				
				
				// Prevent division by zero
				coeffs = FIX(coeffs);
				coeffs = 2.0 * sin(coeffs) * sin(coeffs / 2.0) / (coeffs * coeffs);
                
				// Normalize
				coeffs /= dot(coeffs, vec4(1.0));

                // Calculate the effective colour of the current and next
                // scanlines at the horizontal location of the current pixel,
                // using the Lanczos coefficients above.								
                vec4 col  = clamp(coeffs.x * TEX2D(xy + vec2(-one.x, 0.0))   + coeffs.y * TEX2D(xy)                    + coeffs.z * TEX2D(xy + vec2(one.x, 0.0)) + coeffs.w * TEX2D(xy + vec2(2.0 * one.x, 0.0)),   0.0, 1.0);
                vec4 col2 = clamp(coeffs.x * TEX2D(xy + vec2(-one.x, one.y)) + coeffs.y * TEX2D(xy + vec2(0.0, one.y)) + coeffs.z * TEX2D(xy + one)              + coeffs.w * TEX2D(xy + vec2(2.0 * one.x, one.y)), 0.0, 1.0);

				// col  = pow(col, vec4(inputGamma));    
				// col2 = pow(col2, vec4(inputGamma));
                
                // Calculate the influence of the current and next scanlines on
                // the current pixel.
                vec4 weights  = scanlineWeights(abs(uv_ratio.y) , col);
                vec4 weights2 = scanlineWeights(1.0 - uv_ratio.y, col2);
                vec3 mul_res  = (col * weights + col2 * weights2).xyz;

                // mod_factor is the x-coordinate of the current output pixel.
                float mod_factor = texCoord.x * rubyOutputSize.x * rubyTextureSize.x / rubyInputSize.x;
				
                // dot-mask emulation:
                // Output pixels are alternately tinted green and magenta.
                vec3 dotMaskWeights = mix(
                        vec3(1.05, 0.75, 1.05),
                        vec3(0.75, 1.05, 0.75),
                        floor(mod(mod_factor, 2.0))
                    );
					
                mul_res *= dotMaskWeights;
				
                // Convert the image gamma for display on our output device.
                mul_res = pow(mul_res, vec3(1.0 / (2.0 * inputGamma - outputGamma)));
				
                gl_FragColor = vec4(mul_res, 1.0);
        }

*/uniform float     time;
uniform int mode;

uniform sampler2D texCol;
float pi = 3.14159265;

        vec2 radialDistortion(vec2 coord) {	
                vec2 cc = (coord - 0.5);
                float dist = dot(cc, cc) * 0.2;				
                return (coord + cc * (1.0 + dist) * dist);
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
        
void main( void )
{

	vec2 coords = gl_FragCoord.xy*vec2(1.0/640, 1.0/360);
// coords = 0.5 + (coords-0.5)*(0.99 + 0.01*sin(0.2*time*0.02));
	coords = radialDistortion(coords);
	vec4 final = texture2D(texCol,coords);
	
	
	 final.r = texture2D(texCol,coords +vec2(+0.002*cos(time*0.01+gl_FragCoord.y*0.07)*sin(gl_FragCoord.y*1.7+time*0.4+sin(time*0.1)), 0)).r;
	  final.g = texture2D(texCol,coords +vec2(+0.0002*sin(gl_FragCoord.y*.05+time*0.004), 0)).g;
	   final.b = texture2D(texCol,coords +vec2(-0.0002*sin(gl_FragCoord.y*.07+time*0.004), 0)).b;
	   
	  
    //col.g = texture2D(tex0,vec2(uv.x+0.000,uv.y)).y;
    //col.b = texture2D(tex0,vec2(uv.x-0.003,uv.y)).z;
	
	
	final = (final - vec4(0.5))*1.4 +vec4(0.5)+vec4(0.3);
final *= 0.7 + 0.3*30.0*coords.x*coords.y*(1.0-coords.x)*(1.0-coords.y);
final = final *((1+ sin(2*pi*(coords.y-0.5)*256/2))/4+0.5);
 final = final *0.8 + final *0.3*(1+snoise(coords*100+time*0.2))/2;

	 final *= vec4(0.8,1.0,0.7,1.0);
	   final *= 0.97+0.03*sin(time*1);
	   //final += vec4(0.2)*sin(time*0.8+coords.y*900+cos(time*0.2+coords.x*900));
	 
	 final *= 1-smoothstep(0.996, 1.004, max(coords.x,coords.y));
	 final *= 1-smoothstep(0.004, -0.004, min(coords.x,coords.y));
   gl_FragColor = vec4(final.xyz, 1.0);

}