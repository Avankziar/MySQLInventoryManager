package main.java.me.avankziar.mim.spigot.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockCanBuildEvent;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.objects.SyncTask;
import main.java.me.avankziar.mim.spigot.objects.SyncTask.RunType;
import main.java.me.avankziar.mim.spigot.objects.SyncType;

public class BlockCanBuildListener extends BaseListener
{
	public BlockCanBuildListener(MIM plugin)
	{
		super(plugin, BaseListener.Type.BLOCK_CANBUILD);
	}
	
	@EventHandler (priority = EventPriority.NORMAL)
	public void onBlockCanBuild(BlockCanBuildEvent event)
	{
		if(!plugin.getConfigHandler().isEventEnabled(this.bType.getName(), event.getBlock().getWorld()))
		{
			return;
		}
		Player player = event.getPlayer();
		addCooldown(player.getUniqueId());
		new SyncTask(plugin, SyncType.INV_ONLY, RunType.SAVE, player).run();
		removeCooldown(player.getUniqueId());	
	}
}