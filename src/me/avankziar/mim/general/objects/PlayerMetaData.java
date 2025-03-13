package me.avankziar.mim.general.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.GameMode;

import me.avankziar.mim.general.database.DatabaseHandler;
import me.avankziar.mim.general.database.DatabaseSetup;
import me.avankziar.mim.general.database.DatabaseTable;
import me.avankziar.mim.general.database.QueryType;
import me.avankziar.mim.general.database.ServerType;

public class PlayerMetaData implements DatabaseTable<PlayerMetaData>
{
	private int id;
	private UUID uuid;
	private long updateTime;
	
	private GameMode gameMode;
	
	private double maxHealth;
	private double health;
	private int remainingAir;
	private int fireTicks;
	private int freezeTicks;
	
	private float exp;
	private int expLevel;
	
	private float exhaustion;
	private int foodLevel;
	private float saturation;
	
	public PlayerMetaData(){}
	
	public PlayerMetaData(int id, UUID uuid, long updateTime,
			GameMode gameMode,
			double maxHealth, double health, int remainingAir, int fireTicks, int freezeTicks,
			float exp, int expLevel,
			float exhaustion, int foodLevel, float saturation)
	{
		setId(id);
		setUUID(uuid);
		setUpdateTime(updateTime);
		setGameMode(gameMode);
		setMaxHealth(maxHealth);
		setHealth(health);
		setRemainingAir(remainingAir);
		setFireTicks(fireTicks);
		setFreezeTicks(freezeTicks);
		setExp(exp);
		setExpLevel(expLevel);
		setExhaustion(exhaustion);
		setFoodLevel(foodLevel);
		setSaturation(saturation);
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

	public GameMode getGameMode()
	{
		return gameMode;
	}

	public void setGameMode(GameMode gameMode)
	{
		this.gameMode = gameMode;
	}

	public double getMaxHealth()
	{
		return maxHealth;
	}

	public void setMaxHealth(double maxHealth)
	{
		this.maxHealth = maxHealth;
	}

	public double getHealth()
	{
		return health;
	}

	public void setHealth(double health)
	{
		this.health = health;
	}

	public int getRemainingAir()
	{
		return remainingAir;
	}

	public void setRemainingAir(int remainingAir)
	{
		this.remainingAir = remainingAir;
	}

	public int getFireTicks()
	{
		return fireTicks;
	}

	public void setFireTicks(int fireTicks)
	{
		this.fireTicks = fireTicks;
	}

	public int getFreezeTicks()
	{
		return freezeTicks;
	}

	public void setFreezeTicks(int freezeTicks)
	{
		this.freezeTicks = freezeTicks;
	}

	public float getExp()
	{
		return exp;
	}

	public void setExp(float exp)
	{
		this.exp = exp;
	}

	public int getExpLevel()
	{
		return expLevel;
	}

	public void setExpLevel(int expLevel)
	{
		this.expLevel = expLevel;
	}

	public float getExhaustion()
	{
		return exhaustion;
	}

	public void setExhaustion(float exhaustion)
	{
		this.exhaustion = exhaustion;
	}

	public int getFoodLevel()
	{
		return foodLevel;
	}

	public void setFoodLevel(int foodLevel)
	{
		this.foodLevel = foodLevel;
	}

	public float getSaturation()
	{
		return saturation;
	}

	public void setSaturation(float saturation)
	{
		this.saturation = saturation;
	}

	public String getTableName() 
    {
        return "MySQL".equalsIgnoreCase(databaseType) ? "mimPlayerMetaData" : "public.mim_player_meta_data";
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
               .append("player_uuid char(36) NOT NULL, ")
               .append("upload_time BIGINT, ")
               .append("game_mode tinytext, ")
               .append("max_health double, ")
               .append("health double, ")
               .append("remaining_air int, ")
               .append("fire_ticks int, ")
               .append("freeze_ticks int, ")
               .append("exp float, ")
               .append("exp_level int, ")
               .append("exhaustion float, ")
               .append("food_level int, ")
               .append("saturation float")
               .append(");");
        } 
        else if ("PostgreSQL".equalsIgnoreCase(databaseType)) 
        {
            sql.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (")
               .append("id SERIAL PRIMARY KEY, ")
               .append("player_uuid UUID NOT NULL, ")
               .append("upload_time BIGINT, ")
               .append("game_mode tinytext, ")
               .append("max_health double, ")
               .append("health double, ")
               .append("remaining_air int, ")
               .append("fire_ticks int, ")
               .append("freeze_ticks int, ")
               .append("exp float, ")
               .append("exp_level int, ")
               .append("exhaustion float, ")
               .append("food_level int, ")
               .append("saturation float")
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
					+ " (player_uuid, upload_time, game_mode, "
					+ "max_health, health, remaining_air, fire_ticks, freeze_ticks, "
					+ "exp, exp_level, "
					+ "exhaustion, food_level, saturation) " 
					+ "VALUES(?, ?, ?, "
					+ "?, ?, ?, ?, ?, "
					+ "?, ?, "
					+ "?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
	        ps.setString(1, getUUID().toString());
	        ps.setLong(2, getUpdateTime());
	        ps.setString(3, getGameMode().toString());
	        ps.setDouble(4, getMaxHealth());
	        ps.setDouble(5, getHealth());
	        ps.setInt(6, getRemainingAir());
	        ps.setInt(7, getFireTicks());
	        ps.setInt(8, getFreezeTicks());
	        ps.setFloat(9, getExp());
	        ps.setInt(10, getExpLevel());
	        ps.setFloat(11, getExhaustion());
	        ps.setInt(12, getFoodLevel());
	        ps.setFloat(13, getSaturation());
	        
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
				+ " SET player_uuid = ?, upload_time = ?, game_mode = ?,"
				+ " max_health = ?, health = ?, remaining_air = ?, fire_ticks = ?, freeze_ticks = ?,"
				+ " exp = ?, exp_level = ?,"
				+ " exhaustion = ?, food_level = ?, saturation = ?" 
				+ " WHERE "+whereColumn;
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, getUUID().toString());
	        ps.setLong(2, getUpdateTime());
	        ps.setString(3, getGameMode().toString());
	        ps.setDouble(4, getMaxHealth());
	        ps.setDouble(5, getHealth());
	        ps.setInt(6, getRemainingAir());
	        ps.setInt(7, getFireTicks());
	        ps.setInt(8, getFreezeTicks());
	        ps.setFloat(9, getExp());
	        ps.setInt(10, getExpLevel());
	        ps.setFloat(11, getExhaustion());
	        ps.setInt(12, getFoodLevel());
	        ps.setFloat(13, getSaturation());
			
			int i = 14;
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
	public ArrayList<PlayerMetaData> get(Connection conn, String orderby, String limit, String whereColumn, Object... whereObject)
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
			ArrayList<PlayerMetaData> al = new ArrayList<>();
			while (rs.next()) 
			{
				al.add(new PlayerMetaData(rs.getInt("id"),
						UUID.fromString(rs.getString("player_uuid")),
						rs.getLong("upload_time"),
						GameMode.valueOf(rs.getString("game_mode")),
						rs.getDouble("max_health"),
						rs.getDouble("health"),
						rs.getInt("remaining_air"),
						rs.getInt("fire_ticks"),
						rs.getInt("freeze_ticks"),
						rs.getFloat("exp"),
						rs.getInt("exp_level"),
						rs.getFloat("exhaustion"),
						rs.getInt("food_level"),
						rs.getFloat("saturation")));
			}
			return al;
		} catch (SQLException e)
		{
			this.log(DatabaseHandler.getLogger(), Level.WARNING, "SQLException! Could not get a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return new ArrayList<>();
	}
}