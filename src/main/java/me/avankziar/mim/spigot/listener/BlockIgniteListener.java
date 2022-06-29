package main.java.me.avankziar.mim.spigot.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockIgniteEvent;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.objects.SyncTask.RunType;
import main.java.me.avankziar.mim.spigot.objects.SyncType;

public class BlockIgniteListener extends BaseListener
{
	public BlockIgniteListener(MIM plugin)
	{
		super(plugin, BaseListener.Type.BLOCK_IGNITE);
	}
	
	@EventHandler (priority = EventPriority.NORMAL)
	public void onBlockIgnite(BlockIgniteEvent event)
	{
		if(event.isCancelled())
		{
			return;
		}
		if(!plugin.getConfigHandler().isEventEnabled(this.bType.getName(), event.getBlock().getWorld()))
		{
			return;
		}
		Player player = event.getPlayer();
		doSync(player, SyncType.INV_ONLY, RunType.SAVE);
	}
}