package main.java.me.avankziar.mim.spigot.handler;

import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.listener.BaseListener;

public class ConfigHandler
{
	private MIM plugin;
	
	public ConfigHandler(MIM plugin)
	{
		this.plugin = plugin;
	}
	
	public YamlConfiguration getSyncYaml(World world, boolean console)
	{
		YamlConfiguration w = plugin.getYamlHandler().getSynServer();
		if(console)
		{
			if(w.getBoolean("ServerOverWorldSettings"))
			{
				w = plugin.getYamlHandler().getSynServer();
				if(w == null)
				{
					return null;
				} 
			}
		} else
		{
			w = plugin.getYamlHandler().getSyncWorld(world);
			if(w == null)
			{
				w = plugin.getYamlHandler().getSynServer();
				if(w == null)
				{
					return null;
				}
			} else
			{
				if(w.getBoolean("ServerOverWorldSettings"))
				{
					w = plugin.getYamlHandler().getSynServer();
					if(w == null)
					{
						return null;
					} 
				}
			}
		}
		return w;
	}
	
	public String getSynchroKey(Player player, boolean console)
	{
		YamlConfiguration w = getSyncYaml(player == null ? null : player.getWorld(), console);
		if(w == null)
		{
			return "default";
		}
		return w.getString("Synchrokey", "default");
	}
	
	public boolean getDefaultClearToggle()
	{
		return plugin.getYamlHandler().getConfig().getBoolean("Default.ClearToggle");
	}
	
	public boolean isEventEnabled(String event, World world)
	{
		YamlConfiguration w = getSyncYaml(world, false);
		if(w == null)
		{
			return false;
		}
		return w.getBoolean("SyncEvents."+event, false);
	}
	
	public boolean inSleepMode()
	{
		return plugin.getYamlHandler().getConfig().getBoolean("SleepMode", false);
	}
	
	public boolean loadPredefineOnFirstJoin(World world)
	{
		YamlConfiguration w = getSyncYaml(world, false);
		if(w == null)
		{
			return false;
		}
		return w.getBoolean("Load.OnFirstJoin.PredefineState", false);
	}
	
	public String getPredefineStatenameOnFristJoin(World world)
	{
		YamlConfiguration w = getSyncYaml(world, false);
		if(w == null)
		{
			return "default";
		}
		return w.getString("Load.OnFirstJoin.PredefineStatename", "default");
	}
	
	public boolean loadPredefineAlways(World world)
	{
		YamlConfiguration w = getSyncYaml(world, false);
		if(w == null)
		{
			return false;
		}
		return w.getBoolean("Load.Always.PredefineState", false);
	}
	
	public String getPredefineStatenameAlways(World world)
	{
		YamlConfiguration w = getSyncYaml(world, false);
		if(w == null)
		{
			return null;
		}
		return w.getString("Load.Always.PredefineStatename", "default");
	}
	
	public boolean isClearAndResetByQuit(World world)
	{
		YamlConfiguration w = getSyncYaml(world, false);
		if(w == null)
		{
			return false;
		}
		return w.getBoolean("ClearAndReset.OnLeaveOrQuit", false);
	}
	
	public int getMaximalAmountDeathMemoryStatePerPlayer(World world)
	{
		YamlConfiguration w = getSyncYaml(world, false);
		if(w == null)
		{
			return 0;
		}
		return w.getInt("MaximalDeathMemoryStatePerPlayerPerGameModePerSynchrokey", 0);
	}
	
	public int getTimeDelayInSecsRemoveCooldown(World world, BaseListener.Type btype)
	{
		YamlConfiguration w = getSyncYaml(world, false);
		if(w == null)
		{
			return 5;
		}
		return w.getInt("SyncEvents."+btype.getName()+".TimeDelayInSecs.RemoveCooldown", 5);
	}
}
