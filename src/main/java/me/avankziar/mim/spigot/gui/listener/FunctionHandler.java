package main.java.me.avankziar.mim.spigot.gui.listener;

import main.java.me.avankziar.mim.spigot.MIM;

public class FunctionHandler
{
	private MIM plugin;
	
	public FunctionHandler(MIM plugin)
	{
		this.plugin = plugin;
	}
	
	/**
	 * default function handler
	 */
	public void bottomFunction1()
	{
		plugin.getLogger().info("");
		return;
	}
	
	public void upperFunction1()
	{
		plugin.getLogger().info("");
		return;
	}
}