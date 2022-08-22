package main.java.me.avankziar.mim.spigot.ifh.provider;

import org.bukkit.World;
import org.bukkit.entity.Player;

import main.java.me.avankziar.ifh.spigot.synchronization.Synchronization;
import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.handler.ConfigHandler;

public class SynchronizationProvider implements Synchronization
{
	private MIM plugin;
	
	public SynchronizationProvider(MIM plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public String getSynchroKey()
	{
		Player p = null;
		return new ConfigHandler(plugin).getSynchroKey(p);
	}
	
	@Override
	public String getSynchroKey(World world)
	{
		return new ConfigHandler(plugin).getSynchroKey(world);
	}
}