package me.avankziar.mim.general.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;

public class DatabaseHandler 
{
    public enum DatabaseType
    {
        MYSQL, POSTGRESQL
    }

    public static long startRecordTime = System.currentTimeMillis();
    public static int inserts = 0;
    public static int updates = 0;
    public static int deletes = 0;
    public static int reads = 0;

    public static void addRows(QueryType type, int amount) 
    {
        switch (type) 
        {
            case DELETE:
                deletes += amount;
                break;
            case INSERT:
                inserts += amount;
                break;
            case READ:
                reads += amount;
                break;
            case UPDATE:
                updates += amount;
                break;
        }
    }

    public static void resetsRows() 
    {
        inserts = 0;
        updates = 0;
        reads = 0;
        deletes = 0;
    }

    @Nullable
    private static Logger logger;
    private DatabaseSetup databaseSetup;
    private DatabaseType dbType;

    public DatabaseHandler(Logger logger, DatabaseSetup databaseSetup, String dbType) 
    {
        DatabaseHandler.logger = logger;
        this.databaseSetup = databaseSetup;
        this.dbType = dbType.equalsIgnoreCase("PostgreSQL") ? DatabaseType.POSTGRESQL : DatabaseType.MYSQL;
    }

    @Nullable
    public static Logger getLogger() 
    {
        return DatabaseHandler.logger;
    }

    private String wrapColumnName(String columnName) 
    {
    	if(dbType == DatabaseType.POSTGRESQL)
    	{
    		return columnName.replace("`", "\"");
    	}
        return columnName;
    }

    private String wrapTableName(String tableName) 
    {
        return dbType == DatabaseType.POSTGRESQL ? "\"" + tableName + "\"" : "`" + tableName + "`";
    }

    private PreparedStatement getPreparedStatement(Connection conn, String sql, int count, Object... whereObject)
            throws SQLException 
    {
        PreparedStatement ps = conn.prepareStatement(sql);
        int i = count;
        for (Object o : whereObject) 
        {
            ps.setObject(i, o);
            i++;
        }
        return ps;
    }

    public <T extends DatabaseTable<T>> boolean exist(T t, String whereColumn, Object... whereObject) 
    {
        try (Connection conn = databaseSetup.getConnection()) 
        {
            PreparedStatement ps = getPreparedStatement(conn,
                    "SELECT id FROM " + wrapTableName(t.getTableName()) + " WHERE " + wrapColumnName(whereColumn) + " LIMIT 1",
                    1, whereObject);
            ResultSet rs = ps.executeQuery();
            DatabaseHandler.addRows(QueryType.READ, rs.getMetaData().getColumnCount());
            while (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            t.log(logger, Level.WARNING, "Could not check " + t.getClass().getName() + " if it exists!", e);
        }
        return false;
    }

    public <T extends DatabaseTable<T>> boolean create(T t) 
    {
        try (Connection conn = databaseSetup.getConnection()) {
            t.create(conn);
            return true;
        } catch (Exception e) {
            t.log(logger, Level.WARNING, "Could not create " + t.getClass().getName() + "!", e);
        }
        return false;
    }

    public <T extends DatabaseTable<T>> boolean updateData(T t, String whereColumn, Object... whereObject) 
    {
        try (Connection conn = databaseSetup.getConnection()) 
        {
            t.update(conn, whereColumn, whereObject);
            return true;
        } catch (Exception e) {
            t.log(logger, Level.WARNING, "Could not update " + t.getClass().getName() + "!", e);
        }
        return false;
    }

    public <T extends DatabaseTable<T>> int deleteData(T t, String whereColumn, Object... whereObject) 
    {
        try (Connection conn = databaseSetup.getConnection()) 
        {
            PreparedStatement ps = getPreparedStatement(conn,
                    "DELETE FROM " + wrapTableName(t.getTableName()) + " WHERE " + wrapColumnName(whereColumn),
                    1, whereObject);
            int d = ps.executeUpdate();
            DatabaseHandler.addRows(QueryType.DELETE, d);
            return d;
        } catch (SQLException e) {
            t.log(logger, Level.WARNING, "Could not delete " + t.getClass().getName() + "!", e);
        }
        return 0;
    }

    public <T extends DatabaseTable<T>> int lastID(T t) 
    {
        try (Connection conn = databaseSetup.getConnection()) 
        {
            String sql = dbType == DatabaseType.POSTGRESQL
                    ? "SELECT id FROM " + wrapTableName(t.getTableName()) + " ORDER BY id DESC LIMIT 1"
                    : "SELECT `id` FROM `" + t.getTableName() + "` ORDER BY `id` DESC LIMIT 1";
            PreparedStatement ps = getPreparedStatement(conn, sql, 1);
            ResultSet rs = ps.executeQuery();
            DatabaseHandler.addRows(QueryType.READ, rs.getMetaData().getColumnCount());
            while (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            t.log(logger, Level.WARNING, "Could not get last ID from " + t.getClass().getName() + "!", e);
        }
        return 0;
    }
    
    public <T extends DatabaseTable<T>> int lastID(T t, String orderBy, String whereColumn, Object...whereObject) 
    {
        try (Connection conn = databaseSetup.getConnection()) 
        {
            String sql = "SELECT " + wrapColumnName("`id`") + " FROM " + wrapTableName(t.getTableName()) 
                    + " WHERE " + wrapColumnName(whereColumn) + " ORDER BY "+wrapColumnName(orderBy)+" LIMIT 1";
            PreparedStatement ps = getPreparedStatement(conn, sql, 1, whereObject);
            ResultSet rs = ps.executeQuery();
            DatabaseHandler.addRows(QueryType.READ, rs.getMetaData().getColumnCount());
            while (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            t.log(logger, Level.WARNING, "Could not get last ID from " + t.getClass().getName() + "!", e);
        }
        return 0;
    }

    public <T extends DatabaseTable<T>> int getCount(T t, String whereColumn, Object... whereObject) 
    {
        try (Connection conn = databaseSetup.getConnection()) 
        {
            PreparedStatement ps = getPreparedStatement(conn,
                    "SELECT count(*) FROM " + wrapTableName(t.getTableName()) + " WHERE " + wrapColumnName(whereColumn),
                    1, whereObject);
            ResultSet rs = ps.executeQuery();
            DatabaseHandler.addRows(QueryType.READ, rs.getMetaData().getColumnCount());
            while (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            t.log(logger, Level.WARNING, "Could not count " + t.getClass().getName() + "!", e);
        }
        return 0;
    }
    
    public <T extends DatabaseTable<T>> T getData(T t, String orderBy, String whereColumn, Object... whereObject)
    {
        try (Connection conn = databaseSetup.getConnection())
        {
            ArrayList<T> list = t.get(conn, orderBy, " LIMIT 1", whereColumn, whereObject);
            if (!list.isEmpty())
            {
                return list.get(0);
            }
        }
        catch (Exception e)
        {
            t.log(logger, Level.WARNING, "Could not retrieve data for " + t.getClass().getName(), e);
        }
        return null;
    }
    
    public <T extends DatabaseTable<T>> ArrayList<T> getList(T t, String databaseType, String orderByColumn, int start, int quantity, String whereColumn, Object... whereObject)
    {
        try (Connection conn = databaseSetup.getConnection())
        {
            ArrayList<T> list = t.get(conn, orderByColumn, " LIMIT " + start + ", " + quantity, whereColumn, whereObject);
            if (!list.isEmpty())
            {
                return list;
            }
        }
        catch (Exception e)
        {
            t.log(logger, Level.WARNING, "Could not retrieve list for " + t.getClass().getName(), e);
        }
        return new ArrayList<>();
    }
    
    public <T extends DatabaseTable<T>> ArrayList<T> getFullList(T t, String databaseType, String orderByColumn, String whereColumn, Object... whereObject)
    {
        try (Connection conn = databaseSetup.getConnection())
        {
            ArrayList<T> list = t.get(conn, orderByColumn, " ", whereColumn, whereObject);
            if (!list.isEmpty())
            {
                return list;
            }
        }
        catch (Exception e)
        {
            t.log(logger, Level.WARNING, "Could not retrieve full list for " + t.getClass().getName(), e);
        }
        return new ArrayList<>();
    }


}
