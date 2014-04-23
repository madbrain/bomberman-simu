package com.open.bomberman;

public enum GameTile {

	EMPTY(false, "EM"),
	BLOCK(false, "BL"),
	WALL(true, "WA"),
	BOMB(true, "BO"),
	FIRE(false, "FI"),
	BONUS_BOMB(true, "BB"),
	BONUS_FIRE(true, "BF"),
	BONUS_ROLLER(true, "BR");

	private boolean isBurnable;
	private String code;

	private GameTile(boolean isBurnable, String code) {
		this.isBurnable = isBurnable;
		this.code = code;
	}

	public boolean isBurnable() {
		return isBurnable;
	}

	public String getCode() {
		return code;
	}

	public static GameTile fromCode(String code) {
		for (GameTile tile : values()) {
			if (tile.getCode().equals(code)) {
				return tile;
			}
		}
		throw new IllegalArgumentException("Unknown tile " + code);
	}
}
