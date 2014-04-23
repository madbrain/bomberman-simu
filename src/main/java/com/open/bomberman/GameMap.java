package com.open.bomberman;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.ImageObserver;
import java.util.Collections;
import java.util.List;

public class GameMap {

	public static final int WIDTH = 13;
	public static final int HEIGHT = 11;
	public static final int TILE_WIDTH = 16;
	public static final int TILE_HEIGHT = 16;

	public static Point getPoint(int x, int y) {
		return new Point(x * GameMap.TILE_WIDTH, y * GameMap.TILE_HEIGHT);
	}

	protected GameTile[][] tiles = new GameTile[GameMap.HEIGHT][GameMap.WIDTH];

	public GameTile get(int x, int y) {
		if (x >= 0 && y >= 0 && x < GameMap.WIDTH && y < GameMap.HEIGHT) {
			return tiles[y][x];
		}
		return null;
	}

	public void set(int x, int y, GameTile tile) {
		if (x >= 0 && y >= 0 && x < GameMap.WIDTH && y < GameMap.HEIGHT) {
			tiles[y][x] = tile;
		}
	}

	public void draw(Graphics2D g2d, ImageObserver imageObserver) {

	}

	public List<Player> getPlayers() {
		return Collections.emptyList();
	}

}
