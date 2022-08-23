package main.java.me.avankziar.mim.spigot.cmd;

import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.mim.general.ChatApi;
import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.assistance.MatchApi;
import main.java.me.avankziar.mim.spigot.cmdtree.ArgumentConstructor;
import main.java.me.avankziar.mim.spigot.cmdtree.ArgumentModule;
import main.java.me.avankziar.mim.spigot.cmdtree.CommandConstructor;
import main.java.me.avankziar.mim.spigot.cmdtree.CommandExecuteType;
import main.java.me.avankziar.mim.spigot.cmdtree.CommandSuggest;
import main.java.me.avankziar.mim.spigot.database.MysqlHandler;
import main.java.me.avankziar.mim.spigot.handler.ConfigHandler;
import main.java.me.avankziar.mim.spigot.objects.PredefinePlayerState;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class PredefinePlayerStateCmdExecutor implements CommandExecutor
{
	private MIM plugin;
	private static CommandConstructor cc;
	
	public PredefinePlayerStateCmdExecutor(MIM plugin, CommandConstructor cc)
	{
		this.plugin = plugin;
		PredefinePlayerStateCmdExecutor.cc = cc;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) 
	{
		if(cc == null)
		{
			return false;
		}
		if(args.length == 0)
		{
			if(!(sender instanceof Player))
			{
				return false;
			}
			sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("UseAFollowingArgument")));
			return false;
		} else if(args.length == 1 && MatchApi.isInteger(args[0]))
		{
			if(!(sender instanceof Player))
			{
				return false;
			}
			int page = Integer.parseInt(args[1]);
			Player player = (Player) sender;
			list(player, page);
		}
		int length = args.length-1;
		ArrayList<ArgumentConstructor> aclist = cc.subcommands;
		for(int i = 0; i <= length; i++)
		{
			for(ArgumentConstructor ac : aclist)
			{
				if(args[i].equalsIgnoreCase(ac.getName()))
				{
					if(length >= ac.minArgsConstructor && length <= ac.maxArgsConstructor)
					{
						if (sender instanceof Player)
						{
							Player player = (Player) sender;
							if(player.hasPermission(ac.getPermission()))
							{
								ArgumentModule am = plugin.getArgumentMap().get(ac.getPath());
								if(am != null)
								{
									try
									{
										am.run(sender, args);
									} catch (IOException e)
									{
										e.printStackTrace();
									}
								} else
								{
									MIM.log.info("ArgumentModule from ArgumentConstructor %ac% not found! ERROR!"
											.replace("%ac%", ac.getName()));
									player.spigot().sendMessage(ChatApi.tctl(
											"ArgumentModule from ArgumentConstructor %ac% not found! ERROR!"
											.replace("%ac%", ac.getName())));
									return false;
								}
								return false;
							} else
							{
								player.spigot().sendMessage(ChatApi.tctl(plugin.getYamlHandler().getLang().getString("NoPermission")));
								return false;
							}
						} else
						{
							ArgumentModule am = plugin.getArgumentMap().get(ac.getPath());
							if(am != null)
							{
								try
								{
									am.run(sender, args);
								} catch (IOException e)
								{
									e.printStackTrace();
								}
							} else
							{
								MIM.log.info("ArgumentModule from ArgumentConstructor %ac% not found! ERROR!"
										.replace("%ac%", ac.getName()));
								sender.spigot().sendMessage(ChatApi.tctl(
										"ArgumentModule from ArgumentConstructor %ac% not found! ERROR!"
										.replace("%ac%", ac.getName())));
								return false;
							}
							return false;
						}
					} else
					{
						aclist = ac.subargument;
						break;
					}
				}
			}
		}
		sender.spigot().sendMessage(ChatApi.clickEvent(plugin.getYamlHandler().getLang().getString("InputIsWrong"),
				ClickEvent.Action.RUN_COMMAND, MIM.infoCommand));
		return false;
	}
	
	private void list(Player player, int page)
	{
		String synchroKey = new ConfigHandler(plugin).getSynchroKey(player);
		int count = plugin.getMysqlHandler().getCount(MysqlHandler.Type.PREDEFINEPLAYERSTATE,
				"`synchro_key` = ?", synchroKey);
		if(count <= 0)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("PredefinePlayerState.NoItemsWaiting")));
			return;
		}
		int quantity = 10;
		int start = page * quantity;
		if(start < 0)
			start = 0;
		ArrayList<PredefinePlayerState> list = PredefinePlayerState.convert(
				plugin.getMysqlHandler().getList(MysqlHandler.Type.PREDEFINEPLAYERSTATE, "`id` ASC",
				start, quantity, "`synchro_key` = ?", synchroKey));
		ArrayList<ArrayList<BaseComponent>> bc = new ArrayList<>();
		ArrayList<BaseComponent> bc1 = new ArrayList<>();
		bc1.add(ChatApi.tctl(plugin.getYamlHandler().getLang().getString("PredefinePlayerState.Headline")
				.replace("%amount%", String.valueOf(count))
				.replace("%synchrokey%", synchroKey)
				.replace("%page%", String.valueOf(page))));
		bc.add(bc1);
		for(PredefinePlayerState pdps : list)
		{
			ArrayList<BaseComponent> bc2 = new ArrayList<>();
			bc2.add(ChatApi.tctl(plugin.getYamlHandler().getLang().getString("PredefinePlayerState.LineStart")
					.replace("%statename%", pdps.getStateName())));
			bc2.add(ChatApi.apiChat(plugin.getYamlHandler().getLang().getString("PredefinePlayerState.LinePartI"),
					ClickEvent.Action.RUN_COMMAND,
					CommandSuggest.get(CommandExecuteType.PREDEFINEPLAYERSTATE_CREATE)+" "+pdps.getStateName(),
					HoverEvent.Action.SHOW_TEXT, plugin.getYamlHandler().getLang().getString("PredefinePlayerState.HoverI")));
			bc2.add(ChatApi.apiChat(plugin.getYamlHandler().getLang().getString("PredefinePlayerState.LinePartII"),
					ClickEvent.Action.RUN_COMMAND,
					CommandSuggest.get(CommandExecuteType.PREDEFINEPLAYERSTATE_LOAD)+" "+pdps.getStateName(),
					HoverEvent.Action.SHOW_TEXT, plugin.getYamlHandler().getLang().getString("PredefinePlayerState.HoverII")));
			bc2.add(ChatApi.apiChat(plugin.getYamlHandler().getLang().getString("PredefinePlayerState.LinePartIII"),
					ClickEvent.Action.RUN_COMMAND,
					CommandSuggest.get(CommandExecuteType.PREDEFINEPLAYERSTATE_DELETE)+" "+pdps.getStateName(),
					HoverEvent.Action.SHOW_TEXT, plugin.getYamlHandler().getLang().getString("PredefinePlayerState.HoverIII")));
			bc.add(bc2);
		}
		for(ArrayList<BaseComponent> bcl : bc)
		{
			TextComponent tx = ChatApi.tc("");
			tx.setExtra(bcl);
			player.spigot().sendMessage(tx);
		}
	}
}