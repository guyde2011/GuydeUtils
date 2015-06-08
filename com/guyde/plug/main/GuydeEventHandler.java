package com.guyde.plug.main;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import net.minecraft.server.v1_8_R2.IChatBaseComponent;
import net.minecraft.server.v1_8_R2.IChatBaseComponent.ChatSerializer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.metadata.FixedMetadataValue;

import com.guyde.plug.json.ClickAction;
import com.guyde.plug.json.ClickEvent;
import com.guyde.plug.json.JsonColor;
import com.guyde.plug.json.JsonMessage;
import com.guyde.plug.json.JsonText;
import com.guyde.plug.utils.ToolHelper;




public class GuydeEventHandler implements Listener{
	/**
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		ItemStack stack = event.getItem();
		net.minecraft.server.v1_8_R1.ItemStack stak = CraftItemStack.asNMSCopy(stack);
		NBTTagCompound comp = stak.getTag();
		if (comp!=null && comp.getString("info")!=null){
			ItemInfo info = MainClass.items.get(comp.getString("info"));
			if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
				event.setCancelled(!info.leftClick.runnable.run(event));
			}
			if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
				event.setCancelled(!info.rightClick.runnable.run(event));
			}
		}
	}**/
	
	@EventHandler
	public void onPing(ServerListPingEvent event){
		String motd = MainClass.instance.getConfig().getString("motd");
    	event.setMotd(motd);
    	List<Player> players = new ArrayList<Player>();
    	players.addAll(Bukkit.getOnlinePlayers());
    	BufferedImage full = new BufferedImage(64, 64, BufferedImage.TYPE_4BYTE_ABGR);
    	for (int i = 0; i<4; i++ ){
        	for (int j = 0; j<4; j++ ){
        		if (i*4 + j >= players.size()) break;
        		BufferedImage img = null;
        		try {
					img = ImageIO.read(new URL("https://minotar.net/avatar/" + players.get(i*4 + j).getDisplayName() + "/16.png"));
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		full.getGraphics().drawImage(img,i*16,j*16,null);
        		
        	}
    	}
    	
    	try {
			event.setServerIcon(Bukkit.loadServerIcon(full));
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	event.setMaxPlayers(100 + event.getNumPlayers());
	}
	@EventHandler(priority=EventPriority.LOW)
	public void playerChat(AsyncPlayerChatEvent event){
		Player player = event.getPlayer();
		if (!MainClass.instance.pre.containsKey(player.getDisplayName()) && !MainClass.instance.readPrefix(player.getDisplayName())){
			MainClass.instance.pre.put(player.getDisplayName(),ChatColor.GRAY + "[" + ChatColor.DARK_GRAY + "Guest" + ChatColor.GRAY + "]" + ChatColor.RESET);
		}
		
		String prefix = MainClass.instance.pre.get(player.getDisplayName());
		String name = "";
		
		if (!MainClass.instance.chat.containsKey(player.getDisplayName()) && !MainClass.instance.readChatname(player.getDisplayName())){
			name = ChatColor.RESET + "<" + player.getDisplayName() + ">";
		} else {
			name = MainClass.instance.chat.get(player.getDisplayName());
		}
		
		String karma = ChatColor.DARK_AQUA + "[0]" + ChatColor.RESET + " ";
		if (MainClass.instance.karma.containsKey(player.getDisplayName()) || MainClass.instance.readKarma(player.getDisplayName())){
			karma = ChatColor.DARK_AQUA + "[" + MainClass.instance.karma.get(player.getDisplayName()) + ChatColor.DARK_AQUA + "]" + ChatColor.RESET + " ";
		} else {
			MainClass.instance.setKarma(player.getDisplayName(), "0");
		}
		String message = karma + prefix + name + " " + event.getMessage();
	    Bukkit.broadcastMessage(message);
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockDestroy(BlockBreakEvent event){
		if (ToolHelper.isUsingTool("copy_command",event.getPlayer(),Material.MAGMA_CREAM,0)){
			Block block = event.getBlock();
			int x = block.getLocation().getBlockX();
			int y = block.getLocation().getBlockY();
			int z = block.getLocation().getBlockZ();
			if (block.getType()==Material.COMMAND){
				if (event.getPlayer().isSneaking() && event.getPlayer().hasMetadata("copied_cmd")){
					event.getPlayer().sendMessage("§3[§bInfo§3] §bPasted your command at " + x + " " + y + " " + z);
					CommandBlock cmd = (CommandBlock)block.getState();
					cmd.setCommand(event.getPlayer().getMetadata("copied_cmd").get(0).asString());
					cmd.update();
					event.setCancelled(true);
				}
			}
		}
		if (ToolHelper.isUsingTool("copy_commands",event.getPlayer(),Material.NETHER_BRICK_ITEM,0)){
			Block block = event.getBlock();
			int x = block.getLocation().getBlockX();
			int y = block.getLocation().getBlockY();
			int z = block.getLocation().getBlockZ();
			if (block.getType()==Material.COMMAND){
				if (event.getPlayer().hasMetadata("copied_cmds")){
					List<String> list = (List<String>)event.getPlayer().getMetadata("copied_cmds").get(0).value();
					if (event.getPlayer().isSneaking()){
						CommandBlock cmd = (CommandBlock)block.getState();
						cmd.setCommand(list.get(0));
						cmd.update();
						list.remove(0);
						event.getPlayer().sendMessage("§3[§bInfo§3] §bPasted and removed your first command on the list at " + x + " " + y + " " + z + ", there are currently " + list.size() + " commands in the list" );
					} else {
						CommandBlock cmd = (CommandBlock)block.getState();
						cmd.setCommand(list.get(0));
						cmd.update();
						event.getPlayer().sendMessage("§3[§bInfo§3] §bPasted your first command on the list at " + x + " " + y + " " + z + ", there are currently " + list.size() + " commands in the list" );
					}
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority=EventPriority.HIGH)
	public void onPlayerUse(PlayerInteractEvent event){
		if (event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.LEFT_CLICK_AIR)) return;
		if (ToolHelper.isUsingTool("color",event.getPlayer(),Material.FEATHER,0)){
			if (event.getClickedBlock().getState() instanceof Sign){
				Sign state = (Sign)event.getClickedBlock().getState();
				int i = 0;
				for(String line : state.getLines()){
					state.setLine(i, line.replace("&", "§"));
					i++;
				}
				state.update();
			} else 	if (event.getClickedBlock().getState() instanceof CommandBlock){
				CommandBlock state = (CommandBlock)event.getClickedBlock().getState();
				state.setCommand(state.getCommand().replace("&", "§"));
				state.update();
			}
			
		}
		if (ToolHelper.isUsingTool("get_zone",event.getPlayer(),Material.SLIME_BALL,0)){
			Block block = event.getClickedBlock();
			int x = block.getLocation().getBlockX();
			int y = block.getLocation().getBlockY();
			int z = block.getLocation().getBlockZ();
			if (event.getPlayer().isSneaking()){
				JsonMessage msg = new JsonMessage();
				msg.addText("§3[§bInfo§3] §bSet §bthe §bsecond §bposition §bto §b" + x + ", §b" + y + ", §b" + z + "§b. §bWould §byou §blike §bto §bhave §bthe §bzone §bparams? ");
				JsonText txt = new JsonText("(" + block.getType().name().toLowerCase() + ", " + block.getData() + ") ");
				txt.setColor(JsonColor.WHITE);
				msg.addComponent(txt);
				JsonText text = new JsonText("§2[§aYes§2]");
				if (event.getPlayer().hasMetadata("zone_copy")){
					Location loc = (Location)event.getPlayer().getMetadata("zone_copy").get(0).value();
					int x1 = loc.getBlockX();
					int y1 = loc.getBlockY();
					int z1 = loc.getBlockZ();
					text.setClickEvent(new ClickEvent(ClickAction.SUGGEST_COMMAND,"x=" + Math.min(x,x1) + ",y=" + Math.min(y,y1) + ",z=" + Math.min(z,z1) + ",dx=" + Math.abs(x-x1) + ",dy=" + Math.abs(y-y1) + ",dz=" + Math.abs(z-z1)));
				}
				msg.addComponent(text);
				msg.sendTo(event.getPlayer());
			} else {
				event.getPlayer().setMetadata("zone_copy",new FixedMetadataValue(MainClass.instance,block.getLocation()));
				event.getPlayer().sendMessage("§3[§bInfo§3] §bSet §bthe §bfirst §bposition §bto §b" + x + ", §b" + y + ", §b" + z + "§b.");
			}
		}
		if (ToolHelper.isUsingTool("get_coords",event.getPlayer(),Material.BONE,0)){
			Block block = event.getClickedBlock();
			int x = block.getLocation().getBlockX();
			int y = block.getLocation().getBlockY();
			int z = block.getLocation().getBlockZ();
			String JSON = "{text:\"§aWould you like to have the coords of: [" + x + ", " + y + ", " + z + "] (" + block.getType().name().toLowerCase() + ", " + block.getData() + ")\",extra:[{text:\" §3[§bCMD Params§3]\",clickEvent:{action:suggest_command,value:\"" + x + " " + y + " " + z + "\"}},{text:\" §3[§bCMD Selector§3]\",clickEvent:{action:suggest_command,value:\"x=" + x + ",y=" + y + ",z=" + z + "\"}}]}";
			IChatBaseComponent msg = ChatSerializer.a(JSON);
			((CraftPlayer)event.getPlayer()).getHandle().sendMessage(msg);
		}
		
		if (ToolHelper.isUsingTool("copy_command",event.getPlayer(),Material.MAGMA_CREAM,0)){
			Block block = event.getClickedBlock();
			int x = block.getLocation().getBlockX();
			int y = block.getLocation().getBlockY();
			int z = block.getLocation().getBlockZ();
			if (block.getType()==Material.COMMAND){
				if (event.getPlayer().isSneaking()){
					event.getPlayer().setMetadata("copied_cmd", new FixedMetadataValue(MainClass.instance,((CommandBlock)block.getState()).getCommand()));
					event.getPlayer().sendMessage("§3[§bInfo§3] §bCopied the command from the command block at " + x + " " + y + " " + z);
				} 
			}
		}
		
		if (ToolHelper.isUsingTool("copy_commands",event.getPlayer(),Material.NETHER_BRICK_ITEM,0)){
			Block block = event.getClickedBlock();
			int x = block.getLocation().getBlockX();
			int y = block.getLocation().getBlockY();
			int z = block.getLocation().getBlockZ();
			if (block.getType()==Material.COMMAND){
				if (event.getPlayer().isSneaking()){
					List<String> list = new ArrayList<String>();
					if (event.getPlayer().hasMetadata("copied_cmds") && event.getPlayer().getMetadata("copied_cmds").size()>0){ 
						list = (List<String>)event.getPlayer().getMetadata("copied_cmds").get(0).value();
					}
					list.add(((CommandBlock)block.getState()).getCommand());
					event.getPlayer().setMetadata("copied_cmds", new FixedMetadataValue(MainClass.instance,list));
					event.getPlayer().sendMessage("§3[§bInfo§3] §bAdded to copied the command from the command block at " + x + " " + y + " " + z + ", there are currently " + list.size() + " commands in the list" );
				} 
			}
		}
		if (ToolHelper.isUsingTool("uncolor",event.getPlayer(),Material.GOLDEN_APPLE,0)){
			if (event.getClickedBlock().getState() instanceof Sign){
				Sign state = (Sign)event.getClickedBlock().getState();
				int i = 0;
				for(String line : state.getLines()){
					state.setLine(i, line.replace("§" , "&"));
					i++;
				}
				state.update();
			} else 	if (event.getClickedBlock().getState() instanceof CommandBlock){
				CommandBlock state = (CommandBlock)event.getClickedBlock().getState();
				state.setCommand(state.getCommand().replace("§" , "&"));
				state.update();
			}
			
		}
	}
	
}
