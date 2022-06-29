package main.java.me.avankziar.mim.spigot.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemDamageEvent;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.objects.SyncTask.RunType;
import main.java.me.avankziar.mim.spigot.objects.SyncType;

public class PlayerItemDamageListener extends BaseListener
{
	public PlayerItemDamageListener(MIM plugin)
	{
		super(plugin, BaseListener.Type.PLAYER_ITEMDAMGE);
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPlayerItemDamage(PlayerItemDamageEvent event)
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
		doSync(player, SyncType.INVENTORY, RunType.SAVE);
	}
}