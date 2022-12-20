package atropos.core.model.md2;

public enum MD2AnimList {
	STAND(0, 39, 9),
	RUN(40, 45, 10),
	ATTACK(46, 53, 10),
	PAIN_A(54, 57, 7),
	PAIN_B(58, 61, 7),
	PAIN_C(62, 65, 7),
	JUMP(66, 71, 7),
	FLIP(72, 83, 7),
	SALUTE(84, 94, 7),
	FALLBACK(95, 111, 10),
	WAVE(112, 122, 7),
	POINT(123, 134, 6),
	CROUCH_STAND(135, 153, 10),
	CROUCH_WALK(154, 159, 7),
	CROUCH_ATTACK(160, 168, 10),
	CROUCH_PAIN(196, 172, 7),
	CROUCH_DEATH(173, 177, 5),
	DEATH_FALLBACK(178, 183, 7),
	DEATH_FALLFORWARD(184, 189, 7),
	DEATH_FALLBACKSLOW(190, 197, 7),
	BOOM(198, 198, 5);
	
	private final int firstFrame;
	private final int lastFrame;
	private final int fps;
	
	private MD2AnimList(int firstFrame, int lastFrame, int fps) {
		this.firstFrame = firstFrame;
		this.lastFrame = lastFrame;
		this.fps = fps;
	}
	
	public int firstFrame() {
		return firstFrame;
	}
	
	public int lastFrame() {
		return lastFrame;
	}
	
	public int fps() {
		return fps;
	}
	
}
