uniform sampler2D sphereMapSampler;
uniform sampler2D normalMapSampler;

varying vec3 tangentSpaceEyeVector;
varying mat3 tbnMatrix;

void main() {
	vec3 tangentSpaceNormal = normalize(texture2D(normalMapSampler, gl_TexCoord[0].st)
				  * 2.0 - 1.0);
	
	vec3 r = tbnMatrix * reflect(-normalize(tangentSpaceEyeVector), tangentSpaceNormal);
	float m = sqrt( r.x * r.x + r.y * r.y + (r.z + 1.0) * (r.z + 1.0) );
	vec2 sphereMapCoord = vec2(r.x / (2.0 * m) + 0.5, r.y / (2.0 * m) + 0.5); 
	
	gl_FragColor = texture2D(sphereMapSampler, sphereMapCoord);
}