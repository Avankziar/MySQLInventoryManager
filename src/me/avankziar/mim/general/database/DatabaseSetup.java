package me.avankziar.mim.general.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import me.avankziar.mim.general.objects.PlayerData;
import me.avankziar.mim.general.objects.PlayerInventory;

public class DatabaseSetup 
{
    @Nullable
    protected static Logger logger;
    protected String host;
    protected int port;
    protected String database;
    protected String user;
    protected String password;
    protected boolean isAutoConnect;
    protected boolean isVerifyServerCertificate;
    protected boolean isSSLEnabled;
    public String databaseType; // "MySQL" oder "PostgreSQL"

    public DatabaseSetup(Logger logger) 
    {
        DatabaseSetup.logger = logger;
    }

    public void init(String databaseType, String host, int port, String database, String user, String password,
                     boolean isAutoConnect, boolean isVerifyServerCertificate, boolean isSSLEnabled) 
    {
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
        this.isAutoConnect = isAutoConnect;
        this.isVerifyServerCertificate = isVerifyServerCertificate;
        this.isSSLEnabled = isSSLEnabled;
        this.databaseType = databaseType;
    }

    public static ArrayList<DatabaseTable<?>> register = new ArrayList<>();

    static 
    {
        register.add(new PlayerData());
        register.add(new PlayerInventory());
    }

    public boolean loadDatabaseSetup(ServerType serverType) 
    {
        if (!connectToDatabase()) 
        {
            return false;
        }
        for (DatabaseTable<?> mh : register) 
        {
            if (mh.getServerType() == ServerType.ALL || serverType == mh.getServerType()) 
            {
                mh.setupDatabase(this);
            }
        }
        return true;
    }

    public boolean connectToDatabase() 
    {
        logger.info("Connecting to the database (" + databaseType + ")...");
        try 
        {
            getConnection();
            logger.info("Database connection successful!");
            return true;
        } 
        catch (Exception e) 
        {
            logger.log(Level.WARNING, "Could not connect to Database!", e);
            return false;
        }
    }

    public Connection getConnection() throws SQLException 
    {
        return reConnect();
    }

    private Connection reConnect() throws SQLException 
    {
        Properties properties = new Properties();
        properties.setProperty("user", user);
        properties.setProperty("password", password);
        properties.setProperty("autoReconnect", String.valueOf(isAutoConnect));
        properties.setProperty("verifyServerCertificate", String.valueOf(isVerifyServerCertificate));
        properties.setProperty("useSSL", String.valueOf(isSSLEnabled));
        properties.setProperty("requireSSL", String.valueOf(isSSLEnabled));

        String url = "";

        if ("MySQL".equalsIgnoreCase(databaseType)) 
        {
            try 
            {
                Class.forName("com.mysql.cj.jdbc.Driver"); // Neuer MySQL-Treiber
            } 
            catch (Exception e) 
            {
                try 
                {
                    Class.forName("com.mysql.jdbc.Driver"); // Alter MySQL-Treiber als Fallback
                } 
                catch (Exception ignored) 
                {
                }
            }
            url = "jdbc:mysql://" + host + ":" + port + "/" + database;
        } 
        else if ("PostgreSQL".equalsIgnoreCase(databaseType)) 
        {
            try 
            {
                Class.forName("org.postgresql.Driver");
            } 
            catch (ClassNotFoundException e) 
            {
                logger.log(Level.SEVERE, "PostgreSQL JDBC Driver not found!", e);
                throw new SQLException("PostgreSQL Driver not found");
            }
            url = "jdbc:postgresql://" + host + ":" + port + "/" + database;
        } 
        else 
        {
            throw new SQLException("Unsupported database type: " + databaseType);
        }

        return DriverManager.getConnection(url, properties);
    }

    public boolean baseSetup(String data) 
    {
        try (Connection conn = getConnection(); PreparedStatement query = conn.prepareStatement(data)) 
        {
            query.execute();
        } 
        catch (SQLException e) 
        {
            logger.log(Level.WARNING, "Could not build data source. Or connection is null", e);
        }
        return true;
    }
}
