package me.avankziar.mim.spigot.handler;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.mim.general.database.YamlHandler;
import me.avankziar.mim.general.objects.PlayerInventory;
import me.avankziar.mim.spigot.MIM;

public class SynchronHandler
{
	private static ConcurrentHashMap<UUID, Boolean> syncStatus = new ConcurrentHashMap<>();
	public static int atTimeDatabaseBackupInventory = 2;
	
	public static void init(YamlHandler yh)
	{
		atTimeDatabaseBackupInventory = yh.getConfig().getInt("AtTimeDatabaseBackUp.Inventory", 2);
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
				new BukkitRunnable()
				{
					@Override
					public void run()
					{
						int c = MIM.getPlugin().getMysqlHandler().getCount(pi, "`player_uuid` = ?", uuid.toString());
						while(c > atTimeDatabaseBackupInventory)
						{
							int id = MIM.getPlugin().getMysqlHandler().lastID(pi, "`upload_time` ASC", "`player_uuid` = ?", uuid.toString());
							MIM.getPlugin().getMysqlHandler().deleteData(pi, "`id` = ?", id);
							c--;
						}
					}
				}.runTaskAsynchronously(MIM.getPlugin());
			}
		} catch(Exception e)
		{
			return false;
		}
		return true;
	}
}