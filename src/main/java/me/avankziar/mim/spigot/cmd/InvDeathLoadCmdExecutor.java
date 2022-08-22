package main.java.me.avankziar.mim.spigot.cmd;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.mim.general.ChatApi;
import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.assistance.MatchApi;
import main.java.me.avankziar.mim.spigot.assistance.TimeHandler;
import main.java.me.avankziar.mim.spigot.assistance.Utility;
import main.java.me.avankziar.mim.spigot.cmdtree.CommandConstructor;
import main.java.me.avankziar.mim.spigot.database.MysqlHandler;
import main.java.me.avankziar.mim.spigot.objects.DeathMemoryState;
import main.java.me.avankziar.mim.spigot.objects.SyncTask;
import main.java.me.avankziar.mim.spigot.objects.SyncType;
import main.java.me.avankziar.mim.spigot.permission.Bypass;

public class InvDeathLoadCmdExecutor implements CommandExecutor
{
	private MIM plugin;
	private static CommandConstructor cc;
	
	public InvDeathLoadCmdExecutor(MIM plugin, CommandConstructor cc)
	{
		this.plugin = plugin;
		InvDeathLoadCmdExecutor.cc = cc;
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
		UUID otheruuid = null;
		Player other = null;
		int dmsid = 0;
		if(args.length == 2)
		{
			if(!sender.hasPermission(Bypass.get(Bypass.Permission.INV_DEATHLOAD_OTHER)))
			{
				sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("NoPermission")));
				return false;
			}
			String othername = args[0];
			otheruuid = Utility.convertNameToUUID(othername);
			if(otheruuid == null)
			{
				sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("PlayerNotExist")));
				return false;
			}
			other = Bukkit.getPlayer(otheruuid);
			if(other == null)
			{
				sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("PlayerNotOnline")));
				return false;
			}
			if(!MatchApi.isInteger(args[1]))
			{
				sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("DidYouMean")));
				sender.sendMessage(ChatApi.tl(cc.getSuggestion()));
				return false;
			}
			dmsid = Integer.parseInt(args[1]);
		} else
		{
			sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("DidYouMean")));
			sender.sendMessage(ChatApi.tl(cc.getSuggestion()));
			return false;
		}
		DeathMemoryState dms = (DeathMemoryState) plugin.getMysqlHandler().getData(MysqlHandler.Type.DEATHMEMORYSTATE, "`id` = ?", dmsid);
		if(dms == null)
		{
			sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("DeathMemoryState.NotExist")));
			return false;
		}
		new SyncTask(plugin, SyncType.FULL, dmsid, other).run();
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("DeathMemoryState.LoadPlayer")
				.replace("%player%", other.getName())
				.replace("%time%", TimeHandler.getDate(dms.getTimeStamp()))));
		other.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("DeathMemoryState.LoadOther")
				.replace("%player%", player.getName())
				.replace("%time%", TimeHandler.getDate(dms.getTimeStamp()))));
		return true;
	}
}