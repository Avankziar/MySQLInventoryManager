package main.java.me.avankziar.mim.spigot.listener;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityPickupItemEvent;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.objects.SyncTask;
import main.java.me.avankziar.mim.spigot.objects.SyncTask.RunType;
import main.java.me.avankziar.mim.spigot.objects.SyncType;

public class EntityPickupItemListener extends BaseListener
{
	public EntityPickupItemListener(MIM plugin)
	{
		super(plugin);
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onEntityPickupItem(EntityPickupItemEvent event)
	{
		if(event.isCancelled())
		{
			return;
		}
		if(event.getEntityType() != EntityType.PLAYER)
		{
			return;
		}
		if(!plugin.getConfigHandler().isEventEnabled(BaseListener.Type.ENTITYPICKUPITEM.getName(), event.getEntity().getWorld()))
		{
			return;
		}
		Player player = (Player) event.getEntity();
		addCooldown(player.getUniqueId());
		new SyncTask(plugin, SyncType.INVENTORY, RunType.SAVE, player).run();
		removeCooldown(player.getUniqueId());	
	}
}