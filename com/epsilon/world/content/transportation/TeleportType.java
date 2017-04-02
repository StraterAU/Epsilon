package com.epsilon.world.content.transportation;

import com.epsilon.model.Animation;
import com.epsilon.model.Graphic;
import com.epsilon.util.Misc;
public enum TeleportType {

	NORMAL(3, new Animation(6724), new Animation(12450), null, null),
	ANCIENT(5, new Animation(9599), new Animation(8941), new Graphic(1681, 0), null),
	LUNAR(4, new Animation(9606), new Animation(9013), new Graphic(1685), null),
	TELE_TAB(2, new Animation(4731), Animations.DEFAULT_RESET_ANIMATION, new Graphic(678), null),
	RING_TELE(2, new Animation(9603), Animations.DEFAULT_RESET_ANIMATION, new Graphic(1684), null),
	LADDERUP(-1, null, null, null, null),
	LADDERDOWN(-1, null, null, null, null),
	PORTAL(-1, null, null, null, null),
	LEVER(-1, null, null, null, null),
	PURO_PURO(9, new Animation(6601), Animations.DEFAULT_RESET_ANIMATION, new Graphic(1118), null);

	TeleportType(int startTick, Animation startAnim, Animation endAnim, Graphic startGraphic, Graphic endGraphic) {
		this.startTick = startTick;
		this.startAnim = startAnim;
		this.endAnim = endAnim;
		this.startGraphic = startGraphic;
		this.endGraphic = endGraphic;
	}
	
	private Animation startAnim, endAnim;
	private Graphic startGraphic, endGraphic;
	private int startTick;

	public Animation getStartAnimation() {
		return startAnim;
	}

	public Animation getEndAnimation() {
		return endAnim;
	}

	public Graphic getStartGraphic() {
		return this == NORMAL ? new Graphic(1176 + Misc.getRandom(1)) : startGraphic;
	}

	public Graphic getEndGraphic() {
		return this == NORMAL ? new Graphic(1176 + Misc.getRandom(1)) : startGraphic;
	}

	public int getStartTick() {
		return startTick;
	}
	
	static class Animations {
		static Animation DEFAULT_RESET_ANIMATION = new Animation(65535);
	}
}
