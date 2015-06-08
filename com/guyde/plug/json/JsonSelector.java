package com.guyde.plug.json;

public class JsonSelector extends JsonComponent{
	
	private String Selector;
	
	public String getSelector() {
		return Selector;
	}
	
	public void setSelector(String Selector) {
		this.Selector = Selector;
	}
	
	public JsonSelector(String txt){
		Selector = txt;
	}
	
	@Override
	protected String getComponentJson() {
		return "selector:\"" + Selector.replace("\"", "\\\"") + "\"";
	}

}
