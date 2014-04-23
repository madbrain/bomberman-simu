package com.open.bomberman;

public interface Burnable extends DrawableObject {

	boolean isAt(int x, int y);

	void doDestroy(ServerGameMap gameMap);

}
