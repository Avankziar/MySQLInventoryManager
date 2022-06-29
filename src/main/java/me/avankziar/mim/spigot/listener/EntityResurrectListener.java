package main.java.me.avankziar.mim.spigot.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityResurrectEvent;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.objects.SyncTask.RunType;
import main.java.me.avankziar.mim.spigot.objects.SyncType;

public class EntityResurrectListener extends BaseListener
{
	public EntityResurrectListener(MIM plugin)
	{
		super(plugin, BaseListener.Type.ENTITY_RESURRECT);
	}
	
	@EventHandler (priority = EventPriority.NORMAL)
	public void onEntityResurrect(EntityResurrectEvent event)
	{
		if(event.isCancelled())
		{
			return;
		}
		if(!plugin.getConfigHandler().isEventEnabled(this.bType.getName(), event.getEntity().getWorld()))
		{
			return;
		}
		if(!(event.getEntity() instanceof Player))
		{
			return;
		}
		Player player = (Player) event.getEntity();
		doSync(player, SyncType.FULL, RunType.SAVE);
	}
}