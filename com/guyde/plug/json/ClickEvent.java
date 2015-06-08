package com.guyde.plug.json;

public class ClickEvent {
	private ClickAction action;
	private String value;
	
	public ClickEvent(ClickAction act , String val){
		value = val;
		action = act;
	}
	
	public ClickEvent(ClickAction act){
		action = act;
	}
	
	public ClickEvent(String val){
		value = val;
	}
	
	public ClickEvent(){
	}
	
	public String toJSON(){
		return "clickEvent:{action:" + action.json + ",value:\"" + new String(value).replace("\"", "\\\"") + "\"}";
	}

	public ClickAction getAction() {
		return action;
	}

	public void setAction(ClickAction action) {
		this.action = action;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
