package me.avankziar.mim.spigot.handler;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.mim.general.database.DatabaseTable;
import me.avankziar.mim.general.database.YamlHandler;
import me.avankziar.mim.general.objects.PlayerEnderchest;
import me.avankziar.mim.general.objects.PlayerInventory;
import me.avankziar.mim.general.objects.PlayerMetaData;
import me.avankziar.mim.general.objects.PlayerPotionEffect;
import me.avankziar.mim.spigot.MIM;

public class SynchronHandler
{
	private static ConcurrentHashMap<UUID, Boolean> syncStatus = new ConcurrentHashMap<>();
	public static int atTimeDatabaseBackupInventory = 2;
	public static int atTimeDatabaseBackupMetaData = 2;
	public static HashMap<String, GameMode> overrideGameModeForWorld = new HashMap<>();
	public static int atTimeDatabaseBackupPotionEffect = 2;
	public static int atTimeDatabaseBackupEnderchest = 2;
	
	public static void init(YamlHandler yh)
	{
		atTimeDatabaseBackupInventory = yh.getConfig().getInt("AtTimeDatabaseBackUp.Inventory", 2);
		atTimeDatabaseBackupMetaData = yh.getConfig().getInt("AtTimeDatabaseBackUp.MetaData", 2);
		for(String ovw : yh.getConfig().getStringList("OverrideGameModeForWorld"))
		{
			String[] s = ovw.split(";");
			if(s.length != 2)
			{
				continue;
			}
			try
			{
				GameMode gm = GameMode.valueOf(s[1]);
				overrideGameModeForWorld.put(s[0], gm);
			} catch(Exception e)
			{
				continue;
			}
		}
		atTimeDatabaseBackupPotionEffect = yh.getConfig().getInt("AtTimeDatabaseBackUp.PotionEffect", 2);
		atTimeDatabaseBackupEnderchest = yh.getConfig().getInt("AtTimeDatabaseBackUp.Enderchest", 2);
	}
	
	public static boolean inSync(UUID uuid)
	{
		Boolean s = syncStatus.get(uuid);
		return s != null ? s : false;
	}
	
	public static void setSync(UUID uuid, boolean boo)
	{
		syncStatus.put(uuid, boo);
	}
	
	public static <T extends DatabaseTable<T>> void deleteOverflowDatabaseEntrys(T t, int atTimeDatabaseBackup, UUID uuid)
	{
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				int c = MIM.getPlugin().getMysqlHandler().getCount(t, "`player_uuid` = ?", uuid.toString());
				while(c > atTimeDatabaseBackup)
				{
					int id = MIM.getPlugin().getMysqlHandler().lastID(t, "`upload_time` ASC", "`player_uuid` = ?", uuid.toString());
					MIM.getPlugin().getMysqlHandler().deleteData(t, "`id` = ?", id);
					c--;
				}
			}
		}.runTaskAsynchronously(MIM.getPlugin());
	}
	
	public static boolean syncInventory(Player player, UUID uuid)
	{
		try
		{
			PlayerInventory pi = MIM.getPlugin().getMysqlHandler().getData(new PlayerInventory(), "`upload_time` DESC", 
					"`player_uuid` = ?", uuid.toString());
			if(pi != null)
			{
				player.getInventory().setStorageContents(pi.getInventory());
				player.getInventory().setItemInOffHand(pi.getOffHand());
				player.getInventory().setHelmet(pi.getHelmet());
				player.getInventory().setChestplate(pi.getChestplate());
				player.getInventory().setLeggings(pi.getLegging());
				player.getInventory().setBoots(pi.getBoots());
				deleteOverflowDatabaseEntrys(pi, atTimeDatabaseBackupInventory, uuid);
			}
		} catch(Exception e)
		{
			return false;
		}
		return true;
	}
	
	public static boolean syncMetaData(Player player, UUID uuid)
	{
		try
		{
			PlayerMetaData pmd = MIM.getPlugin().getMysqlHandler().getData(new PlayerMetaData(), "`upload_time` DESC", 
					"`player_uuid` = ?", uuid.toString());
			if(pmd != null)
			{
				if(overrideGameModeForWorld.containsKey(player.getWorld().getName()))
				{
					GameMode gm = overrideGameModeForWorld.get(player.getWorld().getName());
					if(gm != null)
					{
						player.setGameMode(gm);
					} else
					{
						player.setGameMode(pmd.getGameMode());
					}
				} else
				{
					player.setGameMode(pmd.getGameMode());
				}
				player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(pmd.getMaxHealth());
				player.setHealth(pmd.getHealth());
				player.setRemainingAir(pmd.getRemainingAir());
				player.setFireTicks(pmd.getFireTicks());
				player.setFreezeTicks(pmd.getFreezeTicks());
				player.setLevel(pmd.getExpLevel());
				player.setExp(pmd.getExp());
				player.setExhaustion(pmd.getExhaustion());
				player.setFoodLevel(pmd.getFoodLevel());
				player.setSaturation(pmd.getSaturation());
				deleteOverflowDatabaseEntrys(pmd, atTimeDatabaseBackupMetaData, uuid);
			}
		} catch(Exception e)
		{
			return false;
		}
		return true;
	}
	
	public static boolean syncEnderchest(Player player, UUID uuid)
	{
		try
		{
			PlayerEnderchest pec = MIM.getPlugin().getMysqlHandler().getData(new PlayerEnderchest(), "`upload_time` DESC", 
					"`player_uuid` = ?", uuid.toString());
			if(pec != null)
			{
				player.getEnderChest().setStorageContents(pec.getInventory());
				deleteOverflowDatabaseEntrys(pec, atTimeDatabaseBackupEnderchest, uuid);
			}
		} catch(Exception e)
		{
			return false;
		}
		return true;
	}
	
	public static boolean syncPotionEffect(Player player, UUID uuid)
	{
		try
		{
			PlayerPotionEffect ppe = MIM.getPlugin().getMysqlHandler().getData(new PlayerPotionEffect(), "`upload_time` DESC", 
					"`player_uuid` = ?", uuid.toString());
			if(ppe != null)
			{
				for(PotionEffect pe : player.getActivePotionEffects())
				{
					player.removePotionEffect(pe.getType());
				}
				for(PotionEffect pe : ppe.getPotionEffect())
				{
					player.addPotionEffect(pe);
				}
				deleteOverflowDatabaseEntrys(ppe, atTimeDatabaseBackupPotionEffect, uuid);
			}
		} catch(Exception e)
		{
			return false;
		}
		return true;
	}
}