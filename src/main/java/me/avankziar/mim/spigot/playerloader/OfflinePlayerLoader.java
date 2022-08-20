package main.java.me.avankziar.mim.spigot.playerloader;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public interface OfflinePlayerLoader
{
	Player loadPlayer(OfflinePlayer offlinePlayer);
}