package main.java.me.avankziar.mim.spigot.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLevelChangeEvent;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.objects.SyncTask;
import main.java.me.avankziar.mim.spigot.objects.SyncTask.RunType;
import main.java.me.avankziar.mim.spigot.objects.SyncType;

public class PlayerLevelChangeListener extends BaseListener
{
	public PlayerLevelChangeListener(MIM plugin)
	{
		super(plugin);
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onPlayerLevelChange(PlayerLevelChangeEvent event)
	{
		if(!plugin.getConfigHandler().isEventEnabled("PlayerLevelChange", event.getPlayer().getWorld()))
		{
			return;
		}
		Player player = event.getPlayer();
		if(!preChecks(player))
		{
			return;
		}
		addCooldown(player.getUniqueId());
		new SyncTask(plugin, SyncType.EXP, RunType.SAVE, player).run();
		removeCooldown(player.getUniqueId());
	}
}