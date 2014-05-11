package com.open.bomberman;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class ServerGameMap extends GameMap {

	private static final double WALL_PROBABILITY = 0.75;

	private List<Player> players = new ArrayList<Player>();
	private List<Burnable> burnables = new ArrayList<Burnable>();

	private AnimationProvider animationProvider;
	private boolean isPlaying = true;
	private Player winner = null;

	public ServerGameMap(AnimationProvider animationProvider) {
		this.animationProvider = animationProvider;
		Random random = new Random();

		for (int y = 0; y < tiles.length; ++y) {
			for (int x = 0; x < tiles[y].length; ++x) {
				tiles[y][x] = x % 2 == 1 && y % 2 == 1 ? GameTile.BLOCK : GameTile.EMPTY;
				if (tiles[y][x] == GameTile.EMPTY
						&& !isCorner(x, y)
						&& random.nextFloat() < WALL_PROBABILITY) {
					tiles[y][x] = GameTile.WALL;
					Bonus bonus = makeBonus(random, animationProvider);
					burnables.add(new Wall(x, y, animationProvider, bonus));
				}
			}
		}
	}

	private Bonus makeBonus(Random random, AnimationProvider animationProvider) {
		double val = random.nextFloat();
		if (val < 0.2) {
			return new Bonus(BonusKind.BOMB, animationProvider);
		}
		if (val < (0.2 + 0.1)) {
			return new Bonus(BonusKind.FIRE, animationProvider);
		}
		if (val < (0.2 + 0.1 + 0.05)) {
			return new Bonus(BonusKind.ROLLER, animationProvider);
		}
		return null;
	}

	private static boolean isCorner(int x, int y) {
		if (x == 0) {
			return y <= 1 || y >= GameMap.HEIGHT - 2;
		}
		if (x == 1) {
			return y < 1 || y > GameMap.HEIGHT - 2;
		}
		if (x == GameMap.WIDTH - 1) {
			return y <= 1 || y >= GameMap.HEIGHT - 2;
		}
		if (x == GameMap.WIDTH - 2) {
			return y < 1 || y > GameMap.HEIGHT - 2;
		}
		return false;
	}

	public void stepObjects() {
		for (Player player : players) {
			player.step(this);
		}
		for (DrawableObject object : new ArrayList<Burnable>(burnables)) {
			object.step(this);
		}
		testEndGame();
	}

	private void testEndGame() {
		int count = 0;
		Player winner = null;
		for (Player player : players) {
			if (!player.isBeingKilled()) {
				++count;
				winner = player;
			}
		}
		if (players.size() > 1 && count == 1) {
			this.winner = winner;
			isPlaying = false;
		} else if (players.size() > 0 && count == 0) {
			isPlaying = false;
		}
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public Bomb createBomb(Point position, int power, Player player) {
		int x = position.x / GameMap.TILE_WIDTH;
		int y = position.y / GameMap.TILE_HEIGHT;

		for (Burnable burnable : burnables) {
			if (burnable instanceof Bomb) {
				Bomb bomb = (Bomb) burnable;
				if (bomb.isAt(x, y)) {
					return null;
				}
			}
		}
		set(x, y, GameTile.BOMB);
		Bomb bomb = new Bomb(x, y, animationProvider, player, power);
		burnables.add(bomb);
		return bomb;
	}

	public void removeBomb(Bomb bomb) {
		burnables.remove(bomb);
	}

	@Override
	public void draw(Graphics2D g2d, ImageObserver imageObserver) {
		for (Burnable burnable : new ArrayList<Burnable>(burnables)) {
			burnable.draw(g2d, imageObserver);
		}
		for (Player player : players) {
			player.draw(g2d, imageObserver);
		}
		if (isPlaying == false) {
			String str = "No winner!";
			if (winner != null) {
				str = winner.getName() + " is the winner!";
			}
			int width = g2d.getFontMetrics().stringWidth(str) + 20;
			int x = ((13 * 16) - width) / 2;
			g2d.setColor(Color.BLACK);
			g2d.fillRoundRect(x, 50, width, 50, 8, 8);
			g2d.setColor(Color.GRAY);
			g2d.drawRoundRect(x, 50, width, 50, 8, 8);
			g2d.setColor(Color.WHITE);

			g2d.drawString(str, x + 10, 80);
		}
	}

	public Burnable getBurnable(int x, int y) {
		for (Burnable burnable : burnables) {
			if (burnable.isAt(x, y)) {
				return burnable;
			}
		}
		return null;
	}

	public void removeBurnable(Burnable burnable) {
		this.burnables.remove(burnable);
	}

	public void addBonus(Bonus bonus) {
		this.burnables.add(bonus);
		bonus.addTo(this);
	}

	public Bonus removeBonusAt(int x, int y) {
		for (Burnable burnable : new ArrayList<Burnable>(burnables)) {
			if (burnable instanceof Bonus) {
				Bonus bonus = (Bonus) burnable;
				if (bonus.isAt(x, y)) {
					removeBurnable(bonus);
					set(x, y, GameTile.EMPTY);
					return bonus;
				}
			}
		}
		return null;
	}

	public void addPlayer(Player player) {
		this.players.add(player);
	}

	public void kill(Player player) {
		if (!player.isBeingKilled()) {
			player.kill();
		}
	}

	public synchronized Player createPlayer(String name) {
		int index = findFreeIndex();
		Point position = null;
		if (index == 0) {
			position = new Point(8, 8);
		} else if (index == 1) {
			position = new Point(8 + TILE_WIDTH * (WIDTH - 1), 8 + TILE_HEIGHT * (HEIGHT - 1));
		} else if (index == 2) {
			position = new Point(8, 8 + TILE_HEIGHT * (HEIGHT - 1));
		} else if (index == 3) {
			position = new Point(8 + TILE_WIDTH * (WIDTH - 1), 8);
		} else {
			throw new IllegalStateException("too much players");
		}
		Player player = new Player(index, name,
				new PlayerAnimationProviderImpl(animationProvider, index), position, this);
		addPlayer(player);
		return player;
	}

	private int findFreeIndex() {
		Set<Integer> indexes = new HashSet<Integer>();
		for (Player player : players) {
			indexes.add(player.getIndex());
		}
		for (int i = 0; i < 4; ++i) {
			if (! indexes.contains(i)) {
				return i;
			}
		}
		throw new IllegalStateException("maximum number of player reached");
	}

	public void removePlayer(Player player) {
		this.players.remove(player);
	}

	@Override
	public List<Player> getPlayers() {
		return players;
	}

}
