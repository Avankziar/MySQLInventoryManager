package main.java.me.avankziar.mim.spigot.objects;

import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.handler.ClearAndResetHandler;
import main.java.me.avankziar.mim.spigot.handler.ConfigHandler;
import main.java.me.avankziar.mim.spigot.handler.DeathMemoryStateHandler;
import main.java.me.avankziar.mim.spigot.handler.PlayerDataHandler;
import main.java.me.avankziar.mim.spigot.handler.PredefinePlayerStateHandler;

public class SyncTask extends BukkitRunnable
{
	public enum RunType
	{
		CLEAR_AND_RESET,
		LOAD, SAVE, SAVEANDKICK,
		LOAD_DEATHSTATE, SAVE_DEATHSTATE,
		LOAD_PREDEFINESTATE, SAVE_PREDEFINESTATE;
	}
	
	private MIM plugin;
	private SyncType syncType;
	private RunType runType;
	private Player player;
	private GameMode targetMode;
	
	private int deathMemoryState;
	
	private String synchroKey;
	private String statename;
	
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
		this.runType = RunType.LOAD_DEATHSTATE;
		this.player = player;
		this.targetMode = targetMode != null ? targetMode : player.getGameMode();
	}
	
	public SyncTask(MIM plugin, SyncType syncType, RunType runType, final Player player, String statename, @Nullable String sychroKey)
	{
		this.plugin = plugin;
		this.syncType = syncType;
		this.runType = runType;
		this.player = player;
		this.statename = statename;
		this.targetMode = targetMode != null ? targetMode : player.getGameMode();
	}

	@Override
	public void run()
	{
		final UUID uuid = player.getUniqueId();
		plugin.playerSyncComplete.remove(uuid);
		plugin.playerInSync.add(uuid);
		if(runType == RunType.SAVE)
		{
			PlayerDataHandler.save(syncType, player);
		} else if(runType == RunType.SAVEANDKICK)
		{
			PlayerDataHandler.save(syncType, player);
			player.kickPlayer(plugin.getYamlHandler().getLang().getString("SyncTask.SavedAndKicked"));
		} else if(runType == RunType.LOAD)
		{
			if(!new ConfigHandler(plugin).inSleepMode())
			{
				PlayerDataHandler.load(syncType, player, targetMode);
			}
		} else if(runType == RunType.SAVE_DEATHSTATE)
		{
			DeathMemoryStateHandler.save(syncType, player);
		} else if(runType == RunType.LOAD_DEATHSTATE)
		{
			if(!new ConfigHandler(plugin).inSleepMode())
			{
				DeathMemoryStateHandler.load(syncType, player, deathMemoryState);
			}
		} else if(runType == RunType.SAVE_PREDEFINESTATE)
		{
			PredefinePlayerStateHandler.save(syncType, player, statename, synchroKey);
		} else if(runType == RunType.LOAD_PREDEFINESTATE)
		{
			if(!new ConfigHandler(plugin).inSleepMode())
			{
				PredefinePlayerStateHandler.load(syncType, player, statename);
			}
		} else if(runType == RunType.CLEAR_AND_RESET)
		{
			ClearAndResetHandler.clearAndReset(syncType, player); //DO ONLY If the player DONT quit the server!
		}
		plugin.playerInSync.remove(uuid);
		plugin.playerSyncComplete.add(uuid);
	}
}