package main.java.me.avankziar.mim.spigot.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.handler.ClearAndResetHandler;
import main.java.me.avankziar.mim.spigot.handler.PlayerDataHandler;
import main.java.me.avankziar.mim.spigot.objects.SyncType;

public class PlayerQuitListener extends BaseListener
{
	public PlayerQuitListener(MIM plugin)
	{
		super(plugin, BaseListener.Type.PLAYER_QUIT);
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onPlayerQuit(final PlayerQuitEvent event)
	{
		if(!plugin.getConfigHandler().isEventEnabled(this.bType.getName(), event.getPlayer().getWorld()))
		{
			return;
		}
		final Player player = event.getPlayer();
		if(plugin.getConfigHandler().isClearAndResetByQuit(player.getWorld()))
		{
			ClearAndResetHandler.clearAndReset(SyncType.FULL, player);
			return;
		}
		//Ausnahme
		PlayerDataHandler.save(SyncType.FULL, player);
	}
}