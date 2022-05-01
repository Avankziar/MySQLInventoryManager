package main.java.me.avankziar.mim.spigot.objects;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import main.java.me.avankziar.mim.spigot.MIM;

public class CustomPlayerInventory
{
	public enum ListStatus
	{
		WHITELIST, BLACKLIST;
	}
	
	public enum CountType
	{
		ADDUP, HIGHEST;
	}
	
	private static LinkedHashMap<String, ArrayList<Material>> listItem = new LinkedHashMap<>();
	
	private String uniqueName; //Identifier for the customplayerinventory file
	private UUID ownerUUID; //The uuid of the player, which owns the inv
	private int actualRowAmount; //The actualRow of the inv.
	private int targetRowAmount; //The targetRow of the inv. 
								//If actualRow > targetrow, after closing the Items over the limit are dropped.
								//If actualRow < targetrow, before open, the inventory will growing on the targetrow amount.
	private ItemStack[] inventory;
	
	public CustomPlayerInventory(String uniqueName) //Only Access for the YamlInformation
	{
		// TODO Auto-generated constructor stub
	}
	
	public String getUniqueName()
	{
		return uniqueName;
	}

	public void setUniqueName(String uniqueName)
	{
		this.uniqueName = uniqueName;
	}

	private YamlConfiguration getFile()
	{
		if(getUniqueName() == null)
		{
			return null;
		}
		return MIM.getPlugin().getYamlHandler().getCustomPlayerInventory(getUniqueName());
	}
	
	public boolean isActive()
	{
		return getFile() == null ? false : getFile().getBoolean("IsActive", false);
	}
	
	public boolean hasAccess(Player player)
	{
		return getFile() == null ? false : getFile().getBoolean("AccessPermission", false);
	}
	
	public int getPermissionRowAmount(Player player)
	{
		YamlConfiguration y = getFile();
		if(y == null)
		{
			return 0;
		}
		CountType ct = CountType.valueOf(y.getString("CountPermissionType", "HIGHEST"));
		int c = 0;
		switch(ct)
		{
		case ADDUP:
			for(int i = 6; i > 0; i--)
			{
				if(player.hasPermission(y.getString("CountPermission", "mim.customplayerinv.count.default.")+i))
				{
					c += i;
				}
			}
			break;
		case HIGHEST:
			for(int i = 6; i > 0; i--)
			{
				if(player.hasPermission(y.getString("CountPermission", "mim.customplayerinv.count.default.")+i))
				{
					c = i;
					break;
				}
			}
			break;
		}
		return c;
	}
	
	public  List<String> getCosts(int row)
	{
		return getFile() == null ? null : getFile().getStringList("CostPerRow."+row);
	}
}
