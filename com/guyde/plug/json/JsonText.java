package com.guyde.plug.json;

public class JsonText extends JsonComponent{
	
	private String text;
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public JsonText(String txt){
		text = txt;
	}
	
	@Override
	protected String getComponentJson() {
		return "text:\"" + text.replace("\"", "\\\"") + "\"";
	}

}
