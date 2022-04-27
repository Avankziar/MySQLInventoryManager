package main.java.me.avankziar.mim.spigot.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.objects.SyncTask;
import main.java.me.avankziar.mim.spigot.objects.SyncTask.RunType;
import main.java.me.avankziar.mim.spigot.objects.SyncType;

public class PlayerDeathListener extends BaseListener
{
	public PlayerDeathListener(MIM plugin)
	{
		super(plugin);
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		final Player player = event.getEntity();
		new SyncTask(plugin, SyncType.FULL, RunType.SAVEDEATHSTATE, player).run();
	}
}