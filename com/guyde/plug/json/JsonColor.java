package com.guyde.plug.json;

public enum JsonColor {
	GREEN,
	DARK_GREEN,
	RED,
	DARK_RED,
	AQUA,
	DARK_AQUA,
	GRAY,
	DARK_GRAY,
	YELLOW,
	LIGHT_PURPLE,
	DARK_PURPLE,
	GOLD,
	BLUE,
	DARK_BLUE,
	BLACK,
	WHITE;
	
	
	public final String json;
	JsonColor(){
		json = name().toLowerCase();
	}
}
