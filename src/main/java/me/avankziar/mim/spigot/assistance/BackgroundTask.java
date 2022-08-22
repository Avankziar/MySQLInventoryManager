package main.java.me.avankziar.mim.spigot.assistance;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.me.avankziar.ifh.general.assistance.ChatApi;
import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.cmdtree.CommandExecuteType;
import main.java.me.avankziar.mim.spigot.cmdtree.CommandSuggest;
import main.java.me.avankziar.mim.spigot.database.MysqlHandler;
import main.java.me.avankziar.mim.spigot.handler.ConfigHandler;
import net.md_5.bungee.api.chat.ClickEvent;

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
		runWaitingItemsTask(plugin.getYamlHandler().getConfig().getBoolean("RunTask.WaitingsItems.Active", true));
		return true;
	}
	
	private void runWaitingItemsTask(boolean run)
	{
		if(!run)
		{
			return;
		}
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				for(Player player : Bukkit.getOnlinePlayers())
				{
					waitingItemsTask(player);
				}
			}
		}.runTaskTimerAsynchronously(plugin, 25, 20L*plugin.getYamlHandler().getConfig().getInt("RunTask.WaitingsItems.RepeatingInSeconds", 180));
	}
	
	public static void waitingItemsTask(Player player)
	{
		if(player == null || !player.isOnline())
		{
			return;
		}
		String synchroKey = new ConfigHandler(plugin).getSynchroKey(player);
		int count = plugin.getMysqlHandler().getCount(MysqlHandler.Type.WAITINGITEMS,
				"`player_uuid` = ? AND `synchro_key` = ?", player.getUniqueId().toString(), synchroKey);
		if(count <= 0)
		{
			return;
		}
		player.spigot().sendMessage(ChatApi.clickEvent(plugin.getYamlHandler().getLang().getString("WaitingItems.RunTask"),
				ClickEvent.Action.RUN_COMMAND, CommandSuggest.get(CommandExecuteType.WAITINGITEMS_LIST)+" 0"));
	}
}