package com.open.bomberman;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.ImageObserver;

class Sprite implements DrawableObject {
	
	private int index = 0;
	private int time = 0;
	private int speed = 1;
	protected Animation animation;
	protected Point position = new Point(100, 100);
	
	public Sprite(Animation startAnimation, Point position) {
		setAnimation(startAnimation);
		setLocation(position);
	}
	
	public void setLocation(Point position) {
		this.position = new Point(position);
	}
	
	public void setAnimation(Animation animation) {
		this.index = 0;
		this.animation = animation;
	}
	
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
	@Override
	public void step(ServerGameMap gameMap) {
		if (animation != null) {
			++time;
			if (time >= speed) {
				time = 0;
				++index;
				if (index >= animation.sequence.length) {
					index = 0;
				}
			}
		}
	}
	
	@Override
	public void draw(Graphics2D g2d, ImageObserver imageObserver) {
		if (animation != null) {
			int spriteX = animation.sequence[index] * animation.width;
			int spriteY = animation.sequenceIndex * animation.height;
			Point pos = new Point(position);
			pos.translate(-animation.offset.x, -animation.offset.y);
			if (animation.isXMirror) {
				g2d.drawImage(animation.image,
						pos.x + animation.width, pos.y,
						pos.x, pos.y + animation.height,
						spriteX, spriteY,
						spriteX + animation.width, spriteY + animation.height, imageObserver);
			} else {
				g2d.drawImage(animation.image,
						pos.x, pos.y,
						pos.x + animation.width, pos.y + animation.height,
						spriteX, spriteY,
						spriteX + animation.width, spriteY + animation.height, imageObserver);
			}
		}
	}

	public static SpriteBuilder withAnimation(Animation bombAnimation) {
		return new SpriteBuilderImpl(bombAnimation);
	}

}