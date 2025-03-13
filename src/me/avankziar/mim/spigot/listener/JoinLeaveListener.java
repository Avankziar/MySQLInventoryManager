package me.avankziar.mim.spigot.listener;

import java.util.UUID;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.mim.general.assistance.ChatApiS;
import me.avankziar.mim.general.objects.PlayerData;
import me.avankziar.mim.general.objects.PlayerEnderchest;
import me.avankziar.mim.general.objects.PlayerInventory;
import me.avankziar.mim.general.objects.PlayerMetaData;
import me.avankziar.mim.general.objects.PlayerPotionEffect;
import me.avankziar.mim.spigot.MIM;
import me.avankziar.mim.spigot.handler.SynchronHandler;

public class JoinLeaveListener implements Listener
{
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		final UUID uuid = event.getPlayer().getUniqueId();
		SynchronHandler.setSync(uuid, true);
		final Player player = event.getPlayer();
		player.sendMessage("[MIM] Sync startet..."); //REMOVEME
		final String name = player.getName();
		updatePlayer(player, uuid, name, 1);
	}
	
	private void updatePlayer(final Player player, final UUID uuid, final String name, int i)
	{
		if(MIM.getPlugin().getMysqlHandler().exist(new PlayerData(), 
				"`player_uuid` = ? AND `in_sync` = ?", uuid.toString(), true))
		{
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					if(i == 40)
					{
						MIM.logger.warning("40 Attemts done! Player "+name+" cannot be synchronized! Cancel Synchronization...");
						if(player != null)
						{
							player.spigot().sendMessage(ChatApiS.tl("<white>[MIM] <red>ERROR! Cannot be sychronized! <bold>Contact an admin immediately without logging out or moving/teleport ingame!"));
						}
						return;
					}
					int j = i+1;
					updatePlayer(player, uuid, name, j);
				}
			}.runTaskLater(MIM.getPlugin(), 2L);
		} else
		{
			if(player == null)
			{
				/*
				 * Player has quit.
				 */
				return;
			}
			if(!SynchronHandler.syncInventory(player, uuid))
			{
				MIM.logger.info("Sync Inventory of "+name+" was unsuccessful!"); //REMOVEME
			}
			if(!SynchronHandler.syncMetaData(player, uuid))
			{
				MIM.logger.info("Sync MetaData of "+name+" was unsuccessful!"); //REMOVEME
			}
			if(!SynchronHandler.syncPotionEffect(player, uuid))
			{
				MIM.logger.info("Sync PotionEffect of "+name+" was unsuccessful!"); //REMOVEME
			}
			if(!SynchronHandler.syncEnderchest(player, uuid))
			{
				MIM.logger.info("Sync Enderchest of "+name+" was unsuccessful!"); //REMOVEME
			}
			SynchronHandler.setSync(uuid, false);
			player.sendMessage("[MIM] Sync fertig!"); //REMOVEME
			//ToSync
			
			//EndOfSync
			PlayerData pd = MIM.getPlugin().getMysqlHandler().getData(new PlayerData(), "`id` ASC", "`player_uuid` = ?", uuid.toString());
			if(pd == null)
			{
				pd = new PlayerData(0, uuid, name, false, true);
				MIM.getPlugin().getMysqlHandler().create(pd);
			} else
			{
				if(!pd.getName().equals(name))
				{
					pd.setName(name);
					MIM.getPlugin().getMysqlHandler().updateData(pd, "`player_uuid` = ?", uuid.toString());
				}
			}
		}
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		final UUID uuid = event.getPlayer().getUniqueId();
		/*
		 * Goes Offline while Player datas should be sync, so set inSync = false, and dont sync, because it was to quick.
		 * Attemt to dupe items or such.
		 */
		if(SynchronHandler.inSync(uuid))
		{
			SynchronHandler.setSync(uuid, false);
			return;
		}
		final Player player = event.getPlayer();
		final String name = player.getName();
		/*
		 * Must be insert as soon it can be, to ensure the other server, where the player may join,
		 * will not sync a old synchronized inventory or other data.
		 */
		MIM.getPlugin().getMysqlHandler().updateData(new PlayerData(0, uuid, name, true, true), "`player_uuid` = ?", uuid.toString());
		final long timestamp = System.currentTimeMillis();
		
		// Alle Daten in Objekte speichern
		final PlayerInventory playerInventory = new PlayerInventory(
		    0, uuid, timestamp, 
		    player.getInventory().getStorageContents(),
		    player.getInventory().getItemInOffHand(),
		    player.getInventory().getHelmet(),
		    player.getInventory().getChestplate(),
		    player.getInventory().getLeggings(),
		    player.getInventory().getBoots()
		);
		final PlayerMetaData playerMetaData = new PlayerMetaData(
		    0, uuid, timestamp, 
		    player.getGameMode(),
		    player.getAttribute(Attribute.MAX_HEALTH).getBaseValue(),
		    player.getHealth(),
		    player.getRemainingAir(),
		    player.getFireTicks(),
		    player.getFreezeTicks(),
		    player.getExp(),
		    player.getLevel(),
		    player.getExhaustion(),
		    player.getFoodLevel(),
		    player.getSaturation()
		);
		final PlayerPotionEffect playerPotionEffect = new PlayerPotionEffect(
				0, uuid, timestamp,
				player.getActivePotionEffects().toArray(new PotionEffect[player.getActivePotionEffects().size()])
		);
		final PlayerEnderchest playerEnderchest = new PlayerEnderchest(
				0, uuid, timestamp,
				player.getEnderChest().getStorageContents()
		);
		MIM.getPlugin().getMysqlHandler().runTransaction((conn) -> 
		{
		    playerInventory.create(conn);
		    playerMetaData.create(conn);
		    playerPotionEffect.create(conn);
		    playerEnderchest.create(conn);
		    new PlayerData(0, uuid, name, false, true).update(conn, "`player_uuid` = ?", uuid.toString()); //Unsync Player in database to let him sync on a other server if he only changed servers.
		});
	}
}