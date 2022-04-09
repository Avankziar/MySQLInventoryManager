package main.java.me.avankziar.mim.spigot.gui;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import main.java.me.avankziar.mim.spigot.MIM;

public class GUI
{
	public enum PersistentType
	{
		STRING, BYTE, BYTE_ARRAY, DOUBLE, FLOAT, INTEGER, INTEGER_ARRAY, LONG, LONG_ARRAY, SHORT;
	}
	/*
	 * Als Einstellung zu verstehen, dass man bei bestimmten LEvel, nur bestimmte einstellmöglichkeiten sieht.
	 * Um Spieler nicht zu überforndern
	 */
	public enum SettingsLevel
	{
		NOLEVEL, BASE, ADVANCED, EXPERT;
		
		public String getName()
		{
			switch(this)
			{
			case BASE:
				return "BASE";
			case ADVANCED:
				return "ADVANCED";
			case EXPERT:
				return "EXPERT";
			case NOLEVEL:
				return "NOLEVEL";
			}
			return null;
		}
	}
	
	public static final String PLUGINNAME = "pluginname";
	public static final String INVENTORYIDENTIFIER = "inventoryidentifier";
	public static final String CLICKEVENTCANCEL = "clickeventcancel";
	public static final String FUNCTION = "funtion";
	public static final String SETTINGLEVEL = "settinglevel";
	
	private Inventory inventory;
	private String pluginName;
	private String inventoryIdentifier;
	private static JavaPlugin plugin = MIM.getPlugin();
	
	public GUI(String pluginName, String inventoryIdentifier, Inventory inventory)
	{
		this.inventory = inventory;
		this.pluginName = pluginName;
		this.inventoryIdentifier = inventoryIdentifier;
	}
	
	public GUI(String pluginName, String inventoryIdentifier, InventoryHolder owner, int row, String title)
	{
		if(row > 6) row = 6;
		this.inventory = Bukkit.createInventory(owner, row*9, title);
		this.pluginName = pluginName;
		this.inventoryIdentifier = inventoryIdentifier;
	}
	
	/**
	 * Recreate a itemstack with the needed pdc values.
	 * @param itemstack
	 * @param pluginName
	 * @param inventoryIdentifier
	 * @param function
	 * @param settingsLevel
	 * @param clickEventCancel
	 * @param values
	 * @return
	 */
	public static ItemStack recreate(ItemStack itemstack, String pluginName, String inventoryIdentifier, 
			String function, SettingsLevel settingsLevel, boolean clickEventCancel,
			LinkedHashMap<String, Entry<PersistentType, Object>> values)
	{
		ItemStack i = itemstack.clone();
		ItemMeta im = i.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		pdc.set(new NamespacedKey(plugin, PLUGINNAME), PersistentDataType.STRING, pluginName);
		pdc.set(new NamespacedKey(plugin, INVENTORYIDENTIFIER), PersistentDataType.STRING, inventoryIdentifier);
		pdc.set(new NamespacedKey(plugin, CLICKEVENTCANCEL), PersistentDataType.STRING, String.valueOf(clickEventCancel));
		pdc.set(new NamespacedKey(plugin, FUNCTION), PersistentDataType.STRING, function);
		pdc.set(new NamespacedKey(plugin, SETTINGLEVEL), PersistentDataType.STRING, settingsLevel.getName());
		if(values != null)
		{
			for(String key : values.keySet())
			{
				Entry<PersistentType, Object> value = values.get(key);
				String fullkey = key+":::"+value.getKey();
				switch(value.getKey())
				{
				case BYTE:
					if(value.getValue() instanceof Byte)
					{
						pdc.set(new NamespacedKey(plugin, fullkey), PersistentDataType.BYTE, (Byte) value.getValue());
					}
					break;
				case BYTE_ARRAY:
					if(value.getValue() instanceof byte[])
					{
						pdc.set(new NamespacedKey(plugin, fullkey), PersistentDataType.BYTE_ARRAY, (byte[]) value.getValue());
					}
					break;
				case DOUBLE:
					if(value.getValue() instanceof Double)
					{
						pdc.set(new NamespacedKey(plugin, fullkey), PersistentDataType.DOUBLE, (Double) value.getValue());
					}
					break;
				case FLOAT:
					if(value.getValue() instanceof Float)
					{
						pdc.set(new NamespacedKey(plugin, fullkey), PersistentDataType.FLOAT, (Float) value.getValue());
					}
					break;
				case INTEGER:
					if(value.getValue() instanceof Integer)
					{
						pdc.set(new NamespacedKey(plugin, fullkey), PersistentDataType.INTEGER, (Integer) value.getValue());
					}
					break;
				case INTEGER_ARRAY: 
					if(value.getValue() instanceof int[])
					{
						pdc.set(new NamespacedKey(plugin, fullkey), PersistentDataType.INTEGER_ARRAY, (int[]) value.getValue());
					}
					break;
				case LONG:
					if(value.getValue() instanceof Long)
					{
						pdc.set(new NamespacedKey(plugin, fullkey), PersistentDataType.LONG, (Long) value.getValue());
					}
					break;
				case LONG_ARRAY:
					if(value.getValue() instanceof long[])
					{
						pdc.set(new NamespacedKey(plugin, fullkey), PersistentDataType.LONG_ARRAY, (long[]) value.getValue());
					}
					break;
				case SHORT:
					if(value.getValue() instanceof Short)
					{
						pdc.set(new NamespacedKey(plugin, fullkey), PersistentDataType.SHORT, (Short) value.getValue());
					}
					break;
				case STRING:
					if(value.getValue() instanceof String)
					{
						pdc.set(new NamespacedKey(plugin, fullkey), PersistentDataType.STRING, (String) value.getValue());
					}
					break;
				}
			}
		}
		i.setItemMeta(im);
		return i;
	}
	
	public void add(int slot, ItemStack itemstack, String function, SettingsLevel settingsLevel, boolean directEventCancelByClicking,
			LinkedHashMap<String, Entry<PersistentType, Object>> values)
	{
		ItemStack i = itemstack.clone();
		ItemMeta im = i.getItemMeta();
		PersistentDataContainer pdc = im.getPersistentDataContainer();
		pdc.set(new NamespacedKey(plugin, PLUGINNAME), PersistentDataType.STRING, this.pluginName);
		pdc.set(new NamespacedKey(plugin, INVENTORYIDENTIFIER), PersistentDataType.STRING, this.inventoryIdentifier);
		pdc.set(new NamespacedKey(plugin, CLICKEVENTCANCEL), PersistentDataType.STRING, String.valueOf(directEventCancelByClicking));
		pdc.set(new NamespacedKey(plugin, FUNCTION), PersistentDataType.STRING, function);
		pdc.set(new NamespacedKey(plugin, SETTINGLEVEL), PersistentDataType.STRING, settingsLevel.getName());
		if(values != null)
		{
			for(String key : values.keySet())
			{
				Entry<PersistentType, Object> value = values.get(key);
				String fullkey = key+":::"+value.getKey();
				switch(value.getKey())
				{
				case BYTE:
					if(value.getValue() instanceof Byte)
					{
						pdc.set(new NamespacedKey(plugin, fullkey), PersistentDataType.BYTE, (Byte) value.getValue());
					}
					break;
				case BYTE_ARRAY:
					if(value.getValue() instanceof byte[])
					{
						pdc.set(new NamespacedKey(plugin, fullkey), PersistentDataType.BYTE_ARRAY, (byte[]) value.getValue());
					}
					break;
				case DOUBLE:
					if(value.getValue() instanceof Double)
					{
						pdc.set(new NamespacedKey(plugin, fullkey), PersistentDataType.DOUBLE, (Double) value.getValue());
					}
					break;
				case FLOAT:
					if(value.getValue() instanceof Float)
					{
						pdc.set(new NamespacedKey(plugin, fullkey), PersistentDataType.FLOAT, (Float) value.getValue());
					}
					break;
				case INTEGER:
					if(value.getValue() instanceof Integer)
					{
						pdc.set(new NamespacedKey(plugin, fullkey), PersistentDataType.INTEGER, (Integer) value.getValue());
					}
					break;
				case INTEGER_ARRAY: 
					if(value.getValue() instanceof int[])
					{
						pdc.set(new NamespacedKey(plugin, fullkey), PersistentDataType.INTEGER_ARRAY, (int[]) value.getValue());
					}
					break;
				case LONG:
					if(value.getValue() instanceof Long)
					{
						pdc.set(new NamespacedKey(plugin, fullkey), PersistentDataType.LONG, (Long) value.getValue());
					}
					break;
				case LONG_ARRAY:
					if(value.getValue() instanceof long[])
					{
						pdc.set(new NamespacedKey(plugin, fullkey), PersistentDataType.LONG_ARRAY, (long[]) value.getValue());
					}
					break;
				case SHORT:
					if(value.getValue() instanceof Short)
					{
						pdc.set(new NamespacedKey(plugin, fullkey), PersistentDataType.SHORT, (Short) value.getValue());
					}
					break;
				case STRING:
					if(value.getValue() instanceof String)
					{
						pdc.set(new NamespacedKey(plugin, fullkey), PersistentDataType.STRING, (String) value.getValue());
					}
					break;
				}
			}
		}
		i.setItemMeta(im);
		if(this.inventory != null)
		{
			this.inventory.setItem(slot, i);
		}
	}

	public void open(Player player) 
	{
		if(this.inventory != null) player.openInventory(this.inventory);
	}
	
	//Key == playeruuid
	//Value == InventoryIdentifier
	private static LinkedHashMap<String, String> playerInGui = new LinkedHashMap<>();
	
	public static boolean isInGui(String uuid)
    {
    	return playerInGui.containsKey(uuid);
    }
	
	public static String getGui(String uuid)
	{
		return playerInGui.get(uuid);
	}
    
	public static void addInGui(String uuid, String inventoryIdentifier)
    {
    	if(!playerInGui.containsKey(uuid))
    	{
    		playerInGui.put(uuid, inventoryIdentifier);
    	} else
    	{
    		playerInGui.replace(uuid, inventoryIdentifier);
    	}
    }
    
	public static void removeInGui(String uuid)
    {
    	if(playerInGui.containsKey(uuid))
    	{
    		playerInGui.remove(uuid);
    	}
    }
}
