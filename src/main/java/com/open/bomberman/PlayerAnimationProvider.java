package com.open.bomberman;

public interface PlayerAnimationProvider {

	Animation getIdleAnimation();

	Animation getKillAnimation();

	Animation get(Direction direction);

}
