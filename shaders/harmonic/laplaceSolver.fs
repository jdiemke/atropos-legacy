#pragma optionNV(fastmath off)
#pragma optionNV(fastprecision off)

uniform sampler2D iteration;
uniform vec2 discretizationResolution;

vec2 ds_set(float a);				// float to double-single conversion
vec2 ds_add(vec2 dsa, vec2 dsb);	// double-single addition
vec2 ds_mul(vec2 dsa, vec2 dsb);	// double-single multiplication

void main() {
	vec2 pos = gl_FragCoord.xy / discretizationResolution;
		
	vec4 sample = texture2D(iteration, pos);
	
	if(sample.b == 1.0) {			// do not alter boundary values
		gl_FragColor = sample;
		return;
	}
	
	vec2 offset = vec2(1.0) / discretizationResolution; 
	vec2 sample1 = texture2D(iteration, pos + vec2(-offset.x,       0.0)).rg;
	vec2 sample2 = texture2D(iteration, pos + vec2(+offset.x,       0.0)).rg;
	vec2 sample3 = texture2D(iteration, pos + vec2(      0.0, -offset.y)).rg;
	vec2 sample4 = texture2D(iteration, pos + vec2(      0.0, +offset.y)).rg;
	
	// jacobi iteration using double-single arithmetic
	vec2 result = ds_mul(ds_add(ds_add(ds_add(sample1, sample2),sample3),
					sample4), ds_set(0.25));
	gl_FragColor = vec4(result, 0.0, 0.0);
}

vec2 ds_set(float a) {
	vec2 z;
 	z.x = a;
 	z.y = 0.0;
 	return z;
}

vec2 ds_add (vec2 dsa, vec2 dsb) {
	vec2 dsc;
	float t1, t2, e;
 
 	t1 = dsa.x + dsb.x;
 	e = t1 - dsa.x;
 	t2 = ((dsb.x - e) + (dsa.x - (t1 - e))) + dsa.y + dsb.y;
 
 	dsc.x = t1 + t2;
 	dsc.y = t2 - (dsc.x - t1);
 	return dsc;
}

vec2 ds_mul (vec2 dsa, vec2 dsb) {
	vec2 dsc;
	float c11, c21, c2, e, t1, t2;
	float a1, a2, b1, b2, cona, conb, split = 8193.;
 
 	cona = dsa.x * split;
	conb = dsb.x * split;
 	a1 = cona - (cona - dsa.x);
 	b1 = conb - (conb - dsb.x);
 	a2 = dsa.x - a1;
 	b2 = dsb.x - b1;
 
 	c11 = dsa.x * dsb.x;
 	c21 = a2 * b2 + (a2 * b1 + (a1 * b2 + (a1 * b1 - c11)));
 
 	c2 = dsa.x * dsb.y + dsa.y * dsb.x;
 
 	t1 = c11 + c2;
 	e = t1 - c11;
 	t2 = dsa.y * dsb.y + ((c2 - e) + (c11 - (t1 - e))) + c21;
 
 	dsc.x = t1 + t2;
 	dsc.y = t2 - (dsc.x - t1);
 
 	return dsc;
}

/*
void main() {
		
		
	vec2 pos = gl_FragCoord.xy / dimension;
		
	// solve laplace iterativ
	float original = texture2D( iteration, pos).r;
	
	vec4 color;
	if(original == 1.0 || original == 0.0) {
		color = original;
		gl_FragColor = color;
		return;
	}
	
		
	vec2 delta = vec2(1.0) / dimension;
	 

	double sample1 = texture2D(iteration, pos + vec2(-delta.x,      0.0)).r;
	double sample2 = texture2D(iteration, pos + vec2(+delta.x,      0.0)).r;
	double sample3 = texture2D(iteration, pos + vec2(     0.0, -delta.x)).r;
	double sample4 = texture2D(iteration, pos + vec2(     0.0, +delta.x)).r;
	color =vec4( vec3((sample1 + sample2 + sample3 + sample4) * 0.25),1.0);
	gl_FragColor = color;
}*/