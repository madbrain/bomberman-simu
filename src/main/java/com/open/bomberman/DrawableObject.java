package com.open.bomberman;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

public interface DrawableObject {

	void step(ServerGameMap gameMap);

	void draw(Graphics2D g2d, ImageObserver imageObserver);

}
