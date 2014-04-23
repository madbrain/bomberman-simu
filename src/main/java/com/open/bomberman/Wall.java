package com.open.bomberman;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

public class Wall implements DrawableObject, Burnable {

	private int x;
	private int y;
	private Sprite sprite;
	private Bonus bonus;
	private AnimationProvider animationProvider;
	private boolean isDestroying;
	private int time;

	public Wall(int x, int y, AnimationProvider animationProvider, Bonus bonus) {
		this.x = x;
		this.y = y;
		this.animationProvider = animationProvider;
		this.bonus = bonus;
		this.sprite = Sprite.withAnimation(animationProvider.get(AnimationKind.WALL)).location(ServerGameMap.getPoint(x, y)).speed(0).build();
	}

	@Override
	public void step(ServerGameMap gameMap) {
		if (isDestroying) {
			sprite.step(gameMap);
			if (++time >= 30) {
				removeFrom(gameMap);
			}
		}
	}

	@Override
	public void draw(Graphics2D g2d, ImageObserver imageObserver) {
		sprite.draw(g2d, imageObserver);
	}

	@Override
	public boolean isAt(int x, int y) {
		return this.x == x && this.y == y;
	}

	private void removeFrom(ServerGameMap gameMap) {
		gameMap.removeBurnable(this);
		gameMap.set(x, y, GameTile.EMPTY);
		if (bonus != null) {
			bonus.setLocation(x, y);
			gameMap.addBonus(bonus);
		}
	}

	@Override
	public void doDestroy(ServerGameMap gameMap) {
		this.isDestroying = true;
		this.sprite = Sprite.withAnimation(animationProvider.get(AnimationKind.FIRE_WALL)).location(ServerGameMap.getPoint(x, y)).speed(6).build();
	}

}
