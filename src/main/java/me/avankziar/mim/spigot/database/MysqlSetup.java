package main.java.me.avankziar.mim.spigot.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import main.java.me.avankziar.mim.spigot.MIM;

public class MysqlSetup 
{
	private MIM plugin;
	private static HikariDataSource dataSource;
	
	public MysqlSetup(MIM plugin)
	{
		this.plugin = plugin;
		loadMysqlSetup();
	}
	
	public boolean loadMysqlSetup()
	{
		if(!connectToDatabase())
		{
			return false;
		}
		if(!setupDatabaseI())
		{
			return false;
		}
		return true;
	}
	
	public boolean connectToDatabase() 
	{
		MIM.log.info("Connecting to the database...");
		boolean bool = false;
		Properties props = new Properties();
	    try
	    {
	    	// Load new Drivers for papermc
	    	props.setProperty("dataSourceClassName", "com.mysql.cj.jdbc.Driver");
	    	bool = true;
	    } catch (Exception e)
	    {
	    	bool = false;
	    }
	    if (bool == false)
		{
			// Load old Drivers for spigot
			props.setProperty("dataSourceClassName", "com.mysql.jdbc.Driver");
		}
		props.setProperty("dataSource.serverName", plugin.getYamlHandler().getConfig().getString("Mysql.Host"));
		props.setProperty("dataSource.portNumber", String.valueOf(plugin.getYamlHandler().getConfig().getInt("Mysql.Port", 3306)));
		props.setProperty("dataSource.user", plugin.getYamlHandler().getConfig().getString("Mysql.User"));
		props.setProperty("dataSource.password", plugin.getYamlHandler().getConfig().getString("Mysql.Password"));
		props.setProperty("dataSource.databaseName", plugin.getYamlHandler().getConfig().getString("Mysql.DatabaseName"));

		HikariConfig config = new HikariConfig(props);

		config.setMaximumPoolSize(10);
		dataSource = new HikariDataSource(config);
		MIM.log.info("Database connection successful!");
		return true;
	}
	
	public boolean setupDatabaseI() 
	{
		  String data = "CREATE TABLE IF NOT EXISTS `" + MysqlHandler.Type.PLAYERDATA.getValue()
		  		+ "` (id int AUTO_INCREMENT PRIMARY KEY,"
		  		+ " player_uuid char(36) NOT NULL,"
		  		+ " player_name varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,"
		  		+ " synchro_key text,"
		  		+ " game_mode text,"
		  		
		  		+ " inventory_content LONGTEXT,"
		  		+ " armor_content LONGTEXT,"
		  		+ " off_hand MEDIUMTEXT,"
		  		+ " enderchest_content LONGTEXT,"
		  		
		  		+ " food_level int,"
		  		+ " saturation float,"
		  		+ " saturated_regen_rate int,"
		  		+ " unsaturated_regen_rate int,"
		  		+ " starvation_rate int,"
		  		+ " exhaustion float,"
		  		+ " attributes LONGTEXT,"
		  		+ " health double,"
		  		+ " absorption_amount double,"
		  		+ " exp_towards_next_level float,"
		  		+ " exp_level int,"
		  		+ " total_experience int"
		  		+ " walk_speed float,"
		  		+ " fly_speed float,"
		  		+ " fire_ticks int,"
		  		+ " freeze_ticks int,"
		  		+ " glowing boolean,"
		  		+ " gravity boolean,"
		  		+ " potion_effects LONGTEXT,"
		  		+ " entity_category text,"
		  		+ " arrows_in_body int,"
		  		+ " maximum_air int,"
		  		+ " remaining_air int,"
		  		+ " custom_name text,"
		  		+ " persistent_data LONGTEXT,"
		  		+ " clear_toggle boolean);";
		baseSetup(data);
		return true;
	}
	
	private boolean baseSetup(String data) 
	{
		try (Connection conn = getConnection(); PreparedStatement query = conn.prepareStatement(data))
		{
			query.execute();
		} catch (SQLException e) 
		{
			MIM.log.log(Level.WARNING, "Could not build data source. Or connection is null", e);
		}
		return true;
	}
	
	public Connection getConnection() throws SQLException 
	{
		return dataSource.getConnection();
	}
}