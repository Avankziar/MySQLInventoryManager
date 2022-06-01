package main.java.me.avankziar.mim.spigot.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.handler.CustomPlayerInventoryHandler;
import main.java.me.avankziar.mim.spigot.objects.SyncTask;
import main.java.me.avankziar.mim.spigot.objects.SyncTask.RunType;
import main.java.me.avankziar.mim.spigot.objects.SyncType;

public class InventoryCloseListener extends BaseListener
{
	public InventoryCloseListener(MIM plugin)
	{
		super(plugin, BaseListener.Type.INVENTORY_CLOSE);
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onInventoryClose(InventoryCloseEvent event)
	{
		if(CustomPlayerInventoryHandler.cpiInInventory.containsKey(event.getPlayer().getUniqueId()))
		{
			if(CustomPlayerInventoryHandler.playerInShulkerInInventory.containsKey(event.getPlayer().getUniqueId()))
			{
				final ItemStack[] isa = event.getInventory().getContents();
				new CustomPlayerInventoryHandler(CustomPlayerInventoryHandler.cpiInInventory.get(event.getPlayer().getUniqueId()))
					.closeShulkerInInventory((Player) event.getPlayer(), isa);
				return;
			}
			final ItemStack[] isa = event.getInventory().getContents();
			new CustomPlayerInventoryHandler(CustomPlayerInventoryHandler.cpiInInventory.get(event.getPlayer().getUniqueId()))
				.closeInventory((Player) event.getPlayer(), isa);
			return;
		}
		if(!plugin.getConfigHandler().isEventEnabled(this.bType.getName(), event.getPlayer().getWorld()))
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