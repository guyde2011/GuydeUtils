package com.guyde.plug.json;

public enum ClickAction {
	
	CHANGE_PAGE,
	OPEN_URL,
	RUN_COMMAND,
	SUGGEST_COMMAND;
	
	public final String json;
	
	ClickAction(){
		json = name().toLowerCase();
	}
	
}
