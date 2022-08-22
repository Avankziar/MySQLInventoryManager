package main.java.me.avankziar.mim.spigot.listener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.messaging.PluginMessageListener;

import main.java.me.avankziar.ifh.general.assistance.ChatApi;
import main.java.me.avankziar.mim.general.StaticValues;
import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.assistance.Utility;
import main.java.me.avankziar.mim.spigot.database.MysqlHandler;
import main.java.me.avankziar.mim.spigot.objects.PlayerData;

public class IsOnlineListener implements PluginMessageListener
{	
	private MIM plugin;
	
	public IsOnlineListener(MIM plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] bytes) 
	{
		if(channel.equals(StaticValues.ISONLINE_TOSPIGOT)) 
		{
        	ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
            DataInputStream in = new DataInputStream(stream);
            String task = null;
            try 
            {
            	task = in.readUTF();
            	if(task.equals(StaticValues.ISONLINE))
            	{
            		String mode = in.readUTF();
            		String requesterUUID = in.readUTF();
            		String targetuuid = in.readUTF();
            		boolean isOnline = in.readBoolean();
            		String requestServer = in.readUTF();
            		String targetserver = in.readUTF();
            		String targetname = Utility.convertUUIDToName(targetuuid);
            		if(!isOnline || (isOnline && requestServer.equals(targetserver)))
            		{
            			Player requester = Bukkit.getPlayer(UUID.fromString(requesterUUID));
            			String synchroKey = MIM.getPlugin().getConfigHandler().getSynchroKey(player);
            			UUID otheruuid = UUID.fromString(targetuuid);
            			Inventory inv = null;
            			Player other = Bukkit.getPlayer(otheruuid);
            			PlayerData pd = (PlayerData) MIM.getPlugin().getMysqlHandler().getData(MysqlHandler.Type.PLAYERDATA,
                				"`player_uuid` = ? AND `synchro_key` = ? AND `game_mode` = ?",
                				targetuuid, synchroKey,	requester.getGameMode().toString());
            			switch(mode)
            			{
            			case "INV":
            				inv = other != null ? other.getInventory() : InventoryCloseListener.getExternInventory(otheruuid,"INV","INV");
            				if(inv == null)
            				{
            					if(pd == null)
            					{
            						requester.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("PlayerNotExist")));
            						return;
            					}
            					inv = Bukkit.createInventory(player, 9*4, targetname+"`s Inventory");
            					inv.setContents(pd.getInventoryStorageContents());
            				}
            				InventoryCloseListener.addToExternInventory(player.getUniqueId(), otheruuid, inv,
            						"INV", "INV", synchroKey);
            				player.openInventory(inv);
            				requester.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Openable.InventoryOther")
            						.replace("%player%", targetname)));
            				break;
            			case "EC":
            				inv = other != null ? other.getEnderChest() : InventoryCloseListener.getExternInventory(otheruuid, "EC", "EC");
            				if(inv == null)
            				{
            					if(pd == null)
            					{
            						requester.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("PlayerNotExist")));
            						return;
            					}
            					inv = Bukkit.createInventory(player, 9*3, targetname+"`s EnderChest");
            					inv.setContents(pd.getEnderchestContents());
            				}
            				InventoryCloseListener.addToExternInventory(player.getUniqueId(), otheruuid, inv,
            						"EC", "EC", synchroKey);
            				player.openInventory(inv);
            				requester.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Openable.EnderchestOther")
            						.replace("%player%", targetname)));
            				
            				break;
            			case "ARMOR":
            				if(other != null)
            				{
            					inv = Bukkit.createInventory(player, 9*1, targetname+"`s Armor & OffHand");
            					inv.addItem(other.getInventory().getItemInOffHand());
            					for(int i = 0; i < other.getInventory().getArmorContents().length; i++)
            					{
            						ItemStack is = other.getInventory().getArmorContents()[i];
            						if(is != null)
            						{
            							inv.setItem(i+1, is);
            						}
            					}
            				} else
            				{
            					if(pd == null)
            					{
            						requester.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("PlayerNotExist")));
            						return;
            					}
            					inv = Bukkit.createInventory(player, 9*1, targetname+"`s Armor & OffHand");
            					inv.addItem(pd.getOffHand());
            					for(int i = 0; i < pd.getArmorContents().length; i++)
            					{
            						ItemStack is = pd.getArmorContents()[i];
            						if(is != null)
            						{
            							inv.setItem(i+1, is);
            						}
            					}	
            				}
            				InventoryCloseListener.addToExternInventory(player.getUniqueId(), otheruuid, inv,
            						"ARMOR", "ARMOR", synchroKey);
            				player.openInventory(inv);
            				break;
            			default:
            				break;
            			}
            		} else if(isOnline && !requestServer.equals(targetserver))
            		{
            			Player requester = Bukkit.getPlayer(UUID.fromString(requesterUUID));
            			if(requester != null)
            			{
            				requester.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("PlayerIsOnlineOnAnotherServer")
            						.replace("%player%", targetname)
            						.replace("%server%", targetserver)));
            			}
            		}
            	}
            } catch (IOException e) 
            {
    			e.printStackTrace();
    		}
		}
	}
	
	public static void sendRequest(Player player, String mode, String targetuuid)
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try 
        {
			out.writeUTF(StaticValues.ISONLINE);
			out.writeUTF(mode);
			out.writeUTF(player.getUniqueId().toString());
			out.writeUTF(targetuuid);
		} catch (IOException e) {
			e.printStackTrace();
		}
        player.sendPluginMessage(MIM.getPlugin(), StaticValues.ISONLINE_TOBUNGEE, stream.toByteArray());
	}
}