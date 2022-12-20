
// So, as i understand it, as long as GL_TEXTURE_COMPARE_MODE is
// GL_NONE the depth value "D" is returned in the vec4 as 000D,DDD1 or
// DDDD according to GL_DEPTH_TEXTURE_MODE and when using
// GL_TEXTURE_COMPARE_MODE == GL_COMPARE_R_TO_TEXTURE the third component is
// used as "compare value" and i only get 0 or 1 as "D", right?

// http://www.opengl.org/discussion_boards/ubbthreads.php?ubb=showflat&Number=263535
//

vec3 shadowMapPos;
uniform sampler2DShadow shadowMap;
uniform sampler2D colorMap;
uniform sampler2D spotMap;



const float epsilon =  0.002;


//const float epsilon = 0.0006;

// point light stuff 
varying vec3 normal;
varying vec3 lightDir;
varying vec3 eyeVec;
varying vec4 eyeSpaceVertex;
varying vec4 pos;


// 1 = shadow
// 0 = no shadow
float lookup(vec2 look) 
{
	//float depth = shadow2D(shadowMap, shadowMapPos + vec3(look,0) * epsilon).x;
	
	float depth = shadow2D(shadowMap, shadowMapPos + vec3(look,0) * epsilon).z;
	
	// if !gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_COMPARE_MODE, GL2.GL_COMPARE_R_TO_TEXTURE);
	// if fragment.z is smaller then depth -> 0.0 (no shadow)
	// return shadowMapPos.z < depth ?  0.0 : 1.0;
	
	// if gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_COMPARE_MODE, GL2.GL_COMPARE_R_TO_TEXTURE);
	// if fragment.z is in shadow depth != 1.0
	//return depth != 1.0 ? 1.0 : 0.0;
	return 1-depth;
}

void main()
{
	shadowMapPos = vec3(pos / pos.w) - vec3(0.0, 0.0, 0.0001);
	

	
	
	vec4 ambient = (gl_FrontLightModelProduct.sceneColor * gl_FrontMaterial.ambient) + 
	(gl_LightSource[0].ambient * gl_FrontMaterial.ambient);
	vec4 final_color = ambient;
	vec3 N = normalize(normal);
	vec3 L = normalize(lightDir);
	vec4 specularC = vec4(0);
	float lambertTerm = dot(N,L);
	
	float sum = 4.0;
	
	
	
	if(lambertTerm > 0.0)
	{
		
		final_color += gl_LightSource[0].diffuse * 
		               gl_FrontMaterial.diffuse * 
					   lambertTerm;	
		
		vec3 E = normalize(eyeVec);
		vec3 R = reflect(-L, N);
		float specular = pow( max(dot(R, E), 0.0), 
		                 41.0);//gl_FrontMaterial.shininess );
		specularC += gl_LightSource[0].specular * 
		              // removed this because ms3d has no specular material gl_FrontMaterial.specular * 
					   specular;
					   
		// shadow stuff
		vec2 o=mod(floor(gl_FragCoord.xy), 2.0);
	
		if(lambertTerm > 0.0) {
		sum = 0.0;
		
		sum += lookup(vec2(-1.5,  1.5)+o);
		sum += lookup(vec2( 0.5,  1.5)+o);
		sum += lookup(vec2(-1.5, -0.5)+o);
		sum += lookup(vec2( 0.5, -0.5)+o);
		
		
		}
	}
	float sumnew = 4*( 1-clamp((lambertTerm - 0.2) / 0.1, 0.0, 1.0));
	sum = max(sum, sumnew);

	
	//gl_FragColor =mix( final_color,ambient,sum *0.25);	
	float shadow = 0.0;//pow((-(eyeSpaceVertex.z/eyeSpaceVertex.w)*0.2),2);
	
		if(pos.q< 0.0) // stop backward projection
		  sum = 4;

	
		gl_FragData[1] = eyeSpaceVertex;
	gl_FragData[2] = vec4(N,1);
	
	gl_FragData[0] = (1-shadow)*((vec4(1,1,1,1) -texture2D( spotMap, shadowMapPos.xy)) * ambient * texture2D( colorMap, gl_TexCoord[0].st)+texture2D( spotMap, shadowMapPos.xy) *
					mix(texture2D( colorMap, gl_TexCoord[0].st) * final_color + specularC, texture2D( colorMap, gl_TexCoord[0].st) *ambient, sum *0.25));
		
	
	
}
