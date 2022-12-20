
uniform sampler2D colorMap;
uniform sampler2D positionMap;
uniform sampler2D normalMap;

float g_sample_rad = 2.0;
float g_scale = 2.0;
float g_bias = 0.0;
float g_intensity = 0.46;



vec3 getPosition(vec2 uv)
{	
    return texture2D(positionMap, uv).xyz;
    //return textureFetch(positionMap, uv, 4).xyz;
}

vec3 getNormal(vec2 uv)
{
    return texture2D(normalMap, uv).xyz;
   // return textureFetch(normalMap, uv, 4).xyz;
}

float doAmbientOcclusion(vec2 tcoord, vec2 uv, vec3 p, vec3 cnorm)
{
    vec3 diff = getPosition(tcoord + uv) - p;
    vec3 v = normalize(diff);
    float d = length(diff) * g_scale;
    return max(0.0, dot(cnorm, v) - g_bias) * (1.0 / (1.0+d)) * g_intensity;
}

vec2 getRandom(vec2 co)
{
// TODO make lookup texture, because this is totally slow!!!!!

    float x = fract(sin(dot(co, vec2(12.9898,78.233))) * 43758.5453);
    float y = fract(sin(dot(vec2(x, co.y), vec2(12.9898,78.233))) * 43758.5453);
    return normalize(vec2(x, y));
}

void main(void)
{
    vec2[4] vec = {
        vec2(1.0,0.0), vec2(-1.0,0.0),
        vec2(0.0,1.0), vec2(0.0,-1.0) };

    vec2 uv = gl_TexCoord[0];
    vec3 p = getPosition(uv);
    vec3 n = getNormal(uv);
    vec2 rand = getRandom(uv);
    
    float ao = 0.0;
    float rad = //1.0; //TODO 
    g_sample_rad / p.z;
    
    int iterations = 4;
    for (int j=0; j<iterations; ++j)
    {
        vec2 coord1 = reflect(vec[j], rand) * rad;
        vec2 coord2 = vec2(coord1.x*0.707 - coord1.y*0.707, coord1.x*0.707 + coord1.y*0.707);
        
        coord1 /= 10.0;
        coord2 /= 10.0;

        ao += doAmbientOcclusion(uv, coord1 * 0.25, p, n);
        ao += doAmbientOcclusion(uv, coord2 * 0.50, p, n);
        ao += doAmbientOcclusion(uv, coord1 * 0.75, p, n);
        ao += doAmbientOcclusion(uv, coord2,        p, n);
    } 
    ao /= (float)iterations * 4.0;
    ao*=10.0; // XXX 
    
    ao = clamp(1.0-ao,0.0, 1.0);
    
      float dofAmount = 1.0;
        
          float FocalDepth = 3.4;
          float NoBlurRange = 3.0;
          float MaxBlurRange =5.0;
          
        float  CoC = clamp((dofAmount * max(0.0, abs(-getPosition(uv).z - FocalDepth) - NoBlurRange)) /
          		(MaxBlurRange - NoBlurRange), 0.0, 1.0);
          		
          		//   CoC = ((-getPosition(uv).z - FocalDepth)- NoBlurRange)/(MaxBlurRange - NoBlurRange) *dofAmount;
          											 
          		//CoC = -getPosition(uv).z;									 ;
          		//CoC = (-getPosition(uv).z-12)*0.2;
    
    //contrast
    vec3 color = (vec3(texture2D(colorMap, uv).xyz) -vec3(0.5))*1.2 +vec3(0.5)+vec3(0.2);
    gl_FragColor = vec4( color,CoC)*vec4(ao, ao, ao,1.0);
  //  gl_FragColor =  vec4(texture2D(colorMap, uv, 4).xyz,0);//*vec4(ao, ao, ao, 1.0);
    //gl_FragColor =  vec4(ao, ao, ao, 1.0);
   // gl_FragColor = vec4(texture2D(normalMap, uv).xyz,1);
}