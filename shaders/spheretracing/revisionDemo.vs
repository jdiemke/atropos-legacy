  attribute vec3 position;

  void main(void) {
    gl_Position = gl_ProjectionMatrix*gl_ModelViewMatrix*vec4(position, 1.0);
  }