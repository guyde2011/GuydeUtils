package com.guyde.plug.json;

public abstract class JsonComponent {
	
	private JsonColor color; 
	private ClickEvent click_event;
	private HoverEvent hover_event;
	
	public boolean isStrikethrough() {
		return strikethrough;
	}

	public void setStrikethrough(boolean strikethrough) {
		this.strikethrough = strikethrough;
	}

	public boolean isUnderlined() {
		return underlined;
	}

	public void setUnderlined(boolean underlined) {
		this.underlined = underlined;
	}

	public boolean isItalic() {
		return italic;
	}

	public void setItalic(boolean italic) {
		this.italic = italic;
	}

	private boolean strikethrough = false;
	private boolean underlined = false;
	private boolean italic = false;
	private boolean bold = false;
	protected abstract String getComponentJson();
	
	public JsonColor getColor() {
		return color;
	}

	public void setColor(JsonColor color) {
		this.color = color;
	}

	public ClickEvent getClickEvent() {
		return click_event;
	}

	public void setClickEvent(ClickEvent click_event) {
		this.click_event = click_event;
	}

	public HoverEvent getHoverEvent() {
		return hover_event;
	}

	public void setHoverEvent(HoverEvent hover_event) {
		this.hover_event = hover_event;
	}
	
	public String toJSON(){
		String click = "";
		if (click_event!=null){
			click = "," + click_event.toJSON();
		}
		String hover = "";
		if (hover_event!=null){
			hover = "," + hover_event.toJSON();
		}
		String col = "";
		if (color!=null){
			col = ",color:" + color.json;
		}
		String ext = "";
		if (strikethrough){
			ext = ext + ",strikethrough:true";
		}
		if (italic){
			ext = ext + ",italic:true";
		}
		if (bold){
			ext = ext + ",bold:true";
		}
		if (underlined){
			ext = ext + ",underline:true";
		}
		return getComponentJson() + click + hover + col + ext;
	}
	

	
	
}
