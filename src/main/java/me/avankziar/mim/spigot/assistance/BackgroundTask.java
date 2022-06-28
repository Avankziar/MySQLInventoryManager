package main.java.me.avankziar.mim.spigot.assistance;

import main.java.me.avankziar.mim.spigot.MIM;

public class BackgroundTask
{
	private static MIM plugin;
	
	public BackgroundTask(MIM plugin)
	{
		BackgroundTask.plugin = plugin;
		initBackgroundTask();
	}
	
	public boolean initBackgroundTask()
	{
		//ADDME eventuell ein automatisches saven machen?
		return true;
	}
}
