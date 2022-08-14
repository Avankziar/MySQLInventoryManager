package main.java.me.avankziar.mim.spigot.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener
{
	@EventHandler (priority = EventPriority.HIGH)
	public void onInteract(PlayerInteractEvent event)
	{
		/*if(event.useInteractedBlock() == Result.DENY || event.useItemInHand() == Result.DENY)
		{
			return;
		}
		if(event.getClickedBlock() == null)
		{
			if(event.getAction() == Action.RIGHT_CLICK_AIR
					&& event.getItem() != null && InventoryClickListener.isShulker(event.getItem().getType()))
			{
				final ItemStack is = event.getItem();
				if(!(is.getItemMeta() instanceof BlockStateMeta))
				{
					return;
				}
				BlockStateMeta im = (BlockStateMeta) is.getItemMeta();
		        if(!(im.getBlockState() instanceof ShulkerBox))
		        {
		        	return;
		        }
		        ShulkerBox shulker = (ShulkerBox) im.getBlockState();
				InventoryCloseListener.openShulkerInInventory(event.getPlayer(), event.getPlayer().getUniqueId(),
						event.getPlayer().getInventory(),
						event.getPlayer().getInventory().getHeldItemSlot(), event.getItem(),
						shulker, event.getPlayer().getName(), im.hasDisplayName() ? im.getDisplayName() : "Shulkerbox");
			}
		}*/
	}
}