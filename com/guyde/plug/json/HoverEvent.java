package com.guyde.plug.json;

public class HoverEvent {
	private HoverAction action;
	private String value;
	
	public HoverEvent(HoverAction act , String val){
		value = val;
		action = act;
	}
	
	public HoverEvent(HoverAction act){
		action = act;
	}
	
	public HoverEvent(String val){
		value = val;
	}
	
	public HoverEvent(){
	}
	
	public String toJSON(){
		return "hoverEvent:{action:" + action.json + ",value:\"" + new String(value).replace("\"", "\\\"") + "\"}";
	}

	public HoverAction getAction() {
		return action;
	}

	public void setAction(HoverAction action) {
		this.action = action;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
