package main.java.me.avankziar.mim.spigot.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLevelChangeEvent;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.objects.SyncTask.RunType;
import main.java.me.avankziar.mim.spigot.objects.SyncType;

public class PlayerLevelChangeListener extends BaseListener
{
	public PlayerLevelChangeListener(MIM plugin)
	{
		super(plugin, BaseListener.Type.PLAYER_LEVELCHANGE);
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onPlayerLevelChange(PlayerLevelChangeEvent event)
	{
		if(!plugin.getConfigHandler().isEventEnabled(this.bType.getName(), event.getPlayer().getWorld()))
		{
			return;
		}
		Player player = event.getPlayer();
		doSync(player, SyncType.EXP, RunType.SAVE);
	}
}