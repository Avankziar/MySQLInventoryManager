package main.java.me.avankziar.mim.spigot.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener
{	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event)
	{
		/*Player player = (Player) event.getWhoClicked();
		if(!player.hasPermission(Bypass.get(Bypass.Permission.SHULKER_OPEN_IN_INVENTORY))
				|| event.getClick() != ClickType.RIGHT
				|| InventoryCloseListener.inExternInventory(player.getUniqueId())
				|| InventoryCloseListener.executorToShulkerSlot.containsKey(player.getUniqueId())
				|| event.getClickedInventory() == null 
				|| event.getCurrentItem() == null
				|| !isShulker(event.getCurrentItem().getType()))
		{
			return;
		}
		event.setCancelled(true);
        event.setResult(Result.DENY);
		int slot = event.getSlot();
		final ItemStack is = event.getCurrentItem();
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
        /*InventoryCloseListener.openShulkerInInventory(player,
        		player.getUniqueId(), player.getInventory(),
        		slot, is, shulker, player.getName(),
        		im.hasDisplayName() ? im.getDisplayName() : null);*/
	}
	
	public static boolean isShulker(Material mat)
	{
		switch(mat)
		{
		case SHULKER_BOX:
		case BLACK_SHULKER_BOX:
		case BLUE_SHULKER_BOX:
		case BROWN_SHULKER_BOX:
		case CYAN_SHULKER_BOX:
		case GRAY_SHULKER_BOX:
		case GREEN_SHULKER_BOX:
		case LIGHT_BLUE_SHULKER_BOX:
		case LIGHT_GRAY_SHULKER_BOX:
		case LIME_SHULKER_BOX:
		case MAGENTA_SHULKER_BOX:
		case ORANGE_SHULKER_BOX:
		case PINK_SHULKER_BOX:
		case PURPLE_SHULKER_BOX:
		case RED_SHULKER_BOX:
		case WHITE_SHULKER_BOX:
		case YELLOW_SHULKER_BOX:
			return true;
		default:
			break;
		}
		return false;
	}
}