package com.guyde.plug.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import com.guyde.plug.json.ClickAction;
import com.guyde.plug.json.ClickEvent;
import com.guyde.plug.json.HoverAction;
import com.guyde.plug.json.HoverEvent;
import com.guyde.plug.json.JsonColor;
import com.guyde.plug.json.JsonMessage;
import com.guyde.plug.json.JsonText;
import com.guyde.plug.utils.ToolData;
import com.guyde.plug.utils.ToolHelper;



public class MainClass extends JavaPlugin {
    @Override
    public void onEnable() {
    	getServer().getPluginManager().registerEvents(new GuydeEventHandler(), this);
    	FileConfiguration config = this.getConfig();
    	config.addDefault("perms.players", Arrays.asList(new String[]{}));
    	this.saveConfig();
    	instance = this;
    }
    private static List<ToolData> tools = Arrays.asList(new ToolData[]{
    		new ToolData("copy_command","Used to copy one command at a time","magma cream"),
    		new ToolData("copy_commands","Used to copy multiple commands to your command list","nether brick"),
    		new ToolData("color","Used to color command blocks and signs","feather"),
    		new ToolData("uncolor","Used to uncolor command blocks and signs","golden apple"),
    		new ToolData("get_coords","Used to get the coords of the clicked block","bone"),
    		new ToolData("get_zone","Used to get the arguments of a zone between two blocks","slimeball")
    });
    
 
    public void setPrefix(String player , String prefix){
    	FileConfiguration config = this.getConfig();
    	config.set("players." + player + ".prefix",prefix);
    	pre.put(player,prefix.replace("(&)","§"));
    	this.saveConfig();
    }
    
    public boolean readPrefix(String player){
    	FileConfiguration config = this.getConfig();
    	if (config.contains("players." + player + ".prefix")){ 
    		pre.put(player,config.getString("players." + player + ".prefix").replace("(&)","§"));
    		return true;
    	}
    	return false;
    }
    
    public void setChatname(String player , String name){
    	FileConfiguration config = this.getConfig();
    	config.set("players." + player + ".chatname",name);
    	chat.put(player,name.replace("(&)","§"));
    	this.saveConfig();
    }
    
    public boolean readChatname(String player){
    	FileConfiguration config = this.getConfig();
    	if (config.contains("players." + player + ".chatname")){ 
    		chat.put(player,config.getString("players." + player + ".chatname").replace("(&)","§"));
    		return true;
    	}
    	return false;
    }
    
    
    public void setKarma(String player , String karma){
    	FileConfiguration config = this.getConfig();
    	config.set("players." + player + ".karma",karma);
    	this.karma.put(player,Pattern.compile("(&).").matcher(karma).replaceAll(""));
    	this.saveConfig();
    }
    
    public boolean readKarma(String player){
    	FileConfiguration config = this.getConfig();
    	if (config.contains("players." + player + ".karma")){ 
    		String str = Pattern.compile("(&).").matcher(config.getString("players." + player + ".karma")).replaceAll("");
    		str = str.replace("§", "");
    		this.karma.put(player,str);
    		return true;
    	}
    	return false;
    }
    public static MainClass instance;
    
    
    @Override
    public void onDisable() {

    }
    
    //prefix map//
    public Map<String,String> pre = new HashMap<String,String>();
    //chatname map//
    public Map<String,String> chat = new HashMap<String,String>();
    //karma map//
    public Map<String,String> karma = new HashMap<String,String>();
    //command handler//
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//sets the tool//
		if (cmd.getName().toLowerCase().equals("gtool")){
			Player player = Bukkit.getPlayer(sender.getName());
			if (args.length<1){
				JsonMessage msg = new JsonMessage();
				player.sendMessage("§cInvalid command format.");
				msg.addText("§cUsage: /gtool ");
				JsonText text = new JsonText("§c[ToolName]");
				text.setClickEvent(new ClickEvent(ClickAction.RUN_COMMAND,"/gtool list"));
				text.setHoverEvent(new HoverEvent(HoverAction.SHOW_TEXT,"Click this to see a list of all available tools"));
				msg.addComponent(text);
				msg.sendTo(player);
			} else if (args[0].equals("list")){
				player.sendMessage("§bHere is a list of all of the available tools:");
				for (ToolData tool : tools){
					JsonMessage msg = new JsonMessage();
					JsonText text = new JsonText("§b[§3" + tool.name + "§b]:");
					text.setClickEvent(new ClickEvent(ClickAction.RUN_COMMAND,"/gtool " + tool.name));
					msg.addComponent(text);
					JsonText t = new JsonText(" " + tool.explanation + ".");
					t.setColor(JsonColor.GOLD);
					msg.addComponent(t);
					msg.addText("\n§dDefault Item: §a" + tool.def);
					msg.sendTo(player);
				}
			} else if (player.getItemInHand()==null || player.getItemInHand().getType()==Material.AIR){
				JsonMessage msg = new JsonMessage();
				msg.addText("§cPlease hold an item in your hand to set it as a tool");
				msg.sendTo(player);
			} else {
				boolean bool = false;
				for (ToolData dat : tools){
					if (dat.name.equals(args[0])){
						bool = true;
						break;
					}
				}
				if (!bool){
					player.sendMessage("§cCould not find the tool " + args[0] );
					player.sendMessage("§cUse /gtool list   for a list of all the available tools ");
				} else {
					ToolHelper.setTool(args[0],player);
					player.sendMessage("§bSuccessfuly set (" + player.getItemInHand().getType().name() + "," + player.getItemInHand().getData().getData() + ") to the tool §3" + args[0]);
				}
			}
			
			return true;
		}
		//manages commands list//
		if (cmd.getName().toLowerCase().equals("cmdlist")){
			Player player = Bukkit.getPlayer(sender.getName());
			if (args[0].equals("undo")){
				if (player.hasMetadata("copied_cmds") && ((List<String>)player.getMetadata("copied_cmds").get(0).value()).size()>0){
					List<String> list = (List<String>)player.getMetadata("copied_cmds").get(0).value();
					list.remove(list.size()-1);
					player.setMetadata("copied_cmds", new FixedMetadataValue(MainClass.instance,list));
					player.sendMessage("§3[§bInfo§3] §bRemoved the last command from your list, there are currently " + list.size() + " commands in the list" );
				}
				
				return true;
			}
			if (args[0].equals("clear")){
				player.removeMetadata("copied_cmds", this);
				player.sendMessage("§3[§bInfo§3] §bCleared you command list" );
				return true;
			}
			if (args[0].equals("add")){
				String cmd_ = args[1];
				for (int i = 2;i<args.length; i++){
					cmd_ = cmd_ + " " + args[i];
				}
				JsonMessage msg = new JsonMessage();
				msg.addText("§3[§bInfo§3] §bAdded ");
				List<String> list = new ArrayList<String>();
				if (player.hasMetadata("copied_cmds")){
					list = (List<String>)player.getMetadata("copied_cmds").get(0).value();
				}
				JsonText text1 = new JsonText("§3[§bCMD #" + (list.size()+1) + "§3]");
				text1.setHoverEvent(new HoverEvent(HoverAction.SHOW_TEXT,cmd_));
				msg.addComponent(text1);
				msg.addText("§b to your commands list");
				msg.sendTo(player);
				return true;
			}
			if (args[0].equals("down")){
				int num = Integer.parseInt(args[1])-1;
				List<String> list = (List<String>)player.getMetadata("copied_cmds").get(0).value();
				if (list.size()>num+1){
					String st1 = list.get(num);
					list.set(num, list.get(num+1));
					list.set(num+1, st1);
					player.setMetadata("copied_cmds", new FixedMetadataValue(MainClass.instance,list));
					JsonMessage msg = new JsonMessage();
					msg.addText("§3[§bInfo§3] §bSwapped ");
					JsonText text1 = new JsonText("§3[§bCMD #" + (num+1) + "§3]");
					text1.setHoverEvent(new HoverEvent(HoverAction.SHOW_TEXT,list.get(num+1)));
					msg.addComponent(text1);
					msg.addText("§b with ");
					JsonText text2 = new JsonText("§3[§bCMD #" + (num+2) + "§3]");
					text2.setHoverEvent(new HoverEvent(HoverAction.SHOW_TEXT,list.get(num)));
					msg.addComponent(text2);
					msg.sendTo(player);
					player.sendMessage("§3[§bInfo§3] §bHere is your commands list, which contains " + list.size() + " commands:" );
					int i = 0;
					for (String cur : list){
						JsonMessage line = new JsonMessage();
						JsonText cmd_msg = new JsonText("§3[§bCMD #" + (i+1)+"§3] ");
						cmd_msg.setHoverEvent(new HoverEvent(HoverAction.SHOW_TEXT,cur));
						line.addComponent(cmd_msg);
						JsonText up_msg = new JsonText("§8[§7▲§8] ");
						if (i!=0){
							up_msg = new JsonText("§2[§a▲§2] ");
							up_msg.setClickEvent(new ClickEvent(ClickAction.RUN_COMMAND,"/cmdlist up " + (i+1)));
							
						}
						up_msg.setHoverEvent(new HoverEvent(HoverAction.SHOW_TEXT,"Used to move up the command"));
						line.addComponent(up_msg);
						JsonText down_msg = new JsonText("§8[§7▼§8] ");
						if (i+1!=list.size()){
							down_msg = new JsonText("§2[§a▼§2] ");
							down_msg.setClickEvent(new ClickEvent(ClickAction.RUN_COMMAND,"/cmdlist down " + (i+1)));
						}
						down_msg.setHoverEvent(new HoverEvent(HoverAction.SHOW_TEXT,"Used to move down the command"));
						line.addComponent(down_msg);
						JsonText del_msg = new JsonText("§4[§cX§4] ");
						del_msg.setHoverEvent(new HoverEvent(HoverAction.SHOW_TEXT,"Used to delete the command"));
						del_msg.setClickEvent(new ClickEvent(ClickAction.RUN_COMMAND,"/cmdlist delete " + (i+1)));
						line.addComponent(del_msg);
						line.sendTo(player);
						i++;
					}
				}
				return true;
			}
			if (args[0].equals("delete")){
				int num = Integer.parseInt(args[1])-1;
				List<String> list = (List<String>)player.getMetadata("copied_cmds").get(0).value();
				if (list.size()>num){
					list.remove(num);
					player.setMetadata("copied_cmds", new FixedMetadataValue(MainClass.instance,list));
					player.sendMessage("§3[§bInfo§3] §bDeleted §3[§bCMD #" + (num+1) + "§3]" );
					player.sendMessage("§3[§bInfo§3] §bHere is your commands list, which contains " + list.size() + " commands:" );
					int i = 0;
					for (String cur : list){
						JsonMessage line = new JsonMessage();
						JsonText cmd_msg = new JsonText("§3[§bCMD #" + (i+1)+"§3] ");
						cmd_msg.setHoverEvent(new HoverEvent(HoverAction.SHOW_TEXT,cur));
						line.addComponent(cmd_msg);
						JsonText up_msg = new JsonText("§8[§7▲§8] ");
						if (i!=0){
							up_msg = new JsonText("§2[§a▲§2] ");
							up_msg.setClickEvent(new ClickEvent(ClickAction.RUN_COMMAND,"/cmdlist up " + (i+1)));
							
						}
						up_msg.setHoverEvent(new HoverEvent(HoverAction.SHOW_TEXT,"Used to move up the command"));
						line.addComponent(up_msg);
						JsonText down_msg = new JsonText("§8[§7▼§8] ");
						if (i+1!=list.size()){
							down_msg = new JsonText("§2[§a▼§2] ");
							down_msg.setClickEvent(new ClickEvent(ClickAction.RUN_COMMAND,"/cmdlist down " + (i+1)));
						}
						down_msg.setHoverEvent(new HoverEvent(HoverAction.SHOW_TEXT,"Used to move down the command"));
						line.addComponent(down_msg);
						JsonText del_msg = new JsonText("§4[§cX§4] ");
						del_msg.setHoverEvent(new HoverEvent(HoverAction.SHOW_TEXT,"Used to delete the command"));
						del_msg.setClickEvent(new ClickEvent(ClickAction.RUN_COMMAND,"/cmdlist delete " + (i+1)));
						line.addComponent(del_msg);
						line.sendTo(player);
						i++;
					}
				}
				return true;
			}
			if (args[0].equals("up")){
				int num = Integer.parseInt(args[1])-1;
				List<String> list = (List<String>)player.getMetadata("copied_cmds").get(0).value();
				if (list.size()>num){
					String st1 = list.get(num);
					list.set(num, list.get(num-1));
					list.set(num-1, st1);
					player.setMetadata("copied_cmds", new FixedMetadataValue(MainClass.instance,list));
					JsonMessage msg = new JsonMessage();
					msg.addText("§3[§bInfo§3] §bSwapped ");
					JsonText text1 = new JsonText("§3[§bCMD #" + (num+1) + "§3]");
					text1.setHoverEvent(new HoverEvent(HoverAction.SHOW_TEXT,list.get(num-1)));
					msg.addComponent(text1);
					msg.addText("§b with ");
					JsonText text2 = new JsonText("§3[§bCMD #" + num + "§3]");
					text2.setHoverEvent(new HoverEvent(HoverAction.SHOW_TEXT,list.get(num)));
					msg.addComponent(text2);
					msg.sendTo(player);
					player.sendMessage("§3[§bInfo§3] §bHere is your commands list, which contains " + list.size() + " commands:" );
					int i = 0;
					for (String cur : list){
						JsonMessage line = new JsonMessage();
						JsonText cmd_msg = new JsonText("§3[§bCMD #" + (i+1)+"§3] ");
						cmd_msg.setHoverEvent(new HoverEvent(HoverAction.SHOW_TEXT,cur));
						line.addComponent(cmd_msg);
						JsonText up_msg = new JsonText("§8[§7▲§8] ");
						if (i!=0){
							up_msg = new JsonText("§2[§a▲§2] ");
							up_msg.setClickEvent(new ClickEvent(ClickAction.RUN_COMMAND,"/cmdlist up " + (i+1)));
							
						}
						up_msg.setHoverEvent(new HoverEvent(HoverAction.SHOW_TEXT,"Used to move up the command"));
						line.addComponent(up_msg);
						JsonText down_msg = new JsonText("§8[§7▼§8] ");
						if (i+1!=list.size()){
							down_msg = new JsonText("§2[§a▼§2] ");
							down_msg.setClickEvent(new ClickEvent(ClickAction.RUN_COMMAND,"/cmdlist down " + (i+1)));
						}
						down_msg.setHoverEvent(new HoverEvent(HoverAction.SHOW_TEXT,"Used to move down the command"));
						line.addComponent(down_msg);
						JsonText del_msg = new JsonText("§4[§cX§4] ");
						del_msg.setHoverEvent(new HoverEvent(HoverAction.SHOW_TEXT,"Used to delete the command"));
						del_msg.setClickEvent(new ClickEvent(ClickAction.RUN_COMMAND,"/cmdlist delete " + (i+1)));
						line.addComponent(del_msg);
						line.sendTo(player);
						i++;
					}
				}
				return true;
			}
			if (args[0].equals("edit")){
				if (player.hasMetadata("copied_cmds") && ((List<String>)player.getMetadata("copied_cmds").get(0).value()).size()>0){
					List<String> list = (List<String>)player.getMetadata("copied_cmds").get(0).value();
					player.sendMessage("§3[§bInfo§3] §bHere is your commands list, which contains " + list.size() + " commands:" );
					int i = 0;
					for (String cur : list){
						JsonMessage line = new JsonMessage();
						JsonText cmd_msg = new JsonText("§3[§bCMD #" + (i+1)+"§3] ");
						cmd_msg.setHoverEvent(new HoverEvent(HoverAction.SHOW_TEXT,cur));
						line.addComponent(cmd_msg);
						JsonText up_msg = new JsonText("§8[§7▲§8] ");
						if (i!=0){
							up_msg = new JsonText("§2[§a▲§2] ");
							up_msg.setClickEvent(new ClickEvent(ClickAction.RUN_COMMAND,"/cmdlist up " + (i+1)));
							
						}
						up_msg.setHoverEvent(new HoverEvent(HoverAction.SHOW_TEXT,"Used to move up the command"));
						line.addComponent(up_msg);
						JsonText down_msg = new JsonText("§8[§7▼§8] ");
						if (i+1!=list.size()){
							down_msg = new JsonText("§2[§a▼§2] ");
							down_msg.setClickEvent(new ClickEvent(ClickAction.RUN_COMMAND,"/cmdlist down " + (i+1)));
						}
						down_msg.setHoverEvent(new HoverEvent(HoverAction.SHOW_TEXT,"Used to move down the command"));
						line.addComponent(down_msg);
						JsonText del_msg = new JsonText("§4[§cX§4] ");
						del_msg.setHoverEvent(new HoverEvent(HoverAction.SHOW_TEXT,"Used to delete the command"));
						del_msg.setClickEvent(new ClickEvent(ClickAction.RUN_COMMAND,"/cmdlist delete " + (i+1)));
						line.addComponent(del_msg);
						line.sendTo(player);
						i++;
					}
				}
				return true;
			}
		}
		//reloads the config files//
		if (cmd.getName().toLowerCase().equals("guydereload")){
			this.reloadConfig();
			for(Entry<String,String> e : pre.entrySet()){
				readPrefix(e.getKey());
			}
			
			for(Entry<String,String> e : chat.entrySet()){
				readChatname(e.getKey());
			}
			
			for(Entry<String,String> e : karma.entrySet()){
				readKarma(e.getKey());
			}
			return true;
		}
		//sets prefix//
		if (cmd.getName().toLowerCase().equals("prefix")){
			if (!(sender instanceof org.bukkit.command.ConsoleCommandSender) && !this.getConfig().getStringList("perms.players").contains(sender.getName())){
				((Entity)sender).sendMessage(ChatColor.RED + "You have no permission to run this command");
				return true;
			}
			String player = args[0];
			String prefix = args[1];
			for (int i = 2; i<args.length;i++){
				prefix = prefix + " " + args[i];
			}
			setPrefix(player,prefix);
			
			return true;
		}
		//sets karma//
		if (cmd.getName().toLowerCase().equals("karma")){
			if (!(sender instanceof org.bukkit.command.ConsoleCommandSender) && !this.getConfig().getStringList("perms.players").contains(sender.getName())){
				((Entity)sender).sendMessage(ChatColor.RED + "You have no permission to run this command");
				return true;
			}
			String player = args[0];
			String kar = args[1];
			for (int i = 2; i<args.length;i++){
				kar = kar + " " + args[i];
			}
			setKarma(player,kar);
			return true;
		}
		//set the chatname//
		if (cmd.getName().toLowerCase().equals("chatname")){
			if (!(sender instanceof org.bukkit.command.ConsoleCommandSender) && !this.getConfig().getStringList("perms.players").contains(sender.getName())){
				((Entity)sender).sendMessage(ChatColor.RED + "You have no permission to run this command");
				return true;
			}
			String player = args[0];
			String chatname = args[1];
			for (int i = 2; i<args.length;i++){
				chatname = chatname + " " + args[i];
			}
			setChatname(player,chatname);
			return true;
		}
		//shocks somebody//
		if (cmd.getName().toLowerCase().equals("shock")){
			Bukkit.broadcastMessage(ChatColor.YELLOW + sender.getName() + " shocked " + args[0] );
			Bukkit.getPlayer(args[0]).getWorld().strikeLightning(Bukkit.getPlayer(args[0]).getLocation());
			String arg1 = "";
			String arg2 = "";
			if (args.length>1){
				arg1 = args[1]; 
			}
			if (args.length>2){
				arg2 = args[2]; 
			}
			if (arg1.equals("-a") || arg1.equals("-a")){
				Bukkit.getPlayer(args[0]).getWorld().strikeLightning(Bukkit.getPlayer(args[0]).getLocation());
				Bukkit.getPlayer(args[0]).getWorld().strikeLightning(Bukkit.getPlayer(args[0]).getLocation());
				Bukkit.getPlayer(args[0]).getWorld().strikeLightning(Bukkit.getPlayer(args[0]).getLocation());
			}
			if (arg2.equals("-k") || arg1.equals("-k")){
				Bukkit.getPlayer(args[0]).setHealth(0);
				Bukkit.getPlayer(args[0]).damage(1000000000);
			}
			return true;
		}
		//slaps somebody//
		if (cmd.getName().toLowerCase().equals("slap")){
			Bukkit.broadcastMessage(ChatColor.YELLOW + sender.getName() + " slapped " + args[0] );
			Bukkit.getPlayer(args[0]).setVelocity(new org.bukkit.util.Vector((new Random().nextInt(30)-15)/3d,3d,(new Random().nextInt(30)-15)/3d).add(Bukkit.getPlayer(args[0]).getVelocity()));
			String arg1 = "";
			String arg2 = "";
			if (args.length>1){
				arg1 = args[1]; 
			}
			if (args.length>2){
				arg2 = args[2]; 
			}
			if (arg2.equals("-k") || arg1.equals("-k")){
				Bukkit.getPlayer(args[0]).setHealth(0);
				Bukkit.getPlayer(args[0]).damage(1000000000);
			}
			return true;
		}
		//sets the chat name//
		if (cmd.getName().toLowerCase().equals("chatname")){
			String path = args[0];
			for (int i = 1; i<args.length;i++){
				path = path + " " + args[i];
			}
			chat.put(sender.getName(), path.replace("(&)","§"));
			return true;
		}
		//declare yourself as AFK//
		if (cmd.getName().toLowerCase().equals("afk")){
			Player player = Bukkit.getPlayer(sender.getName());
			Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + sender.getName() + " is now AFK");
			return true;
		}
		if (cmd.getName().toLowerCase().equals("cmdcol")){
			Player player = Bukkit.getPlayer(sender.getName());
			int x,y,z;
			if (args[0].startsWith("~")){
				x = Integer.parseInt(args[0].substring(1))+player.getLocation().getBlockX();
			} else {
				x = Integer.parseInt(args[0]);
			}
			if (args[1].startsWith("~")){
				y = Integer.parseInt(args[1].substring(1))+player.getLocation().getBlockY();
			} else {
				y = Integer.parseInt(args[1]);
			}
			if (args[2].startsWith("~")){
				z = Integer.parseInt(args[2].substring(1))+player.getLocation().getBlockZ();
			} else {
				z = Integer.parseInt(args[2]);
			}
			
			Block bl = player.getWorld().getBlockAt(x, y, z);
			CommandBlock block = (CommandBlock)bl.getState();
			block.setCommand(block.getCommand().replace('&', '§'));
			block.update();
			return true;

		}

		return false;
	}
	

}

