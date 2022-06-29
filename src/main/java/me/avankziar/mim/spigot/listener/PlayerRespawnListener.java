package main.java.me.avankziar.mim.spigot.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerRespawnEvent;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.objects.SyncTask.RunType;
import main.java.me.avankziar.mim.spigot.objects.SyncType;

public class PlayerRespawnListener extends BaseListener
{
	public PlayerRespawnListener(MIM plugin)
	{
		super(plugin, BaseListener.Type.PLAYER_RESPAWN);
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		if(!plugin.getConfigHandler().isEventEnabled(this.bType.getName(), event.getRespawnLocation().getWorld()))
		{
			return;
		}
		Player player = event.getPlayer();
		doSync(player, SyncType.FULL, RunType.SAVE);
	}
}