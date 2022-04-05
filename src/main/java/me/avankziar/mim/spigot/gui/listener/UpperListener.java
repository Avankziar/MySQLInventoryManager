package main.java.me.avankziar.mim.spigot.gui.listener;

import java.io.IOException;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.gui.GuiValues;
import main.java.me.avankziar.mim.spigot.gui.events.UpperGuiClickEvent;

public class UpperListener implements Listener
{
	private MIM plugin;
	
	public UpperListener(MIM plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onUpperGui(UpperGuiClickEvent event) throws IOException
	{
		if(!event.getPluginName().equals(GuiValues.PLUGINNAME))
		{
			return;
		}
		switch(event.getInventoryIdentifier())
		{
		case GuiValues.ITEM_REPLACER_INVENTORY:
			new FunctionHandler(plugin).upperFunction1();
			return;
		}
	}
}