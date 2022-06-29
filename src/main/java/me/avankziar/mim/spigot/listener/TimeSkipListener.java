package main.java.me.avankziar.mim.spigot.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.TimeSkipEvent;
import org.bukkit.event.world.TimeSkipEvent.SkipReason;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.objects.SyncTask.RunType;
import main.java.me.avankziar.mim.spigot.objects.SyncType;

public class TimeSkipListener extends BaseListener
{
	public TimeSkipListener(MIM plugin)
	{
		super(plugin, BaseListener.Type.WORLD_TIMESKIP);
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onTimeSkip(TimeSkipEvent event)
	{
		if(event.isCancelled())
		{
			return;
		}
		if(event.getSkipReason() != SkipReason.NIGHT_SKIP)
		{
			return;
		}
		if(!plugin.getConfigHandler().isEventEnabled(this.bType.getName(), event.getWorld()))
		{
			return;
		}
		for(Player player : event.getWorld().getPlayers())
		{
			if(!preChecks(player))
			{
				return;
			}
			doSync(player, SyncType.FULL, RunType.SAVE);
		}		
	}
}