package me.avankziar.mim.spigot.listener;

import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.mim.general.assistance.ChatApiS;
import me.avankziar.mim.general.objects.PlayerData;
import me.avankziar.mim.general.objects.PlayerInventory;
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
		player.sendMessage("[MIM] Sync startet...");
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
			/*
			 * Player has quit.
			 */
			if(player == null)
			{
				return;
			}
			if(SynchronHandler.syncInventory(player, uuid))
			{
				MIM.logger.info("Sync Inventory of "+name+" was successful!");
			} else
			{
				MIM.logger.info("Sync Inventory of "+name+" was unsuccessful!");
			}
			SynchronHandler.setSync(uuid, false);
			player.sendMessage("[MIM] Sync fertig!");
			//ToSync
			player.setArrowsInBody(0);
			player.setExhaustion(0);
			player.setExp(0);
			player.setFireTicks(0);
			player.setFlying(false);
			player.setFoodLevel(0);
			player.setFreezeTicks(0);
			player.setGameMode(GameMode.SURVIVAL);
			player.setHealth(0);
			player.setLevel(0);
			player.setRemainingAir(0);
			player.setSaturatedRegenRate(0);
			player.setSaturation(0);
			player.setStarvationRate(0);
			//EndOfSync
			PlayerData pd = MIM.getPlugin().getMysqlHandler().getData(new PlayerData(), "`id` ASC", "`player_uuid` = ?", uuid.toString());
			if(pd == null)
			{
				pd = new PlayerData(0, uuid, name, false);
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
		final String name = event.getPlayer().getName();
		MIM.getPlugin().getMysqlHandler().updateData(new PlayerData(0, uuid, name, true), "`player_uuid` = ?", uuid.toString());
		final ItemStack[] inv = event.getPlayer().getInventory().getStorageContents();
		final ItemStack offhand = event.getPlayer().getInventory().getItemInOffHand();
		final ItemStack helmet = event.getPlayer().getInventory().getHelmet();
		final ItemStack chestplate = event.getPlayer().getInventory().getChestplate();
		final ItemStack legging = event.getPlayer().getInventory().getLeggings();
		final ItemStack boots = event.getPlayer().getInventory().getBoots();
		PlayerInventory pi = new PlayerInventory(0, uuid, System.currentTimeMillis(), inv, offhand, helmet, chestplate, legging, boots);
		MIM.getPlugin().getMysqlHandler().create(pi);
		MIM.getPlugin().getMysqlHandler().updateData(new PlayerData(0, uuid, name, false), "`player_uuid` = ?", uuid.toString());
	}
}