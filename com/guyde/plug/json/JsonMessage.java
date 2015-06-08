package com.guyde.plug.json;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.v1_8_R2.IChatBaseComponent.ChatSerializer;

import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;


public class JsonMessage {
	
	private JsonComponent main;
	
	private List<JsonComponent> extra = new ArrayList<JsonComponent>();
	
	public JsonMessage(){
		
	}
	
	public JsonMessage(JsonComponent comp){
		main = comp;
	}
	
	public JsonComponent getMain() {
		return main;
	}

	public void setMain(JsonComponent main) {
		this.main = main;
	}
	
	public void addComponent(JsonComponent comp){
		if (main!=null){
			extra.add(comp);
		} else {
			main = comp;
		}
	}
	
	public void addText(String text){
		if (main!=null){
			extra.add(new JsonText(text));
		} else {
			main = new JsonText(text);
		}
	}

	public void removeComponent(JsonComponent comp){
		extra.remove(comp);
	}
	
	public void removeComponent(int index){
		extra.remove(index);
	}
	
	public String toJSON(){
		String ext = "";
		if (extra.size()>0){
			ext = ",extra:[";
			for (JsonComponent comp : extra){
				ext = ext + "{" + comp.toJSON() + "},";
			}
			ext = ext.substring(0,ext.length()-1) + "]";
		}
		return "{" + main.toJSON() + ext + "}";
	}
	
	public void sendTo(Player player){
		CraftPlayer craft = (CraftPlayer)player;
		craft.getHandle().sendMessage(ChatSerializer.a(toJSON()));
	}
}
