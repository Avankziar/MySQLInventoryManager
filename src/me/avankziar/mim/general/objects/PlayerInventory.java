package me.avankziar.mim.general.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.inventory.ItemStack;

import me.avankziar.mim.general.database.DatabaseHandler;
import me.avankziar.mim.general.database.DatabaseSetup;
import me.avankziar.mim.general.database.DatabaseTable;
import me.avankziar.mim.general.database.QueryType;
import me.avankziar.mim.general.database.ServerType;
import me.avankziar.mim.spigot.handler.Base64Handler;

public class PlayerInventory implements DatabaseTable<PlayerInventory> 
{
    private int id;
    private UUID uuid;
    private long updateTime;
    private ItemStack[] inventory;
    private ItemStack offHand;
    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack legging;
    private ItemStack boots;

    public PlayerInventory() {}

    public PlayerInventory(int id, UUID uuid, long updateTime, ItemStack[] inventory,
                           ItemStack offHand, ItemStack helmet, ItemStack chestplate, ItemStack legging,
                           ItemStack boots) 
    {
        setId(id);
        setUUID(uuid);
        setUpdateTime(updateTime);
        setInventory(inventory);
        setOffHand(offHand);
        setHelmet(helmet);
        setChestplate(chestplate);
        setLegging(legging);
        setBoots(boots);
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

    public ItemStack[] getInventory() 
    {
        return inventory;
    }

    public void setInventory(ItemStack[] inventory) 
    {
        this.inventory = inventory;
    }

    public ItemStack getOffHand() 
    {
        return offHand;
    }

    public void setOffHand(ItemStack offHand) 
    {
        this.offHand = offHand;
    }

    public ItemStack getHelmet() 
    {
        return helmet;
    }

    public void setHelmet(ItemStack helmet) 
    {
        this.helmet = helmet;
    }

    public ItemStack getChestplate() 
    {
        return chestplate;
    }

    public void setChestplate(ItemStack chestplate) 
    {
        this.chestplate = chestplate;
    }

    public ItemStack getLegging() 
    {
        return legging;
    }

    public void setLegging(ItemStack legging) 
    {
        this.legging = legging;
    }

    public ItemStack getBoots() 
    {
        return boots;
    }

    public void setBoots(ItemStack boots) 
    {
        this.boots = boots;
    }

    public String getTableName() 
    {
        return "MySQL".equalsIgnoreCase(databaseType) ? "mimPlayerInventory" : "public.mim_player_inventory";
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
               .append("inventory MEDIUMTEXT, ")
               .append("off_hand TEXT, ")
               .append("helmet TEXT, ")
               .append("chestplate TEXT, ")
               .append("legging TEXT, ")
               .append("boots TEXT);");
        } 
        else if ("PostgreSQL".equalsIgnoreCase(databaseType)) 
        {
            sql.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (")
               .append("id SERIAL PRIMARY KEY, ")
               .append("player_uuid UUID NOT NULL, ")
               .append("upload_time BIGINT, ")
               .append("inventory TEXT, ")
               .append("off_hand TEXT, ")
               .append("helmet TEXT, ")
               .append("chestplate TEXT, ")
               .append("legging TEXT, ")
               .append("boots TEXT);");
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
                       + " (player_uuid, upload_time, inventory, off_hand, helmet, chestplate, legging, boots) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, getUUID().toString());
            ps.setLong(2, getUpdateTime());
            ps.setString(3, Base64Handler.getBase64Inventory(getInventory()));
            ps.setString(4, Base64Handler.getBase64Item(getOffHand()));
            ps.setString(5, Base64Handler.getBase64Item(getHelmet()));
            ps.setString(6, Base64Handler.getBase64Item(getChestplate()));
            ps.setString(7, Base64Handler.getBase64Item(getLegging()));
            ps.setString(8, Base64Handler.getBase64Item(getBoots()));

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
                       + " SET player_uuid = ?, upload_time = ?, inventory = ?,"
                       + " off_hand = ?, helmet = ?, chestplate = ?, legging = ?, boots = ?"
                       + " WHERE " + whereColumn;

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, getUUID().toString());
            ps.setLong(2, getUpdateTime());
            ps.setString(3, Base64Handler.getBase64Inventory(getInventory()));
            ps.setString(4, Base64Handler.getBase64Item(getOffHand()));
            ps.setString(5, Base64Handler.getBase64Item(getHelmet()));
            ps.setString(6, Base64Handler.getBase64Item(getChestplate()));
            ps.setString(7, Base64Handler.getBase64Item(getLegging()));
            ps.setString(8, Base64Handler.getBase64Item(getBoots()));

            int i = 9;
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
    public ArrayList<PlayerInventory> get(Connection conn, String orderby, String limit, String whereColumn, Object... whereObject) 
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

            ArrayList<PlayerInventory> al = new ArrayList<>();
            while (rs.next()) 
            {
                al.add(new PlayerInventory(rs.getInt("id"),
                        UUID.fromString(rs.getString("player_uuid")),
                        rs.getLong("upload_time"),
                        Base64Handler.fromBase64Inventory(rs.getString("inventory")),
                        Base64Handler.fromBase64Item(rs.getString("off_hand")),
                        Base64Handler.fromBase64Item(rs.getString("helmet")),
                        Base64Handler.fromBase64Item(rs.getString("chestplate")),
                        Base64Handler.fromBase64Item(rs.getString("legging")),
                        Base64Handler.fromBase64Item(rs.getString("boots"))));
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