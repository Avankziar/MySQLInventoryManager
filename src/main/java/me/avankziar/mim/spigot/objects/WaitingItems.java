package main.java.me.avankziar.mim.spigot.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.inventory.ItemStack;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.database.MysqlHandable;
import main.java.me.avankziar.mim.spigot.database.MysqlHandler;

public class WaitingItems implements MysqlHandable
{
	private int id;
	private String synchroKey;
	private UUID receiver;
	private String sender; //Playername or Pluginname
	private String reason;
	private long sendTime;
	private ItemStack[] items;
	
	public WaitingItems()
	{
		
	}
	
	public WaitingItems(int id, String synchroKey, UUID receiver, String sender, String reason, long sendTime, ItemStack[] items)
	{
		setId(id);
		setSynchroKey(synchroKey);
		setReceiver(receiver);
		setSender(sender);
		setReason(reason);
		setSendTime(sendTime);
		setItems(items);
	}
	
	public WaitingItems(int id, String synchroKey, UUID receiver, String sender, String reason, long sendTime, String items)
	{
		setId(id);
		setSynchroKey(synchroKey);
		setReceiver(receiver);
		setSender(sender);
		setReason(reason);
		setSendTime(sendTime);
		ArrayList<ItemStack> invc = new ArrayList<>();
		for(Object o : MIM.getPlugin().getBase64Provider().fromBase64Array(items))
		{
			invc.add((ItemStack) o);
		}
		setItems(invc.toArray(new ItemStack[invc.size()]));
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getSynchroKey()
	{
		return synchroKey;
	}

	public void setSynchroKey(String synchroKey)
	{
		this.synchroKey = synchroKey;
	}

	public UUID getReceiver()
	{
		return receiver;
	}

	public void setReceiver(UUID receiver)
	{
		this.receiver = receiver;
	}

	public String getSender()
	{
		return sender;
	}

	public void setSender(String sender)
	{
		this.sender = sender;
	}

	public String getReason()
	{
		return reason;
	}

	public void setReason(String reason)
	{
		this.reason = reason;
	}

	public long getSendTime()
	{
		return sendTime;
	}

	public void setSendTime(long sendTime)
	{
		this.sendTime = sendTime;
	}

	public ItemStack[] getItems()
	{
		return items;
	}

	public void setItems(ItemStack[] items)
	{
		this.items = items;
	}
	@Override
	public boolean create(Connection conn, String tablename)
	{
		try
		{
			String sql = "INSERT INTO `" + tablename
					+ "`(`player_uuid`, `synchro_key`, `sender_name`,"
					+ " `reason`, `time_stamp`, `items`) " 
					+ "VALUES("
					+ "?, ?, ?, "
					+ "?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, getReceiver().toString());
	        ps.setString(2, getSender());
	        ps.setString(3, getReason());
	        ps.setLong(4, getSendTime());
	        ps.setString(5, MIM.getPlugin().getBase64Provider().toBase64Array(getItems()));
	        
	        int i = ps.executeUpdate();
	        MysqlHandler.addRows(MysqlHandler.QueryType.INSERT, i);
	        return true;
		} catch (SQLException e)
		{
			this.log(Level.WARNING, "SQLException! Could not create a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return false;
	}

	@Override
	public boolean update(Connection conn, String tablename, String whereColumn, Object... whereObject)
	{
		try 
		{
			String sql = "UPDATE `" + tablename
				+ "` SET `player_uuid` = ?, `synchro_key` = ?, `sender_name` = ?,"
				+ " `reason` = ?, `time_stamp` = ?, `items` = ?" 
				+ " WHERE "+whereColumn;
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, getReceiver().toString());
			ps.setString(2, getSynchroKey());
	        ps.setString(3, getSender());
	        ps.setString(4, getReason());
	        ps.setLong(5, getSendTime());
	        ps.setString(6, MIM.getPlugin().getBase64Provider().toBase64Array(getItems()));
			int i = 7;
			for(Object o : whereObject)
			{
				ps.setObject(i, o);
				i++;
			}			
			int u = ps.executeUpdate();
			MysqlHandler.addRows(MysqlHandler.QueryType.UPDATE, u);
			return true;
		} catch (SQLException e)
		{
			this.log(Level.WARNING, "SQLException! Could not update a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return false;
	}

	@Override
	public ArrayList<Object> get(Connection conn, String tablename, String orderby, String limit, String whereColumn, Object... whereObject)
	{
		try
		{
			String sql = "SELECT * FROM `" + tablename
				+ "` WHERE "+whereColumn+" ORDER BY "+orderby+limit;
			PreparedStatement ps = conn.prepareStatement(sql);
			int i = 1;
			for(Object o : whereObject)
			{
				ps.setObject(i, o);
				i++;
			}
			
			ResultSet rs = ps.executeQuery();
			MysqlHandler.addRows(MysqlHandler.QueryType.READ, rs.getMetaData().getColumnCount());
			ArrayList<Object> al = new ArrayList<>();
			while (rs.next()) 
			{
				al.add(new WaitingItems(
						rs.getInt("id"),
						rs.getString("synchro_key"),
						UUID.fromString(rs.getString("player_uuid")),
						rs.getString("sender_name"),
						rs.getString("reason"),
						rs.getLong("time_stamp"),
						rs.getString("items")
						));
			}
			return al;
		} catch (SQLException e)
		{
			this.log(Level.WARNING, "SQLException! Could not get a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return new ArrayList<>();
	}
	
	public static ArrayList<WaitingItems> convert(ArrayList<Object> arrayList)
	{
		ArrayList<WaitingItems> l = new ArrayList<>();
		for(Object o : arrayList)
		{
			if(o instanceof WaitingItems)
			{
				l.add((WaitingItems) o);
			}
		}
		return l;
	}	
}