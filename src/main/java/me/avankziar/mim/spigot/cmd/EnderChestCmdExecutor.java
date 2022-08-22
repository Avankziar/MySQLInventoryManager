package main.java.me.avankziar.mim.spigot.cmd;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import main.java.me.avankziar.mim.general.ChatApi;
import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.assistance.Utility;
import main.java.me.avankziar.mim.spigot.cmdtree.CommandConstructor;
import main.java.me.avankziar.mim.spigot.listener.InventoryCloseListener;
import main.java.me.avankziar.mim.spigot.listener.IsOnlineListener;
import main.java.me.avankziar.mim.spigot.permission.Bypass;

public class EnderChestCmdExecutor implements CommandExecutor
{
	private MIM plugin;
	private static CommandConstructor cc;
	
	public EnderChestCmdExecutor(MIM plugin, CommandConstructor cc)
	{
		this.plugin = plugin;
		EnderChestCmdExecutor.cc = cc;
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
		if (args.length == 0) 
		{
			Inventory inv = InventoryCloseListener.getExternInventory(player.getUniqueId(), "EC", "EC");
			String synchroKey = MIM.getPlugin().getConfigHandler().getSynchroKey(player);
			if(inv == null)
			{
				inv = player.getEnderChest();
			}
			InventoryCloseListener.addToExternInventory(player.getUniqueId(), player.getUniqueId(), inv,
					"EC", "EC", synchroKey);
			player.openInventory(inv);
			return true;
		} else if(args.length == 1)
		{
			if(!sender.hasPermission(Bypass.get(Bypass.Permission.ENDERCHEST_OTHERPLAYER)))
			{
				sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("NoPermission")));
				return false;
			}
			String othername = args[0];
			UUID otheruuid = Utility.convertNameToUUID(othername);
			if(otheruuid == null)
			{
				sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("PlayerNotExist")));
				return false;
			}
			IsOnlineListener.sendRequest(player, "EC", otheruuid.toString());
			return true;
		} else
		{
			sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("DidYouMean")));
			sender.sendMessage(ChatApi.tl(cc.getSuggestion()));
			return false;
		}
	}
}