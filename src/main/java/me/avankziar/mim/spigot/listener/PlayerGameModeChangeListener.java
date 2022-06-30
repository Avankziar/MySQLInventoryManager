package main.java.me.avankziar.mim.spigot.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.objects.SyncTask;
import main.java.me.avankziar.mim.spigot.objects.SyncTask.RunType;
import main.java.me.avankziar.mim.spigot.objects.SyncType;

public class PlayerGameModeChangeListener extends BaseListener
{
	public PlayerGameModeChangeListener(MIM plugin)
	{
		super(plugin, BaseListener.Type.PLAYER_GAMEMODECHANGE);
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onGameModeChange(PlayerGameModeChangeEvent event)
	{
		if(event.isCancelled())
		{
			return;
		}
		if(!plugin.getConfigHandler().isEventEnabled(this.bType.getName(), event.getPlayer().getWorld()))
		{
			return;
		}
		//Dieses ist eine Ausnahme! Es macht NICHT doSync!
		Player player = event.getPlayer();
		addCooldown(player.getUniqueId());
		new SyncTask(plugin, SyncType.FULL, RunType.SAVE, player).run();
		new SyncTask(plugin, SyncType.FULL, RunType.LOAD, player, event.getNewGameMode()).run();
		removeCooldown(player.getUniqueId());
	}
}