package main.java.me.avankziar.mim.spigot.objects;

import javax.annotation.Nullable;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.handler.ConfigHandler;
import main.java.me.avankziar.mim.spigot.handler.DeathMemoryStateHandler;
import main.java.me.avankziar.mim.spigot.handler.PlayerDataHandler;

public class SyncTask extends BukkitRunnable
{
	public enum RunType
	{
		LOAD, SAVE, 
		LOADDEATHSTATE, SAVEDEATHSTATE,
		LOADPREDEFINESTATE, SAVEPREDEFINESTATE;
	}
	
	private MIM plugin;
	private SyncType syncType;
	private RunType runType;
	private Player player;
	private GameMode targetMode;
	private int deathMemoryState;
	
	public SyncTask(MIM plugin, SyncType syncType, RunType runType, final Player player)
	{
		this.plugin = plugin;
		this.syncType = syncType;
		this.runType = runType;
		this.player = player;
		this.targetMode = player.getGameMode();
	}
	
	public SyncTask(MIM plugin, SyncType syncType, RunType runType, final Player player, @Nullable GameMode targetMode)
	{
		this.plugin = plugin;
		this.syncType = syncType;
		this.runType = runType;
		this.player = player;
		this.targetMode = targetMode != null ? targetMode : player.getGameMode();
	}
	
	public SyncTask(MIM plugin, SyncType syncType, int deathMemoryState, final Player player)
	{
		this.plugin = plugin;
		this.syncType = syncType;
		this.runType = RunType.LOADDEATHSTATE;
		this.player = player;
		this.targetMode = targetMode != null ? targetMode : player.getGameMode();
	}

	@Override
	public void run()
	{
		plugin.playerSyncComplete.remove(player.getUniqueId());
		plugin.playerInSync.add(player.getUniqueId());
		if(runType == RunType.SAVE)
		{
			PlayerDataHandler.save(syncType, player);
		} else if(runType == RunType.LOAD)
		{
			if(!new ConfigHandler(plugin).inSleepMode())
			{
				PlayerDataHandler.load(syncType, player, targetMode);
			}
		} else if(runType == RunType.SAVEDEATHSTATE)
		{
			DeathMemoryStateHandler.save(syncType, player);
		} else if(runType == RunType.LOADDEATHSTATE)
		{
			DeathMemoryStateHandler.load(syncType, player, deathMemoryState);
		} else if(runType == RunType.SAVEPREDEFINESTATE)
		{
			
		} else if(runType == RunType.LOADPREDEFINESTATE)
		{
			
		}
		plugin.playerInSync.remove(player.getUniqueId());
		plugin.playerSyncComplete.add(player.getUniqueId());
	}
}