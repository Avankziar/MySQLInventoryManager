package main.java.me.avankziar.mim.spigot.listener;

import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.assistance.BackgroundTask;
import main.java.me.avankziar.mim.spigot.database.MysqlHandler;
import main.java.me.avankziar.mim.spigot.handler.ConfigHandler;
import main.java.me.avankziar.mim.spigot.handler.PlayerDataHandler;
import main.java.me.avankziar.mim.spigot.objects.SyncTask;
import main.java.me.avankziar.mim.spigot.objects.SyncTask.RunType;
import main.java.me.avankziar.mim.spigot.objects.SyncType;
import main.java.me.avankziar.mim.spigot.objects.SynchronStatus;

public class PlayerJoinListener extends BaseListener
{	
	public PlayerJoinListener(MIM plugin)
	{
		super(plugin, BaseListener.Type.PLAYER_JOIN);
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerJoinEvent event)
	{
		loadstatus.add(event.getPlayer().getUniqueId());
		World world = event.getPlayer().getWorld();
		Player player = event.getPlayer();
		if(isSychrnonStatusFinish(player.getUniqueId()))
		{
			doJoin(player, world);
		} else
		{
			new BukkitRunnable()
			{
				
				@Override
				public void run()
				{
					if(!isSychrnonStatusFinish(player.getUniqueId()))
					{
						return;
					}
					doJoin(player, world);
					cancel();
				}
			}.runTaskTimer(plugin, 0, 2L);
		}
	}
	
	public void doJoin(final Player player, final World world)
	{
		if(plugin.getConfigHandler().loadPredefineOnFirstJoin(world)
				&& !plugin.getMysqlHandler().exist(MysqlHandler.Type.PLAYERDATA, "`player_uuid` = ? AND `synchro_key` = ?",
						player.getUniqueId().toString(), plugin.getConfigHandler().getSynchroKey(player)))
		{ //Info Wenn der Spieler zum ersten Mal mit dem GameMode und dem SynchroKey Joint
			if(!preChecks(player))
			{
				loadstatus.remove(player.getUniqueId());
				return;
			}
			addCooldown(player.getUniqueId());
			new SyncTask(plugin, SyncType.FULL, RunType.LOAD_PREDEFINESTATE, player, 
					plugin.getConfigHandler().getPredefineStatenameOnFristJoin(world), null).run();
			removeCooldown(player.getUniqueId());
			loadstatus.remove(player.getUniqueId());
			return;
		} else if(plugin.getConfigHandler().loadPredefineAlways(world))
		{ //Info Egal wie oft der Spieler schon gejoint ist, wird immer überschrieben
			if(!preChecks(player))
			{
				loadstatus.remove(player.getUniqueId());
				return;
			}
			addCooldown(player.getUniqueId());
			new SyncTask(plugin, SyncType.FULL, RunType.LOAD_PREDEFINESTATE, player, 
					plugin.getConfigHandler().getPredefineStatenameAlways(world), null).run();
			removeCooldown(player.getUniqueId());
			loadstatus.remove(player.getUniqueId());
			return;
		} else if(!plugin.getMysqlHandler().exist(MysqlHandler.Type.PLAYERDATA, "`player_uuid` = ? AND `synchro_key` = ?",
						player.getUniqueId().toString(), plugin.getConfigHandler().getSynchroKey(player)))
		{
			//Wenn der Spieler noch nie gejoint ist und kein Vordefiniertes existiert.
			doSync(player, SyncType.FULL, RunType.SAVE);
			loadstatus.remove(player.getUniqueId());
			return;
		}
		if(!new ConfigHandler(plugin).inSleepMode())
		{
			PlayerDataHandler.load(SyncType.FULL, player);
		}
		loadstatus.remove(player.getUniqueId());
		BackgroundTask.waitingItemsInfoTask(player);
	}
	
	public static boolean inLoadStatus(UUID uuid)
	{
		return loadstatus.contains(uuid);
	}
	
	public static boolean isSychrnonStatusFinish(UUID uuid)
	{
		SynchronStatus sys = (SynchronStatus) MIM.getPlugin().getMysqlHandler().getData(
				MysqlHandler.Type.SYNCHRONSTATUS, "`player_uuid` = ?", uuid.toString());
		if(sys == null)
		{
			sys = new SynchronStatus(uuid, SynchronStatus.Type.FINISH);
			MIM.getPlugin().getMysqlHandler().create(MysqlHandler.Type.SYNCHRONSTATUS, sys);
		}
		return sys != null ? sys.getType() == SynchronStatus.Type.FINISH : false;
	}
	
	public static void setSynchroStatus(UUID uuid, SynchronStatus.Type type)
	{
		if(MIM.getPlugin().getMysqlHandler().exist(MysqlHandler.Type.SYNCHRONSTATUS, "`player_uuid` = ?", uuid.toString()))
		{
			//MIM.getPlugin().getLogger().log(Level.INFO, "Player "+uuid.toString()+" was "+type.toString());
			MIM.getPlugin().getMysqlHandler().updateData(MysqlHandler.Type.SYNCHRONSTATUS, new SynchronStatus(uuid, type),
					"`player_uuid` = ?", uuid.toString());
		} else
		{
			MIM.getPlugin().getMysqlHandler().create(MysqlHandler.Type.SYNCHRONSTATUS, new SynchronStatus(uuid, type));
		}
	}
}