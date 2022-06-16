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
		
		configSpigotKeys.put("EnableCommands.Base"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				true}));
		configSpigotKeys.put("Default.ClearToggle"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				false}));
		/*
		 * The "Stringlist" are define so.
		 */
		configSpigotKeys.put("GuiFlatFileNames"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"guiOne",
				"guiTwo",}));
		/*
		 * If there was a second language, with also 2 entry, so would Entry 1 and two for the first and 3 and 4 four the second language.
		 */	
	}
	
	public void initCommands()
	{
		comBypass();
		commandsInput("mim", "mim", "mim.cmd.mim", 
				"/mim [pagenumber]", "/mim ",
				"&c/mim &f| Infoseite für alle Befehle.",
				"&c/mim &f| Info page for all commands.");
		String basePermission = "mim.cmd.mim.";
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
				"/wbench [playername]", "/wbench ",
				"&c/wbench [Spielername] &f| Öffnet eine Werkbank. Optional für andere Spieler.",
				"&c/wbench [playername] &f| Opens a workbench. Optional for other players.");
		commandsInput("enderchest", "enderchest", "enderchest.cmd.enderchest", 
				"/enderchest [playername]", "/enderchest ",
				"&c/enderchest [Spielername] &f| Öffnet deine Enderchest. Optional für andere Spieler.",
				"&c/enderchest [playername] &f| Opens your enderchest. Optional for other players.");
		commandsInput("enchantingtable", "enchantingtable", "enchantingtable.cmd.enchantingtable", 
				"/enchantingtable [playername]", "/enchantingtable ",
				"&c/enchantingtable [Spielername] &f| Öffnet einen Verzauberungstisch der maximalen Stufe. Optional für andere Spieler.",
				"&c/enchantingtable [playername] &f| Opens a enchanting table of the maximum level. Optional for other players.");
		commandsInput("anvil", "anvil", "anvil.cmd.anvil", 
				"/anvil [playername]", "/anvil ",
				"&c/anvil [Spielername] &f| Öffnet einen Amboss. Optional für andere Spieler.",
				"&c/anvil [playername] &f| Opens a anvil. Optional for other players.");
		
	}
	
	private void comBypass() //INFO:ComBypass
	{
		List<Bypass.Permission> list = new ArrayList<Bypass.Permission>(EnumSet.allOf(Bypass.Permission.class));
		for(Bypass.Permission ept : list)
		{
			commandsKeys.put("Bypass."+ept.toString().replace("_", "")
					, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
					"aep."+ept.toString().toLowerCase().replace("_", "")}));
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
		initLangSyncTask();
		initLangGameMode();
		initLangOpenableBlocks();
		initLangCustomPlayerInventory();
		/*languageKeys.put(""
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"",
				""}))*/
	}
	
	private void initLangSyncTask()
	{
		String path = "SyncTask.";
		languageKeys.put(path+"SavedAndKicked"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&eDein Spielstand wurde erfolgreich gespeichert und du wurdest vom Server gekickt!",
				"&eYour game state was successfully saved and you were kicked from the server!"}));
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
				"&eDer Server %server% wurde angewiesen, alle zurzeit vorhandenen Spieler rechtzeitig zu speichern und dann zu kicken. &cBedenke, ohne aktive Whiteliste oder ähnlichem können diese aber wieder joinen!",
				"&eThe server %server% are instructed to save all currently existing players in time and then kick them. &cBeware, without active whitelist or similar, but they can join again!"}));
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
		languageKeys.put(path+"WorkbenchOther",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&eDu hast für den Spieler &f%player% &edie Werkbank geöffnet!",
				"&eYou have opened for the &f%player% &ethe workbench!"}));
		languageKeys.put(path+"EnderchestOther",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&eDu hast die Enderchest des Spielers &f%player% &egeöffnet!",
				"&eYou have opened the enderchest of the player &f%player% &e!"}));
		languageKeys.put(path+"EnchantingTableOther",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&eDu hast für den Spieler &f%player% &eden Verzauberungstisch geöffnet!",
				"&eYou have opened for the &f%player% &ethe enchanting table!"}));
		languageKeys.put(path+"AnvilOther",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"&eDu hast für den Spieler &f%player% &eden Amboss geöffnet!",
				"&eYou have opened for the &f%player% &ethe anvil!"}));
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
			syncKeys.put("SyncEvents."+event.getName(),
					new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
					true}));
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
				"default"}));
		cpiKeysI.put("InventoryName",
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"%player%´s Default Inventory"}));
		cpiKeysI.put("InventoryShulkerName",
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"%player%´s Shulkerbox Default Inventory"}));
		cpiKeysI.put("IsActive",
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				false}));
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
				"MONEY;25;default",
				"MONEY;5;specialcurrencyuniquename",
				"MATERIAL;5;DIAMOND",
				"EXP;5"}));
		cpiKeysI.put("CostPerRow.3",
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"MONEY;25;default", //Type of Cost, Costamount, Currency or Item. For Exp(Exp isnt Level) dont need
				"MATERIAL;5;DIAMOND",
				"EXP;5"}));
		cpiKeysI.put("CostPerRow.4",
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"MONEY;25;default", //Type of Cost, Costamount, Currency or Item. For Exp(Exp isnt Level) dont need
				"MATERIAL;5;DIAMOND",
				"EXP;5"}));
		cpiKeysI.put("CostPerRow.5",
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"MONEY;25;default", //Type of Cost, Costamount, Currency or Item. For Exp(Exp isnt Level) dont need
				"MATERIAL;5;DIAMOND",
				"EXP;5"}));
		cpiKeysI.put("CostPerRow.6",
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"MONEY;25;default", //Type of Cost, Costamount, Currency or Item. For Exp(Exp isnt Level) dont need
				"MATERIAL;5;DIAMOND",
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
