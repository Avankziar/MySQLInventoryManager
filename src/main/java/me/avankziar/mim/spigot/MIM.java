package main.java.me.avankziar.mim.spigot;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.me.avankziar.ifh.general.interfaces.PlayerTimes;
import main.java.me.avankziar.ifh.spigot.economy.Economy;
import main.java.me.avankziar.ifh.spigot.interfaces.Vanish;
import main.java.me.avankziar.ifh.spigot.position.LastKnownPosition;
import main.java.me.avankziar.mim.general.StaticValues;
import main.java.me.avankziar.mim.spigot.assistance.BackgroundTask;
import main.java.me.avankziar.mim.spigot.assistance.Utility;
import main.java.me.avankziar.mim.spigot.cmd.AnvilCmdExecutor;
import main.java.me.avankziar.mim.spigot.cmd.ArmorSeeCmdExecutor;
import main.java.me.avankziar.mim.spigot.cmd.CustomPlayerInventoryCmdExecutor;
import main.java.me.avankziar.mim.spigot.cmd.EnchantingTableCmdExecutor;
import main.java.me.avankziar.mim.spigot.cmd.EnderChestCmdExecutor;
import main.java.me.avankziar.mim.spigot.cmd.GameModeCmdExecutor;
import main.java.me.avankziar.mim.spigot.cmd.InventorySeeCmdExecutor;
import main.java.me.avankziar.mim.spigot.cmd.MiMCmdExecutor;
import main.java.me.avankziar.mim.spigot.cmd.OnlineCmdExecutor;
import main.java.me.avankziar.mim.spigot.cmd.TabCompletion;
import main.java.me.avankziar.mim.spigot.cmd.WhoIsCmdExecutor;
import main.java.me.avankziar.mim.spigot.cmd.WorkbenchCmdExecutor;
import main.java.me.avankziar.mim.spigot.cmd.clear.ClearSub;
import main.java.me.avankziar.mim.spigot.cmd.cpi.CPIBuy;
import main.java.me.avankziar.mim.spigot.cmd.cpi.CPIDrop;
import main.java.me.avankziar.mim.spigot.cmd.cpi.CPIInfo;
import main.java.me.avankziar.mim.spigot.cmd.cpi.CPISee;
import main.java.me.avankziar.mim.spigot.cmd.mim.MiMSave;
import main.java.me.avankziar.mim.spigot.cmd.mim.MiMSaveAndKick;
import main.java.me.avankziar.mim.spigot.cmdtree.ArgumentConstructor;
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
import main.java.me.avankziar.mim.spigot.ifh.CommandToBungeeApi;
import main.java.me.avankziar.mim.spigot.ifh.PlayerParameterApi;
import main.java.me.avankziar.mim.spigot.listener.BlockCanBuildListener;
import main.java.me.avankziar.mim.spigot.listener.BlockIgniteListener;
import main.java.me.avankziar.mim.spigot.listener.BlockSignChangeListener;
import main.java.me.avankziar.mim.spigot.listener.EntityPickupItemListener;
import main.java.me.avankziar.mim.spigot.listener.EntityResurrectListener;
import main.java.me.avankziar.mim.spigot.listener.EntityTameListener;
import main.java.me.avankziar.mim.spigot.listener.InventoryCloseListener;
import main.java.me.avankziar.mim.spigot.listener.OnlineListener;
import main.java.me.avankziar.mim.spigot.listener.PlayerChangedWorldListener;
import main.java.me.avankziar.mim.spigot.listener.PlayerDeathListener;
import main.java.me.avankziar.mim.spigot.listener.PlayerDropItemListener;
import main.java.me.avankziar.mim.spigot.listener.PlayerExpChangeListener;
import main.java.me.avankziar.mim.spigot.listener.PlayerGameModeChangeListener;
import main.java.me.avankziar.mim.spigot.listener.PlayerHarvestBlockListener;
import main.java.me.avankziar.mim.spigot.listener.PlayerItemBreakListener;
import main.java.me.avankziar.mim.spigot.listener.PlayerItemConsumeListener;
import main.java.me.avankziar.mim.spigot.listener.PlayerItemDamageListener;
import main.java.me.avankziar.mim.spigot.listener.PlayerJoinListener;
import main.java.me.avankziar.mim.spigot.listener.PlayerLevelChangeListener;
import main.java.me.avankziar.mim.spigot.listener.PlayerQuitListener;
import main.java.me.avankziar.mim.spigot.listener.PlayerRespawnListener;
import main.java.me.avankziar.mim.spigot.listener.PlayerTeleportListener;
import main.java.me.avankziar.mim.spigot.listener.PrepareItemEnchantListener;
import main.java.me.avankziar.mim.spigot.listener.TimeSkipListener;
import main.java.me.avankziar.mim.spigot.listener.WhoIsListener;
import main.java.me.avankziar.mim.spigot.objects.CustomPlayerInventory;
import main.java.me.avankziar.mim.spigot.objects.PlayerData;
import main.java.me.avankziar.mim.spigot.objects.SyncType;
import main.java.me.avankziar.mim.spigot.permission.Bypass;
import net.luckperms.api.LuckPerms;

public class MIM extends JavaPlugin
{
	public static Logger log;
	private static MIM plugin;
	public String pluginName = "MysqlInventoryManager";
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
	
	public Set<UUID> playerInSync = new HashSet<>();
	public Set<UUID> playerSyncComplete = new HashSet<>();
	
	private Base64Api base64Api;
	private CommandToBungeeApi cmdToBungeeApi;
	private PlayerParameterApi ppmApi;
	private Economy ecoConsumer;
	private net.milkbowl.vault.economy.Economy ecoVault;
	private PlayerTimes playerTimesConsumer;
	private LastKnownPosition lastKnownPositionConsumer;
	private Vanish vanishconsumer;
	
	private LuckPerms lpapi;
	
	public void onEnable()
	{
		plugin = this;
		log = getLogger();
		
		//https://patorjk.com/software/taag/#p=display&f=ANSI%20Shadow&t=MIM
		log.info(" ███╗   ███╗██╗███╗   ███╗ | API-Version: "+plugin.getDescription().getAPIVersion());
		log.info(" ████╗ ████║██║████╗ ████║ | Author: "+plugin.getDescription().getAuthors().toString());
		log.info(" ██╔████╔██║██║██╔████╔██║ | Plugin Website: "+plugin.getDescription().getWebsite());
		log.info(" ██║╚██╔╝██║██║██║╚██╔╝██║ | Depend Plugins: "+plugin.getDescription().getDepend().toString());
		log.info(" ██║ ╚═╝ ██║██║██║ ╚═╝ ██║ | SoftDepend Plugins: "+plugin.getDescription().getSoftDepend().toString());
		log.info(" ╚═╝     ╚═╝╚═╝╚═╝     ╚═╝ | LoadBefore: "+plugin.getDescription().getLoadBefore().toString());
		
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
		
		setupIFH();
		setupVaultEconomy();
		setupLuckPerm();
		setupBypassPerm();
		setupCommandTree();
		setupListeners();
		configHandler = new ConfigHandler(this);
		CustomPlayerInventory.initListItem();
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
		LinkedHashMap<Integer, ArrayList<String>> playerMapI = new LinkedHashMap<>();
		LinkedHashMap<Integer, ArrayList<String>> playerMapII = new LinkedHashMap<>();
		LinkedHashMap<Integer, ArrayList<String>> playerMapIII = new LinkedHashMap<>();
		LinkedHashMap<Integer, ArrayList<String>> playerMapIV = new LinkedHashMap<>();
		LinkedHashMap<Integer, ArrayList<String>> playerMapV = new LinkedHashMap<>();
		
		setupPlayers();
		ArrayList<String> playerarray = getPlayers();
		
		Collections.sort(playerarray);
		playerMapI.put(1, playerarray);
		playerMapII.put(2, playerarray);
		playerMapIII.put(3, playerarray);
		playerMapIV.put(4, playerarray);
		playerMapV.put(5, playerarray);
		
		infoCommand += plugin.getYamlHandler().getCommands().getString("base.Name");
		TabCompletion tab = new TabCompletion(plugin);
		
		ArgumentConstructor saveAndKick = new ArgumentConstructor(CommandExecuteType.MIM_SAVEANDKICK, "mim_saveandkick", 0, 0, 1, false, null);
		new MiMSaveAndKick(saveAndKick);
		
		ArgumentConstructor save = new ArgumentConstructor(CommandExecuteType.MIM_SAVE, "mim_save", 0, 0, 1, false, null);
		new MiMSave(save);
		
		CommandConstructor mim = new CommandConstructor(CommandExecuteType.MIM, "mim", false,
				saveAndKick, save);
		registerCommand(mim.getPath(), mim.getName());
		getCommand(mim.getName()).setExecutor(new MiMCmdExecutor(plugin, mim));
		getCommand(mim.getName()).setTabCompleter(tab);
		
		CommandConstructor gm = new CommandConstructor(CommandExecuteType.GAMEMODE, "gm", false);
		registerCommand(gm.getPath(), gm.getName());
		getCommand(gm.getName()).setExecutor(new GameModeCmdExecutor(plugin, gm));
		getCommand(gm.getName()).setTabCompleter(tab);
		
		CommandConstructor workbench = new CommandConstructor(CommandExecuteType.WORKBENCH, "workbench", false);
		registerCommand(workbench.getPath(), workbench.getName());
		getCommand(workbench.getName()).setExecutor(new WorkbenchCmdExecutor(plugin, workbench));
		getCommand(workbench.getName()).setTabCompleter(tab);
		
		CommandConstructor enderchest = new CommandConstructor(CommandExecuteType.ENDERCHEST, "enderchest", false);
		registerCommand(enderchest.getPath(), enderchest.getName());
		getCommand(enderchest.getName()).setExecutor(new EnderChestCmdExecutor(plugin, enderchest));
		getCommand(enderchest.getName()).setTabCompleter(tab);
		
		CommandConstructor enchantingtable = new CommandConstructor(CommandExecuteType.ENCHANTINGTABLE, "enchantingtable", false);
		registerCommand(enchantingtable.getPath(), enchantingtable.getName());
		getCommand(enchantingtable.getName()).setExecutor(new EnchantingTableCmdExecutor(plugin, enchantingtable));
		getCommand(enchantingtable.getName()).setTabCompleter(tab);
		
		CommandConstructor anvil = new CommandConstructor(CommandExecuteType.ANVIL, "anvil", false);
		registerCommand(anvil.getPath(), anvil.getName());
		getCommand(anvil.getName()).setExecutor(new AnvilCmdExecutor(plugin, anvil));
		getCommand(anvil.getName()).setTabCompleter(tab);
		
		CommandConstructor invsee = new CommandConstructor(CommandExecuteType.INVSEE, "invsee", false);
		registerCommand(invsee.getPath(), invsee.getName());
		getCommand(invsee.getName()).setExecutor(new InventorySeeCmdExecutor(plugin, invsee));
		getCommand(invsee.getName()).setTabCompleter(tab);
		
		CommandConstructor armorsee = new CommandConstructor(CommandExecuteType.ARMORSEE, "armorsee", false);
		registerCommand(armorsee.getPath(), armorsee.getName());
		getCommand(armorsee.getName()).setExecutor(new ArmorSeeCmdExecutor(plugin, armorsee));
		getCommand(armorsee.getName()).setTabCompleter(tab);
		
		CommandConstructor whois = new CommandConstructor(CommandExecuteType.WHOIS, "whois", false);
		registerCommand(whois.getPath(), whois.getName());
		getCommand(whois.getName()).setExecutor(new WhoIsCmdExecutor(plugin, whois));
		getCommand(whois.getName()).setTabCompleter(tab);
		
		CommandConstructor online = new CommandConstructor(CommandExecuteType.ONLINE, "online", false);
		registerCommand(online.getPath(), online.getName());
		getCommand(online.getName()).setExecutor(new OnlineCmdExecutor(plugin, online));
		getCommand(online.getName()).setTabCompleter(tab);
		
		setupCmdClear(tab);
		setupCmdCustomPlayerInventory(tab);
	}
	
	private void setupCmdClear(TabCompletion tab)
	{
		ArgumentConstructor armor = new ArgumentConstructor(CommandExecuteType.CLEAR_ARMOR, "clear_armor", 0, 1, 2, false, null);
		new ClearSub(armor, SyncType.INV_ARMOR);
		
		ArgumentConstructor attribute = new ArgumentConstructor(CommandExecuteType.CLEAR_ATTRIBUTE, "clear_attribute", 0, 1, 2, false, null);
		new ClearSub(attribute, SyncType.ATTRIBUTE);
		
		ArgumentConstructor ec = new ArgumentConstructor(CommandExecuteType.CLEAR_EC, "clear_ec", 0, 1, 2, false, null);
		new ClearSub(ec, SyncType.INV_ENDERCHEST);
		
		ArgumentConstructor effect = new ArgumentConstructor(CommandExecuteType.CLEAR_EFFECT, "clear_effect", 0, 1, 2, false, null);
		new ClearSub(effect, SyncType.EFFECT);
		
		ArgumentConstructor exp = new ArgumentConstructor(CommandExecuteType.CLEAR_EXP, "clear_exp", 0, 1, 2, false, null);
		new ClearSub(exp, SyncType.EXP);
		
		ArgumentConstructor full = new ArgumentConstructor(CommandExecuteType.CLEAR_FULL, "clear_full", 0, 1, 2, false, null);
		new ClearSub(full, SyncType.FULL);
		
		ArgumentConstructor offhand = new ArgumentConstructor(CommandExecuteType.CLEAR_OFFHAND, "clear_offhand", 0, 1, 2, false, null);
		new ClearSub(offhand, SyncType.INV_OFFHAND);
		
		ArgumentConstructor invonly = new ArgumentConstructor(CommandExecuteType.CLEAR_INVONLY, "clear_invonly", 0, 1, 2, false, null);
		new ClearSub(invonly, SyncType.INV_ONLY);
		
		ArgumentConstructor inv = new ArgumentConstructor(CommandExecuteType.CLEAR_INV, "clear_inv", 0, 1, 2, false, null);
		new ClearSub(inv, SyncType.INVENTORY);
		
		ArgumentConstructor pd = new ArgumentConstructor(CommandExecuteType.CLEAR_PERSISTENTDATA, "clear_persistentdata", 0, 1, 2, false, null);
		new ClearSub(pd, SyncType.PERSITENTDATA);
		
		CommandConstructor clear = new CommandConstructor(CommandExecuteType.CLEAR, "clear", false,
				armor, attribute, ec, effect, exp, full, offhand, invonly, inv, pd);
		registerCommand(clear.getPath(), clear.getName());
		getCommand(clear.getName()).setExecutor(new ArmorSeeCmdExecutor(plugin, clear));
		getCommand(clear.getName()).setTabCompleter(tab);
	}
	
	private void setupCmdCustomPlayerInventory(TabCompletion tab)
	{
		for(Entry<String, YamlConfiguration> cpi : yamlHandler.getCustomPlayerInventory().entrySet())
		{
			YamlConfiguration y = cpi.getValue();
			String cpiname = cpi.getKey();
			
			ArgumentConstructor buy = new ArgumentConstructor(CommandExecuteType.CPI_DROP, "Argument_buy", 0, 0, 1, false, null);
			new CPIBuy(buy, cpiname);
			
			ArgumentConstructor drop = new ArgumentConstructor(CommandExecuteType.CPI_DROP, "Argument_drop", 0, 0, 1, false, null);
			new CPIDrop(drop, cpiname);
			
			ArgumentConstructor info = new ArgumentConstructor(CommandExecuteType.CPI_SEE, "Argument_info", 0, 0, 0, false, null);
			new CPIInfo(info, cpiname);
			
			ArgumentConstructor see = new ArgumentConstructor(CommandExecuteType.CPI_SEE, "Argument_see", 0, 1, 1, false, null);
			new CPISee(see, cpiname);
			
			CommandConstructor xyz = new CommandConstructor(CommandExecuteType.CUSTOMPLAYERINVENTORY, y, "Command", false,
					buy, drop, info, see);
			registerCommand(xyz.getPath(), xyz.getName());
			getCommand(xyz.getName()).setExecutor(new CustomPlayerInventoryCmdExecutor(plugin, xyz, cpiname));
			getCommand(xyz.getName()).setTabCompleter(tab);
		}
	}
	
	private void setupBypassPerm()
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
	
	public ArrayList<BaseConstructor> getCommandHelpList()
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
		pm.registerEvents(new BlockCanBuildListener(plugin), plugin);
		pm.registerEvents(new BlockIgniteListener(plugin), plugin);
		pm.registerEvents(new BlockSignChangeListener(plugin), plugin);
		pm.registerEvents(new EntityPickupItemListener(plugin), plugin);
		pm.registerEvents(new EntityResurrectListener(plugin), plugin);
		pm.registerEvents(new EntityTameListener(plugin), plugin);
		pm.registerEvents(new InventoryCloseListener(plugin), plugin);
		pm.registerEvents(new PlayerChangedWorldListener(plugin), plugin);
		pm.registerEvents(new PlayerDeathListener(plugin), plugin);
		pm.registerEvents(new PlayerDropItemListener(plugin), plugin);
		pm.registerEvents(new PlayerExpChangeListener(plugin), plugin);
		pm.registerEvents(new PlayerGameModeChangeListener(plugin), plugin);
		pm.registerEvents(new PlayerHarvestBlockListener(plugin), plugin);
		pm.registerEvents(new PlayerItemBreakListener(plugin), plugin);
		pm.registerEvents(new PlayerItemConsumeListener(plugin), plugin);
		pm.registerEvents(new PlayerItemDamageListener(plugin), plugin);
		pm.registerEvents(new PlayerJoinListener(plugin), plugin);
		pm.registerEvents(new PlayerLevelChangeListener(plugin), plugin);
		pm.registerEvents(new PlayerQuitListener(plugin), plugin);
		pm.registerEvents(new PlayerRespawnListener(plugin), plugin);
		pm.registerEvents(new PlayerTeleportListener(plugin), plugin);
		pm.registerEvents(new PrepareItemEnchantListener(), plugin);
		pm.registerEvents(new TimeSkipListener(plugin), plugin);
		Messenger me = getServer().getMessenger();
		me.registerOutgoingPluginChannel(this, StaticValues.CMDTB_TOBUNGEE);
		if(ppmApi != null)
		{
			me.registerOutgoingPluginChannel(this, StaticValues.PP_TOBUNGEE);
			me.registerIncomingPluginChannel(this, StaticValues.PP_TOSPIGOT, ppmApi);
		}
		me.registerOutgoingPluginChannel(this, StaticValues.WHOIS_TOBUNGEE);
		me.registerIncomingPluginChannel(this, StaticValues.WHOIS_TOSPIGOT, new WhoIsListener());
		me.registerOutgoingPluginChannel(this, StaticValues.ONLINE_TOBUNGEE);
		me.registerIncomingPluginChannel(this, StaticValues.ONLINE_TOSPIGOT, new OnlineListener());
	}
	
	public ConfigHandler getConfigHandler()
	{
		return configHandler;
	}
	
	public Base64Api getBase64Api()
	{
		return base64Api;
	}
	
	public Economy getEconomy()
	{
		return ecoConsumer;
	}
	
	public net.milkbowl.vault.economy.Economy getVaultEconomy()
	{
		return ecoVault;
	}
	
	public CommandToBungeeApi getCmdToBungeeApi()
	{
		return cmdToBungeeApi;
	}
	
	public PlayerParameterApi getPlayerParameterApi()
	{
		return ppmApi;
	}
	
	public PlayerTimes getPlayerTimes()
	{
		return playerTimesConsumer;
	}
	
	public LastKnownPosition getLastKnownPosition()
	{
		return lastKnownPositionConsumer;
	}
	
	public LuckPerms getLP()
	{
		return lpapi;
	}
	
	public Vanish getVanish()
	{
		return this.vanishconsumer;
	}
	
	public boolean reload() throws IOException
	{
		yamlHandler = new YamlHandler(this);
		return true;
	}
	
	private void setupIFH()
	{
		setupBase64();
		setupEconomy();
		setupCommandToBungee();
		
		setupPlayerParameter();
		setupPlayerTimes();
		setupLastKnownPosition();
		setupVanisch();
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
	
	private boolean setupCommandToBungee()
	{      
		if(!plugin.getServer().getPluginManager().isPluginEnabled("InterfaceHub")) 
	    {
			log.severe("IFH is not set in the Plugin " + pluginName + "! Disable plugin!");
			Bukkit.getPluginManager().getPlugin(pluginName).getPluginLoader().disablePlugin(this);
	    	return false;
	    }
		cmdToBungeeApi = new CommandToBungeeApi(plugin);
    	plugin.getServer().getServicesManager().register(
        main.java.me.avankziar.ifh.spigot.tobungee.commands.CommandToBungee.class,
        cmdToBungeeApi,
        this,
        ServicePriority.Normal);
    	log.info(pluginName + " detected InterfaceHub >>> CommandToBungee.class is provided!");
		return true;
	}
	
	private boolean setupPlayerParameter()
	{      
		if(!plugin.getServer().getPluginManager().isPluginEnabled("InterfaceHub")) 
	    {
			log.severe("IFH is not set in the Plugin " + pluginName + "! Disable plugin!");
			Bukkit.getPluginManager().getPlugin(pluginName).getPluginLoader().disablePlugin(this);
	    	return false;
	    }
		ppmApi = new PlayerParameterApi(plugin);
    	plugin.getServer().getServicesManager().register(
        main.java.me.avankziar.ifh.spigot.synchronization.PlayerParameter.class,
        ppmApi,
        this,
        ServicePriority.Normal);
    	log.info(pluginName + " detected InterfaceHub >>> Base64.class is provided!");
		return true;
	}
	
	private void setupEconomy()
    {
		if(!plugin.getServer().getPluginManager().isPluginEnabled("InterfaceHub")) 
	    {
	    	return;
	    }
		new BukkitRunnable()
        {
        	int i = 0;
			@Override
			public void run()
			{
			    if(i == 20)
			    {
				cancel();
				return;
			    }
			    RegisteredServiceProvider<main.java.me.avankziar.ifh.spigot.economy.Economy> rsp = 
		                         getServer().getServicesManager().getRegistration(Economy.class);
			    if (rsp == null) 
			    {
			    	i++;
			        return;
			    }
			    ecoConsumer = rsp.getProvider();
			    log.info(pluginName + " detected InterfaceHub >>> Economy.class is consumed!");
			    cancel();
			}
        }.runTaskTimer(plugin, 20L, 20*2);
        return;
    }
	
	private void setupVaultEconomy()
    {
		if(!plugin.getServer().getPluginManager().isPluginEnabled("Vault")) 
	    {
	    	return;
	    }
		new BukkitRunnable()
        {
        	int i = 0;
			@Override
			public void run()
			{
			    if(i == 20)
			    {
				cancel();
				return;
			    }
			    RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> rsp = 
		                         getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
			    if (rsp == null) 
			    {
			    	i++;
			        return;
			    }
			    ecoVault = rsp.getProvider();
			    log.info(pluginName + " detected Vault >>> Economy.class is consumed!");
			    cancel();
			}
        }.runTaskTimer(plugin, 20L, 20*2);
        return;
    }
	
	private void setupLuckPerm()
	{
		if(!plugin.getServer().getPluginManager().isPluginEnabled("LuckPerms")) 
	    {
	    	return;
	    }
		new BukkitRunnable()
        {
        	int i = 0;
			@Override
			public void run()
			{
			    if(i == 20)
			    {
				cancel();
				return;
			    }
			    RegisteredServiceProvider<LuckPerms> rsp = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
			    if (rsp == null) 
			    {
			    	i++;
			        return;
			    }
			    lpapi = rsp.getProvider();
			    log.info(pluginName + " detected LuckPerms >>> Hooking!");
			    cancel();
			}
        }.runTaskTimer(plugin, 20L, 20*2);
        return;
	}
	
	private void setupPlayerTimes()
    {
		if(!plugin.getServer().getPluginManager().isPluginEnabled("InterfaceHub")) 
	    {
	    	return;
	    }
		new BukkitRunnable()
        {
        	int i = 0;
			@Override
			public void run()
			{
			    if(i == 20)
			    {
				cancel();
				return;
			    }
			    RegisteredServiceProvider<main.java.me.avankziar.ifh.general.interfaces.PlayerTimes> rsp = 
		                         getServer().getServicesManager().getRegistration(PlayerTimes.class);
			    if (rsp == null) 
			    {
			    	i++;
			        return;
			    }
			    playerTimesConsumer = rsp.getProvider();
			    log.info(pluginName + " detected InterfaceHub >>> PlayerTimes.class is consumed!");
			    cancel();
			}
        }.runTaskTimer(plugin, 20L, 20*2);
        return;
    }
	
	private void setupLastKnownPosition()
    {
		if(!plugin.getServer().getPluginManager().isPluginEnabled("InterfaceHub")) 
	    {
	    	return;
	    }
		new BukkitRunnable()
        {
        	int i = 0;
			@Override
			public void run()
			{
			    if(i == 20)
			    {
				cancel();
				return;
			    }
			    RegisteredServiceProvider<main.java.me.avankziar.ifh.spigot.position.LastKnownPosition> rsp = 
		                         getServer().getServicesManager().getRegistration(LastKnownPosition.class);
			    if (rsp == null) 
			    {
			    	i++;
			        return;
			    }
			    lastKnownPositionConsumer = rsp.getProvider();
			    log.info(pluginName + " detected InterfaceHub >>> LastKnownPosition.class is consumed!");
			    cancel();
			}
        }.runTaskTimer(plugin, 20L, 20*2);
        return;
    }
	
	private void setupVanisch()
	{ 
		if(!plugin.getServer().getPluginManager().isPluginEnabled("InterfaceHub")) 
	    {
	    	return;
	    }
        if(yamlHandler.getConfig().getBoolean("Enable.InterfaceHub.Consuming.Vanish", false))
		{
            new BukkitRunnable()
            {
            	int i = 0;
    			@Override
    			public void run()
    			{
    			    if(i == 20)
    			    {
    				cancel();
    				return;
    			    }
    			    RegisteredServiceProvider<main.java.me.avankziar.ifh.spigot.interfaces.Vanish> rsp = 
    		                         getServer().getServicesManager().getRegistration(Vanish.class);
    			    if (rsp == null) 
    			    {
    			    	i++;
    			        return;
    			    }
    			    vanishconsumer = rsp.getProvider();
    			    log.info(pluginName + " detected InterfaceHub >>> Vanish.class is consumed!");
    			    cancel();
    			}
            }.runTaskTimer(plugin, 20L, 20*2);
		}
	}
	
	public ArrayList<String> getPlayers()
	{
		return players;
	}

	public void setPlayers(ArrayList<String> players)
	{
		this.players = players;
	}
	
	public void setupPlayers()
	{
		ArrayList<PlayerData> pd = new ArrayList<>();
		try
		{
			pd = PlayerData.convert(plugin.getMysqlHandler().getFullList(MysqlHandler.Type.PLAYERDATA, "`id` ASC", "?", 1));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		ArrayList<String> cus = new ArrayList<>();
		for(PlayerData chus : pd) 
		{
			cus.add(chus.getName());	
		}
		Collections.sort(cus);
		setPlayers(cus);
	}
}