package main.java.me.avankziar.mim.spigot.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import main.java.me.avankziar.ifh.general.assistance.ChatApi;
import main.java.me.avankziar.mim.spigot.MIM;

public class LoadStatusListener implements Listener
{
	private MIM plugin;
	public LoadStatusListener(MIM plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onCommand(PlayerCommandPreprocessEvent event)
	{
		if(PlayerJoinListener.inLoadStatus(event.getPlayer().getUniqueId()))
		{
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("SyncTask.NotFullLoaded")));
		}
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onCommand(PlayerDropItemEvent event)
	{
		if(PlayerJoinListener.inLoadStatus(event.getPlayer().getUniqueId()))
		{
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("SyncTask.NotFullLoaded")));
		}
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onCommand(EntityPickupItemEvent event)
	{
		if(!(event.getEntity() instanceof Player))
		{
			return;
		}
		if(PlayerJoinListener.inLoadStatus(((Player) event.getEntity()).getUniqueId()))
		{
			event.setCancelled(true);
		}
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onCommand(PlayerInteractEvent event)
	{
		if(PlayerJoinListener.inLoadStatus(event.getPlayer().getUniqueId()))
		{
			event.setCancelled(true);
		}
	}
}