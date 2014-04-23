package com.open.bomberman;

public interface State {

	State step(ServerGameMap gameMap);

	State doDestroy(ServerGameMap gameMap);
	
}