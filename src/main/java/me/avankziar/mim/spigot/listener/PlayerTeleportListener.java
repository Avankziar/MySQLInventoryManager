package main.java.me.avankziar.mim.spigot.listener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerTeleportEvent;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.objects.SyncTask.RunType;
import main.java.me.avankziar.mim.spigot.objects.SyncType;

public class PlayerTeleportListener extends BaseListener
{
	public PlayerTeleportListener(MIM plugin)
	{
		super(plugin, BaseListener.Type.PLAYER_TELEPORT);
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onPlayerTeleport(PlayerTeleportEvent event)
	{
		if(event.isCancelled())
		{
			return;
		}
		if(event.getFrom().getWorld().getName().equals(event.getTo().getWorld().getName()))
		{
			return;
		}
		if(!plugin.getConfigHandler().isEventEnabled(this.bType.getName(), event.getFrom().getWorld()))
		{
			return;
		}
		Player player = event.getPlayer();
		doSync(player, SyncType.FULL, RunType.SAVE);
	}
}