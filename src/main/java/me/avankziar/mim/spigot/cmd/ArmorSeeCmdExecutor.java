package main.java.me.avankziar.mim.spigot.cmd;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import main.java.me.avankziar.mim.general.ChatApi;
import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.assistance.Utility;
import main.java.me.avankziar.mim.spigot.cmdtree.CommandConstructor;
import main.java.me.avankziar.mim.spigot.listener.InventoryCloseListener;
import main.java.me.avankziar.mim.spigot.permission.Bypass;

public class ArmorSeeCmdExecutor implements CommandExecutor
{
	private MIM plugin;
	private static CommandConstructor cc;
	
	public ArmorSeeCmdExecutor(MIM plugin, CommandConstructor cc)
	{
		this.plugin = plugin;
		ArmorSeeCmdExecutor.cc = cc;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) 
	{
		if(cc == null)
		{
			return false;
		}
		if(!(sender instanceof Player))
		{
			sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CommandWorkOnlyForPlayer")));
			return false;
		}
		Player player = (Player) sender;
		if(!player.hasPermission(cc.getPermission()))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("NoPermission")));
			return false;
		}
		if(args.length == 1)
		{
			if(!sender.hasPermission(Bypass.get(Bypass.Permission.INVENTORYSEE_OTHERPLAYER)))
			{
				sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("NoPermission")));
				return false;
			}
			String synchroKey = MIM.getPlugin().getConfigHandler().getSynchroKey(player, false);
			String othername = args[0];
			UUID otheruuid = Utility.convertNameToUUID(othername);
			if(otheruuid == null)
			{
				sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("PlayerNotExist")));
				return false;
			}
			Player other = Bukkit.getPlayer(otheruuid);
			Inventory inv = null;
			if(other == null)
			{
				sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("PlayerNotOnline")));
				return false;	
			} else
			{
				inv = Bukkit.createInventory(player, 9*1, othername+"`s Armor & OffHand");
				inv.addItem(other.getInventory().getItemInOffHand());
				for(int i = 0; i < other.getInventory().getArmorContents().length; i++)
				{
					ItemStack is = other.getInventory().getArmorContents()[i];
					if(is != null)
					{
						inv.setItem(i+1, is);
					}
				}		
			}
			InventoryCloseListener.addToExternInventory(player.getUniqueId(), otheruuid, inv,
					"ARMOR", "ARMOR", player.getGameMode(), synchroKey);
			player.openInventory(inv);
			return true;
		} else
		{
			sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("DidYouMean")));
			sender.sendMessage(ChatApi.tl(cc.getSuggestion()));
			return false;
		}
	}
}