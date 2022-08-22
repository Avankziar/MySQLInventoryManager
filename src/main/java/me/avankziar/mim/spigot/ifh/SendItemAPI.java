package main.java.me.avankziar.mim.spigot.ifh;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.database.MysqlHandler;
import main.java.me.avankziar.mim.spigot.objects.WaitingItems;

public class SendItemAPI
{
	private MIM plugin;
	private static String d = "default";
	private static String n = "n";
	
	public SendItemAPI(MIM plugin)
	{
		this.plugin = plugin;
	}
	
	public void sendItem(UUID uuid, String sender, ItemStack...itemStack)
	{
		sendItem(uuid, sender, d, n, itemStack);
	}
	
	public void sendItem(UUID uuid, String sender, String synchroKey, ItemStack...itemStack)
	{
		sendItem(uuid, sender, synchroKey, n, itemStack);
	}
	
	public void sendItem(UUID uuid, String sender, String synchroKey, String reason, ItemStack...itemStack)
	{
		WaitingItems wi = new WaitingItems(0, synchroKey, uuid, sender, reason, System.currentTimeMillis(), itemStack);
		plugin.getMysqlHandler().create(MysqlHandler.Type.WAITINGITEMS, wi);
	}
	
	public void sendItem(UUID[] uuid, String sender, ItemStack...itemStack)
	{
		sendItem(uuid, sender, d, n, itemStack);
	}
	
	public void sendItem(UUID[] uuid, String sender, String synchroKey, ItemStack...itemStack)
	{
		sendItem(uuid, sender, synchroKey, n, itemStack);
	}
	
	public void sendItem(UUID[] uuid, String sender, String synchroKey, String reason, ItemStack...itemStack)
	{
		for(UUID u : uuid)
		{
			sendItem(u, sender, synchroKey, reason, itemStack);
		}
	}
	
	public void sendItem(String sender, ItemStack...itemStack)
	{
		sendItem(sender, d, n, itemStack);
	}
	
	public void sendItem(String sender, String synchroKey, ItemStack...itemStack)
	{
		sendItem(sender, synchroKey, n, itemStack);
	}
	
	public void sendItem(String sender, String synchroKey, String reason, ItemStack...itemStack)
	{
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				ArrayList<UUID> al = new ArrayList<>();
				try (Connection conn = plugin.getMysqlSetup().getConnection();)
				{
					String sql = "SELECT * FROM `" + MysqlHandler.Type.SYNCHRONSTATUS.getValue()
						+ "` WHERE 1";
					PreparedStatement ps = conn.prepareStatement(sql);
					
					ResultSet rs = ps.executeQuery();
					MysqlHandler.addRows(MysqlHandler.QueryType.READ, rs.getMetaData().getColumnCount());
					
					while (rs.next()) 
					{
						al.add(UUID.fromString(rs.getString("player_uuid")));
					}
				} catch (SQLException e)
				{
					MIM.log.log(Level.WARNING, "SQLException! Could not get a "+this.getClass().getSimpleName()+" Object!", e);
				}
				for(UUID uuid : al)
				{
					sendItem(uuid, sender, synchroKey, reason, itemStack);
				}
			}
		}.runTaskAsynchronously(plugin);
		
	}
	
	public ArrayList<WaitingItems> getWaitingItems(UUID uuid, String synchroKey)
	{
		return WaitingItems.convert(plugin.getMysqlHandler().getFullList(MysqlHandler.Type.WAITINGITEMS, "`id` ASC",
				"`player_uuid` = ? AND `synchro_key` = ?", uuid.toString(), synchroKey));
	}
}