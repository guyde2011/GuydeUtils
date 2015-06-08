package com.guyde.plug.utils;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.guyde.plug.main.MainClass;

public class ToolHelper {
	

	public static boolean isUsingTool(String tool, Player player, Material d_mat, int d_dam){
		ConfigurationSection config = getPlayerConfig(player);
		Material mat = d_mat;
		int dam = d_dam;
		if (config.contains(tool + ".item")){
			mat = Material.getMaterial(config.getString(tool + ".item"));
			dam = config.getInt(tool + ".dmg");
		}
		return dam==(int)player.getItemInHand().getData().getData() && mat.equals(player.getItemInHand().getType());
	}
	
	public static void setTool(String tool, Player player){
		ConfigurationSection config = getPlayerConfig(player);
		config.set(tool + ".item",player.getItemInHand().getType().name());
		config.set(tool + ".dmg",(int)player.getItemInHand().getData().getData());
		MainClass.instance.saveConfig();
	}
	
	private static ConfigurationSection getPlayerConfig(Player player){
		FileConfiguration config = MainClass.instance.getConfig();
		ConfigurationSection sec = getConfig(config,"tool_selection");
		ConfigurationSection sec1 = getConfig(sec,player.getUniqueId().toString());
		ConfigurationSection sec2 = getConfig(sec1,"tools");
		return sec2;
	}
	
	private static ConfigurationSection getConfig(ConfigurationSection config,String str){
		if (!config.contains(str)) config.createSection(str);
		return config.getConfigurationSection(str);
	}
	
}
