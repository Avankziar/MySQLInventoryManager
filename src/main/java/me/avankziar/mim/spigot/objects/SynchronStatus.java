package main.java.me.avankziar.mim.spigot.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

import main.java.me.avankziar.mim.spigot.database.MysqlHandable;
import main.java.me.avankziar.mim.spigot.database.MysqlHandler;

public class SynchronStatus implements MysqlHandable
{
	public enum Type
	{
		LOADING, SAVING, FINISH;
	}
	
	private UUID uuid;
	private SynchronStatus.Type type;
	
	public SynchronStatus(){}
	
	public SynchronStatus(UUID uuid, SynchronStatus.Type type)
	{
		setUuid(uuid);
		setType(type);
	}

	public UUID getUuid()
	{
		return uuid;
	}

	public void setUuid(UUID uuid)
	{
		this.uuid = uuid;
	}

	public SynchronStatus.Type getType()
	{
		return type;
	}

	public void setType(SynchronStatus.Type type)
	{
		this.type = type;
	}
	
	@Override
	public boolean create(Connection conn, String tablename)
	{
		try
		{
			String sql = "INSERT INTO `" + tablename
					+ "`(`player_uuid`, `synchron_status`) " 
					+ "VALUES("
					+ "?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
	        ps.setString(1, getUuid().toString());
	        ps.setString(2, getType().toString());
	        
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
				+ "` SET `player_uuid` = ?, `synchron_status` = ?" 
				+ " WHERE "+whereColumn;
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, getUuid().toString());
	        ps.setString(2, getType().toString());
			int i = 3;
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
				al.add(new SynchronStatus(
						UUID.fromString(rs.getString("player_uuid")),
						SynchronStatus.Type.valueOf(rs.getString("synchron_status"))
						));
			}
			return al;
		} catch (SQLException e)
		{
			this.log(Level.WARNING, "SQLException! Could not get a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return new ArrayList<>();
	}
	
	public static ArrayList<SynchronStatus> convert(ArrayList<Object> arrayList)
	{
		ArrayList<SynchronStatus> l = new ArrayList<>();
		for(Object o : arrayList)
		{
			if(o instanceof SynchronStatus)
			{
				l.add((SynchronStatus) o);
			}
		}
		return l;
	}
}