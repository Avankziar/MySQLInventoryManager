package main.java.me.avankziar.mim.spigot.gui;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import main.java.me.avankziar.mim.spigot.gui.events.BottomGuiClickEvent;
import main.java.me.avankziar.mim.spigot.gui.events.UpperGuiClickEvent;

public class GuiPreListener implements Listener
{
	private JavaPlugin plugin;
	
	public GuiPreListener(JavaPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onClickListener(InventoryClickEvent event)
	{
		if(event.isCancelled())
		{
			return;
		}
		if(event.getResult() == Result.DENY)
		{
			return;
		}
		if(event.getClickedInventory() == null)
		{
			return;
		}
		if(event.getClickedInventory().getType() == InventoryType.CHEST)
		{
			getUpperGuiEvent(plugin, event);
		} else if(event.getClickedInventory().getType() == InventoryType.PLAYER)
		{
			getBottomGuiEvent(event);
		}
		return;
	}
	
	@EventHandler
	public void onGuiClose(InventoryCloseEvent event)
	{
		String uuid = event.getPlayer().getUniqueId().toString();
		if(GUI.isInGui(uuid))
		{
			GUI.removeInGui(uuid);
		}
	}
	
	private void getBottomGuiEvent(InventoryClickEvent event)
	{
		String uuid = event.getWhoClicked().getUniqueId().toString();
		if(!GUI.isInGui(uuid))
		{
			return;
		}
		event.setCancelled(true);
		event.setResult(Result.DENY);
		BottomGuiClickEvent gce = new BottomGuiClickEvent(
				event, 
				GuiValues.PLUGINNAME,
				GUI.getGui(uuid));
		Bukkit.getPluginManager().callEvent(gce);
	}
	
	private void getUpperGuiEvent(JavaPlugin plugin, InventoryClickEvent event)
	{
		if(event.getCurrentItem() == null)
		{
			return;
		}
		ItemStack i = event.getCurrentItem().clone();
		NamespacedKey npluginName = new NamespacedKey(plugin, GUI.PLUGINNAME);
		NamespacedKey ninventoryIdentifier = new NamespacedKey(plugin, GUI.INVENTORYIDENTIFIER);
		NamespacedKey nclickEventCancel = new NamespacedKey(plugin, GUI.CLICKEVENTCANCEL);
		NamespacedKey nfunction = new NamespacedKey(plugin, GUI.FUNCTION);
		NamespacedKey nsettingslevel = new NamespacedKey(plugin, GUI.SETTINGLEVEL);
		PersistentDataContainer pdc = i.getItemMeta().getPersistentDataContainer();
		if(!pdc.has(npluginName, PersistentDataType.STRING)
				|| !pdc.has(ninventoryIdentifier, PersistentDataType.STRING)
				|| !pdc.has(nclickEventCancel, PersistentDataType.STRING)
				|| !pdc.has(nfunction, PersistentDataType.STRING)
				|| !pdc.has(nsettingslevel, PersistentDataType.STRING))
		{
			return;
		}
		boolean clickEventCancel = Boolean.parseBoolean(pdc.get(nclickEventCancel, PersistentDataType.STRING));
		if(!clickEventCancel)
		{
			event.setCancelled(true);
			event.setResult(Result.DENY);
		}
		UpperGuiClickEvent gce = new UpperGuiClickEvent(
				event, 
				pdc.get(npluginName, PersistentDataType.STRING),
				pdc.get(ninventoryIdentifier, PersistentDataType.STRING), 
				pdc.get(nfunction, PersistentDataType.STRING), 
				GUI.SettingsLevel.valueOf(pdc.get(nsettingslevel, PersistentDataType.STRING)));
		for(NamespacedKey key : pdc.getKeys())
		{
			if(!key.getKey().contains(":::"))
			{
				continue;
			}
			String[] split = key.getKey().split(":::");
			String purekey = split[0];
			GUI.PersistentType type = GUI.PersistentType.valueOf(split[1]);
			switch(type)
			{
			case BYTE:
				if(pdc.has(key, PersistentDataType.BYTE))
				{
					gce.getValuesByte().put(purekey, pdc.get(key, PersistentDataType.BYTE));
				}
				continue;
			case BYTE_ARRAY:
				if(pdc.has(key, PersistentDataType.BYTE_ARRAY))
				{
					gce.getValuesByteArray().put(purekey, pdc.get(key, PersistentDataType.BYTE_ARRAY));
				}
				continue;
			case DOUBLE:
				if(pdc.has(key, PersistentDataType.DOUBLE))
				{
					gce.getValuesDouble().put(purekey, pdc.get(key, PersistentDataType.DOUBLE));
				}
				continue;
			case FLOAT:
				if(pdc.has(key, PersistentDataType.FLOAT))
				{
					gce.getValuesFloat().put(purekey, pdc.get(key, PersistentDataType.FLOAT));
				}
				continue;
			case INTEGER:
				if(pdc.has(key, PersistentDataType.INTEGER))
				{
					gce.getValuesInteger().put(purekey, pdc.get(key, PersistentDataType.INTEGER));
				}
				continue;
			case INTEGER_ARRAY:
				if(pdc.has(key, PersistentDataType.INTEGER_ARRAY))
				{
					gce.getValuesIntegerArray().put(purekey, pdc.get(key, PersistentDataType.INTEGER_ARRAY));
				}
				continue;
			case LONG:
				if(pdc.has(key, PersistentDataType.LONG))
				{
					gce.getValuesLong().put(purekey, pdc.get(key, PersistentDataType.LONG));
				}
				continue;
			case LONG_ARRAY:
				if(pdc.has(key, PersistentDataType.LONG_ARRAY))
				{
					gce.getValuesLongArray().put(purekey, pdc.get(key, PersistentDataType.LONG_ARRAY));
				}
				continue;
			case SHORT:
				if(pdc.has(key, PersistentDataType.SHORT))
				{
					gce.getValuesShort().put(purekey, pdc.get(key, PersistentDataType.SHORT));
				}
				continue;
			case STRING:
				if(pdc.has(key, PersistentDataType.STRING))
				{
					gce.getValuesString().put(purekey, pdc.get(key, PersistentDataType.STRING));
				}
				continue;
			}
		}
		Bukkit.getPluginManager().callEvent(gce);
	}
	
	public String getPureKey(String key)
	{
		if(!key.contains(":::"))
		{
			return key;
		}
		String[] split = key.split(":::");
		return split[0];
	}
}
