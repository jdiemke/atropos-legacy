uniform sampler2D colorMap;
uniform sampler2D depthMap;

varying float depth;

void main() {
	
	vec2 coord = gl_FragCoord.xy / vec2(640,360);
	vec4 depthMapDepth = -texture2D(depthMap, coord).z;
	vec4 color = texture2D(colorMap, gl_TexCoord[0].st);
	
	float scale = 0.2f;
	float fade = clamp((depthMapDepth.x-depth)*scale, 0.0, 1.0);
	
	gl_FragColor = vec4(vec3(color),color.a * fade);
}