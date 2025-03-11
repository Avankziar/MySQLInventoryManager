package me.avankziar.mim.general.database;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface DatabaseTable<T>
{
	public String getTableName();
	
	public boolean setupDatabase(DatabaseSetup mysqlSetup);
	
	public boolean create(Connection conn);
	
	public boolean update(Connection conn, String whereColumn, Object... whereObject);
	
	public ArrayList<T> get(Connection conn, String orderby, String limit, String whereColumn, Object... whereObject);
	
	public ServerType getServerType();
	
	default void log(Logger logger, Level level, String log, Exception e)
	{
		logger.log(level, log, e);
	}
}
