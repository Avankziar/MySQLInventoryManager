package main.java.me.avankziar.mim.spigot.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

import main.java.me.avankziar.mim.spigot.database.Language.ISO639_2B;
import main.java.me.avankziar.mim.spigot.listener.BaseListener;
import main.java.me.avankziar.mim.spigot.permission.Bypass;

public class YamlManager
{
	private ISO639_2B languageType = ISO639_2B.GER;
	//The default language of your plugin. Mine is german.
	private ISO639_2B defaultLanguageType = ISO639_2B.GER;
	
	//Per Flatfile a linkedhashmap.
	private static LinkedHashMap<String, Language> configSpigotKeys = new LinkedHashMap<>();
	private static LinkedHashMap<String, Language> commandsKeys = new LinkedHashMap<>();
	private static LinkedHashMap<String, Language> languageKeys = new LinkedHashMap<>();
	private static LinkedHashMap<String, Language> worldKeys = new LinkedHashMap<>();
	private static LinkedHashMap<String, Language> syncKeys = new LinkedHashMap<>();
	/*
	 * Here are mutiplefiles in one "double" map. The first String key is the filename
	 * So all filename must be predefine. For example in the config.
	 */
	private static LinkedHashMap<String, LinkedHashMap<String, Language>> customInventoryKeys = new LinkedHashMap<>();
	
	public YamlManager()
	{
		initConfig();
		initCommands();
		initLanguage();
		initSync();
		initCustomPlayerInventory();
	}
	
	public ISO639_2B getLanguageType()
	{
		return languageType;
	}

	public void setLanguageType(ISO639_2B languageType)
	{
		this.languageType = languageType;
	}
	
	public ISO639_2B getDefaultLanguageType()
	{
		return defaultLanguageType;
	}
	
	public LinkedHashMap<String, Language> getConfigSpigotKey()
	{
		return configSpigotKeys;
	}
	
	public LinkedHashMap<String, Language> getCommandsKey()
	{
		return commandsKeys;
	}
	
	public LinkedHashMap<String, Language> getLanguageKey()
	{
		return languageKeys;
	}
	
	public LinkedHashMap<String, Language> getWorldKey()
	{
		return worldKeys;
	}
	
	public LinkedHashMap<String, Language> getSyncKey()
	{
		return syncKeys;
	}
	
	public LinkedHashMap<String, LinkedHashMap<String, Language>> getCustomInventoryKey()
	{
		return customInventoryKeys;
	}
	
	/*
	 * The main method to set all paths in the yamls.
	 */
	public void setFileInput(YamlConfiguration yml, LinkedHashMap<String, Language> keyMap, String key, ISO639_2B languageType)
	{
		if(!keyMap.containsKey(key))
		{
			return;
		}
		if(yml.get(key) != null)
		{
			return;
		}
		if(keyMap.get(key).languageValues.get(languageType).length == 1)
		{
			if(keyMap.get(key).languageValues.get(languageType)[0] instanceof String)
			{
				yml.set(key, ((String) keyMap.get(key).languageValues.get(languageType)[0]).replace("\r\n", ""));
			} else
			{
				yml.set(key, keyMap.get(key).languageValues.get(languageType)[0]);
			}
		} else
		{
			List<Object> list = Arrays.asList(keyMap.get(key).languageValues.get(languageType));
			ArrayList<String> stringList = new ArrayList<>();
			if(list instanceof List<?>)
			{
				for(Object o : list)
				{
					if(o instanceof String)
					{
						stringList.add(((String) o).replace("\r\n", ""));
					} else
					{
						stringList.add(o.toString().replace("\r\n", ""));
					}
				}
			}
			yml.set(key, (List<String>) stringList);
		}
	}
	
	public void initConfig() //INFO:Config
	{		
		configSpigotKeys.put("Language"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"ENG"}));
		configSpigotKeys.put("Mysql.Status"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				false}));
		configSpigotKeys.put("Mysql.Host"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"127.0.0.1"}));
		configSpigotKeys.put("Mysql.Port"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				3306}));
		configSpigotKeys.put("Mysql.DatabaseName"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"mydatabase"}));
		configSpigotKeys.put("Mysql.SSLEnabled"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				false}));
		configSpigotKeys.put("Mysql.AutoReconnect"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				true}));
		configSpigotKeys.put("Mysql.VerifyServerCertificate"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				false}));
		configSpigotKeys.put("Mysql.User"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"admin"}));
		configSpigotKeys.put("Mysql.Password"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"not_0123456789"}));
		
		configSpigotKeys.put("SleepMode"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				false}));
		
		configSpigotKeys.put("Default.ClearToggle"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				false}));
		configSpigotKeys.put("SaveOption.Invulnerable"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				false}));
	}
	
	public void initCommands()
	{
		comBypass();
		commandsInput("mim", "mim", "mim.cmd.mim", 
				"/mim [pagenumber]", "/mim ",
				"&c/mim &f| Infoseite für alle Befehle.",
				"&c/mim &f| Info page for all commands.");
		String basePermission = "mim.cmd.mim";
		argumentInput("mim_saveandkick", "saveandkick", basePermission,
				"/mim saveandkick [servername]", "/mim saveandkick ",
				"&c/mim saveandkick [servername] &f| Speichert den vollen Spielstand aller Spieler und kick dies dann. Optional nur für einen Server.",
				"&c/mim saveandkick [servername] &f| Saves the full score of all players and then kick those. Optional for one server only.");
		argumentInput("mim_save", "save", basePermission,
				"/mim save [servername]", "/mim save ",
				"&c/mim save [servername] &f| Speichert den vollen Spielstand aller Spieler. Optional nur für einen Server.",
				"&c/mim save [servername] &f| Saves the full score of all players. Optional for one server only.");
		commandsInput("gm", "gm", "gm.cmd.gm", 
				"/gm <number> [playername]", "/gm ",
				"&c/gm <Zahl> [Spielername] &f| Wechselt den GameMode. Optional für andere Spieler.",
				"&c/gm <number> [playername] &f| Switches the GameMode. Optional for other players.");
		commandsInput("workbench", "wbench", "workbench.cmd.wbench", 
				"/wbench", "/wbench ",
				"&c/wbench &f| Öffnet eine Werkbank. Optional für andere Spieler.",
				"&c/wbench &f| Opens a workbench. Optional for other players.");
		commandsInput("enderchest", "enderchest", "enderchest.cmd.enderchest", 
				"/enderchest [playername]", "/enderchest ",
				"&c/enderchest [Spielername] &f| Öffnet deine Enderchest. Optional für andere Spieler.",
				"&c/enderchest [playername] &f| Opens your enderchest. Optional for other players.");
		commandsInput("enchantingtable", "enchantingtable", "enchantingtable.cmd.enchantingtable", 
				"/enchantingtable", "/enchantingtable ",
				"&c/enchantingtable &f| Öffnet einen Verzauberungstisch der maximalen Stufe. Optional für andere Spieler.",
				"&c/enchantingtable &f| Opens a enchanting table of the maximum level. Optional for other players.");
		commandsInput("invsee", "invsee", "invsee.cmd.invsee", 
				"/invsee <playername>", "/invsee ",
				"&c/invsee <Spielername> &f| Öffnet das Inventar des Spielers.",
				"&c/invsee <playername> &f| Opens the inventory of that player.");
		commandsInput("armorsee", "armorsee", "armorsee.cmd.armorsee", 
				"/armorsee <playername>", "/armorsee ",
				"&c/armorsee <Spielername> &f| Öffnet die Rüstungs- und Offhand Ansicht des Spielers.",
				"&c/armorsee <playername> &f| Opens the armor and offhand view of the player.");
		commandsInput("clear", "clear", "clear.cmd.clear", 
				"/clear <type> [playername]", "/clear ",
				"&c/clear <Type> [Spielername] &f| Löscht und resettet bestimmte Sektionen der Spielerdaten. Zielspieler muss auf dem gleichen Server sein.",
				"&c/clear <type> [playername] &f| Deletes and resets certain sections of the player data. Target player must be on the same server.");
		argumentInput("clear_armor", "armor", basePermission,
				"/clear armor [playername]", "/clear armor ",
				"&c/clear armor [Spielername] &f| Löscht und resettet bestimmte Sektionen der Spielerdaten. Zielspieler muss auf dem gleichen Server sein.",
				"&c/clear armor [playername] &f| Deletes and resets certain sections of the player data. Target player must be on the same server.");
		argumentInput("clear_attribute", "attribute", basePermission,
				"/clear attribute [playername]", "/clear attribute ",
				"&c/clear attribute [Spielername] &f| Löscht und resettet bestimmte Sektionen der Spielerdaten. Zielspieler muss auf dem gleichen Server sein.",
				"&c/clear attribute [playername] &f| Deletes and resets certain sections of the player data. Target player must be on the same server.");
		argumentInput("clear_ec", "enderchest", basePermission,
				"/clear enderchest [playername]", "/clear enderchest ",
				"&c/clear enderchest [Spielername] &f| Löscht und resettet bestimmte Sektionen der Spielerdaten. Zielspieler muss auf dem gleichen Server sein.",
				"&c/clear enderchest [playername] &f| Deletes and resets certain sections of the player data. Target player must be on the same server.");
		argumentInput("clear_effect", "effect", basePermission,
				"/clear effect [playername]", "/clear effect ",
				"&c/clear effect [Spielername] &f| Löscht und resettet bestimmte Sektionen der Spielerdaten. Zielspieler muss auf dem gleichen Server sein.",
				"&c/clear effect [playername] &f| Deletes and resets certain sections of the player data. Target player must be on the same server.");
		argumentInput("clear_exp", "exp", basePermission,
				"/clear exp [playername]", "/clear exp ",
				"&c/clear exp [Spielername] &f| Löscht und resettet bestimmte Sektionen der Spielerdaten. Zielspieler muss auf dem gleichen Server sein.",
				"&c/clear exp [playername] &f| Deletes and resets certain sections of the player data. Target player must be on the same server.");
		argumentInput("clear_full", "full", basePermission,
				"/clear full [playername]", "/clear full ",
				"&c/clear full [Spielername] &f| Löscht und resettet bestimmte Sektionen der Spielerdaten. Zielspieler muss auf dem gleichen Server sein.",
				"&c/clear full [playername] &f| Deletes and resets certain sections of the player data. Target player must be on the same server.");
		argumentInput("clear_offhand", "offhand", basePermission,
				"/clear offhand [playername]", "/clear offhand ",
				"&c/clear offhand [Spielername] &f| Löscht und resettet bestimmte Sektionen der Spielerdaten. Zielspieler muss auf dem gleichen Server sein.",
				"&c/clear offhand [playername] &f| Deletes and resets certain sections of the player data. Target player must be on the same server.");
		argumentInput("clear_invonly", "inventoryonyl", basePermission,
				"/clear inventoryonyl [playername]", "/clear inventoryonyl ",
				"&c/clear inventoryonyl [Spielername] &f| Löscht und resettet bestimmte Sektionen der Spielerdaten. Zielspieler muss auf dem gleichen Server sein.",
				"&c/clear inventoryonyl [playername] &f| Deletes and resets certain sections of the player data. Target player must be on the same server.");
		argumentInput("clear_inv", "inv", basePermission,
				"/clear inv [playername]", "/clear inv ",
				"&c/clear inv [Spielername] &f| Löscht und resettet bestimmte Sektionen der Spielerdaten. Zielspieler muss auf dem gleichen Server sein.",
				"&c/clear inv [playername] &f| Deletes and resets certain sections of the player data. Target player must be on the same server.");
		argumentInput("clear_persistentdata", "persistentdata", basePermission,
				"/clear persistentdata [playername]", "/clear persistentdata ",
				"&c/clear persistentdata [Spielername] &f| Löscht und resettet bestimmte Sektionen der Spielerdaten. Zielspieler muss auf dem gleichen Server sein.",
				"&c/clear persistentdata [playername] &f| Deletes and resets certain sections of the player data. Target player must be on the same server.");
		commandsInput("whois", "whois", "whois.cmd.whois", 
				"/whois [playername]", "/whois ",
				"&c/whois [Spielername] &f| Öffnet generalisierte Infos zu dem Spieler.",
				"&c/whois [playername] &f| Opens generalized info about the player.");
		commandsInput("online", "online", "online.cmd.online", 
				"/online", "/online ",
				"&c/online &f| Zeigt alle Onlinespieler in Proxynetzwerk an.",
				"&c/online &f| Shows all online player in the proxy network.");
		commandsInput("fly", "fly", "fly.cmd.fly", 
				"/fly", "/fly ",
				"&c/fly [Spielername] &f| Toggelt den Flugmodus",
				"&c/fly [Spielername] &f| Toggle the flying modus.");
		commandsInput("attributes", "attributes", "attributes.cmd.attributes", 
				"/attributes <attribute> <value>", "/attributes ",
				"&c/attributes <Attribute> <Wert> &f| Verändert einen Wert eines der Spielereigenschaften.",
				"&c/attributes <attribute> <value> &f| Changes a value of one of the player properties.");
	}
	
	private void comBypass() //INFO:ComBypass
	{
		List<Bypass.Permission> list = new ArrayList<Bypass.Permission>(EnumSet.allOf(Bypass.Permission.class));
		for(Bypass.Permission ept : list)
		{
			commandsKeys.put("Bypass."+ept.toString().replace("_", ".")
					, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
					"mim."+ept.toString().toLowerCase().replace("_", ".")}));
		}
	}
	
	private void commandsInput(String path, String name, String basePermission, 
			String suggestion, String commandString,
			String helpInfoGerman, String helpInfoEnglish)
	{
		commandsKeys.put(path+".Name"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				name}));
		commandsKeys.put(path+".Permission"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				basePermission}));
		commandsKeys.put(path+".Suggestion"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				suggestion}));
		commandsKeys.put(path+".CommandString"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				commandString}));
		commandsKeys.put(path+".HelpInfo"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				helpInfoGerman,
				helpInfoEnglish}));
	}
	
	private void argumentInput(String path, String argument, String basePermission, 
			String suggestion, String commandString,
			String helpInfoGerman, String helpInfoEnglish)
	{
		commandsKeys.put(path+".Argument"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				argument}));
		commandsKeys.put(path+".Permission"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				basePermission+"."+argument}));
		commandsKeys.put(path+".Suggestion"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				suggestion}));
		commandsKeys.put(path+".CommandString"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				commandString}));
		commandsKeys.put(path+".HelpInfo"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				helpInfoGerman,
				helpInfoEnglish}));
	}
	
	private void initLanguage() //INFO:Languages
	{
		languageKeys.put("NoPermission"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&cDu hast dafür keine Rechte!",
				"&cYou have no rights!"}));
		languageKeys.put("InputIsWrong",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&cDeine Eingabe ist fehlerhaft! Klicke hier auf den Text, um weitere Infos zu bekommen!",
				"&cYour input is incorrect! Click here on the text to get more information!"}));
		languageKeys.put("PlayerNotExist"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&cDer Spieler existiert nicht!",
				"&cThe player does not exist!"}));
		languageKeys.put("PlayerNotOnline"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&cDer Spieler ist nicht online!",
				"&cThe player is not online!"}));
		languageKeys.put("CommandWorkOnlyForPlayer"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&cBefehl, wie er angegeben ist, wirk nur für Spieler!",
				"&cCommand as it is specified, work only for players!"}));
		languageKeys.put("DidYouMean"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&cMeintest du:",
				"&cDid you mean:"}));
		languageKeys.put("Boolean.True"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&aJa",
				"&aYes"}));
		languageKeys.put("Boolean.False"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&cNein",
				"&cNo"}));
		languageKeys.put("NotOnline"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&cNicht Online",
				"&cNot online"}));
		languageKeys.put("PlayerIsOnlineOnAnotherServer"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&eDer Spieler &f%player% &eist auf dem Server &f%server% &eonline. Bitte begib dich dorthin um in sein Inventar zu sehen.",
				"&eThe player &f%player% &eis on the server &f%server% &eonline. Please go there to see his inventory."}));
		languageKeys.put("Headline"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&f=====&cMIM&f=====",
				"&f=====&cMIM&f====="}));
		languageKeys.put("GeneralHover",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&eKlick mich!",
				"&eClick me!"}));
		languageKeys.put("Next", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&e&nnächste Seite &e==>",
				"&e&nnext page &e==>"}));
		languageKeys.put("Past", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&e<== &nvorherige Seite",
				"&e<== &nprevious page"}));
		languageKeys.put("WhoIs.List"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&f=====&cWer ist: &6%player%&f=====",
				"&cUUID: &e%uuid%",
				"&cSpielzeiten: &f%totaltime% &f<> &a%onlinetime% &f<> &c%afktime%",
				"&cUrlaub: &f%vacation%",
				"&cKontostände: &f%money%",
				"&cLetzte Position: &e%backpos%",
				
				"&f=====&cWho is: &6%player%&f=====",
				"&cUUID: &e%uuid%",
				"&cPlaytimes: &f%totaltime% &f<> &a%onlinetime% &f<> &c%afktime%",
				"&cVacation: &f%vacation%",
				"&cAccount balances: &f%money%",
				"&cLast Position: &e%backpos%"}));
		languageKeys.put("WhoIs.OnlineList"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&cAktuelle Position: &e%pos%",
				"&cIP: &f%ip%",
				"&cGameMode: &f%gm%",
				"&cLP & Rüstung: &f%lp%/%lpmax% <> %armor%/20",
				"&cHunger: &f%hunger%/20 &cSättigung: &f%saturation%",
				"&cGesamtExp: &f%exp% <> &cLevel: &f%level%",
				"&cUnberwundbar: &f%invun%",
				"&cUnsichtbar: &f%invis%",
				"&cFlugmodus: &f%flying%",
				"&cLauf- & Fluggeschwindigkeit: &f%walkspeed% <> &f%flyspeed%",
				"&cAktive Effekte: &f%effect%",
				"&cPersistent Daten: &f%perdata%",
				
				"&cCurrent Position: &e%pos%",
				"&cIP: &f%ip%",
				"&cGameMode: &f%gm%",
				"&cLP & Armor: &f%lp%/%lpmax% <> %armor%/%armormax%",
				"&cHunger: &f%hunger%/%hungermax% &cSättigung: &f%saturation%",
				"&cTotalExp: &f%exp% <> &cLevel: &f%level%",
				"&cInvulnerable: &f%invun%",
				"&cInvisible: &f%invis%",
				"&cFlymodus: &f%flying%",
				"&cWalk- & Flyspeed: &f%walkspeed% <> &f%flyspeed%",
				"&cActive Effects: &f%effect%",
				"&cPersistent Data: &f%perdata%"}));
		languageKeys.put("Online.FirstLine"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&f=====[&6Online &f| &7Anzahl: &f%amount%]=====",
				"&f=====[&6Online &f| &7Amount: &f%amount%]====="}));
		languageKeys.put("Online.SecondLine"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&bGleicher Server &f| &cAnderer Server",
				"&bSame Server &f| &cOther Server"}));
		languageKeys.put("Online.SameServer"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&b",
				"&b"}));
		languageKeys.put("Online.OtherServer"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&c",
				"&c"}));
		initLangAttribute();
		initLangSyncTask();
		initLangGameMode();
		initLangOpenableBlocks();
		initLangCmd();
		initLangCustomPlayerInventory();
		/*languageKeys.put(""
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"",
				""}))*/
	}
	
	private void initLangAttribute()
	{
		languageKeys.put("Attribute.ValueNotExist"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&cDas Attribute &f%attribute% &cexistiert nicht!",
				"&c"}));
		languageKeys.put("Attribute.ValueNotExist"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&cDas Attribute &f%attribute% &cbenötigt einen Wert &f%value%(Zum Bsp.: %example%)&c!",
				"&c"}));
		languageKeys.put("Attribute.ValueIsSet"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&eDas Attribute &f%attribute% &ewurde auf den Wert &f%value% &egesetzt!",
				"&c"}));
	}
	
	private void initLangSyncTask()
	{
		String path = "SyncTask.";
		languageKeys.put(path+"NotFullLoaded"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&cDu wurdest noch nicht vollständig synchronisiert! Bitte warte ab, bevor du Befehle eingibst.",
				"&cYou have not been fully synchronized yet! Please wait before entering commands."}));
		languageKeys.put(path+"SavedAndKicked"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"§eDein Spielstand wurde erfolgreich gespeichert und du wurdest vom Server gekickt!",
				"§eYour game state was successfully saved and you were kicked from the server!"}));
		languageKeys.put(path+"SaveAndKickAll"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&eAlle Server werden angewiesen, alle zurzeit vorhandenen Spieler rechtzeitig zu speichern und dann zu kicken. &cBedenke, ohne aktive Whiteliste oder ähnlichem können diese aber wieder joinen!",
				"&eAll servers are instructed to save all currently existing players in time and then kick them. &cBeware, without active whitelist or similar, but they can join again!"}));
		languageKeys.put(path+"SaveAndKickServer"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&eDer Server %server% wurde angewiesen, alle zurzeit vorhandenen Spieler rechtzeitig zu speichern und dann zu kicken. &cBedenke, ohne aktive Whiteliste oder ähnlichem können diese aber wieder joinen!",
				"&eThe server %server% are instructed to save all currently existing players in time and then kick them. &cBeware, without active whitelist or similar, but they can join again!"}));
		languageKeys.put(path+"SaveAll"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&eAlle Server werden angewiesen, alle zurzeit vorhandenen Spieler rechtzeitig zu speichern.",
				"&eAll servers are instructed to save all currently existing players in time."}));
		languageKeys.put(path+"SaveServer"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&eDer Server %server% wurde angewiesen, alle zurzeit vorhandenen Spieler rechtzeitig zu speichern. &cBedenke, ohne aktive Whiteliste oder ähnlichem können diese aber wieder joinen!",
				"&eThe server %server% are instructed to save all currently existing players in time. &cBeware, without active whitelist or similar, but they can join again!"}));
		languageKeys.put(path+"PleaseConfirm"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&cBitte bestätige den Befehl mit &fbestätigen &cam Ende des Befehls.",
				"&cPlease confirm the command with &fconfirm &cat the end of the command."}));
		languageKeys.put(path+"Clear.ATTRIBUTE"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&e%player% Attribute wurden zurückgesetzt.",
				"&e%player% attributes have been reset."}));
		languageKeys.put(path+"Clear.FULL"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&e%player% Inventar, Enderchest, Rüstung, Attribute, Exp, Effekte und PersistentData wurden gelöscht und zurückgesetzt.",
				"&e%player% Inventory, Enderchest, Armor, Attributes, Exp, Effects and PersistentData have been cleared and reset."}));
		languageKeys.put(path+"Clear.EXP"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&e%player% Exp wurden zurückgesetzt.",
				"&e%player% Exp have been reset."}));
		languageKeys.put(path+"Clear.INVENTORY"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&e%player% Inventar, Enderkiste und Rüstung wurden zurückgesetzt.",
				"&e%player% Inventory, Enderchest and Armors have been reset."}));
		languageKeys.put(path+"Clear.INV_ONLY"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&e%player% Inventar wurden zurückgesetzt.",
				"&e%player% Inventory have been reset."}));
		languageKeys.put(path+"Clear.INV_ARMOR"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&e%player% Rüstung wurden zurückgesetzt.",
				"&e%player% Armor have been reset."}));
		languageKeys.put(path+"Clear.INV_OFFHAND"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&e%player% Zweithand wurden zurückgesetzt.",
				"&e%player% OffHand have been reset."}));
		languageKeys.put(path+"Clear.INV_ENDERCHEST"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&e%player% Enderkiste wurden zurückgesetzt.",
				"&e%player% Enderchest have been reset."}));
		languageKeys.put(path+"Clear.EFFECT"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&e%player% Effekte wurden zurückgesetzt.",
				"&e%player% effects have been reset."}));
		languageKeys.put(path+"Clear.PERSISTENTDATA"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&e%player% PersistentData wurden zurückgesetzt.",
				"&e%player% PersistentData have been reset."}));
	}
	
	private void initLangGameMode()
	{
		String path = "GameMode.";
		languageKeys.put(path+"SURVIVAL",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"Überleben",
				"Survival"}));
		languageKeys.put(path+"CREATIVE",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"Kreativ",
				"Creative"}));
		languageKeys.put(path+"ADVENTURE",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"Adventure",
				"Adventure"}));
		languageKeys.put(path+"SPECTATOR",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"Beobachter",
				"Spectator"}));
		languageKeys.put(path+"SetGameMode",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&eDu bist in den GameMode &f%gamemode% &egewechselt.",
				"&eYou have changed to the GameMode &f%gamemode%&e."}));
		languageKeys.put(path+"SetOtherGameMode",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&eDu hast Spieler &f%player% &ein den GameMode &f%gamemode% &egewechselt.",
				"&eYou have changed player &f%player% &eto GameMode &f%gamemode%&e."}));
		languageKeys.put(path+"SettedGameMode",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&eDu wurdest von &f%sender% &ein den GameMode &f%gamemode% &egesetzt.",
				"&eYou have been set by &f%sender% &to GameMode &f%gamemode%."}));
	}
	
	private void initLangOpenableBlocks()
	{
		String path = "Openable.";
		languageKeys.put(path+"EnderchestOther",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&eDu hast die Enderchest des Spielers &f%player% &egeöffnet!",
				"&eYou have opened the enderchest of the player &f%player%&e!"}));
		languageKeys.put(path+"InventoryOther",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&eDu hast das Inventar des Spielers &f%player% &egeöffnet!",
				"&eYou have opened the inventory of the player &f%player%&e!"}));
	}
	
	private void initLangCmd()
	{
		languageKeys.put("Fly.YouFly",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&eDu fliegst nun.",
				"&eYou are flying now."}));
		languageKeys.put("Fly.OtherFly",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&eDu hast den Flugmodus für &f%player% &eaktiviert.",
				"&eYou have activated the flight mode for &f%player%&e."}));
		languageKeys.put("Fly.YouDontFly",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&eDu fliegst nun nicht mehr.",
				"&eYou are not flying now."}));
		languageKeys.put("Fly.OtherDontFly",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&eDu hast den Flugmodus für &f%player% &edeaktiviert.",
				"&eYou have disabled the flight mode for &f%player%&e."}));
	}
	
	private void initLangCustomPlayerInventory()
	{
		String path = "CPI.";
		languageKeys.put(path+"ActionLogCategory",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"CustomSpielerInventar",
				"CustomPlayerInventory"}));
		languageKeys.put(path+"ActionLogComment",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&bDu hast &c%format% &bfür das %cpi% Reihe %row% bezahlt.",
				"&bYou have paid &c%format% &bfor the %cpi% row %row%."}));
		languageKeys.put(path+"NotActiveOrDontExist",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&cDas Custom Spieler Inventar ist nicht aktiv oder existiert nicht!",
				"&cThe Custom Player Inventory is not active or does not exist!"}));
		languageKeys.put(path+"DoNotHaveAccess",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&cDu hast kein Zugriff auf dieses Custom Spieler Inventar!",
				"&cYou do not have access to this Custom Player inventory!"}));
		languageKeys.put(path+"AreYouSureToDrop",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&cBist du dir sicher, dass du alle Items des %cpi% droppen möchtest? Dann füge dem Befehl ein &fbestätigen &chinzu.",
				"&cAre you sure you want to drop all items of the %cpi%? Then add &fconfirm &cto the command."}));
		languageKeys.put(path+"HaveNothingToDrop",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&cDas Custom Spieler Inventar ist leer, es gibt nichts zum droppen!",
				"&cThe Custom Player Inventory is empty, there is nothing to drop!"}));
		languageKeys.put(path+"HaveNotBuyOneSingeRow",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&cDu hast noch keine einzelne Reihe!",
				"&cThe Custom Player Inventory is empty, there is nothing to drop!"}));
		languageKeys.put(path+"InfoArray",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&f===&7[&c%cpi% &6Info&7]&f===",
				"&cMöglich Reihe: &r%permrowamount%",
				"&cGekaufte Reihe: &r%maxbuyedrow%",
				"&cKosten für Reihe 1: &r%costrowone%",
				"&cKosten für Reihe 2: &r%costrowtwo%",
				"&cKosten für Reihe 3: &r%costrowthree%",
				"&cKosten für Reihe 4: &r%costrowfour%",
				"&cKosten für Reihe 5: &r%costrowfive%",
				"&cKosten für Reihe 6: &r%costrowsix%",
				"&cListen Status: &r%liststatus%",
				"&cMaterialliste: &r%list%",
				"&f===&7[&c%cpi% &6Info&7]&f===",
				"&cPossible row: &r%permrowamount%",
				"&cBuyed row: &r%maxbuyedrow%",
				"&cCost for row 1: &r%costrowone%",
				"&cCost for row 2: &r%costrowtwo%",
				"&cCost for row 3: &r%costrowthree%",
				"&cCost for row 4: &r%costrowfour%",
				"&cCost for row 5: &r%costrowfive%",
				"&cCost for row 6: &r%costrowsix%",
				"&cLists Status: &r%liststatus%",
				"&cMateriallists: &r%list%"}));
		languageKeys.put(path+"CostReplacer.Free",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&aKostenlos",
				"&aFree of charge"}));
		languageKeys.put(path+"UsePredefineSetsCannotBeBuyed",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&cCustom Spieler Inventare welche vordefinierte Sets benutzen, können nicht gekauft werden!",
				"&cCustom player inventories which use predefined sets cannot be purchased!"}));
		languageKeys.put(path+"YouHaveAlreadyBuyedRowSix",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&cDu hast schon alle Reihen des Custom Spieler Inventar gekauft!",
				"&cYou have already bought all rows of the custom player inventory!"}));
		languageKeys.put(path+"YouCannotBuyWhereYouHaveNoPermission",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&cDu kannst keine weitere Inventarreihe kaufen, wo du nicht die Permission hast!",
				"&cYou cannot buy another inventory row where you do not have the permission!"}));
		languageKeys.put(path+"YouCannotBuyAAnotherPlayerARow",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&cDu darfst keinem anderem Spieler eine weitere Inventarreihe kaufen!",
				"&cYou may not buy another inventory row for another player!"}));
		languageKeys.put(path+"YouHaveNoAccountToWithdrawTheCost",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&cDu besitzt keinen Konto um die &r%format% &cabzuziehen!",
				"&cYou do not have an account to deduct the &r%format%&c!"}));
		languageKeys.put(path+"YouHaveNoEnoughMoneyAmount",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&cDu besitzt keine &r%format%&c!",
				"&cYou do not own a &r%format%&c!"}));
		languageKeys.put(path+"YouHaveNoEnoughMaterials",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&cDu hast im Inventar nur &r%actual%/%needed% %mat%&c!",
				"&cYou do not own a &r%format%&c!"}));
		languageKeys.put(path+"YouHaveNoEnoughExp",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&cDu hast nur &r%actual%/%needed% Exp&c!",
				"&cYou have only &r%actual%/%needed% Exp&c!"}));
		languageKeys.put(path+"YouPaidForTheNextRow",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&eDu hast die Reihe %row% des %cpi% freigeschaltet. Bezahlt wurde: &r%cost%",
				"&eYou have unlocked the %row% series of the %cpi%. Paid: &r%cost%"}));
		languageKeys.put(path+"YouGetTheNextRow",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&eDu hast die Reihe %row% des %cpi% freigeschaltet.",
				"&eYou have unlocked the %row% series of the %cpi%."}));
	}
	
	private void initSync() //INFO:Synchro
	{
		worldKeys.put("ServerOverWorldSettings",
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				true}));
		syncKeys.put("Synchrokey",
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"default"}));
		syncKeys.put("Load.OnFirstJoin.PredefineState",
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				false}));
		syncKeys.put("Load.OnFirstJoin.PredefineStatename",
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"here_statename"}));
		syncKeys.put("Load.Always.PredefineState",
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				false}));
		syncKeys.put("Load.Always.PredefineStatename",
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"here_statename"}));
		syncKeys.put("ClearAndReset.OnLeaveOrQuit",
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				false}));
		syncKeys.put("MaximalDeathMemoryStatePerPlayerPerGameModePerSynchrokey",
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				5}));
		syncKeys.put("TimeDelayInSecs.RemoveCooldown",
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				5}));
		List<BaseListener.Type> events = new ArrayList<BaseListener.Type>(EnumSet.allOf(BaseListener.Type.class));
		for(BaseListener.Type event : events)
		{
			int i = 5;
			switch(event)
			{
			default:
				break;
			case BLOCK_SIGNCHANGE:
			case ENTITY_RESURRECT:
			case ENTITY_TAME:
			case PLAYER_CHANGEDWORLD:
			case PLAYER_JOIN:
			case PLAYER_QUIT:
			case PLAYER_RESPAWN:
			case PLAYER_TELEPORT:
				i = 15;
				break;
			case PLAYER_GAMEMODECHANGE:
				i = 30;
				break;
			case WORLD_TIMESKIP:
				i = 60;
				break;
			}
			syncKeys.put("SyncEvents."+event.getName()+".Enabled",
					new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
					true}));
			syncKeys.put("SyncEvents."+event.getName()+".TimeDelayInSecs.RemoveCooldown",
					new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
					i}));
		}
	}
	
	public void initCustomPlayerInventory() //INFO:CustomPlayerInventory
	{
		LinkedHashMap<String, Language> cpiKeysI = new LinkedHashMap<>();
		cpiKeysI.put("UniqueName",
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"defaultbox"}));
		cpiKeysI.put("InventoryName",
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"%player%´s Defaultbox Inventory"}));
		cpiKeysI.put("InventoryShulkerName",
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"%player%´s Shulkerbox Defaultbox Inventory"}));
		cpiKeysI.put("IsActive",
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				true}));
		cpiKeysI.put("UsePredefineCustomInventory",
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				false}));
		cpiKeysI.put("UsedPredefineCustomInventory",
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"predefinecustominventoryname"}));
		cpiKeysI.put("CountPermissionType",
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"ADDUP"}));
		cpiKeysI.put("CountPermission",
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"mim.customplayerinv.defaultbox.count."}));
		cpiKeysI.put("CostPerRow.1",
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"FREE;",
				"FREE;"}));
		cpiKeysI.put("CostPerRow.2",
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"MONEY;25.0;default",
				"MONEY;5.0;specialcurrencyuniquename",
				"MATERIAL;64;COAL",
				"EXP;5"}));
		cpiKeysI.put("CostPerRow.3",
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"MONEY;50.0;default", //Type of Cost, Costamount, Currency or Item. For Exp(Exp isnt Level) dont need
				"MATERIAL;32;IRON_INGOT",
				"EXP;5"}));
		cpiKeysI.put("CostPerRow.4",
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"MONEY;75.0;default", //Type of Cost, Costamount, Currency or Item. For Exp(Exp isnt Level) dont need
				"MATERIAL;16;GOLD_INGOT",
				"EXP;5"}));
		cpiKeysI.put("CostPerRow.5",
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"MONEY;100.0;default", //Type of Cost, Costamount, Currency or Item. For Exp(Exp isnt Level) dont need
				"MATERIAL;8;DIAMOND",
				"EXP;5"}));
		cpiKeysI.put("CostPerRow.6",
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"MONEY;200.0;default", //Type of Cost, Costamount, Currency or Item. For Exp(Exp isnt Level) dont need
				"MATERIAL;4;NETHERITE_INGOT",
				"EXP;5"}));
		cpiKeysI.put("ShulkerOpenInInventoryPermission",
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"mim.customplayerinv.openshulker"}));
		cpiKeysI.put("List.Status",
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"BLACKLIST"}));
		cpiKeysI.put("List.Material",
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"DIAMOND_AXE",
				"DIAMOND_HOE",
				"DIAMOND_PICKAXE",
				"DIAMOND_SHOVEL"}));
		String path = "Command";
		String perm = "mim.customplayerinv.defaultbox.";
		cpiKeysI.put(path+".Name"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"defaultbox"}));
		cpiKeysI.put(path+".Permission",
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				perm+"cmd"}));
		cpiKeysI.put(path+".Suggestion"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"/defaultbox"}));
		cpiKeysI.put(path+".CommandString"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"/defaultbox "}));
		cpiKeysI.put(path+".HelpInfo"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&c/defaultbox &f| Öffnet das Benutzerdefinierte Spielerinventar.",
				"&c/defaultbox &f| Open the custom player inventory."}));
		path = "Argument_buy";
		cpiKeysI.put(path+".Argument"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"buy"}));
		cpiKeysI.put(path+".Permission"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				perm+"buy"}));
		cpiKeysI.put(path+".Suggestion"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"/defaultbox buy [Playername]"}));
		cpiKeysI.put(path+".CommandString"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"/defaultbox buy "}));
		cpiKeysI.put(path+".HelpInfo"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&c/defaultbox buy [Spielername] &f| Kauft die nächsthöhere Reihe des CustomSpielerInventars, wenn es möglich ist.",
				"&c/defaultbox buy [Playername] &f| Buy the next higher row of the CustomPlayer inventory, if possible."}));
		path = "Argument_drop";
		cpiKeysI.put(path+".Argument"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"drop"}));
		cpiKeysI.put(path+".Permission"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				perm+"drop"}));
		cpiKeysI.put(path+".Suggestion"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"/defaultbox drop [confirm]"}));
		cpiKeysI.put(path+".CommandString"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"/defaultbox drop "}));
		cpiKeysI.put(path+".HelpInfo"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&c/defaultbox drop [bestätigen] &f| Dropt alle Items des Custom Spieler Inventars auf dem Boden.",
				"&c/defaultbox drop [confirm] &f| Drops all items of the custom player inventory on the floor."}));
		path = "Argument_info";
		cpiKeysI.put(path+".Argument"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"info"}));
		cpiKeysI.put(path+".Permission"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				perm+"info"}));
		cpiKeysI.put(path+".Suggestion"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"/defaultbox info"}));
		cpiKeysI.put(path+".CommandString"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"/defaultbox info "}));
		cpiKeysI.put(path+".HelpInfo"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&c/defaultbox info &f| Gibt Infos bezüglich der defaultbox aus.",
				"&c/defaultbox info &f| Outputs info regarding the defaultbox."}));
		path = "Argument_see";
		cpiKeysI.put(path+".Argument"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"see"}));
		cpiKeysI.put(path+".Permission"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				perm+"see"}));
		cpiKeysI.put(path+".Suggestion"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"/defaultbox see <Playername>"}));
		cpiKeysI.put(path+".CommandString"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"/defaultbox see "}));
		cpiKeysI.put(path+".HelpInfo"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&c/defaultbox see <Spielername> &f| Schaut in die defaultbox des angegeben Spielers.",
				"&c/defaultbox see <Playername> &f| Look in the defaultbox of the specified player."}));
		customInventoryKeys.put("default", cpiKeysI);
	}
}
