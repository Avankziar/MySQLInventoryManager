package main.java.me.avankziar.mim.spigot.permission;

import java.util.LinkedHashMap;

public class Bypass
{
	public enum Permission
	{
		GM_SURVIVAL,
		GM_CREATIVE,
		GM_ADVENTURE,
		GM_SPECTATOR,
		GM_OTHERPLAYER,
		WORKBENCH_OTHERPLAYER,
		ENCHANTINGTABLE_OTHERPLAYER,
		ANVIL_OTHERPLAYER,
		CUSTOMPLAYERINVENTORY_BUY,
		CUSTOMPLAYERINVENTORY_COST,
	}
	private static LinkedHashMap<Bypass.Permission, String> mapPerm = new LinkedHashMap<>();
	
	public static void set(Bypass.Permission bypass, String perm)
	{
		mapPerm.put(bypass, perm);
	}
	
	public static String get(Bypass.Permission bypass)
	{
		return mapPerm.get(bypass);
	}
	
	public enum CountPermission
	{
		BASE
	}
	private static LinkedHashMap<Bypass.CountPermission, String> mapCount = new LinkedHashMap<>();
	
	public static void set(Bypass.CountPermission bypass, String perm)
	{
		mapCount.put(bypass, perm);
	}
	
	public static String get(Bypass.CountPermission bypass)
	{
		return mapCount.get(bypass);
	}
}