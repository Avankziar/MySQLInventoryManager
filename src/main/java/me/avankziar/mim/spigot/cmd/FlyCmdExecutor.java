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

public class FlyCmdExecutor implements CommandExecutor
{
	private MIM plugin;
	private static CommandConstructor cc;
	
	public FlyCmdExecutor(MIM plugin, CommandConstructor cc)
	{
		this.plugin = plugin;
		FlyCmdExecutor.cc = cc;
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
		if(args.length == 0)
		{
			if(player.isFlying())
			{
				player.setFlying(false);
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Fly.YouFly")));
			} else
			{
				player.setFlying(true);
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Fly.YouDontFly")));
			}
		} else if(args.length == 1)
		{
			if(!sender.hasPermission(Bypass.get(Bypass.Permission.FLY_OTHERPLAYER)))
			{
				sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("NoPermission")));
				return false;
			}
			String othername = args[0];
			UUID otheruuid = Utility.convertNameToUUID(othername);
			if(otheruuid == null || Bukkit.getPlayer(otheruuid) == null)
			{
				sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("PlayerNotExist")));
				return false;
			}
			Player other = Bukkit.getPlayer(otheruuid);
			if(other.isFlying())
			{
				other.setFlying(false);
				other.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Fly.YouFly")));
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Fly.OtherFly").replace("%player%", othername)));
			} else
			{
				other.setFlying(true);
				other.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Fly.YouDontFly")));
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Fly.OtherDontFly").replace("%player%", othername)));
			}
		}
		return true;
	}
}