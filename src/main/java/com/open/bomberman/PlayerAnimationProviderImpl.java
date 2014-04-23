package com.open.bomberman;

public class PlayerAnimationProviderImpl implements PlayerAnimationProvider {

	private AnimationProvider animationProvider;
	private int id;

	public PlayerAnimationProviderImpl(AnimationProvider animationProvider, int id) {
		this.animationProvider = animationProvider;
		this.id = id;
	}

	@Override
	public Animation getIdleAnimation() {
		return get(Direction.DOWN);
	}

	@Override
	public Animation get(Direction direction) {
		return animationProvider.getPlayer(id).get(direction);
	}

	@Override
	public Animation getKillAnimation() {
		return animationProvider.getPlayer(id).getKill();
	}

}
