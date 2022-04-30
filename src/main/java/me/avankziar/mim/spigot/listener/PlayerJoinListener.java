package main.java.me.avankziar.mim.spigot.listener;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.database.MysqlHandler;
import main.java.me.avankziar.mim.spigot.objects.SyncTask;
import main.java.me.avankziar.mim.spigot.objects.SyncTask.RunType;
import main.java.me.avankziar.mim.spigot.objects.SyncType;

public class PlayerJoinListener extends BaseListener
{
	public PlayerJoinListener(MIM plugin)
	{
		super(plugin, BaseListener.Type.PLAYER_JOIN);
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerJoinEvent event)
	{
		World world = event.getPlayer().getWorld();
		if(!plugin.getConfigHandler().isEventEnabled(this.bType.getName(), world))
		{
			return;
		}
		Player player = event.getPlayer();
		if(plugin.getConfigHandler().loadPredefineOnFirstJoin(world)
				&& !plugin.getMysqlHandler().exist(MysqlHandler.Type.PLAYERDATA, "`player_uuid` = ? AND `synchro_key` = ? AND `game_mode` = ?",
						player.getUniqueId().toString(), plugin.getConfigHandler().getSynchroKey(player), player.getGameMode().toString()))
		{ //Info Wenn der Spieler zum ersten Mal mit dem GameMode und dem SynchroKey Joint
			addCooldown(player.getUniqueId());
			new SyncTask(plugin, SyncType.FULL, RunType.LOAD_PREDEFINESTATE, player, 
					plugin.getConfigHandler().getPredefineStatenameOnFristJoin(world), null).run();
			removeCooldown(player.getUniqueId());
			return;
		} else if(plugin.getConfigHandler().loadPredefineAlways(world))
		{ //Info Egal wie oft der Spieler schon gejoint ist, wird immer überschrieben
			addCooldown(player.getUniqueId());
			new SyncTask(plugin, SyncType.FULL, RunType.LOAD_PREDEFINESTATE, player, 
					plugin.getConfigHandler().getPredefineStatenameAlways(world), null).run();
			removeCooldown(player.getUniqueId());
			return;
		}
		addCooldown(player.getUniqueId());
		new SyncTask(plugin, SyncType.FULL, RunType.LOAD, player, player.getGameMode()).run();
		removeCooldown(player.getUniqueId());
	}
}