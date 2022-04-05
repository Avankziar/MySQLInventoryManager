package main.java.me.avankziar.mim.spigot.gui.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.gui.GuiValues;
import main.java.me.avankziar.mim.spigot.gui.events.BottomGuiClickEvent;

public class BottomListener implements Listener
{
	private MIM plugin;
	
	public BottomListener(MIM plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onBottomGui(BottomGuiClickEvent event)
	{
		if(!event.getPluginName().equals(GuiValues.PLUGINNAME))
		{
			return;
		}
		switch(event.getInventoryIdentifier())
		{
		case GuiValues.ITEM_REPLACER_INVENTORY:
			new FunctionHandler(plugin).bottomFunction1();
			return;
		}
	}
}