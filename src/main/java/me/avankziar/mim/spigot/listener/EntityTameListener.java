package main.java.me.avankziar.mim.spigot.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityTameEvent;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.objects.SyncTask.RunType;
import main.java.me.avankziar.mim.spigot.objects.SyncType;

public class EntityTameListener extends BaseListener
{
	public EntityTameListener(MIM plugin)
	{
		super(plugin, BaseListener.Type.ENTITY_TAME);
	}
	
	@EventHandler (priority = EventPriority.NORMAL)
	public void onEntityTame(EntityTameEvent event)
	{
		if(event.isCancelled())
		{
			return;
		}
		if(!plugin.getConfigHandler().isEventEnabled(this.bType.getName(), event.getEntity().getWorld()))
		{
			return;
		}
		Player player = (Player) event.getOwner();
		doSync(player, SyncType.FULL, RunType.SAVE);
	}
}