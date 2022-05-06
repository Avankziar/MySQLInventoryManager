package main.java.me.avankziar.mim.spigot.database;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.database.Language.ISO639_2B;

public class YamlHandler
{
	private MIM plugin;
	private File config = null;
	private YamlConfiguration cfg = new YamlConfiguration();
	
	private File commands = null;
	private YamlConfiguration com = new YamlConfiguration();
	
	private File server = null;
	private YamlConfiguration ser = new YamlConfiguration();
	
	private LinkedHashMap<String, YamlConfiguration> worlds = new LinkedHashMap<>();
	
	private LinkedHashMap<String, YamlConfiguration> customPlayerInventory = new LinkedHashMap<>();
	
	private String languages;
	private File language = null;
	private YamlConfiguration lang = new YamlConfiguration();

	public YamlHandler(MIM plugin)
	{
		this.plugin = plugin;
		loadYamlHandler();
	}
	
	public YamlConfiguration getConfig()
	{
		return cfg;
	}
	
	public YamlConfiguration getCommands()
	{
		return com;
	}
	
	public YamlConfiguration getLang()
	{
		return lang;
	}
	
	public YamlConfiguration getSynServer()
	{
		return ser;
	}
	
	public YamlConfiguration getSyncWorld(World world)
	{
		return worlds.get(world.getName());
	}
	
	public LinkedHashMap<String, YamlConfiguration> getCustomPlayerInventory()
	{
		return customPlayerInventory;
	}
	
	public YamlConfiguration getCustomPlayerInventory(String uniquename)
	{
		return customPlayerInventory.get(uniquename);
	}
	
	private YamlConfiguration loadYamlTask(File file, YamlConfiguration yaml)
	{
		try 
		{
			yaml.load(file);
		} catch (IOException | InvalidConfigurationException e) 
		{
			MIM.log.severe(
					"Could not load the %file% file! You need to regenerate the %file%! Error: ".replace("%file%", file.getName())
					+ e.getMessage());
			e.printStackTrace();
		}
		return yaml;
	}
	
	private boolean writeFile(File file, YamlConfiguration yml, LinkedHashMap<String, Language> keyMap)
	{
		yml.options().header("For more explanation see \n Your pluginsite");
		for(String key : keyMap.keySet())
		{
			Language languageObject = keyMap.get(key);
			if(languageObject.languageValues.containsKey(plugin.getYamlManager().getLanguageType()) == true)
			{
				plugin.getYamlManager().setFileInput(yml, keyMap, key, plugin.getYamlManager().getLanguageType());
			} else if(languageObject.languageValues.containsKey(plugin.getYamlManager().getDefaultLanguageType()) == true)
			{
				plugin.getYamlManager().setFileInput(yml, keyMap, key, plugin.getYamlManager().getDefaultLanguageType());
			}
		}
		try
		{
			yml.save(file);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return true;
	}
	
	public boolean loadYamlHandler()
	{
		/*
		 * Init all path from all yamls files
		 */
		plugin.setYamlManager(new YamlManager());
		/*
		 * Load all files, which are unique, for examples config.yml, commands.yml etc.
		 */
		if(!mkdirStaticFiles())
		{
			return false;
		}
		/*
		 * Load all files, which exist mutiple versions. Languages etc.
		 */
		if(!mkdirDynamicFiles())
		{
			return false;
		}
		return true;
	}
	
	public boolean mkdirStaticFiles()
	{
		/*
		 * Create the plugin general directory
		 */
		File directory = new File(plugin.getDataFolder()+"");
		if(!directory.exists())
		{
			directory.mkdir();
		}
		/*
		 * Init config.yml
		 */
		config = new File(plugin.getDataFolder(), "config.yml");
		if(!config.exists()) 
		{
			MIM.log.info("Create config.yml...");
			try(InputStream in = plugin.getResource("default.yml"))
			{
				/*
				 * If config.yml dont exist in the main directory, than create config.yml as empty file
				 */
				Files.copy(in, config.toPath());
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		/*
		 * Load the config.yml
		 */
		cfg = loadYamlTask(config, cfg);
		if (cfg == null)
		{
			return false;
		}
		/*
		 * Write all path for the configfile
		 * Make sure, you use the right linkedHashmap from the YamlManager
		 */
		writeFile(config, cfg, plugin.getYamlManager().getConfigSpigotKey());
		/*
		 * Define the language
		 */
		languages = cfg.getString("Language", "ENG").toUpperCase();
		/*
		 * Repeat for all other single flatfiles.
		 */
		commands = new File(plugin.getDataFolder(), "commands.yml");
		if(!commands.exists()) 
		{
			MIM.log.info("Create commands.yml...");
			try(InputStream in = plugin.getResource("default.yml"))
			{
				//Erstellung einer "leere" config.yml
				Files.copy(in, commands.toPath());
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		com = loadYamlTask(commands, com);
		if (com == null)
		{
			return false;
		}
		writeFile(commands, com, plugin.getYamlManager().getCommandsKey());
		return true;
	}
	
	private boolean mkdirDynamicFiles()
	{
		//Compare the using languages and set this as standard
		List<Language.ISO639_2B> types = new ArrayList<Language.ISO639_2B>(EnumSet.allOf(Language.ISO639_2B.class));
		ISO639_2B languageType = ISO639_2B.ENG;
		for(ISO639_2B type : types)
		{
			if(type.toString().equals(languages))
			{
				languageType = type;
				break;
			}
		}
		
		plugin.getYamlManager().setLanguageType(languageType);
		
		if(!mkdirLanguage())
		{
			return false;
		}
		
		if(!mkdirSyncro())
		{
			return false;
		}
		
		if(!mkdirCustomPlayerInventory())
		{
			return false;
		}
		return true;
	}
	
	private boolean mkdirLanguage()
	{
		/*
		 * Making the "prefix" thing for the filename.
		 */
		String languageString = plugin.getYamlManager().getLanguageType().toString().toLowerCase();
		/*
		 * Adding a new directory
		 */
		File directory = new File(plugin.getDataFolder()+"/Languages/");
		if(!directory.exists())
		{
			directory.mkdir();
		}
		/*
		 * The rest is equals part from the config.yml
		 */
		language = new File(directory.getPath(), languageString+".yml");
		if(!language.exists()) 
		{
			MIM.log.info("Create %lang%.yml...".replace("%lang%", languageString));
			try(InputStream in = plugin.getResource("default.yml"))
			{
				Files.copy(in, language.toPath());
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		lang = loadYamlTask(language, lang);
		if (lang == null)
		{
			return false;
		}
		writeFile(language, lang, plugin.getYamlManager().getLanguageKey());
		return true;
	}
	
	private boolean mkdirSyncro()
	{
		File directory = new File(plugin.getDataFolder()+"/Synchronization/");
		if(!directory.exists())
		{
			directory.mkdir();
		}
		/*
		 * The rest is equals part from the config.yml
		 */
		server = new File(directory.getPath(), "server.yml");
		if(!server.exists()) 
		{
			MIM.log.info("Create %lang%.yml...".replace("%lang%", "server"));
			try(InputStream in = plugin.getResource("default.yml"))
			{
				Files.copy(in, server.toPath());
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		ser = loadYamlTask(server, ser);
		if (ser == null)
		{
			return false;
		}
		writeFile(server, ser, plugin.getYamlManager().getSyncKey());
		for(World w : Bukkit.getWorlds())
		{
			File world = new File(directory.getPath(), w.getName()+".yml");
			if(!world.exists()) 
			{
				MIM.log.info("Create %lang%.yml...".replace("%lang%", w.getName()));
				try(InputStream in = plugin.getResource("default.yml"))
				{
					Files.copy(in, world.toPath());
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			YamlConfiguration wo = loadYamlTask(world, new YamlConfiguration());
			if (wo == null)
			{
				return false;
			}
			writeFile(world, wo, plugin.getYamlManager().getWorldKey());
			writeFile(world, wo, plugin.getYamlManager().getSyncKey());
			worlds.put(w.getName(), wo);
		}
		return true;
	}
	
	private boolean mkdirCustomPlayerInventory()
	{
		File directory = new File(plugin.getDataFolder()+"/CustomPlayerInventorys/");
		if(!directory.exists())
		{
			directory.mkdir();
		}
		File[] listOfFiles = directory.listFiles();
		if(listOfFiles.length == 0)
		{
			File defaults = new File(plugin.getDataFolder(), "default.yml");
			if(!defaults.exists()) 
			{
				MIM.log.info("Create default.yml...");
				try(InputStream in = plugin.getResource("default.yml"))
				{
					//Erstellung einer "leere" config.yml
					Files.copy(in, defaults.toPath());
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			YamlConfiguration def = loadYamlTask(defaults, new YamlConfiguration());
			if (def == null)
			{
				return false;
			}
			writeFile(defaults, def, plugin.getYamlManager().getCustomInventoryKey().get("default"));
		} else
		{
			for(int i = 0; i < listOfFiles.length; i++)
			{
				if(listOfFiles[i].isFile())
				{
					YamlConfiguration f = loadYamlTask(listOfFiles[i], new YamlConfiguration());
					if (f == null)
					{
						return false;
					}
					String uniquename = f.getString("UniqueName");
					if(uniquename != null)
					{
						customPlayerInventory.put(uniquename, f);
					}
				}
			}
		}
		return true;
	}
}