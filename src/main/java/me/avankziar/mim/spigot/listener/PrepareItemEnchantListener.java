package main.java.me.avankziar.mim.spigot.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;

import main.java.me.avankziar.mim.spigot.cmd.EnchantingTableCmdExecutor;

public class PrepareItemEnchantListener implements Listener
{	
	@EventHandler
	public void onPrepareItemEnchantEvent(PrepareItemEnchantEvent event)
	{
		if(EnchantingTableCmdExecutor.inEnchantingTable.contains(event.getEnchanter().getUniqueId()))
		{
			event.getOffers()[0].setCost(1);
			event.getOffers()[1].setCost(15);
			event.getOffers()[2].setCost(30);
		}
	}
}