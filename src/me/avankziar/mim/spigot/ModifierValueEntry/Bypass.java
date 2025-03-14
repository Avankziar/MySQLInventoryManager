package me.avankziar.mim.spigot.ModifierValueEntry;

import java.util.LinkedHashMap;

import me.avankziar.mim.spigot.MIM;

public class Bypass
{
	public enum Permission
	{
		//Here Condition and BypassPermission.
		BASE;
		
		public String getValueLable()
		{
			return MIM.pluginname.toLowerCase()+"-"+this.toString().toLowerCase();
		}
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
	
	public enum Counter
	{
		//Here BonusMalus and CountPermission Things
		BASE(true);
		
		private boolean forPermission;
		
		Counter()
		{
			this.forPermission = true;
		}
		
		Counter(boolean forPermission)
		{
			this.forPermission = forPermission;
		}
	
		public boolean forPermission()
		{
			return this.forPermission;
		}
		
		public String getModification()
		{
			return MIM.pluginname.toLowerCase()+"-"+this.toString().toLowerCase();
		}
	}
	
	private static LinkedHashMap<Bypass.Counter, String> mapCount = new LinkedHashMap<>();
	
	public static void set(Bypass.Counter bypass, String perm)
	{
		mapCount.put(bypass, perm);
	}
	
	public static String get(Bypass.Counter bypass)
	{
		return mapCount.get(bypass);
	}
}