package main.java.me.avankziar.mim.spigot.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.objects.SyncTask;
import main.java.me.avankziar.mim.spigot.objects.SyncTask.RunType;
import main.java.me.avankziar.mim.spigot.objects.SyncType;

public class PlayerChangedWorldListener extends BaseListener
{
	public PlayerChangedWorldListener(MIM plugin)
	{
		super(plugin, BaseListener.Type.PLAYER_CHANGEDWORLD);
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onPlayerChangeWorld(PlayerChangedWorldEvent event)
	{
		if(!plugin.getConfigHandler().isEventEnabled(this.bType.getName(), event.getPlayer().getWorld()))
		{
			return;
		}
		Player player = event.getPlayer();
		addCooldown(player.getUniqueId());
		new SyncTask(plugin, SyncType.FULL, RunType.LOAD, player).run();
		removeCooldown(player.getUniqueId());
	}
}