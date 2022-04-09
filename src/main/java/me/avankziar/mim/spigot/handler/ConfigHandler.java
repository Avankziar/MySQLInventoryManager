package main.java.me.avankziar.mim.spigot.handler;

import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import main.java.me.avankziar.mim.spigot.MIM;

public class ConfigHandler
{
	private MIM plugin;
	
	public ConfigHandler(MIM plugin)
	{
		this.plugin = plugin;
	}
	
	//FIXME
	public String getSynchroKey(Player player)
	{
		return null;
	}
	
	//FIXME
	public boolean getDefaultClearToggle()
	{
		return true;
	}
	
	public boolean isEventEnabled(String event, World world)
	{
		YamlConfiguration w = plugin.getYamlHandler().getSyncWorld(world);
		if(w == null)
		{
			w = plugin.getYamlHandler().getSynServer();
			if(w == null)
			{
				return false;
			}
		} else
		{
			if(w.getBoolean("ServerOverWorldSettings"))
			{
				w = plugin.getYamlHandler().getSynServer();
				if(w == null)
				{
					return false;
				} 
			}
		}
		return w.getBoolean("SyncEvents."+event, false);
	}
}
