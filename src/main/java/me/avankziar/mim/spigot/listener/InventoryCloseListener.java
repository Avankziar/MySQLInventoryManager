package main.java.me.avankziar.mim.spigot.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryCloseEvent;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.objects.SyncTask;
import main.java.me.avankziar.mim.spigot.objects.SyncTask.RunType;
import main.java.me.avankziar.mim.spigot.objects.SyncType;

public class InventoryCloseListener extends BaseListener
{
	public InventoryCloseListener(MIM plugin)
	{
		super(plugin);
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onInventoryClose(InventoryCloseEvent event)
	{
		if(!plugin.getConfigHandler().isEventEnabled(BaseListener.Type.PLAYERTELEPORT.getName(), event.getPlayer().getWorld()))
		{
			return;
		}
		Player player = (Player) event.getPlayer();
		if(!preChecks(player))
		{
			return;
		}
		addCooldown(player.getUniqueId());
		new SyncTask(plugin, SyncType.INVENTORY, RunType.SAVE, player).run();
		removeCooldown(player.getUniqueId());	
	}
}