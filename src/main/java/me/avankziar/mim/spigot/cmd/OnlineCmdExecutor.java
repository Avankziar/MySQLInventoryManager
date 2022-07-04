package main.java.me.avankziar.mim.spigot.cmd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.mim.general.ChatApi;
import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.assistance.Utility;
import main.java.me.avankziar.mim.spigot.cmdtree.CommandConstructor;
import main.java.me.avankziar.mim.spigot.listener.OnlineListener;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;

public class OnlineCmdExecutor implements CommandExecutor
{
	private MIM plugin;
	private static CommandConstructor cc;
	
	public OnlineCmdExecutor(MIM plugin, CommandConstructor cc)
	{
		this.plugin = plugin;
		OnlineCmdExecutor.cc = cc;
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
		if(args.length >= 1)
		{
			sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("DidYouMean")));
			sender.sendMessage(ChatApi.tl(cc.getSuggestion()));
			return false;
		}
		OnlineListener.sendRequest(player);
		return true;
	}
	
	public static void onlineSimple(Player player, String ownServer, LinkedHashMap<String, ArrayList<String>> servermap)
	{
		int online = 0;
		ArrayList<String> lines = new ArrayList<>();
		String ss = MIM.getPlugin().getYamlHandler().getLang().getString("Online.SameServer");
		String so = MIM.getPlugin().getYamlHandler().getLang().getString("Online.OtherServer");
		LinkedHashMap<String, ArrayList<String>> map = new LinkedHashMap<>();//server, groupname, uuids
		//Vorsortierung
		for(Entry<String, ArrayList<String>> entry : servermap.entrySet())
		{
			String server = entry.getKey();
			ArrayList<String> list = entry.getValue();
			for(String uuid : list)
			{
				if(MIM.getPlugin().getVanish() != null)
				{
					if(MIM.getPlugin().getVanish().isInvisibleOffline(UUID.fromString(uuid)))
					{
						continue;
					}
				}
				online++;
				ArrayList<String> uuids = null;
				if(map.containsKey(server))
				{
					uuids = map.get(server);
				} else
				{
					uuids = new ArrayList<>();
				}
				uuids.add(uuid);
				map.put(server, uuids);
			}
		}
		lines.add(MIM.getPlugin().getYamlHandler().getLang().getString("Online.FirstLine").replace("%amount%", String.valueOf(online)));
		lines.add(MIM.getPlugin().getYamlHandler().getLang().getString("Online.SecondLine"));
		for(Entry<String, ArrayList<String>> entry : map.entrySet())
		{
			String server = entry.getKey();
			ArrayList<String> uuids = entry.getValue();
			String bl = server.equals(ownServer) ? ss : so;
			lines.add(bl+server+":");
			String g = "";
			for(String uuid : uuids)
			{
				String n = Utility.convertUUIDToName(uuid);
				if(n != null)
				{
					g += "&6"+n+", ";
				}
			}
			lines.add(g);
		}
		for(String s : lines)
		{
			player.sendMessage(ChatApi.tl(s));
		}
	}
	
	public static void onlineExpert(Player player, String ownServer, LinkedHashMap<String, ArrayList<String>> servermap)
	{
		int online = 0;
		ArrayList<String> lines = new ArrayList<>();
		String ss = MIM.getPlugin().getYamlHandler().getLang().getString("Online.SameServer");
		String so = MIM.getPlugin().getYamlHandler().getLang().getString("Online.OtherServer");
		LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> map = new LinkedHashMap<>();//server, groupname, uuids
		//Vorsortierung
		for(Entry<String, ArrayList<String>> entry : servermap.entrySet())
		{
			String server = entry.getKey();
			ArrayList<String> list = entry.getValue();
			for(String uuid : list)
			{
				online++;
				User user = MIM.getPlugin().getLP().getUserManager().getUser(UUID.fromString(uuid));
				Collection<Group> inheritedGroups = user.getInheritedGroups(user.getQueryOptions());
				Group hgr = null;
				for(Group gr : inheritedGroups)
				{
					gr.getWeight();
					if(hgr == null)
					{
						hgr = gr;
					} else
					{
						if(hgr.getWeight().orElse(0) < gr.getWeight().orElse(0))
						{
							hgr = gr;
						}
					}
				}
				LinkedHashMap<String, ArrayList<String>> grouping = null;
				if(map.containsKey(server))
				{
					grouping = map.get(server);
				} else
				{
					grouping = new LinkedHashMap<>();
				}
				if(grouping.containsKey(hgr.getName()))
				{
					ArrayList<String> uuids = grouping.get(hgr.getName());
					uuids.add(uuid);
					grouping.put(hgr.getName(), uuids);
				} else
				{
					ArrayList<String> uuids = new ArrayList<>();
					uuids.add(uuid);
					grouping.put(hgr.getName(), uuids);
				}
				map.put(server, grouping);
			}
		}
		lines.add(MIM.getPlugin().getYamlHandler().getLang().getString("Online.FirstLine").replace("%amount%", String.valueOf(online)));
		lines.add(MIM.getPlugin().getYamlHandler().getLang().getString("Online.SecondLine"));
		for(Entry<String, LinkedHashMap<String, ArrayList<String>>> entry : map.entrySet())
		{
			String server = entry.getKey();
			LinkedHashMap<String, ArrayList<String>> grouping = entry.getValue();
			String bl = server.equals(ownServer) ? ss : so;
			lines.add(bl+server+":");
			for(Entry<String, ArrayList<String>> entrz : grouping.entrySet())
			{
				String group = entrz.getKey();
				ArrayList<String> uuids = entrz.getValue();
				String g = "&r"+group+": ";
				for(String uuid : uuids)
				{
					String n = Utility.convertUUIDToName(uuid);
					if(n != null)
					{
						g += "&6"+n+", ";
					}
				}
				lines.add(g);
			}
		}
		for(String s : lines)
		{
			player.sendMessage(ChatApi.tl(s));
		}
	}
}