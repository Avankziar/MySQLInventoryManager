package main.java.me.avankziar.mim.spigot.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerDropItemEvent;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.objects.SyncTask;
import main.java.me.avankziar.mim.spigot.objects.SyncTask.RunType;
import main.java.me.avankziar.mim.spigot.objects.SyncType;

public class PlayerDropItemListener extends BaseListener
{
	public PlayerDropItemListener(MIM plugin)
	{
		super(plugin);
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onPlayerLevelChange(PlayerDropItemEvent event)
	{
		if(!plugin.getConfigHandler().isEventEnabled("PlayerDropItem", event.getPlayer().getWorld()))
		{
			return;
		}
		Player player = event.getPlayer();
		if(!preChecks(player))
		{
			return;
		}
		addCooldown(player.getUniqueId());
		new SyncTask(plugin, SyncType.INVENTORY, RunType.SAVE, player).run();
		removeCooldown(player.getUniqueId());
	}
}