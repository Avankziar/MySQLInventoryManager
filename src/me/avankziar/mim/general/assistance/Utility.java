package me.avankziar.mim.general.assistance;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import javax.annotation.Nullable;

import me.avankziar.mim.general.database.DatabaseHandler;
import me.avankziar.mim.general.objects.PlayerData;

public class Utility
{
	@Nullable
	private static DatabaseHandler mysqlBaseHandler;
	
	public Utility(DatabaseHandler mysqlBaseHandler)
	{
		Utility.mysqlBaseHandler = mysqlBaseHandler;
	}
	
	public static double getNumberFormat(double d)
	{
		BigDecimal bd = new BigDecimal(d).setScale(1, RoundingMode.HALF_UP);
		double newd = bd.doubleValue();
		return newd;
	}
	
	public static double getNumberFormat(double d, int scale)
	{
		BigDecimal bd = new BigDecimal(d).setScale(scale, RoundingMode.HALF_UP);
		double newd = bd.doubleValue();
		return newd;
	}
	
	public static String convertUUIDToName(String uuid)
	{
		if(mysqlBaseHandler.exist(new PlayerData(), "player_uuid = ?", uuid))
		{
			return ((PlayerData) mysqlBaseHandler.getData(new PlayerData(), "player_uuid = ?", uuid)).getName();
		}
		return null;
	}
	
	public static UUID convertNameToUUID(String playername)
	{
		if(mysqlBaseHandler.exist(new PlayerData(), "`player_name` = ?", playername))
		{
			return ((PlayerData) mysqlBaseHandler.getData(new PlayerData(), "`player_name` = ?", playername)).getUUID();
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
	
	public static double round(double value, int places) 
	{
	    if (places < 0) throw new IllegalArgumentException();
	    try
	    {
	    	BigDecimal bd = BigDecimal.valueOf(value);
		    bd = bd.setScale(places, RoundingMode.HALF_UP);
		    return bd.doubleValue();
	    } catch (NumberFormatException e)
	    {
	    	return 0;
	    }
	}
}
