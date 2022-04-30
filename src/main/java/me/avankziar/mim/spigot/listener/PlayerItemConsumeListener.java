package main.java.me.avankziar.mim.spigot.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.objects.SyncTask;
import main.java.me.avankziar.mim.spigot.objects.SyncTask.RunType;
import main.java.me.avankziar.mim.spigot.objects.SyncType;

public class PlayerItemConsumeListener extends BaseListener
{
	public PlayerItemConsumeListener(MIM plugin)
	{
		super(plugin, BaseListener.Type.PLAYER_ITEMCONSUME);
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPlayerItemConsume(PlayerItemConsumeEvent event)
	{
		if(event.isCancelled())
		{
			return;
		}
		if(!plugin.getConfigHandler().isEventEnabled(this.bType.getName(), event.getPlayer().getWorld()))
		{
			return;
		}
		Player player = event.getPlayer();
		addCooldown(player.getUniqueId());
		new SyncTask(plugin, SyncType.FULL, RunType.SAVE, player).run();
		removeCooldown(player.getUniqueId());	
	}
}