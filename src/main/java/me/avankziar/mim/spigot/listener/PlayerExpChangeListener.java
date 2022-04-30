package main.java.me.avankziar.mim.spigot.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerExpChangeEvent;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.objects.SyncTask;
import main.java.me.avankziar.mim.spigot.objects.SyncTask.RunType;
import main.java.me.avankziar.mim.spigot.objects.SyncType;

public class PlayerExpChangeListener extends BaseListener
{
	public PlayerExpChangeListener(MIM plugin)
	{
		super(plugin, BaseListener.Type.PLAYER_EXPCHANGE);
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onPlayerExpChangeBlock(PlayerExpChangeEvent event)
	{
		if(!plugin.getConfigHandler().isEventEnabled(this.bType.getName(), event.getPlayer().getWorld()))
		{
			return;
		}
		Player player = event.getPlayer();
		addCooldown(player.getUniqueId());
		new SyncTask(plugin, SyncType.EXP, RunType.SAVE, player).run();
		removeCooldown(player.getUniqueId());	
	}
}