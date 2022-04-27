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
	
	public String getSynchroKey(Player player)
	{
		YamlConfiguration w = plugin.getYamlHandler().getSyncWorld(player.getWorld());
		if(w == null)
		{
			w = plugin.getYamlHandler().getSynServer();
			if(w == null)
			{
				return "default";
			}
		} else
		{
			if(w.getBoolean("ServerOverWorldSettings"))
			{
				w = plugin.getYamlHandler().getSynServer();
				if(w == null)
				{
					return "default";
				} 
			}
		}
		return w.getString("Synchrokey", "default");
	}
	
	public boolean getDefaultClearToggle()
	{
		return plugin.getYamlHandler().getConfig().getBoolean("Default.ClearToggle");
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
	
	public boolean inSleepMode()
	{
		return plugin.getYamlHandler().getConfig().getBoolean("SleepMode", false);
	}
	
	public int getMaximalAmountDeathMemoryStatePerPlayer(World world)
	{
		YamlConfiguration w = plugin.getYamlHandler().getSyncWorld(world);
		if(w == null)
		{
			w = plugin.getYamlHandler().getSynServer();
			if(w == null)
			{
				return 0;
			}
		} else
		{
			if(w.getBoolean("ServerOverWorldSettings", true))
			{
				w = plugin.getYamlHandler().getSynServer();
				if(w == null)
				{
					return 0;
				} 
			}
		}
		return w.getInt("MaximalDeathMemoryStatePerPlayerPerGameModePerSynchrokey", 0);
	}
}
