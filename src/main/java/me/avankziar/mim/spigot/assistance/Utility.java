package main.java.me.avankziar.mim.spigot.assistance;

import java.lang.reflect.Method;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
	
	public static String convertUUIDToName(String uuid)
	{
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.PLAYERDATA, "player_uuid = ?", uuid))
		{
			return ((PlayerData) plugin.getMysqlHandler().getData(MysqlHandler.Type.PLAYERDATA, "player_uuid = ?", uuid)).getName();
		}
		return null;
	}
	
	public static UUID convertNameToUUID(String playername)
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
	
	public static String getLocalization(Material mat)
	{
		ItemStack is = new ItemStack(mat);
		ItemMeta im = is.getItemMeta();
		return im.hasLocalizedName() ? im.getLocalizedName() : mat.toString();
	}
	
	//TODO IFH anbindung für eine Sprachen
}
