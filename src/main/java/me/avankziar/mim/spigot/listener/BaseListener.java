package main.java.me.avankziar.mim.spigot.listener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.me.avankziar.mim.general.ChatApi;
import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.assistance.Utility;
import main.java.me.avankziar.mim.spigot.objects.SyncTask;
import main.java.me.avankziar.mim.spigot.objects.SyncTask.RunType;
import main.java.me.avankziar.mim.spigot.objects.SyncType;

public class BaseListener implements Listener
{
	public enum Type
	{
		IFH_API("EMPTY"),
		
		INVENTORY_CLOSE("Inventory.Close.SaveInventory"),
		
		PLAYER_CHANGEDWORLD("Player.ChangedWorld.LoadFull"),
		PLAYER_DEATH("Player.Death.SaveDeathState"),
		PLAYER_JOIN("Player.Join.LoadFull"),
		PLAYER_QUIT("Player.Quit.SaveFull"),
		PLAYER_RESPAWN("Player.Respawn.SaveFull"),
		PLAYER_TELEPORT("Player.Teleport.SaveFull"),
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
	protected static ArrayList<UUID> loadstatus = new ArrayList<>();
	
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
	
	public static void removeCooldown(UUID uuid, BaseListener.Type bType)
	{
		Player player = Bukkit.getPlayer(uuid);
		if(player != null)
		{
			int tdrc = MIM.getPlugin().getConfigHandler().getTimeDelayInSecsRemoveCooldown(player.getWorld(), bType);
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					cooldown.remove(uuid);
				}
			}.runTaskLaterAsynchronously(MIM.getPlugin(), 20*tdrc);
			return;
		}
		cooldown.remove(uuid);
	}
	
	public boolean preChecks(Player player)
	{
		if(player == null || !player.isOnline())
		{
			return false;
		}
		if(inCooldown(player.getUniqueId()))
		{
			return false;
		}
		return true;
	}
	
	public void doSync(Player player, SyncType syncType, RunType runType)
	{
		if(!preChecks(player))
		{
			return;
		}
		addCooldown(player.getUniqueId());
		new SyncTask(plugin, syncType, runType, player).run();
		removeCooldown(player.getUniqueId());
	}
	
	public static void clearAndReset(final Player player, String args[], SyncType syncType)
	{
		String playername = player.getName();
		if(args.length == 1)
		{
			player.sendMessage(ChatApi.tl(MIM.getPlugin().getYamlHandler().getLang().getString("SyncTask.PleaseConfirm")));	
			return;
		} else if(args.length == 2)
		{
			if(!"bestätigen".equalsIgnoreCase(args[1]) && !"confirm".equalsIgnoreCase(args[1]))
			{
				player.sendMessage(ChatApi.tl(MIM.getPlugin().getYamlHandler().getLang().getString("SyncTask.PleaseConfirm")));	
				return;
			}
			cooldown.add(player.getUniqueId());
			new SyncTask(MIM.getPlugin(), syncType, RunType.CLEAR_AND_RESET, player).run();
			removeCooldown(player.getUniqueId(), Type.INVENTORY_CLOSE);
		} else if(args.length == 3)
		{
			if(!"bestätigen".equalsIgnoreCase(args[2]) && !"confirm".equalsIgnoreCase(args[2]))
			{
				player.sendMessage(ChatApi.tl(MIM.getPlugin().getYamlHandler().getLang().getString("SyncTask.PleaseConfirm")));	
				return;
			}
			String othername = args[1];
			UUID otheruuid = Utility.convertNameToUUID(othername);
			if(otheruuid == null)
			{
				player.sendMessage(ChatApi.tl(MIM.getPlugin().getYamlHandler().getLang().getString("PlayerNotExist")));
				return;
			}
			Player other = Bukkit.getPlayer(otheruuid);
			if(other == null)
			{
				player.sendMessage(ChatApi.tl(MIM.getPlugin().getYamlHandler().getLang().getString("PlayerNotOnline")));
				return;
			}
			playername = other.getName();
			cooldown.add(other.getUniqueId());
			new SyncTask(MIM.getPlugin(), syncType, RunType.CLEAR_AND_RESET, other).run();
			removeCooldown(other.getUniqueId(), Type.INVENTORY_CLOSE);
		}
		player.sendMessage(ChatApi.tl(MIM.getPlugin().getYamlHandler().getLang().getString("SyncTask.Clear."+syncType.toString())
				.replace("%player%", playername)));	
	}
}