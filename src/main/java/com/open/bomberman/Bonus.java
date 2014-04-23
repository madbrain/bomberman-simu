package com.open.bomberman;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

public class Bonus implements Burnable {

	private BonusKind kind;
	private int x;
	private int y;
	private Sprite sprite;
	private boolean isDestroying;
	private AnimationProvider animationProvider;
	private int time;

	public Bonus(BonusKind kind, AnimationProvider animationProvider) {
		this.kind = kind;
		this.animationProvider = animationProvider;
		switch (kind) {
		case BOMB:
			sprite = Sprite.withAnimation(animationProvider.get(AnimationKind.BONUS_BOMB)).build();
			break;
		case FIRE:
			sprite = Sprite.withAnimation(animationProvider.get(AnimationKind.BONUS_FIRE)).build();
			break;
		case ROLLER:
			sprite = Sprite.withAnimation(animationProvider.get(AnimationKind.BONUS_ROLLER)).build();
			break;
		}
	}
	
	public BonusKind getKind() {
		return kind;
	}
	
	@Override
	public boolean isAt(int x, int y) {
		return this.x == x && this.y == y;
	}

	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
		sprite.setLocation(GameMap.getPoint(x, y));
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

	private void removeFrom(ServerGameMap gameMap) {
		gameMap.removeBurnable(this);
		gameMap.set(x, y, GameTile.EMPTY);
	}

	@Override
	public void draw(Graphics2D g2d, ImageObserver imageObserver) {
		sprite.draw(g2d, imageObserver);
	}

	public void addTo(ServerGameMap gameMap) {
		switch (kind) {
		case BOMB:
			gameMap.set(x, y, GameTile.BONUS_BOMB);
			break;
		case FIRE:
			gameMap.set(x, y, GameTile.BONUS_FIRE);
			break;
		case ROLLER:
			gameMap.set(x, y, GameTile.BONUS_ROLLER);
			break;
		}
		
	}

	@Override
	public void doDestroy(ServerGameMap gameMap) {
		this.isDestroying = true;
		this.sprite = Sprite.withAnimation(animationProvider.get(AnimationKind.FIRE_OBJECT)).location(ServerGameMap.getPoint(x, y)).speed(6).build();
	}

}
