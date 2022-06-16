package main.java.me.avankziar.mim.spigot.cmdtree;

public enum CommandExecuteType
{
	MIM, // /mim | Zeigt alle Befehle an.
	MIM_SAVEANDKICK,
	MIM_SAVE,
	GAMEMODE,
	WORKBENCH,
	ENDERCHEST,
	ENCHANTINGTABLE,
	ANVIL,
	CUSTOMPLAYERINVENTORY, // /xyz | Alle Befehle für die CPI gemeint sind.
	CPI_DROP,
	CPI_SEE,
	CPI_BUY
}
