package main.java.me.avankziar.mim.spigot.cmd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.mim.general.ChatApi;
import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.assistance.MatchApi;
import main.java.me.avankziar.mim.spigot.cmdtree.ArgumentConstructor;
import main.java.me.avankziar.mim.spigot.cmdtree.ArgumentModule;
import main.java.me.avankziar.mim.spigot.cmdtree.BaseConstructor;
import main.java.me.avankziar.mim.spigot.cmdtree.CommandConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class MiMCmdExecutor implements CommandExecutor
{
	private MIM plugin;
	private static CommandConstructor cc;
	
	public MiMCmdExecutor(MIM plugin, CommandConstructor cc)
	{
		this.plugin = plugin;
		MiMCmdExecutor.cc = cc;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) 
	{
		if (!(sender instanceof Player)) 
		{
			MIM.log.info("/%cmd% is only for Player!".replace("%cmd%", cc.getName()));
			return false;
		}
		Player player = (Player) sender;
		if(cc == null)
		{
			return false;
		}
		if (args.length == 1) 
		{
			if(MatchApi.isInteger(args[0]))
			{
				if(!player.hasPermission(cc.getPermission()))
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("NoPermission")));
					return false;
				}
				baseCommands(player, Integer.parseInt(args[0]));
				return true;
			}
		} else if(args.length == 0)
		{
			if(!player.hasPermission(cc.getPermission()))
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("NoPermission")));
				return false;
			}
			baseCommands(player, 0);
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
						aclist = ac.subargument;
						break;
					}
				}
			}
		}
		player.spigot().sendMessage(ChatApi.clickEvent(plugin.getYamlHandler().getLang().getString("InputIsWrong"),
				ClickEvent.Action.RUN_COMMAND, MIM.infoCommand));
		return false;
	}
	
	public void baseCommands(final Player player, int page)
	{
		int count = 0;
		int start = page*10;
		int end = page*10+9;
		int last = 0;
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString(
				"Headline")));
		for(BaseConstructor bc : plugin.getCommandHelpList())
		{
			if(count >= start && count <= end)
			{
				if(player.hasPermission(bc.getPermission()))
				{
					sendInfo(player, bc);
				}
			}
			count++;
			last++;
		}
		boolean lastpage = false;
		if(end >= last)
		{
			lastpage = true;
		}
		pastNextPage(player, page, lastpage, MIM.infoCommand);
	}
	
	private void sendInfo(Player player, BaseConstructor bc)
	{
		player.spigot().sendMessage(ChatApi.apiChat(
				bc.getHelpInfo(),
				ClickEvent.Action.SUGGEST_COMMAND, bc.getSuggestion(),
				HoverEvent.Action.SHOW_TEXT,plugin.getYamlHandler().getLang().getString("GeneralHover")));
	}
	
	public void pastNextPage(Player player,
			int page, boolean lastpage, String cmdstring, String...objects)
	{
		if(page==0 && lastpage)
		{
			return;
		}
		int i = page+1;
		int j = page-1;
		TextComponent MSG = ChatApi.tctl("");
		List<BaseComponent> pages = new ArrayList<BaseComponent>();
		if(page!=0)
		{
			TextComponent msg2 = ChatApi.tctl(
					plugin.getYamlHandler().getLang().getString("Past"));
			String cmd = cmdstring+" "+String.valueOf(j);
			for(String o : objects)
			{
				cmd += " "+o;
			}
			msg2.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));
			pages.add(msg2);
		}
		if(!lastpage)
		{
			TextComponent msg1 = ChatApi.tctl(
					plugin.getYamlHandler().getLang().getString("Next"));
			String cmd = cmdstring+" "+String.valueOf(i);
			for(String o : objects)
			{
				cmd += " "+o;
			}
			msg1.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));
			if(pages.size()==1)
			{
				pages.add(ChatApi.tc(" | "));
			}
			pages.add(msg1);
		}
		MSG.setExtra(pages);	
		player.spigot().sendMessage(MSG);
	}
}