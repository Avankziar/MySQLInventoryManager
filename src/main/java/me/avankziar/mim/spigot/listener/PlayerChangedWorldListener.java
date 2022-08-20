package main.java.me.avankziar.mim.spigot.listener;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.objects.SyncTask.RunType;
import main.java.me.avankziar.mim.spigot.objects.SyncTask;
import main.java.me.avankziar.mim.spigot.objects.SyncType;

public class PlayerChangedWorldListener extends BaseListener
{
	public PlayerChangedWorldListener(MIM plugin)
	{
		super(plugin, BaseListener.Type.IFH_API);
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onPlayerChangeWorld(PlayerChangedWorldEvent event)
	{
		Player player = event.getPlayer();
		World world = player.getWorld();
		if(plugin.getConfigHandler().loadPredefineAlways(world))
		{ //Info Egal wie oft der Spieler schon gejoint ist, wird immer überschrieben
			if(!preChecks(player))
			{
				return;
			}
			addCooldown(player.getUniqueId());
			new SyncTask(plugin, SyncType.FULL, RunType.LOAD_PREDEFINESTATE, player, 
					plugin.getConfigHandler().getPredefineStatenameAlways(world), null).run();
			removeCooldown(player.getUniqueId());
			return;
		}
	}
}