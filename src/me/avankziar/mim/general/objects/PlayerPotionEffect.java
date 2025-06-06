package me.avankziar.mim.general.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.potion.PotionEffect;

import me.avankziar.mim.general.database.DatabaseHandler;
import me.avankziar.mim.general.database.DatabaseSetup;
import me.avankziar.mim.general.database.DatabaseTable;
import me.avankziar.mim.general.database.QueryType;
import me.avankziar.mim.general.database.ServerType;
import me.avankziar.mim.spigot.handler.Base64Handler;

public class PlayerPotionEffect implements DatabaseTable<PlayerPotionEffect> 
{
    private int id;
    private UUID uuid;
    private long updateTime;
    private PotionEffect[] potionEffect;
    
    public PlayerPotionEffect() {}

    public PlayerPotionEffect(int id, UUID uuid, long updateTime, PotionEffect[] potionEffect) 
    {
        setId(id);
        setUUID(uuid);
        setUpdateTime(updateTime);
        setPotionEffect(potionEffect);
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

    public long getUpdateTime() 
    {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) 
    {
        this.updateTime = updateTime;
    }

    public PotionEffect[] getPotionEffect()
	{
		return potionEffect;
	}

	public void setPotionEffect(PotionEffect[] potionEffect)
	{
		this.potionEffect = potionEffect;
	}

    public String getTableName() 
    {
        return "MySQL".equalsIgnoreCase(databaseType) ? "mimPlayerPotionEffect" : "public.mim_player_potioneffect";
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
               .append("player_uuid CHAR(36) NOT NULL, ")
               .append("upload_time BIGINT, ")
               .append("potion_effect MEDIUMTEXT);");
        } 
        else if ("PostgreSQL".equalsIgnoreCase(databaseType)) 
        {
            sql.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (")
               .append("id SERIAL PRIMARY KEY, ")
               .append("player_uuid UUID NOT NULL, ")
               .append("upload_time BIGINT, ")
               .append("potion_effect MEDIUMTEXT);");
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
                       + " (player_uuid, upload_time, potion_effect) "
                       + "VALUES (?, ?, ?)";
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, getUUID().toString());
            ps.setLong(2, getUpdateTime());
            ps.setString(3, Base64Handler.getBase64PotionEffect(getPotionEffect()));

            int i = ps.executeUpdate();
            DatabaseHandler.addRows(QueryType.INSERT, i);
            return true;
        } 
        catch (SQLException e) 
        {
            this.log(DatabaseHandler.getLogger(), Level.WARNING, "SQLException! Could not create a PlayerInventory Object!", e);
        }
        return false;
    }
    
    @Override
    public boolean update(Connection conn, String whereColumn, Object... whereObject) 
    {
        try 
        {
            String sql = "UPDATE " + getTableName()
                       + " SET player_uuid = ?, upload_time = ?, potion_effect = ?"
                       + " WHERE " + whereColumn;

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, getUUID().toString());
            ps.setLong(2, getUpdateTime());
            ps.setString(3, Base64Handler.getBase64PotionEffect(getPotionEffect()));

            int i = 4;
            for (Object o : whereObject) 
            {
                ps.setObject(i, o);
                i++;
            }

            int updatedRows = ps.executeUpdate();
            DatabaseHandler.addRows(QueryType.UPDATE, updatedRows);
            return updatedRows > 0;
        } 
        catch (SQLException e) 
        {
            this.log(DatabaseHandler.getLogger(), Level.WARNING, 
                     "SQLException! Could not update a PlayerInventory object!", e);
        }
        return false;
    }


    @Override
    public ArrayList<PlayerPotionEffect> get(Connection conn, String orderby, String limit, String whereColumn, Object... whereObject) 
    {
        try 
        {
            String sql = "SELECT * FROM " + getTableName() 
                       + " WHERE " + whereColumn + " ORDER BY " + orderby + limit;
            
            PreparedStatement ps = conn.prepareStatement(sql);
            int i = 1;
            for (Object o : whereObject) 
            {
                ps.setObject(i, o);
                i++;
            }

            ResultSet rs = ps.executeQuery();
            DatabaseHandler.addRows(QueryType.READ, rs.getMetaData().getColumnCount());

            ArrayList<PlayerPotionEffect> al = new ArrayList<>();
            while (rs.next()) 
            {
                al.add(new PlayerPotionEffect(rs.getInt("id"),
                        UUID.fromString(rs.getString("player_uuid")),
                        rs.getLong("upload_time"),
                        Base64Handler.fromBase64PotionEffect(rs.getString("potion_effect"))));
            }
            return al;
        } 
        catch (SQLException e) 
        {
            this.log(DatabaseHandler.getLogger(), Level.WARNING, "SQLException! Could not get PlayerInventory data!", e);
        }
        return new ArrayList<>();
    }
}