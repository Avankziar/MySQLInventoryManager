package me.avankziar.mim.spigot.cmd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.avankziar.mim.general.assistance.ChatApiB;
import me.avankziar.mim.general.assistance.ChatApiS;
import me.avankziar.mim.general.assistance.ChatApiV;
import me.avankziar.mim.general.assistance.MatchApi;
import me.avankziar.mim.general.cmdtree.ArgumentConstructor;
import me.avankziar.mim.general.cmdtree.BaseConstructor;
import me.avankziar.mim.general.cmdtree.CommandConstructor;
import me.avankziar.mim.general.cmdtree.CommandSuggest;
import me.avankziar.mim.spigot.MIM;
import me.avankziar.mim.spigot.cmdtree.ArgumentModule;
import net.md_5.bungee.api.chat.ClickEvent;

public class BaseCommandExecutor implements CommandExecutor
{
	private MIM plugin;
	private static CommandConstructor cc;
	
	public BaseCommandExecutor(MIM plugin, CommandConstructor cc)
	{
		this.plugin = plugin;
		BaseCommandExecutor.cc = cc;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) 
	{
		if(cc == null)
		{
			return false;
		}
		if (args.length == 1) 
		{
			if (!(sender instanceof Player)) 
			{
				plugin.getLogger().info("Cmd is only for Player!");
				return false;
			}
			Player player = (Player) sender;
			if(MatchApi.isInteger(args[0]))
			{
				if(!player.hasPermission(cc.getPermission()))
				{
					///Du hast dafür keine Rechte!
					player.spigot().sendMessage(ChatApiS.tl(plugin.getYamlHandler().getLang().getString("NoPermission")));
					return false;
				}
				baseCommands(player, Integer.parseInt(args[0])); //Base and Info Command
				return true;
			}
		} else if(args.length == 0)
		{
			if (!(sender instanceof Player)) 
			{
				plugin.getLogger().info("Cmd is only for Player!");
				return false;
			}
			Player player = (Player) sender;
			if(!player.hasPermission(cc.getPermission()))
			{
				///Du hast dafür keine Rechte!
				player.spigot().sendMessage(ChatApiS.tl(plugin.getYamlHandler().getLang().getString("NoPermission")));
				return false;
			}
			baseCommands(player, 0); //Base and Info Command
			return true;
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
									plugin.getLogger().info("ArgumentModule from ArgumentConstructor %ac% not found! ERROR!"
											.replace("%ac%", ac.getName()));
									player.spigot().sendMessage(ChatApiS.tl(
											"ArgumentModule from ArgumentConstructor %ac% not found! ERROR!"
											.replace("%ac%", ac.getName())));
									return false;
								}
								return false;
							} else
							{
								player.spigot().sendMessage(ChatApiS.tl(plugin.getYamlHandler().getLang().getString("NoPermission")));
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
								plugin.getLogger().info("ArgumentModule from ArgumentConstructor %ac% not found! ERROR!"
										.replace("%ac%", ac.getName()));
								sender.spigot().sendMessage(ChatApiS.tl(
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
		sender.spigot().sendMessage(ChatApiB.clickEvent(plugin.getYamlHandler().getLang().getString("InputIsWrong"),
				ClickEvent.Action.RUN_COMMAND, CommandSuggest.getCmdString(CommandSuggest.Type.BASE)));
		return false;
	}
	
	public void baseCommands(final Player player, int page)
	{
		int index = 0;
		int count = 0;
		int start = page*10;
		int quantity = 9;
		int control = start;
		ArrayList<String> msg = new ArrayList<>();
		msg.add(plugin.getYamlHandler().getLang().getString("Headline"));
		for(BaseConstructor bc : plugin.getHelpList().stream().filter(x -> player.hasPermission(x.getPermission())).collect(Collectors.toList()))
		{
			if(index >= start && count <= quantity)
			{
				msg.add(sendInfo(bc));
				count++;
				control++;
			}
			index++;
		}
		String s = pastNextPage(player, page, control, plugin.getHelpList().size(), CommandSuggest.getCmdString(CommandSuggest.Type.BASE));
		if(s != null)
		{
			msg.add(s);
		}
		msg.stream().forEach(x -> player.spigot().sendMessage(ChatApiS.tl(x)));
	}
	
	private String sendInfo(BaseConstructor bc)
	{
		return (ChatApiV.clickHover(
				bc.getHelpInfo(),
				"SUGGEST_COMMAND", bc.getSuggestion(),
				"SHOW_TEXT", plugin.getYamlHandler().getLang().getString("GeneralHover")));
	}
	
	public String pastNextPage(Player player,
			int page, int control, int total, String cmdstring, String...objects)
	{
		if(page == 0 && control >= total)
		{
			return null;
		}
		int i = page+1;
		int j = page-1;
		StringBuilder sb = new StringBuilder();
		if(page != 0)
		{
			String msg2 = plugin.getYamlHandler().getLang().getString("Past");
			String cmd = cmdstring+String.valueOf(j);
			for(String o : objects)
			{
				cmd += " "+o;
			}
			sb.append(ChatApiV.click(msg2, "RUN_COMMAND", cmd));
		}
		if(control < total)
		{
			String msg1 = plugin.getYamlHandler().getLang().getString("Next");
			String cmd = cmdstring+String.valueOf(i);
			for(String o : objects)
			{
				cmd += " "+o;
			}
			if(sb.length() > 0)
			{
				sb.append(" | ");
			}
			sb.append(ChatApiV.click(msg1, "RUN_COMMAND", cmd));
		}
		return sb.toString();
	}
}