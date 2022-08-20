package main.java.me.avankziar.mim.spigot.listener;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.cmd.EnchantingTableCmdExecutor;
import main.java.me.avankziar.mim.spigot.database.MysqlHandler;
import main.java.me.avankziar.mim.spigot.objects.CustomPlayerInventory;
import main.java.me.avankziar.mim.spigot.objects.PlayerData;
import main.java.me.avankziar.mim.spigot.objects.SyncTask.RunType;
import main.java.me.avankziar.mim.spigot.objects.SyncType;

public class InventoryCloseListener extends BaseListener
{
	public static LinkedHashMap<String, Inventory> ownerToInventory = new LinkedHashMap<>();//InventoryOwner+invType+";"+subType, Inventory
	public static LinkedHashMap<UUID, UUID> executorToOwnerInventory = new LinkedHashMap<>();//CmdExecutor, InventoryOwner
	public static LinkedHashMap<UUID, String> executorToInventoryType = new LinkedHashMap<>();//CmdExecutor, InventoryType(CPI, Enderchest etc.)
	//Only useable for Enderchest etc. (CPI not use this)
	public static LinkedHashMap<UUID, String> executorToSynchroKey = new LinkedHashMap<>(); //CmdExecutor, targetSyncorKey 
	//Only if the person rightclick in a inv on a shulker
	public static LinkedHashMap<UUID, Integer> executorToShulkerSlot = new LinkedHashMap<>(); //CmdExecutor, SlotInOverInventory
	public static LinkedHashMap<UUID, ItemStack> executorToShulkerItem = new LinkedHashMap<>(); //CmdExecutor, ShulkerItemStack
	
	public InventoryCloseListener(MIM plugin)
	{
		super(plugin, BaseListener.Type.INVENTORY_CLOSE);
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onInventoryClose(final InventoryCloseEvent event)
	{
		final Player player = (Player) event.getPlayer();
		if(event.getInventory().getType() == InventoryType.ENDER_CHEST)
		{
			//Wird direkt hier gemacht, weil der doSync das irgendwie nicht hinbekommt.
			ecSave(player);
			return;
		}
		if(EnchantingTableCmdExecutor.inEnchantingTable.contains(event.getPlayer().getUniqueId()))
		{
			EnchantingTableCmdExecutor.inEnchantingTable.remove(event.getPlayer().getUniqueId());
		}
		if(removeToExternInventory((Player) event.getPlayer(), event.getInventory().getContents()))
		{
			return;
		}
		if(!plugin.getConfigHandler().isEventEnabled(this.bType.getName(), event.getPlayer().getWorld()))
		{
			return;
		}
		doSync(player, SyncType.INVENTORY, RunType.SAVE);
	}
	
	public static boolean inExternInventory(UUID executorPlayer)
	{
		return executorToOwnerInventory.containsKey(executorPlayer);
	}
	
	public static boolean inShulkerInventory(UUID executorPlayer)
	{
		return executorToShulkerSlot.containsKey(executorPlayer);
	}
	
	public static Inventory getExternInventory(UUID targetPlayer, String invType, String subType)
	{
		if(ownerToInventory.containsKey(targetPlayer.toString()+invType+";"+subType))
		{
			return ownerToInventory.get(targetPlayer.toString()+invType+";"+subType);
		}
		return null;
	}
	
	public static void addToExternInventory(UUID executorPlayer, UUID targetPlayer,
			Inventory inv, String invType, String subType, String synchroKey)
	{
		ownerToInventory.put(targetPlayer+invType+";"+subType, inv);
		executorToOwnerInventory.put(executorPlayer, targetPlayer);
		executorToInventoryType.put(executorPlayer, invType+";"+subType);
		executorToSynchroKey.put(executorPlayer, synchroKey);
	}
	
	public static boolean removeToExternInventory(Player executor, ItemStack[] shulkerInventoryOnly)
	{
		UUID executorPlayer = executor.getUniqueId();
		if(!inExternInventory(executor.getUniqueId()))
		{
			return false;
		}
		final UUID targetPlayer = executorToOwnerInventory.get(executorPlayer);
		final String invType = executorToInventoryType.get(executorPlayer).split(";")[0];
		final String subType = executorToInventoryType.get(executorPlayer).split(";")[1];
		final Inventory inv = ownerToInventory.get(targetPlayer+invType+";"+subType);
		final String synchroKey = executorToSynchroKey.get(executorPlayer);
		executorToOwnerInventory.remove(executorPlayer);
		executorToInventoryType.remove(executorPlayer);
		executorToSynchroKey.remove(executorPlayer);
		boolean exist = false;
		for(Entry<UUID, UUID> set : executorToOwnerInventory.entrySet())
		{
			if(set.getValue().equals(targetPlayer))
			{
				exist = true;
				break;
			}
		}
		if(!exist)
		{
			ownerToInventory.remove(targetPlayer+invType+";"+subType);
			Player target = Bukkit.getPlayer(targetPlayer);
			switch(invType)
			{
			default:
				return false;
			case "INV":
				if(target != null)
				{
					if(inv.getContents().length > 36)
					{
						ItemStack[] is = new ItemStack[36];
						for(int i = 0; i < is.length; i++)
						{
							is[i] = inv.getItem(i);
						}
						target.getInventory().setStorageContents(is);
					} else
					{
						target.getInventory().setStorageContents(inv.getContents());
					}
					target.updateInventory();
				} else
				{
					try (Connection conn = MIM.getPlugin().getMysqlSetup().getConnection();)
					{
						String sql = "UPDATE `" + MysqlHandler.Type.PLAYERDATA.getValue()
							+ "` SET `inventory_content` = ?"
							+ " WHERE `synchro_key` = ? AND `player_uuid` = ?";
						PreparedStatement ps = conn.prepareStatement(sql);
				        ps.setString(1, MIM.getPlugin().getBase64Api().toBase64Array(inv.getContents()));
				        
				        ps.setString(2, synchroKey);
				        ps.setString(3, targetPlayer.toString());
						int u = ps.executeUpdate();
						MysqlHandler.addRows(MysqlHandler.QueryType.UPDATE, u);
					} catch (SQLException e)
					{
						MIM.log.log(Level.WARNING, "SQLException! Could not update a PlayerData Object!", e);
					}
				}
				break;
			case "ARMOR":
				ArrayList<ItemStack> armor = new ArrayList<>();
				ItemStack offhand = null;
				for(int i = 0; i < inv.getContents().length; i++)
				{
					ItemStack is = inv.getContents()[i];
					switch(i)
					{
					default:
						break;
					case 0:
						offhand = is;
						break;
					case 1:
					case 2:
					case 3:
					case 4:
						armor.add(is);
					}
				}
				if(target != null)
				{
					target.getInventory().setItemInOffHand(offhand);
					target.getInventory().setArmorContents(armor.toArray(new ItemStack[armor.size()]));
					target.updateInventory();
				} else
				{
					try (Connection conn = MIM.getPlugin().getMysqlSetup().getConnection();)
					{
						String sql = "UPDATE `" + MysqlHandler.Type.PLAYERDATA.getValue()
							+ "` SET `armor_content` = ?, `off_hand` = ?"
							+ " WHERE `synchro_key` = ? AND `player_uuid` = ?";
						PreparedStatement ps = conn.prepareStatement(sql);
				        ps.setString(1, MIM.getPlugin().getBase64Api().toBase64Array(armor.toArray(new ItemStack[armor.size()])));
				        ps.setString(2, MIM.getPlugin().getBase64Api().toBase64(offhand));
				        
				        ps.setString(3, synchroKey);
				        ps.setString(4, targetPlayer.toString());	
						int u = ps.executeUpdate();
						MysqlHandler.addRows(MysqlHandler.QueryType.UPDATE, u);
					} catch (SQLException e)
					{
						MIM.log.log(Level.WARNING, "SQLException! Could not update a PlayerData Object!", e);
					}
					break;
				}
			case "EC": //Enderchest
				target.getEnderChest().setContents(inv.getContents());
				try (Connection conn = MIM.getPlugin().getMysqlSetup().getConnection();)
				{
					String sql = "UPDATE `" + MysqlHandler.Type.PLAYERDATA.getValue()
						+ "` SET "
						+ " `enderchest_content` = ?"
						+ " WHERE `synchro_key` = ? AND `player_uuid` = ?";
					PreparedStatement ps = conn.prepareStatement(sql);
			        ps.setString(1, MIM.getPlugin().getBase64Api().toBase64Array(inv.getContents()));
			        
			        ps.setString(2, synchroKey);
			        ps.setString(3, targetPlayer.toString());		
					int u = ps.executeUpdate();
					MysqlHandler.addRows(MysqlHandler.QueryType.UPDATE, u);
				} catch (SQLException e)
				{
					MIM.log.log(Level.WARNING, "SQLException! Could not update a PlayerData Object!", e);
				}
				break;				
			case "CPI": // CustomPlayerInventory
				CustomPlayerInventory cpi = (CustomPlayerInventory) MIM.getPlugin().getMysqlHandler().getData(MysqlHandler.Type.CUSTOMPLAYERINVENTORY,
						"`cpi_name` = ? AND `owner_uuid` = ?", subType, targetPlayer.toString());
				cpi.setInventory(inv.getContents());
				MIM.getPlugin().getMysqlHandler().updateData(MysqlHandler.Type.CUSTOMPLAYERINVENTORY, cpi, 
						"`cpi_name` = ? AND `owner_uuid` = ?", cpi.getUniqueName(), cpi.getOwnerUUID().toString());
				break;
			}
		}
		return true;
	}
	
	private void ecSave(final Player player)
	{
		String synchroKey = MIM.getPlugin().getConfigHandler().getSynchroKey(player, false);
		GameMode gm = player.getGameMode();
		PlayerData pd = (PlayerData) MIM.getPlugin().getMysqlHandler().getData(MysqlHandler.Type.PLAYERDATA,
				"`player_uuid` = ? AND `synchro_key` = ? AND `game_mode` = ?",
				player.getUniqueId().toString(), synchroKey, gm.toString());
		if(pd == null)
		{
			return;
		}
		try (Connection conn = MIM.getPlugin().getMysqlSetup().getConnection();)
		{
			String sql = "UPDATE `" + MysqlHandler.Type.PLAYERDATA.getValue()
				+ "` SET `enderchest_content` = ?"
				+ " WHERE `synchro_key` = ? AND `player_uuid` = ?";
			PreparedStatement ps = conn.prepareStatement(sql);
	        ps.setString(1, MIM.getPlugin().getBase64Api().toBase64Array(player.getEnderChest().getContents()));
	        
	        ps.setString(2, pd.getSynchroKey());
	        ps.setString(3, player.getUniqueId().toString());		
			int u = ps.executeUpdate();
			MysqlHandler.addRows(MysqlHandler.QueryType.UPDATE, u);
		} catch (SQLException e)
		{
			MIM.log.log(Level.WARNING, "SQLException! Could not update a PlayerData Object!", e);
		}
	}
	
	/*public static void openShulkerInInventory(Player player, UUID targetPlayer, Inventory preInventory, 
			int slot, ItemStack shulker, ShulkerBox shulkerbox, String playername, String title)
	{
		executorToShulkerSlot.put(player.getUniqueId(), slot);
		executorToShulkerItem.put(player.getUniqueId(), shulker);
		if(targetPlayer != null && preInventory != null)
		{
			executorToOwnerInventory.put(player.getUniqueId(), targetPlayer);
			ownerToInventory.put(targetPlayer, preInventory);
		}
		Inventory inv = Bukkit.createInventory(null, 27, title != null ? title.replace("%player%", playername) : "Shulkerbox");
        inv.setContents(shulkerbox.getInventory().getContents());
        player.openInventory(inv);
	}*/
}