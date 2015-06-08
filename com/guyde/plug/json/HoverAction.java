package com.guyde.plug.json;

public enum HoverAction {
	SHOW_TEXT,
	SHOW_ITEM,
	SHOW_ACHIEVEMENT,
	SHOW_ENTITY;
	
	public final String json;
	
	HoverAction(){
		json = name().toLowerCase();
	}
	
}
