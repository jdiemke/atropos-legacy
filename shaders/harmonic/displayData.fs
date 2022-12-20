uniform sampler2D iteration;

void main() {
		
		
	vec2 pos = gl_FragCoord.xy / vec2(640, 360);
		
	
	float original = texture2D( iteration, pos).r+ texture2D( iteration, pos).g;
	
	gl_FragColor = vec4(vec3(original), 0.6);
}