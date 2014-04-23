package com.open.bomberman;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.List;

public class Bomb implements DrawableObject, Burnable {
	
	private static final int FIRE_SPEED = 3;
	
	private static final int BOMB_SPEED = 6;
	
	private class WaitingState implements State {
		
		private static final int TIME = 3;
		private final int TICKS = GameEngine.getTicks(TIME);
		
		private int time = 0;

		@Override
		public State step(ServerGameMap gameMap) {
			if (++time > TICKS) {
				return doDestroy(gameMap);
			}
			return this;
		}

		@Override
		public State doDestroy(ServerGameMap gameMap) {
			return new ExplosionState(gameMap);
		}
		
	}
	
	private class ExplosionState implements State {
		
		private static final int TICKS = 8 * FIRE_SPEED;
		
		private int time = 0;
		
		private List<Burnable> burnables = new ArrayList<Burnable>();
		
		private List<Point> fireTiles = new ArrayList<Point>();
		
		public ExplosionState(ServerGameMap gameMap) {
			createFireSprites(gameMap);
		}
		
		private void createFireSprites(ServerGameMap gameMap) {
			sprites.clear();
			gameMap.set(x, y, GameTile.EMPTY);
			
			boolean stopUp    = false;
			boolean stopDown  = false;
			boolean stopLeft  = false;
			boolean stopRight = false;
			for (int i = 1; i < power; ++i) {
				stopUp    |= addSprite(gameMap, x, y-i, Sprite.withAnimation(animationProvider.get(AnimationKind.FIRE_VERT)).speed(FIRE_SPEED).build(), stopUp);
				stopDown  |= addSprite(gameMap, x, y+i, Sprite.withAnimation(animationProvider.get(AnimationKind.FIRE_VERT)).speed(FIRE_SPEED).build(), stopDown);
				stopLeft  |= addSprite(gameMap, x-i, y, Sprite.withAnimation(animationProvider.get(AnimationKind.FIRE_HORZ)).speed(FIRE_SPEED).build(), stopLeft);
				stopRight |= addSprite(gameMap, x+i, y, Sprite.withAnimation(animationProvider.get(AnimationKind.FIRE_HORZ)).speed(FIRE_SPEED).build(), stopRight);
			}
			addSprite(gameMap, x, y, Sprite.withAnimation(animationProvider.get(AnimationKind.FIRE_CENTER)).speed(FIRE_SPEED).build(), false);
			addSprite(gameMap, x, y-power, Sprite.withAnimation(animationProvider.get(AnimationKind.FIRE_UP)).speed(FIRE_SPEED).build(), stopUp);
			addSprite(gameMap, x, y+power, Sprite.withAnimation(animationProvider.get(AnimationKind.FIRE_DOWN)).speed(FIRE_SPEED).build(), stopDown);
			addSprite(gameMap, x-power, y, Sprite.withAnimation(animationProvider.get(AnimationKind.FIRE_LEFT)).speed(FIRE_SPEED).build(), stopLeft);
			addSprite(gameMap, x+power, y, Sprite.withAnimation(animationProvider.get(AnimationKind.FIRE_RIGHT)).speed(FIRE_SPEED).build(), stopRight);
		}
		
		private boolean addSprite(ServerGameMap gameMap, int x, int y, Sprite sprite, boolean doStop) {
			GameTile tile = gameMap.get(x, y);
			if (tile == GameTile.EMPTY && doStop == false) {
				sprite.setLocation(ServerGameMap.getPoint(x, y));
				sprites.add(sprite);
				fireTiles.add(new Point(x, y));
				return false;
			}
			if (!doStop && tile != null && tile.isBurnable()) {
				burnables.add(gameMap.getBurnable(x, y));
			}
			return true;
		}

		@Override
		public State step(ServerGameMap gameMap) {
			for (Point fireTile : fireTiles) {
				gameMap.set(fireTile.x, fireTile.y, GameTile.FIRE);
			}
			if (time == 0) {
				for (Burnable burnable : burnables) {
					burnable.doDestroy(gameMap);
				}
			} else if (time > TICKS) {
				for (Point fireTile : fireTiles) {
					gameMap.set(fireTile.x, fireTile.y, GameTile.EMPTY);
				}
				gameMap.removeBomb(Bomb.this);
				player.removeBomb(Bomb.this);
			}
			++time;
			return this;
		}

		@Override
		public State doDestroy(ServerGameMap gameMap) {
			return this;
		}
		
	}
	
	private int x;
	private int y;
	private final int power;
	private State state = new WaitingState();
	private List<Sprite> sprites = new ArrayList<Sprite>();
	private AnimationProvider animationProvider;
	private Player player;

	public Bomb(int x, int y, AnimationProvider animationProvider, Player player, int power) {
		this.animationProvider = animationProvider;
		this.x = x;
		this.y = y;
		this.player = player;
		this.power = power;
		sprites.add(Sprite.withAnimation(animationProvider.get(AnimationKind.BOMB)).location(ServerGameMap.getPoint(x, y)).speed(BOMB_SPEED).build());
	}
	
	@Override
	public void step(ServerGameMap gameMap) {
		for (Sprite sprite : sprites) {
			sprite.step(gameMap);
		}
		state = state.step(gameMap);
	}

	@Override
	public void draw(Graphics2D g2d, ImageObserver imageObserver) {
		for (Sprite sprite : sprites) {
			sprite.draw(g2d, imageObserver);
		}
	}

	@Override
	public boolean isAt(int x, int y) {
		return this.x == x && this.y == y;
	}

	@Override
	public void doDestroy(ServerGameMap gameMap) {
		state = state.doDestroy(gameMap);
	}

}
