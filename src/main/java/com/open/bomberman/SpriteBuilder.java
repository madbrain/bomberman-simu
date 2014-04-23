package com.open.bomberman;

import java.awt.Point;

public interface SpriteBuilder {

	SpriteBuilder location(Point point);

	SpriteBuilder speed(int speed);

	Sprite build();

}
