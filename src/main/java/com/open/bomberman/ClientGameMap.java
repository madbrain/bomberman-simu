package com.open.bomberman;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.ImageObserver;
import java.util.HashMap;
import java.util.Map;

public class ClientGameMap extends GameMap {

	private AnimationProvider animationProvider;
	private Sprite wallSprite;
	private Sprite fireSprite;
	private Sprite bombSprite;
	private Sprite bonusBombSprite;
	private Sprite bonusFireSprite;
	private Sprite bonusRollerSprite;

	private static class PlayerSprite {

		private Point position;
		private Sprite sprite;
		public boolean isAlive = true;

		public PlayerSprite(Sprite sprite) {
			this.sprite = sprite;
		}

	}

	private Map<Integer, PlayerSprite> players = new HashMap<Integer, PlayerSprite>();

	public ClientGameMap(AnimationProvider animationProvider) {
		this.animationProvider = animationProvider;
		wallSprite = Sprite.withAnimation(animationProvider.get(AnimationKind.WALL)).speed(0).build();
		fireSprite = Sprite.withAnimation(animationProvider.get(AnimationKind.FIRE_CENTER)).speed(0).build();
		bombSprite = Sprite.withAnimation(animationProvider.get(AnimationKind.BOMB)).speed(0).build();

		bonusBombSprite = Sprite.withAnimation(animationProvider.get(AnimationKind.BONUS_BOMB)).speed(0).build();
		bonusFireSprite = Sprite.withAnimation(animationProvider.get(AnimationKind.BONUS_FIRE)).speed(0).build();
		bonusRollerSprite = Sprite.withAnimation(animationProvider.get(AnimationKind.BONUS_ROLLER)).speed(0).build();
	}

	@Override
	public void draw(Graphics2D g2d, ImageObserver imageObserver) {
		for (int y = 0; y < GameMap.HEIGHT; ++y) {
			for (int x = 0; x < GameMap.WIDTH; ++x) {
				GameTile tile = get(x, y);
				if (tile == GameTile.WALL) {
					wallSprite.setLocation(GameMap.getPoint(x, y));
					wallSprite.draw(g2d, imageObserver);
				} else if (tile == GameTile.FIRE) {
					fireSprite.setLocation(GameMap.getPoint(x, y));
					fireSprite.draw(g2d, imageObserver);
				} else if (tile == GameTile.BOMB) {
					bombSprite.setLocation(GameMap.getPoint(x, y));
					bombSprite.draw(g2d, imageObserver);
				} else if (tile == GameTile.BONUS_BOMB) {
					bonusBombSprite.setLocation(GameMap.getPoint(x, y));
					bonusBombSprite.draw(g2d, imageObserver);
				} else if (tile == GameTile.BONUS_FIRE) {
					bonusFireSprite.setLocation(GameMap.getPoint(x, y));
					bonusFireSprite.draw(g2d, imageObserver);
				} else if (tile == GameTile.BONUS_ROLLER) {
					bonusRollerSprite.setLocation(GameMap.getPoint(x, y));
					bonusRollerSprite.draw(g2d, imageObserver);
				}
			}
		}
		g2d.setColor(Color.RED);
		for (Integer id : players.keySet()) {
			PlayerSprite player = players.get(id);
			player.sprite.setLocation(player.position);
			player.sprite.draw(g2d, imageObserver);
		}
	}

	public void setPlayerInfo(int index, Point position, boolean isAlive) {
		PlayerSprite player = players.get(index);
		if (player == null) {
			Animation animation = animationProvider.getPlayer(index).get(Direction.DOWN);
			players.put(index, player = new PlayerSprite(Sprite.withAnimation(animation).speed(0).build()));
		}
		player.position = position;
		if (isAlive == false) {
			player.isAlive = false;
			Animation animation = animationProvider.getPlayer(index).getKill();
			player.sprite = Sprite.withAnimation(animation).speed(0).build();
		}
	}
}
