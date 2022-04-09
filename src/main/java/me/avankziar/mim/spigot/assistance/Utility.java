package main.java.me.avankziar.mim.spigot.assistance;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

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
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.PLAYERDATA, "player_uuid = ?", uuid))
		{
			return ((PlayerData) plugin.getMysqlHandler().getData(MysqlHandler.Type.PLAYERDATA, "player_uuid = ?", uuid)).getName();
		}
		return null;
	}
	
	public static UUID convertNameToUUID(String playername) throws IOException
	{
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.PLAYERDATA, "`player_name` = ?", playername))
		{
			return ((PlayerData) plugin.getMysqlHandler().getData(MysqlHandler.Type.PLAYERDATA, "`player_name` = ?", playername)).getUUID();
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
