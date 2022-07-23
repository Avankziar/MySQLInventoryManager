package main.java.me.avankziar.mim.spigot.cmd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import main.java.me.avankziar.mim.spigot.MIM;


public class TabCompletionOne implements TabCompleter
{	
	private MIM plugin;
	private static ArrayList<String> attributes = new ArrayList<String>();
	
	
	public TabCompletionOne(MIM plugin)
	{
		this.plugin = plugin;
		setAttributes();
	}
	
	public static void setAttributes()
	{
		attributes = new ArrayList<>();
		attributes.add("foodlevel");
		attributes.add("saturation");
		attributes.add("saturatedregenrate");
		attributes.add("unsaturatedregenrate");
		attributes.add("starvationrate");
		attributes.add("exhaustion");
		attributes.add("generic_armor");
		attributes.add("generic_armor_toughness");
		attributes.add("generic_attack_damage");
		attributes.add("generic_attack_knockback");
		attributes.add("generic_attack_speed");
		attributes.add("generic_knockback_resistance");
		attributes.add("generic_luck");
		attributes.add("generic_max_health");
		attributes.add("health");
		attributes.add("absorptionamount");
		attributes.add("exptowardsnextlevel");
		attributes.add("explevel");
		attributes.add("totalexperience");
		attributes.add("walkspeed");
		attributes.add("flyspeed");
		attributes.add("glowing");
		attributes.add("gravity");
		attributes.add("invisible");
		attributes.add("invulnerable");
		attributes.add("entitycategory");
		attributes.add("maximumair");
		Collections.sort(attributes);
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String lable, String[] args) 
	{
		List<String> list = new ArrayList<String>();
		String command = lable;
		if (command.equalsIgnoreCase(plugin.getCommandFromPath("attributes").getName()))
		{
			if(args.length == 1)
			{
				if (!args[0].equals("")) 
				{
					for (String name : plugin.getMysqlPlayers()) 
					{
						if (name.startsWith(args[0])
								|| name.toLowerCase().startsWith(args[0])
								|| name.toUpperCase().startsWith(args[0])) 
						{
							list.add(name);
						}
					}
					Collections.sort(list);
					return list;
				} else
				{
					if(plugin.getMysqlPlayers() != null)
					{
						list.addAll(plugin.getMysqlPlayers());
						Collections.sort(list);
					}
					return list;
				}
			}
		}
		return list;
	}
}
