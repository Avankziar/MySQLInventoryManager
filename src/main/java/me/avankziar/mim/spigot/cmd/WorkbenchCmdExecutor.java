package main.java.me.avankziar.mim.spigot.cmd;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.mim.general.ChatApi;
import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.assistance.Utility;
import main.java.me.avankziar.mim.spigot.cmdtree.CommandConstructor;
import main.java.me.avankziar.mim.spigot.permission.Bypass;

public class WorkbenchCmdExecutor implements CommandExecutor
{
	private MIM plugin;
	private static CommandConstructor cc;
	
	public WorkbenchCmdExecutor(MIM plugin, CommandConstructor cc)
	{
		this.plugin = plugin;
		WorkbenchCmdExecutor.cc = cc;
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
			player.openWorkbench(player.getLocation(), true);
			return true;
		} else if(args.length == 1)
		{
			if(!sender.hasPermission(Bypass.get(Bypass.Permission.WORKBENCH_OTHERPLAYER)))
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
			other.openWorkbench(other.getLocation(), true);
			sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Openable.WorkbenchOther")
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