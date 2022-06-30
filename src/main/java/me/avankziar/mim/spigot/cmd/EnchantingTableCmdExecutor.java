package main.java.me.avankziar.mim.spigot.cmd;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.mim.general.ChatApi;
import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.cmdtree.CommandConstructor;

public class EnchantingTableCmdExecutor implements CommandExecutor
{
	private MIM plugin;
	private static CommandConstructor cc;
	
	public static ArrayList<UUID> inEnchantingTable = new ArrayList<>();
	
	public EnchantingTableCmdExecutor(MIM plugin, CommandConstructor cc)
	{
		this.plugin = plugin;
		EnchantingTableCmdExecutor.cc = cc;
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
			inEnchantingTable.add(player.getUniqueId());
			//player.openInventory(Bukkit.createInventory(null, InventoryType.ENCHANTING)); //Öffnet, aber nichts funktioniert
			//player.openEnchanting(player.getLocation(), true); //Öffnet erst gar nicht
			return true;
		} else
		{
			sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("DidYouMean")));
			sender.sendMessage(ChatApi.tl(cc.getSuggestion()));
			return false;
		}
	}
}