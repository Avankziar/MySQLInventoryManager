package main.java.me.avankziar.mim.spigot.assistance;

import java.io.IOException;
import java.lang.reflect.Method;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.database.MysqlHandler;
import main.java.me.avankziar.mim.spigot.objects.PlayerData;

public class Utility
{
	private static MIM plugin;
	
	public Utility(MIM plugin)
	{
		Utility.plugin = plugin;
	}
	
	public static String convertUUIDToName(String uuid) throws IOException
	{
		String name = null;
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.PLAYERDATA, "player_uuid = ?", uuid))
		{
			name = ((PlayerData) plugin.getMysqlHandler().getData(MysqlHandler.Type.PLAYERDATA, "player_uuid = ?", uuid)).getName();
			return name;
		}
		return null;
	}
	
	public static String convertNameToUUID(String playername) throws IOException
	{
		String uuid = "";
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.PLAYERDATA, "`player_name` = ?", playername))
		{
			uuid = ((PlayerData) plugin.getMysqlHandler().getData(MysqlHandler.Type.PLAYERDATA, "`player_name` = ?", playername)).getUUID();
			return uuid;
		}
		return null;
	}
	
	public boolean existMethod(Class<?> externclass, String method)
	{
	    try 
	    {
	    	Method[] mtds = externclass.getMethods();
	    	for(Method methods : mtds)
	    	{
	    		if(methods.getName().equalsIgnoreCase(method))
	    		{
	    	    	return true;
	    		}
	    	}
	    	return false;
	    } catch (Exception e) 
	    {
	    	return false;
	    }
	}
}
