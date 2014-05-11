package com.open.bomberman;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class Player extends Sprite implements PlayerControl {

	private static final int START_BOMB_COUNT = 1;
	private static final int MAX_BOMB_COUNT = 5;

	private static final int START_FIRE_POWER = 2;
	private static final int MAX_FIRE_COUNT = 6;

	private static final int START_ROLLER_COUNT = 1;
	private static final int MAX_ROLLER_COUNT = 3;

	private static final EnumSet<GameTile> BONUS_TILES = EnumSet.<GameTile> of(
			GameTile.BONUS_BOMB,
			GameTile.BONUS_FIRE,
			GameTile.BONUS_ROLLER);

	private Direction newDirection = null;
	private Direction direction = null;

	private int index;
	private String name;
	private PlayerAnimationProvider animationProvider;
	private final Animation idleAnimation;
	private ServerGameMap gameMap;
	private List<Bonus> bonuses = new ArrayList<Bonus>();
	private List<Bomb> pendingBombs = new ArrayList<Bomb>();

	private int bombCount = START_BOMB_COUNT;
	private int rollerCount = START_ROLLER_COUNT;
	private int fireCount = START_FIRE_POWER;
	private boolean isBeingKilled = false;
	private int time = 0;

	public Player(int index, String name, PlayerAnimationProvider animationProvider, Point position, ServerGameMap gameMap) {
		super(null, position);
		this.index = index;
		this.name = name;
		this.animationProvider = animationProvider;
		this.idleAnimation = animationProvider.getIdleAnimation();
		this.gameMap = gameMap;
		setSpeed(3);
	}
	
	public int getIndex() {
		return index;
	}
	
	public Point getPosition() {
		return position;
	}

	public String getName() {
		return name;
	}

	@Override
	public void setNewDirection(Direction newDirection) {
		this.newDirection = newDirection;
	}

	@Override
	public void createBomb() {
		if (pendingBombs.size() < bombCount) {
			Bomb bomb = this.gameMap.createBomb(position, fireCount, this);
			if (bomb != null) {
				this.pendingBombs.add(bomb);
			}
		}
	}

	@Override
	public void step(ServerGameMap gameMap) {
		super.step(gameMap);
		if (isBeingKilled) {
			++time;
			if (time == 12) {
				setAnimation(null);
			}
		} else {
			if (animation != null && direction != null) {
				Point newPosition = new Point(position);
				int speed = 1 + rollerCount;
				switch (direction) {
				case UP:
					newPosition.translate(0, -speed);
					alignVerticaly(newPosition);
					break;

				case DOWN:
					newPosition.translate(0, speed);
					alignVerticaly(newPosition);
					break;

				case LEFT:
					newPosition.translate(-speed, 0);
					alignHorizontaly(newPosition);
					break;

				case RIGHT:
					newPosition.translate(speed, 0);
					alignHorizontaly(newPosition);
					break;
				}
				if (testPlayerPosition(gameMap, newPosition)) {
					position = newPosition;
				}
			}
			if (direction != newDirection) {
				direction = newDirection;
				setAnimation(null);
				if (direction != null) {
					setAnimation(animationProvider.get(direction));
				}
			}
			if (hasOneCornerOn(gameMap, position.x, position.y, EnumSet.of(GameTile.FIRE))) {
				gameMap.kill(this);
			}
			Bonus bonus = removeBonus(gameMap);
			if (bonus != null) {
				addBonus(bonus);
			}
		}
	}

	private void alignVerticaly(Point newPosition) {
		int column = position.x / ServerGameMap.TILE_WIDTH;
		if (column % 2 == 0) {
			int nx = column * ServerGameMap.TILE_WIDTH + ServerGameMap.TILE_WIDTH / 2;
			newPosition.setLocation(nx, newPosition.y);
		}
	}

	private void alignHorizontaly(Point newPosition) {
		int line = position.y / ServerGameMap.TILE_HEIGHT;
		if (line % 2 == 0) {
			int ny = line * ServerGameMap.TILE_HEIGHT + ServerGameMap.TILE_HEIGHT / 2;
			newPosition.setLocation(newPosition.x, ny);
		}
	}

	private void addBonus(Bonus bonus) {
		this.bonuses.add(bonus);
		if (bonus.getKind() == BonusKind.BOMB && bombCount < MAX_BOMB_COUNT) {
			++bombCount;
		}
		if (bonus.getKind() == BonusKind.FIRE && fireCount < MAX_FIRE_COUNT) {
			++fireCount;
		}
		if (bonus.getKind() == BonusKind.ROLLER && rollerCount < MAX_ROLLER_COUNT) {
			++rollerCount;
		}
	}

	@Override
	public void draw(Graphics2D g2d, ImageObserver imageObserver) {
		super.draw(g2d, imageObserver);
		if (animation == null && !isBeingKilled) {
			Point pos = new Point(position);
			pos.translate(-idleAnimation.offset.x, -idleAnimation.offset.y);
			g2d.drawImage(idleAnimation.image,
					pos.x, pos.y,
					pos.x + idleAnimation.width, pos.y + idleAnimation.height,
					0, 0, idleAnimation.width, idleAnimation.height, imageObserver);
		}
	}

	private Bonus removeBonus(GameMap gameMap) {
		Bonus bonus = removeBonusAt(position.x, position.y);
		if (bonus != null) {
			return bonus;
		}
		return null;
	}

	private Bonus removeBonusAt(int x, int y) {
		if (BONUS_TILES.contains(getTile(gameMap, x, y))) {
			return gameMap.removeBonusAt(x / GameMap.TILE_WIDTH, y / GameMap.TILE_HEIGHT);
		}
		return null;
	}

	/**
	 * Player can't go on BLOCK or WALL and if not already on BOMB, can't go on
	 * BOMB too.
	 * 
	 * @param gameMap
	 * @param newPosition
	 * @return
	 */
	private boolean testPlayerPosition(GameMap gameMap, Point newPosition) {
		boolean isOnWall = hasOneCornerOn(gameMap, newPosition.x, newPosition.y,
				EnumSet.of(GameTile.BLOCK, GameTile.WALL));
		boolean isOnBomb = hasOneCornerOn(gameMap, position.x, position.y, EnumSet.of(GameTile.BOMB));
		boolean willBeOnBomb = hasOneCornerOn(gameMap, newPosition.x, newPosition.y, EnumSet.of(GameTile.BOMB));
		return !isOnWall && (isOnBomb || !willBeOnBomb);
	}

	private boolean hasOneCornerOn(GameMap gameMap, int x, int y, EnumSet<GameTile> tileTypes) {
		boolean count = testTile(gameMap, x - 8, y - 8, tileTypes);
		count |= testTile(gameMap, x - 8, y + 7, tileTypes);
		count |= testTile(gameMap, x + 7, y - 8, tileTypes);
		count |= testTile(gameMap, x + 7, y + 7, tileTypes);
		return count;
	}

	private boolean testTile(GameMap gameMap, int x, int y, EnumSet<GameTile> tileTypes) {
		GameTile tile = getTile(gameMap, x, y);
		return tile == null || tileTypes.contains(tile);
	}

	private GameTile getTile(GameMap gameMap, int x, int y) {
		if (x < 0 || y < 0
				|| x > GameMap.TILE_WIDTH * GameMap.WIDTH
				|| y > GameMap.TILE_HEIGHT * GameMap.HEIGHT) {
			return null;
		}
		return gameMap.get(x / GameMap.TILE_WIDTH, y / GameMap.TILE_HEIGHT);
	}

	public boolean isBeingKilled() {
		return isBeingKilled;
	}

	public void kill() {
		isBeingKilled = true;
		setSpeed(3);
		setAnimation(animationProvider.getKillAnimation());
	}

	public void removeBomb(Bomb bomb) {
		this.pendingBombs.remove(bomb);
	}

	public PlayerAnimationProvider getAnimationProvider() {
		return animationProvider;
	}

}