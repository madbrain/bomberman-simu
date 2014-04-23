package com.open.bomberman;

import java.awt.Point;

public class SpriteBuilderImpl implements SpriteBuilder {

	private Sprite sprite;

	public SpriteBuilderImpl(Animation animation) {
		this.sprite = new Sprite(animation, new Point(100, 100));
	}

	@Override
	public SpriteBuilder location(Point point) {
		this.sprite.setLocation(point);
		return this;
	}

	@Override
	public SpriteBuilder speed(int speed) {
		this.sprite.setSpeed(speed);
		return this;
	}

	@Override
	public Sprite build() {
		return sprite;
	}

}
