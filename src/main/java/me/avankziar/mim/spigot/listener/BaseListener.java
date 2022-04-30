package main.java.me.avankziar.mim.spigot.listener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.me.avankziar.mim.spigot.MIM;

public class BaseListener implements Listener
{
	public enum Type
	{
		BLOCK_CANBUILD("Block.CanBuild.SaveInvOnly"),
		BLOCK_IGNITE("Block.Ignite.SaveInvOnly"),
		BLOCK_SIGNCHANGE("Block.SignChange.SaveFull"),
		
		ENTITY_PICKUPITEM("Entity.PickUpItem.SaveInventory"),
		ENTITY_RESURRECT("Entity.Resurrect.SaveFull"),
		ENTITY_TAME("Entity.Tame.SaveFull"),
		
		INVENTORY_CLOSE("Inventory.Close.SaveInventory"),
		
		PLAYER_CHANGEDWORLD("Player.ChangedWorld.LoadFull"),
		PLAYER_DEATH("Player.Death.SaveDeathState"),
		PLAYER_DROPITEM("Player.DropItem.SaveInventory"),
		PLAYER_EXPCHANGE("Player.ExpChange.SaveExp"),
		PLAYER_GAMEMODECHANGE("Player.GameModeChange.SaveAndLoadFull"),
		PLAYER_HARVESTBLOCK("Player.HarvestBlock.SaveInventory"),
		PLAYER_ITEMBREAK("Player.ItemBreak.SaveInventory"),
		PLAYER_ITEMCONSUME("Player.ItemConsume.SaveFull"),
		PLAYER_ITEMDAMGE("Player.ItemDamage.SaveInventory"),
		PLAYER_JOIN("Player.Join.LoadFull"),
		PLAYER_LEVELCHANGE("Player.LevelChange.SaveExp"),
		PLAYER_QUIT("Player.Quit.SaveFull"),
		PLAYER_RESPAWN("Player.Respawn.SaveFull"),
		PLAYER_TELEPORT("Player.Teleport.SaveFull"),
		
		WORLD_TIMESKIP("World.TimeSkip.SaveFull"),
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
	public BaseListener.Type bType;
	private static Set<UUID> cooldown = new HashSet<>();
	
	public BaseListener(MIM plugin, BaseListener.Type bType)
	{
		this.plugin = plugin;
		this.bType = bType;
	}
	
	public boolean inCooldown(UUID uuid)
	{
		return cooldown.contains(uuid);
	}
	
	public void addCooldown(UUID uuid)
	{
		cooldown.add(uuid);
	}
	
	public void removeCooldown(UUID uuid)
	{
		Player player = Bukkit.getPlayer(uuid);
		if(player != null)
		{
			int tdrc = plugin.getConfigHandler().getTimeDelayInSecsRemoveCooldown(player.getWorld(), this.bType);
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					cooldown.remove(uuid);
				}
			}.runTaskLaterAsynchronously(plugin, 20*tdrc);
			return;
		}
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