package com.open.bomberman;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class AnimationProvider {

	public static class PlayerAnimations {

		private Map<Direction, Animation> animationByDirection = new HashMap<Direction, Animation>();
		private Animation killAnimation;

		public void put(Direction direction, Animation animation) {
			this.animationByDirection.put(direction, animation);
		}

		public void setKillAnimation(Animation animation) {
			this.killAnimation = animation;
		}

		public Animation getKill() {
			return killAnimation;
		}

		public Animation get(Direction direction) {
			return animationByDirection.get(direction);
		}

	}

	private Map<Integer, PlayerAnimations> animationsByPlayer = new HashMap<Integer, AnimationProvider.PlayerAnimations>();
	private Map<Direction, Animation> animationByDirection = new HashMap<Direction, Animation>();
	private Map<AnimationKind, Animation> animations = new HashMap<AnimationKind, Animation>();

	public AnimationProvider() throws IOException {
		// Normal (18, height), ( 9, height-8)
		// Caped (22, height), (11, height-8)
		// Ninja (22, height), (11, height-8)
		// Pirate (20, height), (10, height-8)
		createPlayerAnimation(0, "/sprites/player-white.png", 24, 18);
		createPlayerAnimation(1, "/sprites/player-caped.png", 24, 22);
		createPlayerAnimation(2, "/sprites/player-ninja.png", 24, 22);
		createPlayerAnimation(3, "/sprites/player-pirate.png", 32, 20);

		BufferedImage image = ImageIO.read(getClass().getResourceAsStream("/sprites/bombs.png"));

		animations.put(AnimationKind.BOMB, new Animation(0, new int[] { 0, 1, 2, 1 }, image, 16, 16, new Point(0, 0)));
		animations.put(AnimationKind.FIRE_UP, new Animation(1, new int[] { 0, 1, 2, 3, 4, 3, 2, 1 }, image, 16, 16,
				new Point(0, 0)));
		animations.put(AnimationKind.FIRE_DOWN, new Animation(2, new int[] { 0, 1, 2, 3, 4, 3, 2, 1 }, image, 16, 16,
				new Point(0, 0)));
		animations.put(AnimationKind.FIRE_LEFT, new Animation(3, new int[] { 0, 1, 2, 3, 4, 3, 2, 1 }, image, 16, 16,
				new Point(0, 0)));
		animations.put(AnimationKind.FIRE_RIGHT, new Animation(4, new int[] { 0, 1, 2, 3, 4, 3, 2, 1 }, image, 16, 16,
				new Point(0, 0)));
		animations.put(AnimationKind.FIRE_VERT, new Animation(5, new int[] { 0, 1, 2, 3, 4, 3, 2, 1 }, image, 16, 16,
				new Point(0, 0)));
		animations.put(AnimationKind.FIRE_HORZ, new Animation(6, new int[] { 0, 1, 2, 3, 4, 3, 2, 1 }, image, 16, 16,
				new Point(0, 0)));
		animations.put(AnimationKind.FIRE_CENTER, new Animation(7, new int[] { 0, 1, 2, 3, 4, 3, 2, 1 }, image, 16, 16,
				new Point(0, 0)));

		image = ImageIO.read(getClass().getResourceAsStream("/sprites/explosion.png"));
		animations.put(AnimationKind.FIRE_OBJECT, new Animation(0, new int[] { 0, 1, 2, 3, 3 }, image, 16, 28,
				new Point(0, 12)));

		image = ImageIO.read(getClass().getResourceAsStream("/sprites/walls.png"));
		animations.put(AnimationKind.WALL, new Animation(0, new int[] { 0 }, image, 16, 16, new Point(0, 0)));
		animations.put(AnimationKind.FIRE_WALL, new Animation(1, new int[] { 0, 1, 2, 3, 4, 4 }, image, 16, 16,
				new Point(0, 0)));

		image = ImageIO.read(getClass().getResourceAsStream("/sprites/options.png"));
		animations.put(AnimationKind.BONUS_BOMB, new Animation(0, new int[] { 0 }, image, 16, 16, new Point(0, 0)));
		animations.put(AnimationKind.BONUS_FIRE, new Animation(0, new int[] { 1 }, image, 16, 16, new Point(0, 0)));
		animations.put(AnimationKind.BONUS_ROLLER, new Animation(0, new int[] { 2 }, image, 16, 16, new Point(0, 0)));
	}

	private void createPlayerAnimation(int id, String filename, int height, int killWidth) throws IOException {
		PlayerAnimations playerAnimations = new PlayerAnimations();
		animationsByPlayer.put(id, playerAnimations);

		BufferedImage image = ImageIO.read(getClass().getResourceAsStream(filename));

		int sequenceMarche[] = { 0, 1, 2, 1, 0, 3, 4, 3 };
		playerAnimations.put(Direction.UP,
				new Animation(2, sequenceMarche, image, 16, height, new Point(8, height - 8)));
		playerAnimations.put(Direction.DOWN,
				new Animation(0, sequenceMarche, image, 16, height, new Point(8, height - 8)));
		playerAnimations.put(Direction.LEFT,
				new Animation(1, sequenceMarche, image, 16, height, new Point(8, height - 8), true));
		playerAnimations.put(Direction.RIGHT,
				new Animation(1, sequenceMarche, image, 16, height, new Point(8, height - 8)));
		playerAnimations.setKillAnimation(
				new Animation(3, new int[] { 0, 1, 2, 3, 4 }, image, killWidth, height,
						new Point(killWidth / 2, height - 8)));
	}

	public Animation get(AnimationKind kind) {
		return animations.get(kind);
	}

	public Map<Direction, Animation> getAnimationsByDirection() {
		return animationByDirection;
	}

	public Animation get(Direction dir) {
		return animationByDirection.get(dir);
	}

	public PlayerAnimations getPlayer(int id) {
		return animationsByPlayer.get(id);
	}
}
