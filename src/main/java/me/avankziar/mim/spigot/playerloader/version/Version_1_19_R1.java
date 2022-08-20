package main.java.me.avankziar.mim.spigot.playerloader.version;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;

import main.java.me.avankziar.mim.spigot.playerloader.OfflinePlayerLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.EntityPlayer;

public class Version_1_19_R1 implements OfflinePlayerLoader
{
	public Player loadPlayer(OfflinePlayer offlinePlayer)
	{
		if(offlinePlayer == null || !offlinePlayer.hasPlayedBefore())
		{
			return null;
		}
		GameProfile gp = new GameProfile(offlinePlayer.getUniqueId(), offlinePlayer.getName());
		DedicatedServer ds = ((CraftServer) Bukkit.getServer()).getServer();
		EntityPlayer ep = new EntityPlayer((MinecraftServer)ds, ds.C(), gp, null);
		CraftPlayer cp = ep.getBukkitEntity();
		if(cp != null)
		{
			cp.loadData();
		}
		return (Player)cp;
	}
}