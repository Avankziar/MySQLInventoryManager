package main.java.me.avankziar.mim.spigot.objects;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.handler.PlayerDataHandler;

public class SyncTask extends BukkitRunnable
{
	public enum RunType
	{
		LOAD, SAVE;
	}
	
	private MIM plugin;
	private SyncType syncType;
	private RunType runType;
	private Player player;
	
	public SyncTask(MIM plugin, SyncType syncType, RunType runType, final Player player)
	{
		this.plugin = plugin;
		this.syncType = syncType;
		this.runType = runType;
		this.player = player;
	}

	@Override
	public void run()
	{
		plugin.playerSyncComplete.remove(player.getUniqueId());
		plugin.playerInSync.add(player.getUniqueId());
		if(runType == RunType.SAVE)
		{
			PlayerDataHandler.save(syncType, player);
		} else
		{
			PlayerDataHandler.load(syncType, player);
		}
		plugin.playerInSync.remove(player.getUniqueId());
		plugin.playerSyncComplete.add(player.getUniqueId());
	}

}
