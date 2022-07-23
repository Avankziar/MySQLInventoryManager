package main.java.me.avankziar.mim.bungee;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.java.me.avankziar.mim.bungee.listener.CommandToBungeeListener;
import main.java.me.avankziar.mim.bungee.listener.IsOnlineListener;
import main.java.me.avankziar.mim.bungee.listener.OnlineListener;
import main.java.me.avankziar.mim.bungee.listener.PlayerParameterListener;
import main.java.me.avankziar.mim.bungee.listener.WhoIsListener;
import main.java.me.avankziar.mim.general.StaticValues;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class MIM extends Plugin
{
	public static Logger log;
	public static String pluginName = "MysqlInventoryManager";
	private static MIM plugin;
	
	public void onEnable() 
	{
		plugin = this;
		log = getLogger();
		//https://patorjk.com/software/taag/#p=display&f=ANSI%20Shadow&t=BTM
		log.info(" ███╗   ███╗██╗███╗   ███╗ | API-Version: "+plugin.getDescription().getVersion());
		log.info(" ████╗ ████║██║████╗ ████║ | Author: "+plugin.getDescription().getAuthor());
		log.info(" ██╔████╔██║██║██╔████╔██║ | Plugin Website: ");
		log.info(" ██║╚██╔╝██║██║██║╚██╔╝██║ | Depend Plugins: "+plugin.getDescription().getDepends().toString());
		log.info(" ██║ ╚═╝ ██║██║██║ ╚═╝ ██║ | SoftDepend Plugins: "+plugin.getDescription().getSoftDepends().toString());
		log.info(" ╚═╝     ╚═╝╚═╝╚═╝     ╚═╝ | Have Fun^^");
		ListenerSetup();
	}
	
	public void onDisable()
	{
		getProxy().getScheduler().cancel(this);		
		log.info(pluginName + " is disabled!");
	}
	
	public static MIM getPlugin()
	{
		return plugin;
	}
	
	@SuppressWarnings("deprecation")
	public void disablePlugin()
	{
		Plugin plugin = (Plugin) ProxyServer.getInstance().getPluginManager().getPlugin(pluginName);
	       
		try
		{
			plugin.onDisable();
			for (Handler handler : plugin.getLogger().getHandlers())
			{
				handler.close();
			}
		}
		catch (Throwable t) 
		{
			getLogger().log(Level.SEVERE, "Exception disabling plugin " + plugin.getDescription().getName(), t);
		}
		ProxyServer.getInstance().getPluginManager().unregisterCommands(plugin);
		ProxyServer.getInstance().getPluginManager().unregisterListeners(plugin);
		ProxyServer.getInstance().getScheduler().cancel(plugin);
		plugin.getExecutorService().shutdownNow();
	}
	
	public void ListenerSetup()
	{
		PluginManager pm = getProxy().getPluginManager();
		pm.registerListener(plugin, new CommandToBungeeListener(plugin));
		pm.registerListener(plugin, new PlayerParameterListener(plugin));
		pm.registerListener(plugin, new WhoIsListener(plugin));
		pm.registerListener(plugin, new OnlineListener(plugin));
		pm.registerListener(plugin, new IsOnlineListener(plugin));
		getProxy().registerChannel(StaticValues.CMDTB_TOBUNGEE);
		getProxy().registerChannel(StaticValues.PP_TOBUNGEE);
		getProxy().registerChannel(StaticValues.PP_TOSPIGOT);
		getProxy().registerChannel(StaticValues.WHOIS_TOBUNGEE);
		getProxy().registerChannel(StaticValues.WHOIS_TOSPIGOT);
		getProxy().registerChannel(StaticValues.ONLINE_TOBUNGEE);
		getProxy().registerChannel(StaticValues.ONLINE_TOSPIGOT);
		getProxy().registerChannel(StaticValues.ISONLINE_TOBUNGEE);
		getProxy().registerChannel(StaticValues.ISONLINE_TOSPIGOT);
	}
}