package me.avankziar.mim.spigot.database;

import me.avankziar.mim.general.database.DatabaseHandler;
import me.avankziar.mim.spigot.MIM;

public class SQLHandler extends DatabaseHandler
{	
	public SQLHandler(MIM plugin, String databaseType)
	{
		super(plugin.getLogger(), plugin.getMysqlSetup(), databaseType);
	}
}
