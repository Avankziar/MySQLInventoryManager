package main.java.me.avankziar.mim.spigot.handler;

import org.bukkit.GameMode;
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
		if(world == null)
		{
			YamlConfiguration s = plugin.getYamlHandler().getSyncServer();
			return s;
		}
		YamlConfiguration w = plugin.getYamlHandler().getSyncWorld(world);
		if(console || w == null || w.getBoolean("ServerOverWorldSettings", true))
		{
			YamlConfiguration s = plugin.getYamlHandler().getSyncServer();
			return s;
		}
		return w;
	}
	
	public YamlConfiguration getSyncYaml(World world)
	{
		return getSyncYaml(world, false);
	}
	
	public String getSynchroKey(Player player)
	{
		YamlConfiguration w = getSyncYaml(player == null ? null : player.getWorld());
		if(w == null)
		{
			return "default";
		}
		return w.getString("Synchrokey", "default");
	}
	
	public String getSynchroKey(World world)
	{
		YamlConfiguration w = getSyncYaml(world);
		if(w == null)
		{
			return "default";
		}
		return w.getString("Synchrokey", "default");
	}
	
	public Boolean isOverrideGameMode(World world, boolean console)
	{
		YamlConfiguration w = getSyncYaml(world, console);
		if(w == null)
		{
			return false;
		}
		return w.getBoolean("Load.OverrideGameMode.Active", false);
	}
	
	public GameMode getOverrideGameMode(World world, boolean console)
	{
		YamlConfiguration w = getSyncYaml(world, console);
		if(w == null)
		{
			return GameMode.SURVIVAL;
		}
		return GameMode.valueOf(w.getString("Load.OverrideGameMode.Mode", "SURVIVAL"));
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
		return w.getBoolean("SyncEvents."+event+".Enabled", false);
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
		return w.getInt("MaximalDeathMemoryStatePerPlayerPerSynchrokey", 0);
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
