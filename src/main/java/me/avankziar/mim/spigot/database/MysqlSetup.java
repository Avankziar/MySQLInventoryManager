package main.java.me.avankziar.mim.spigot.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

import main.java.me.avankziar.mim.spigot.MIM;

public class MysqlSetup 
{
	final private String host;
	final private int port;
	final private String database;
	final private String user;
	final private String password;
	final private boolean isAutoConnect;
	final private boolean isVerifyServerCertificate;
	final private boolean isSSLEnabled;
	
	public MysqlSetup(MIM plugin, boolean adm, String path)
	{
		if(adm)
		{
			MIM.log.log(Level.INFO, "Using IFH Administration");
		}		
		host = adm ? plugin.getAdministration().getHost(path)
				: plugin.getYamlHandler().getConfig().getString("Mysql.Host");
		port = adm ? plugin.getAdministration().getPort(path)
				: plugin.getYamlHandler().getConfig().getInt("Mysql.Port", 3306);
		database = adm ? plugin.getAdministration().getDatabase(path)
				: plugin.getYamlHandler().getConfig().getString("Mysql.DatabaseName");
		user = adm ? plugin.getAdministration().getUsername(path)
				: plugin.getYamlHandler().getConfig().getString("Mysql.User");
		password = adm ? plugin.getAdministration().getPassword(path)
				: plugin.getYamlHandler().getConfig().getString("Mysql.Password");
		isAutoConnect = adm ? plugin.getAdministration().isAutoReconnect(path)
				: plugin.getYamlHandler().getConfig().getBoolean("Mysql.AutoReconnect", true);
		isVerifyServerCertificate = adm ? plugin.getAdministration().isVerifyServerCertificate(path)
				: plugin.getYamlHandler().getConfig().getBoolean("Mysql.VerifyServerCertificate", false);
		isSSLEnabled = adm ? plugin.getAdministration().useSSL(path)
				: plugin.getYamlHandler().getConfig().getBoolean("Mysql.SSLEnabled", false);
		loadMysqlSetup();
	}
	
	public boolean connectToDatabase() 
	{
		MIM.log.info("Connecting to the database...");
		Connection conn = getConnection();
		if(conn != null)
		{
			MIM.log.info("Database connection successful!");
		} else
		{
			return false;
		}
		return true;
	}
	
	public Connection getConnection()
	{
		return reConnect();
	}
	
	private Connection reConnect() 
	{
		boolean bool = false;
	    try
	    {
	    	// Load new Drivers for papermc
	    	Class.forName("com.mysql.cj.jdbc.Driver");
	    	bool = true;
	    } catch (Exception e)
	    {
	    	bool = false;
	    } 
	    try
	    {
	    	if (bool == false)
	    	{
	    		// Load old Drivers for spigot
	    		Class.forName("com.mysql.jdbc.Driver");
	    	}
            Properties properties = new Properties();
            properties.setProperty("user", user);
            properties.setProperty("password", password);
            properties.setProperty("autoReconnect", String.valueOf(isAutoConnect));
            properties.setProperty("verifyServerCertificate", String.valueOf(isVerifyServerCertificate));
            properties.setProperty("useSSL", String.valueOf(isSSLEnabled));
            properties.setProperty("requireSSL", String.valueOf(isSSLEnabled));
            //Connect to database
            Connection conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, properties);
            return conn;
		} catch (Exception e) 
		{
			MIM.log.severe("Error (re-)connecting to the database! Error: " + e.getMessage());
			return null;
		}
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
		if(!setupDatabaseII())
		{
			return false;
		}
		if(!setupDatabaseIII())
		{
			return false;
		}
		if(!setupDatabaseIV())
		{
			return false;
		}
		if(!setupDatabaseV())
		{
			return false;
		}
		if(!setupDatabaseVI())
		{
			return false;
		}
		return true;
	}
	
	public boolean setupDatabaseI() 
	{
		  String data = "CREATE TABLE IF NOT EXISTS `" + MysqlHandler.Type.PLAYERDATA.getValue()
		  		+ "` (id int AUTO_INCREMENT PRIMARY KEY,"
		  		+ " player_uuid char(36) NOT NULL,"
		  		+ " player_name varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,"
		  		+ " synchro_key text,"
		  		
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
		  		+ " total_experience int,"
		  		+ " walk_speed float,"
		  		+ " fly_speed float,"
		  		+ " fire_ticks int,"
		  		+ " freeze_ticks int,"
		  		+ " flying boolean,"
		  		+ " glowing boolean,"
		  		+ " gravity boolean,"
		  		+ " invisible boolean,"
		  		+ " invulnerable boolean,"
		  		+ " potion_effects LONGTEXT,"
		  		+ " entity_category text,"
		  		+ " arrows_in_body int,"
		  		+ " maximum_air int,"
		  		+ " remaining_air int,"
		  		+ " custom_name text,"
		  		+ " persistent_data LONGTEXT,"
		  		+ " clear_toggle boolean,"
		  		+ " last_game_mode text);";
		baseSetup(data);
		return true;
	}
	
	public boolean setupDatabaseII() 
	{
		  String data = "CREATE TABLE IF NOT EXISTS `" + MysqlHandler.Type.DEATHMEMORYSTATE.getValue()
		  		+ "` (id int AUTO_INCREMENT PRIMARY KEY,"
		  		+ " time_stamp BIGINT,"
		  		+ " player_uuid char(36) NOT NULL,"
		  		+ " synchro_key text,"
		  		
		  		+ " inventory_content LONGTEXT,"
		  		+ " armor_content LONGTEXT,"
		  		+ " off_hand MEDIUMTEXT,"
		  		
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
		  		+ " walk_speed float,"
		  		+ " fly_speed float,"
		  		+ " fire_ticks int,"
		  		+ " freeze_ticks int,"
		  		+ " flying boolean,"
		  		+ " glowing boolean,"
		  		+ " gravity boolean,"
		  		+ " invisible boolean,"
		  		+ " invulnerable boolean,"
		  		+ " potion_effects LONGTEXT,"
		  		+ " entity_category text,"
		  		+ " arrows_in_body int,"
		  		+ " maximum_air int,"
		  		+ " remaining_air int,"
		  		+ " custom_name text,"
		  		+ " persistent_data LONGTEXT);";
		baseSetup(data);
		return true;
	}
	
	public boolean setupDatabaseIII() 
	{
		  String data = "CREATE TABLE IF NOT EXISTS `" + MysqlHandler.Type.PREDEFINEPLAYERSTATE.getValue()
		  		+ "` (id int AUTO_INCREMENT PRIMARY KEY,"
		  		+ " state_name text,"
		  		+ " synchro_key text,"
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
		  		+ " persistent_data LONGTEXT);";
		baseSetup(data);
		return true;
	}
	
	public boolean setupDatabaseIV() 
	{
		  String data = "CREATE TABLE IF NOT EXISTS `" + MysqlHandler.Type.CUSTOMPLAYERINVENTORY.getValue()
		  		+ "` (id int AUTO_INCREMENT PRIMARY KEY,"
		  		+ " cpi_name text,"
		  		+ " owner_uuid text,"
		  		+ " actual_row_amount int,"
		  		+ " target_row_amount int,"
		  		+ " maxbuyed_row_amount int,"
		  		+ " inventory_content LONGTEXT);";
		baseSetup(data);
		return true;
	}
	
	public boolean setupDatabaseV() 
	{
		  String data = "CREATE TABLE IF NOT EXISTS `" + MysqlHandler.Type.SYNCHRONSTATUS.getValue()
		  		+ "` (id int AUTO_INCREMENT PRIMARY KEY,"
		  		+ " player_uuid char(36) NOT NULL,"
		  		+ " synchron_status text);";
		baseSetup(data);
		return true;
	}
	
	public boolean setupDatabaseVI() 
	{
		  String data = "CREATE TABLE IF NOT EXISTS `" + MysqlHandler.Type.WAITINGITEMS.getValue()
		  		+ "` (id int AUTO_INCREMENT PRIMARY KEY,"
		  		+ " player_uuid char(36) NOT NULL,"
		  		+ " synchro_key text,"
		  		+ " sender_name text,"
		  		+ " reason text,"
		  		+ " time_stamp BIGINT,"		  		
		  		+ " items LONGTEXT);";
		baseSetup(data);
		return true;
	}
}