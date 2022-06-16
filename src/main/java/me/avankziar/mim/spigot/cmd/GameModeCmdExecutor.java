package main.java.me.avankziar.mim.spigot.cmd;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.mim.general.ChatApi;
import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.assistance.MatchApi;
import main.java.me.avankziar.mim.spigot.assistance.Utility;
import main.java.me.avankziar.mim.spigot.cmdtree.CommandConstructor;
import main.java.me.avankziar.mim.spigot.permission.Bypass;

public class GameModeCmdExecutor implements CommandExecutor
{
	private MIM plugin;
	private static CommandConstructor cc;
	
	public GameModeCmdExecutor(MIM plugin, CommandConstructor cc)
	{
		this.plugin = plugin;
		GameModeCmdExecutor.cc = cc;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) 
	{
		if(cc == null)
		{
			return false;
		}
		if(args.length >= 1)
		{
			if(!sender.hasPermission(cc.getPermission()))
			{
				sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("NoPermission")));
				return false;
			}
			String sgm = args[0];
			GameMode gm = GameMode.SURVIVAL;
			if(MatchApi.isInteger(sgm))
			{
				int igm = Integer.parseInt(sgm);
				switch(igm)
				{
				default: 
					gm = GameMode.SURVIVAL;
					break;
				case 0:
					gm = GameMode.SURVIVAL;
					break;
				case 1:
					gm = GameMode.CREATIVE;
					break;
				case 2:
					gm = GameMode.ADVENTURE;
					break;
				case 3:
					gm = GameMode.SPECTATOR;
					break;
				}
			}
			if((gm == GameMode.SURVIVAL && !sender.hasPermission(Bypass.get(Bypass.Permission.GM_SURVIVAL)))
				|| (gm == GameMode.CREATIVE && !sender.hasPermission(Bypass.get(Bypass.Permission.GM_CREATIVE)))
				|| (gm == GameMode.ADVENTURE && !sender.hasPermission(Bypass.get(Bypass.Permission.GM_ADVENTURE)))
				|| (gm == GameMode.SPECTATOR && !sender.hasPermission(Bypass.get(Bypass.Permission.GM_SPECTATOR))))
			{
				sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("NoPermission")));
				return false;
			}
			if(args.length == 1)
			{
				if(!(sender instanceof Player))
				{
					sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CommandWorkOnlyForPlayer")));
					return false;
				}
				Player player = (Player) sender;
				player.setGameMode(gm);
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("GameMode.SetGameMode")
						.replace("%gamemode%", plugin.getYamlHandler().getLang().getString("GameMode."+gm.toString()))));
			} else if(args.length == 2)
			{
				if(!sender.hasPermission(Bypass.get(Bypass.Permission.GM_OTHERPLAYER)))
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
				other.setGameMode(gm);
				sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("GameMode.SetOtherGameMode")
						.replace("%player%", other.getName())
						.replace("%gamemode%", plugin.getYamlHandler().getLang().getString("GameMode."+gm.toString()))));
				other.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("GameMode.SettedGameMode")
						.replace("%gamemode%", plugin.getYamlHandler().getLang().getString("GameMode."+gm.toString()))));
			}
			return true;
		} else
		{
			sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("DidYouMean")));
			sender.sendMessage(ChatApi.tl(cc.getSuggestion()));
			return false;
		}
	}
}