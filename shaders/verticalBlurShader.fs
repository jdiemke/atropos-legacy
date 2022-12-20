#version 130
uniform sampler2D sceneTex; // 0

float rt_w = 640.0/2; // render target width
float rt_h = 360.0/2; // render target height


//float offset[3] = float[]( 0.0, 1.3846153846, 3.2307692308 );
float offset[4] = float[]( 0.0, 1.5, 3.5, 5.5);
//float weight[3] = float[]( 0.2270270270, 0.3162162162, 0.0702702703 );
float weight[4] = float[]( 1.0, 2.0, 2.0, 2.0 );


void main()
{
  vec4 tc = vec4(0.0);
  
    vec2 uv = gl_FragCoord.xy / vec2(640.0/2.0,360/2.0);
    tc = texture2D(sceneTex, uv).rgba;
    float sum =1.0;
     float value =0;
    for (int i=1; i< 3; i++)
    {
      tc += texture2D(sceneTex, uv + vec2(0.0, offset[i])/rt_h).rgba *weight[i]
      	*texture2D(sceneTex, uv + vec2(0.0, offset[i])/rt_h).a;
     sum +=weight[i]*texture2D(sceneTex, uv + vec2(0.0, offset[i])/rt_h).a;
      tc += texture2D(sceneTex, uv - vec2(0.0,offset[i])/rt_h).rgba * texture2D(sceneTex, uv - vec2(0.0,offset[i])/rt_h).a
              *weight[i];
     sum +=weight[i]*texture2D(sceneTex, uv - vec2(0.0,offset[i])/rt_h).a;
    }
  
  gl_FragColor =vec4(tc.rgba/sum);
}
/*

void main()
{
  vec4 tc = vec4(0.0);
  
    vec2 uv = gl_FragCoord.xy / vec2(640.0/1,360/1.0);
    tc = texture2D(sceneTex, uv).rgba;
    float sum =1.0;
     float value =0;
    for (int i=1; i<4; i++)
    {
      tc += texture2D(sceneTex, uv + vec2(0.0, offset[i])/rt_h).rgba * (texture2D(sceneTex, uv).a > texture2D(sceneTex, uv + vec2(0.0, offset[i])/rt_h).a ? texture2D(sceneTex, uv + vec2(0.0, offset[i])/rt_h).a : 1.0)
             *weight[i];
     sum +=(texture2D(sceneTex, uv).a > texture2D(sceneTex, uv + vec2(0.0, offset[i])/rt_h).a ? texture2D(sceneTex, uv + vec2(0.0, offset[i])/rt_h).a : 1.0)*weight[i];
      tc += texture2D(sceneTex, uv - vec2(offset[i])/rt_w, 0.0).rgba
              *(texture2D(sceneTex, uv).a > texture2D(sceneTex, uv - vec2(0.0, offset[i])/rt_h).a ? texture2D(sceneTex, uv - vec2(0.0, offset[i])/rt_h).a : 1.0)*weight[i];
     sum +=(texture2D(sceneTex, uv).a > texture2D(sceneTex, uv - vec2(0.0, offset[i])/rt_h).a ? texture2D(sceneTex, uv - vec2(0.0, offset[i])/rt_h).a : 1.0)*weight[i];
    }
  
  gl_FragColor =vec4(tc.rgba/sum);
}

void main()
{
  vec4 tc = vec4(0.0);
  
    vec2 uv = gl_FragCoord.xy / vec2(640/8,360/8);
    tc = texture2D(sceneTex, uv).rgba * weight[0];
    for (int i=1; i<4; i++)
    {
      tc += texture2D(sceneTex, uv + vec2(0.0, offset[i])/rt_h).rgba
              * weight[i];
      tc += texture2D(sceneTex, uv - vec2(0.0, offset[i])/rt_h).rgba
           * weight[i];
    }
  
  gl_FragColor = tc/13.0;
}*/