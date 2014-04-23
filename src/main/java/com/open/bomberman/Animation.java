package com.open.bomberman;

import java.awt.Image;
import java.awt.Point;

public class Animation {

	int sequenceIndex;
	Point offset;
	int[] sequence;
	Image image;
	int width;
	int height;
	boolean isXMirror = false;

	public Animation(int sequenceIndex, int[] sequence, Image image, int width, int height, Point offset) {
		this(sequenceIndex, sequence, image, width, height, offset, false);
	}

	public Animation(int sequenceIndex, int[] sequence, Image image, int width, int height, Point offset,
			boolean isXMirror) {
		this.sequenceIndex = sequenceIndex;
		this.sequence = sequence;
		this.image = image;
		this.width = width;
		this.height = height;
		this.offset = offset;
		this.isXMirror = isXMirror;
	}

}