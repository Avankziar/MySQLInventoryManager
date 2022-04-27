package main.java.me.avankziar.mim.spigot.listener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import main.java.me.avankziar.mim.spigot.MIM;

public class BaseListener implements Listener
{
	public enum Type
	{
		ENTITYPICKUPITEM("EntityPickUpItem"),
		PLAYERCHANGEWORLD("PlayerChangeWorld"),
		PLAYERDROPITEM("PlayerDropItem"),
		PLAYEREXPCHANGE("PlayerExpChange"),
		PLAYERGAMEMODECHANGE("PlayerGameModeChange"),
		PLAYERHARVESTBLOCK("PlayerHarvestBlock"),
		PLAYERLEVELCHANGE("PlayerLevelChange"),
		PLAYERRESPAWN("PlayerRespawn"),
		PLAYERTELEPORT("PlayerTeleport"),
		TIMESKIP("TimeSkip"),
		;
		
		private String name;
		
		Type(String name)
		{
			this.name = name;
		}

		public String getName()
		{
			return name;
		}
	}
	public MIM plugin;
	private static Set<UUID> cooldown = new HashSet<>();
	
	public BaseListener(MIM plugin)
	{
		this.plugin = plugin;
	}
	
	public boolean inCooldown(UUID uuid)
	{
		return cooldown.contains(uuid) ? true : false;
	}
	
	public void addCooldown(UUID uuid)
	{
		cooldown.add(uuid);
	}
	
	public void removeCooldown(UUID uuid)
	{
		cooldown.remove(uuid);
	}
	
	public boolean preChecks(Player player)
	{
		if(!player.isOnline())
		{
			return false;
		}
		if(inCooldown(player.getUniqueId()))
		{
			return false;
		}
		return true;
	}
}