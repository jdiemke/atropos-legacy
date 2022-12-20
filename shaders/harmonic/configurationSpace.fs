// In dieser haben die Spieler eine kreisrunde Ausdehnung mit dem Radius r = 0.3. Das
//Spielfeld besitzt eine Länge l = 105.0 und Breite b = 68.0.
	// playfield range is:
	// x: -52,5 .. 52,5
	// y: -34 .. 34
	// texture range is:
	// x: -53,5 .. 53,5
	// y: -35 .. 35


uniform float time;
   
   vec2 playerPosition[21] = {vec2(0.0+10*cos(time),0.5+10*sin(time)),
						   vec2(45.0,20.5),
						   vec2(-10.0,2.5+20),
						   vec2(25.0+10*sin(time),5.5),
						   vec2(-30.0,-10.5+10*sin(time)),
						   vec2(-30.0, 0.5),
						   vec2(-40.0,-10.5), 
						   vec2(-20.0,-20.5), 
						   vec2(-10.0,-30.5), 
						   
						   vec2( 50.0, 0.5),
						   vec2(  45.0,-10.5),
						   vec2(42.0,  14.5),
						   vec2(40.0,  20.5),
						   vec2( 38.0,  -30.5),
						   vec2( 27.0,-30.5),
						   vec2(24.0,  19.5),
						   vec2( 20.0,  10.5),
						   vec2( 23.0,  -13.5),
						   vec2( 1.0,   13.5), 
						   vec2( 10.0, 20.5), 
						   vec2(-20.0,  -5.5)};
   
   
//////////
uniform vec2 discretizationResolution;
uniform vec2 playerPositions[21];
uniform vec2 targetPosition;
uniform float clearanceZone;

const float PLAYER_RADIUS = 0.3;

float sdCircle(vec2 position, float r) {
	return length(position) - r ;
}

float sdHalfPlane(vec2 pos, vec2 normal, float distance) {
	return dot(pos, normal) - distance;
}

float sdPlayFieldBounds(vec2 pos) {
	float dist = sdHalfPlane(pos, vec2( 1.0, 0.0), -52.5);
	dist = min(dist, sdHalfPlane(pos, vec2(-1.0,  0.0), -52.5));
	dist = min(dist, sdHalfPlane(pos, vec2( 0.0,  1.0), -34.0));
	return min(dist, sdHalfPlane(pos, vec2( 0.0, -1.0), -34.0));
}

void main() {
	// map fragment coord to c-space
	vec2 pos = (gl_FragCoord / discretizationResolution - vec2(0.5)) * vec2(107.0, 70.0);
	
	// compute radius of circumscribed circle
	vec2 rect = vec2(107.0, 70.0) / discretizationResolution; 
	float r = 0.5 * length(rect);

	// construct signed distance field for obstacle region
	float distB = sdPlayFieldBounds(pos);	

	for(int i=0; i < 21; i++)
		distB = min(distB, sdCircle(pos - playerPosition[i], PLAYER_RADIUS)); 

	float distCB = distB - 0.3 - clearanceZone;
	float distCGoal = sdCircle(pos - targetPosition, 3.5);
		
	vec4 texel = vec4(0.5, 0.0, 0.0, 0.0);

	// boundary condition for obstacle region
	if(distCB - r <= 0.0) texel = vec4(1.0, 0.0, 1.0, 0.0);
		
	// boundary condition for goal region
	if(distCGoal - r <= 0.0) texel = vec4(0.0, 0.0, 1.0, 0.0);

	gl_FragColor = texel; 
}