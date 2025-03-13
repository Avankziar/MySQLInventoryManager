package me.avankziar.mim.general.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

import me.avankziar.mim.general.database.DatabaseHandler;
import me.avankziar.mim.general.database.DatabaseSetup;
import me.avankziar.mim.general.database.DatabaseTable;
import me.avankziar.mim.general.database.QueryType;
import me.avankziar.mim.general.database.ServerType;

/**
 * Example Object
 * @author User
 *
 */
public class PlayerData implements DatabaseTable<PlayerData>
{
	private int id;
	private UUID uuid;
	private String name;
	private boolean inSync;
	private boolean syncMessage;
	
	public PlayerData(){}
	
	public PlayerData(int id, UUID uuid, String name, boolean inSync, boolean syncMessage)
	{
		setId(id);
		setUUID(uuid);
		setName(name);
		setInSync(inSync);
		setSyncMessage(syncMessage);
	}
	
	public static String databaseType = "";
	
	public ServerType getServerType()
	{
		return ServerType.ALL;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}
	
	public UUID getUUID()
	{
		return uuid;
	}

	public void setUUID(UUID uuid)
	{
		this.uuid = uuid;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	public boolean isInSync()
	{
		return inSync;
	}

	public void setInSync(boolean inSync)
	{
		this.inSync = inSync;
	}

	public boolean isSyncMessage()
	{
		return syncMessage;
	}

	public void setSyncMessage(boolean syncMessage)
	{
		this.syncMessage = syncMessage;
	}

	public String getTableName() 
    {
        return "MySQL".equalsIgnoreCase(databaseType) ? "mimPlayerData" : "public.mim_player_data";
    }
	
	public boolean setupDatabase(DatabaseSetup dbSetup) 
    {
    	databaseType = dbSetup.databaseType;
        String tableName = getTableName();
        StringBuilder sql = new StringBuilder();

        if ("MySQL".equalsIgnoreCase(databaseType)) 
        {
            sql.append("CREATE TABLE IF NOT EXISTS `").append(tableName).append("` (")
               .append("id BIGINT AUTO_INCREMENT PRIMARY KEY, ")
               .append("player_uuid char(36) NOT NULL UNIQUE, ")
               .append("player_name varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL, ")
               .append("in_sync boolean, ")
               .append("sync_message boolean")
               .append(");");
        } 
        else if ("PostgreSQL".equalsIgnoreCase(databaseType)) 
        {
            sql.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (")
               .append("id SERIAL PRIMARY KEY, ")
               .append("player_uuid UUID NOT NULL, ")
               .append("player_name varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL, ")
               .append("in_sync boolean, ")
               .append("sync_message boolean")
               .append(");");
        } 
        else 
        {
            throw new IllegalArgumentException("Unsupported database type: " + databaseType);
        }

        return dbSetup.baseSetup(sql.toString());
    }

	@Override
	public boolean create(Connection conn)
	{
		try
		{
			String sql = "INSERT INTO " + getTableName()
					+ " (player_uuid, player_name, in_sync, sync_message) " 
					+ "VALUES(?, ?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
	        ps.setString(1, getUUID().toString());
	        ps.setString(2, getName());
	        ps.setBoolean(3, isInSync());
	        ps.setBoolean(4, isSyncMessage());
	        
	        int i = ps.executeUpdate();
	        DatabaseHandler.addRows(QueryType.INSERT, i);
	        
	        return true;
		} catch (SQLException e)
		{
			this.log(DatabaseHandler.getLogger(), Level.WARNING, "SQLException! Could not create a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return false;
	}

	@Override
	public boolean update(Connection conn, String whereColumn, Object... whereObject)
	{
		try
		{
			String sql = "UPDATE " + getTableName()
				+ " SET player_uuid = ?, player_name = ?, in_sync = ?, sync_message = ?" 
				+ " WHERE "+whereColumn;
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, getUUID().toString());
			ps.setString(2, getName());
			ps.setBoolean(3, isInSync());
	        ps.setBoolean(4, isSyncMessage());
			
			int i = 5;
			for(Object o : whereObject)
			{
				ps.setObject(i, o);
				i++;
			}
			
			int u = ps.executeUpdate();
			DatabaseHandler.addRows(QueryType.UPDATE, u);
			return true;
		} catch (SQLException e)
		{
			this.log(DatabaseHandler.getLogger(), Level.WARNING, "SQLException! Could not update a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return false;
	}

	@Override
	public ArrayList<PlayerData> get(Connection conn, String orderby, String limit, String whereColumn, Object... whereObject)
	{
		try
		{
			String sql = "SELECT * FROM " + getTableName()
				+ " WHERE " + whereColumn + " ORDER BY " + orderby+limit;
			
			PreparedStatement ps = conn.prepareStatement(sql);
			int i = 1;
			for(Object o : whereObject)
			{
				ps.setObject(i, o);
				i++;
			}
			
			ResultSet rs = ps.executeQuery();
			DatabaseHandler.addRows(QueryType.READ, rs.getMetaData().getColumnCount());
			ArrayList<PlayerData> al = new ArrayList<>();
			while (rs.next()) 
			{
				al.add(new PlayerData(rs.getInt("id"),
						UUID.fromString(rs.getString("player_uuid")),
						rs.getString("player_name"),
						rs.getBoolean("in_sync"),
						rs.getBoolean("sync_message")));
			}
			return al;
		} catch (SQLException e)
		{
			this.log(DatabaseHandler.getLogger(), Level.WARNING, "SQLException! Could not get a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return new ArrayList<>();
	}
}