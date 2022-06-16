package main.java.me.avankziar.mim.spigot.cmd;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import main.java.me.avankziar.mim.general.ChatApi;
import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.assistance.Utility;
import main.java.me.avankziar.mim.spigot.cmdtree.CommandConstructor;
import main.java.me.avankziar.mim.spigot.permission.Bypass;

public class AnvilCmdExecutor implements CommandExecutor
{
	private MIM plugin;
	private static CommandConstructor cc;
	
	public AnvilCmdExecutor(MIM plugin, CommandConstructor cc)
	{
		this.plugin = plugin;
		AnvilCmdExecutor.cc = cc;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) 
	{
		if(cc == null)
		{
			return false;
		}
		if(!sender.hasPermission(cc.getPermission()))
		{
			sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("NoPermission")));
			return false;
		}
		if (args.length == 0) 
		{
			if(!(sender instanceof Player))
			{
				sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CommandWorkOnlyForPlayer")));
				return false;
			}
			Player player = (Player) sender;
			Inventory inv = Bukkit.createInventory(null, InventoryType.ANVIL);
			player.openInventory(inv);
			return true;
		} else if(args.length == 1)
		{
			if(!sender.hasPermission(Bypass.get(Bypass.Permission.ANVIL_OTHERPLAYER)))
			{
				sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("NoPermission")));
				return false;
			}
			String othername = args[1];
			UUID uuid = Utility.convertNameToUUID(othername);
			if(uuid == null)
			{
				sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("PlayerNotExist")));
				return false;
			}
			Player other = Bukkit.getPlayer(uuid);
			if(other == null)
			{
				sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("PlayerNotExist")));
				return false;
			}
			Inventory inv = Bukkit.createInventory(null, InventoryType.ANVIL);
			other.openInventory(inv);
			sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Openable.AnvilOther")
					.replace("%player%", other.getName())));
			return true;
		} else
		{
			sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("DidYouMean")));
			sender.sendMessage(ChatApi.tl(cc.getSuggestion()));
			return false;
		}
	}
}