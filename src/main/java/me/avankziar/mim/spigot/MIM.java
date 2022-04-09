package main.java.me.avankziar.mim.spigot;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import main.java.me.avankziar.mim.spigot.assistance.BackgroundTask;
import main.java.me.avankziar.mim.spigot.assistance.Utility;
import main.java.me.avankziar.mim.spigot.cmd.BaseCommandExecutor;
import main.java.me.avankziar.mim.spigot.cmd.TabCompletion;
import main.java.me.avankziar.mim.spigot.cmdtree.ArgumentModule;
import main.java.me.avankziar.mim.spigot.cmdtree.BaseConstructor;
import main.java.me.avankziar.mim.spigot.cmdtree.CommandConstructor;
import main.java.me.avankziar.mim.spigot.cmdtree.CommandExecuteType;
import main.java.me.avankziar.mim.spigot.database.MysqlHandler;
import main.java.me.avankziar.mim.spigot.database.MysqlSetup;
import main.java.me.avankziar.mim.spigot.database.YamlHandler;
import main.java.me.avankziar.mim.spigot.database.YamlManager;
import main.java.me.avankziar.mim.spigot.handler.ConfigHandler;
import main.java.me.avankziar.mim.spigot.ifh.Base64Api;
import main.java.me.avankziar.mim.spigot.permission.Bypass;

public class MIM extends JavaPlugin
{
	public static Logger log;
	private static MIM plugin;
	public String pluginName = "Base";
	private YamlHandler yamlHandler;
	private YamlManager yamlManager;
	private MysqlSetup mysqlSetup;
	private MysqlHandler mysqlHandler;
	private Utility utility;
	private BackgroundTask backgroundTask;
	private ConfigHandler configHandler;
	
	private ArrayList<BaseConstructor> helpList = new ArrayList<>();
	private ArrayList<CommandConstructor> commandTree = new ArrayList<>();
	private LinkedHashMap<String, ArgumentModule> argumentMap = new LinkedHashMap<>();
	private ArrayList<String> players = new ArrayList<>();
	
	public static String infoCommandPath = "CmdBase";
	public static String infoCommand = "/";
	
	private Base64Api base64Api;
	
	public void onEnable()
	{
		plugin = this;
		log = getLogger();
		
		//https://patorjk.com/software/taag/#p=display&f=ANSI%20Shadow&t=MIM
		log.info("  | API-Version: "+plugin.getDescription().getAPIVersion());
		log.info("  | Author: "+plugin.getDescription().getAuthors().toString());
		log.info("  | Plugin Website: "+plugin.getDescription().getWebsite());
		log.info("  | Depend Plugins: "+plugin.getDescription().getDepend().toString());
		log.info("  | SoftDepend Plugins: "+plugin.getDescription().getSoftDepend().toString());
		log.info("  | LoadBefore: "+plugin.getDescription().getLoadBefore().toString());
		
		yamlHandler = new YamlHandler(this);
		
		if (yamlHandler.getConfig().getBoolean("Mysql.Status", false) == true)
		{
			mysqlHandler = new MysqlHandler(plugin);
			mysqlSetup = new MysqlSetup(plugin);
		} else
		{
			log.severe("MySQL is not set in the Plugin " + pluginName + "!");
			Bukkit.getPluginManager().getPlugin(pluginName).getPluginLoader().disablePlugin(this);
			return;
		}
		
		utility = new Utility(plugin);
		backgroundTask = new BackgroundTask(this);
		
		setupBypassPerm();
		setupCommandTree();
		setupListeners();
		configHandler = new ConfigHandler(this);
		setupIFH();
	}
	
	public void onDisable()
	{
		Bukkit.getScheduler().cancelTasks(this);
		HandlerList.unregisterAll(this);
		log.info(pluginName + " is disabled!");
	}

	public static MIM getPlugin()
	{
		return plugin;
	}
	
	public YamlHandler getYamlHandler() 
	{
		return yamlHandler;
	}
	
	public YamlManager getYamlManager()
	{
		return yamlManager;
	}

	public void setYamlManager(YamlManager yamlManager)
	{
		this.yamlManager = yamlManager;
	}
	
	public MysqlSetup getMysqlSetup() 
	{
		return mysqlSetup;
	}
	
	public MysqlHandler getMysqlHandler()
	{
		return mysqlHandler;
	}
	
	public Utility getUtility()
	{
		return utility;
	}
	
	public BackgroundTask getBackgroundTask()
	{
		return backgroundTask;
	}
	
	private void setupCommandTree()
	{		
		infoCommand += plugin.getYamlHandler().getCommands().getString("base.Name");
		TabCompletion tab = new TabCompletion(plugin);
		
		CommandConstructor mim = new CommandConstructor(CommandExecuteType.MIM, "mim", false);
		registerCommand(mim.getPath(), mim.getName());
		getCommand(mim.getName()).setExecutor(new BaseCommandExecutor(plugin, mim));
		getCommand(mim.getName()).setTabCompleter(tab);
	}
	
	public void setupBypassPerm()
	{
		String path = "Count.";
		for(Bypass.CountPermission bypass : new ArrayList<Bypass.CountPermission>(EnumSet.allOf(Bypass.CountPermission.class)))
		{
			Bypass.set(bypass, yamlHandler.getCommands().getString(path+bypass.toString()));
		}
		path = "Bypass.";
		for(Bypass.Permission bypass : new ArrayList<Bypass.Permission>(EnumSet.allOf(Bypass.Permission.class)))
		{
			Bypass.set(bypass, yamlHandler.getCommands().getString(path+bypass.toString()));
		}
	}
	
	public ArrayList<BaseConstructor> getHelpList()
	{
		return helpList;
	}
	
	public void addingCommandHelps(BaseConstructor... objects)
	{
		for(BaseConstructor bc : objects)
		{
			helpList.add(bc);
		}
	}
	
	public ArrayList<CommandConstructor> getCommandTree()
	{
		return commandTree;
	}
	
	public CommandConstructor getCommandFromPath(String commandpath)
	{
		CommandConstructor cc = null;
		for(CommandConstructor coco : getCommandTree())
		{
			if(coco.getPath().equalsIgnoreCase(commandpath))
			{
				cc = coco;
				break;
			}
		}
		return cc;
	}
	
	public CommandConstructor getCommandFromCommandString(String command)
	{
		CommandConstructor cc = null;
		for(CommandConstructor coco : getCommandTree())
		{
			if(coco.getName().equalsIgnoreCase(command))
			{
				cc = coco;
				break;
			}
		}
		return cc;
	}
	
	public void registerCommand(String... aliases) 
	{
		PluginCommand command = getCommand(aliases[0], plugin);
	 
		command.setAliases(Arrays.asList(aliases));
		getCommandMap().register(plugin.getDescription().getName(), command);
	}
	 
	private static PluginCommand getCommand(String name, MIM plugin) 
	{
		PluginCommand command = null;
	 
		try 
		{
			Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
			c.setAccessible(true);
	 
			command = c.newInstance(name, plugin);
		} catch (SecurityException e) 
		{
			e.printStackTrace();
		} catch (IllegalArgumentException e) 
		{
			e.printStackTrace();
		} catch (IllegalAccessException e) 
		{
			e.printStackTrace();
		} catch (InstantiationException e) 
		{
			e.printStackTrace();
		} catch (InvocationTargetException e) 
		{
			e.printStackTrace();
		} catch (NoSuchMethodException e) 
		{
			e.printStackTrace();
		}
	 
		return command;
	}
	 
	private static CommandMap getCommandMap() 
	{
		CommandMap commandMap = null;
		try {
			if (Bukkit.getPluginManager() instanceof SimplePluginManager) 
			{
				Field f = SimplePluginManager.class.getDeclaredField("commandMap");
				f.setAccessible(true);
	 
				commandMap = (CommandMap) f.get(Bukkit.getPluginManager());
			}
		} catch (NoSuchFieldException e) 
		{
			e.printStackTrace();
		} catch (SecurityException e) 
		{
			e.printStackTrace();
		} catch (IllegalArgumentException e) 
		{
			e.printStackTrace();
		} catch (IllegalAccessException e) 
		{
			e.printStackTrace();
		}
	 
		return commandMap;
	}
	
	public LinkedHashMap<String, ArgumentModule> getArgumentMap()
	{
		return argumentMap;
	}
	
	public ArrayList<String> getMysqlPlayers()
	{
		return players;
	}

	public void setMysqlPlayers(ArrayList<String> players)
	{
		this.players = players;
	}
	
	public void setupListeners()
	{
		PluginManager pm = getServer().getPluginManager();
		//pm.registerEvents(new BackListener(plugin), plugin);
	}
	
	public ConfigHandler getConfigHandler()
	{
		return configHandler;
	}
	
	public Base64Api getBase64Api()
	{
		return base64Api;
	}
	
	public boolean reload() throws IOException
	{
		yamlHandler = new YamlHandler(this);
		return true;
	}
	
	private void setupIFH()
	{
		if(!setupBase64())
		{
			return;
		}
	}
	
	//Hier IFH implementieren für später
	private boolean setupBase64()
	{      
		if(!plugin.getServer().getPluginManager().isPluginEnabled("InterfaceHub")) 
	    {
			log.severe("IFH is not set in the Plugin " + pluginName + "! Disable plugin!");
			Bukkit.getPluginManager().getPlugin(pluginName).getPluginLoader().disablePlugin(this);
	    	return false;
	    }
		base64Api = new Base64Api();
    	plugin.getServer().getServicesManager().register(
        main.java.me.avankziar.ifh.spigot.serializer.Base64.class,
        base64Api,
        this,
        ServicePriority.Normal);
    	log.info(pluginName + " detected InterfaceHub >>> Base64.class is provided!");
		return true;
	}
}