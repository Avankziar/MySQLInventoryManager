package main.java.me.avankziar.mim.spigot.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.handler.DeathMemoryStateHandler;
import main.java.me.avankziar.mim.spigot.objects.SyncType;

public class PlayerDeathListener extends BaseListener
{
	public PlayerDeathListener(MIM plugin)
	{
		super(plugin, BaseListener.Type.PLAYER_DEATH);
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		if(!plugin.getConfigHandler().isEventEnabled(this.bType.getName(), event.getEntity().getWorld()))
		{
			return;
		}
		final Player player = event.getEntity();
		//Ausnahme!
		DeathMemoryStateHandler.save(SyncType.FULL, player);
	}
}