package main.java.me.avankziar.mim.spigot.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemBreakEvent;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.objects.SyncTask;
import main.java.me.avankziar.mim.spigot.objects.SyncTask.RunType;
import main.java.me.avankziar.mim.spigot.objects.SyncType;

public class PlayerItemBreakListener extends BaseListener
{
	public PlayerItemBreakListener(MIM plugin)
	{
		super(plugin, BaseListener.Type.PLAYER_ITEMBREAK);
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPlayerItemBreak(PlayerItemBreakEvent event)
	{
		if(!plugin.getConfigHandler().isEventEnabled(this.bType.getName(), event.getPlayer().getWorld()))
		{
			return;
		}
		Player player = event.getPlayer();
		addCooldown(player.getUniqueId());
		new SyncTask(plugin, SyncType.INVENTORY, RunType.SAVE, player).run();
		removeCooldown(player.getUniqueId());	
	}
}