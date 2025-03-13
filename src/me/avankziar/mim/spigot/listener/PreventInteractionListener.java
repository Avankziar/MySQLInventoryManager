package me.avankziar.mim.spigot.listener;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import me.avankziar.mim.spigot.handler.SynchronHandler;

public class PreventInteractionListener implements Listener
{
	@EventHandler
	public void onInteract(PlayerInteractEvent event)
	{
		if(SynchronHandler.inSync(event.getPlayer().getUniqueId()))
		{
			event.setCancelled(true);
			event.setUseInteractedBlock(Result.DENY);
			event.setUseItemInHand(Result.DENY);
		}
	}
	
	@EventHandler
	public void onInteractEntity(PlayerInteractEntityEvent event)
	{
		if(SynchronHandler.inSync(event.getPlayer().getUniqueId()))
		{
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBedEnter(PlayerBedEnterEvent event)
	{
		if(SynchronHandler.inSync(event.getPlayer().getUniqueId()))
		{
			event.setCancelled(true);
			event.setUseBed(Result.DENY);
		}
	}
	
	@EventHandler
	public void onBucketEntity(PlayerBucketEntityEvent event)
	{
		if(SynchronHandler.inSync(event.getPlayer().getUniqueId()))
		{
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onCommandPreprocess(PlayerCommandPreprocessEvent event)
	{
		if(SynchronHandler.inSync(event.getPlayer().getUniqueId()))
		{
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDropItem(PlayerDropItemEvent event)
	{
		if(SynchronHandler.inSync(event.getPlayer().getUniqueId()))
		{
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onHarvestBlock(PlayerHarvestBlockEvent event)
	{
		if(SynchronHandler.inSync(event.getPlayer().getUniqueId()))
		{
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onItemConsume(PlayerItemConsumeEvent event)
	{
		if(SynchronHandler.inSync(event.getPlayer().getUniqueId()))
		{
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onItemDamage(PlayerItemDamageEvent event)
	{
		if(SynchronHandler.inSync(event.getPlayer().getUniqueId()))
		{
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onItemHeld(PlayerItemHeldEvent event)
	{
		if(SynchronHandler.inSync(event.getPlayer().getUniqueId()))
		{
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPickupItem(EntityPickupItemEvent event)
	{
		if(event.getEntityType() == EntityType.PLAYER)
		{
			Player player = (Player) event.getEntity();
			if(SynchronHandler.inSync(player.getUniqueId()))
			{
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onShearEntity(PlayerShearEntityEvent event)
	{
		if(SynchronHandler.inSync(event.getPlayer().getUniqueId()))
		{
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onSwapHandItems(PlayerSwapHandItemsEvent event)
	{
		if(SynchronHandler.inSync(event.getPlayer().getUniqueId()))
		{
			event.setCancelled(true);
		}
	}
}