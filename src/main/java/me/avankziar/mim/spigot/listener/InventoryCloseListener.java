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
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.cmd.EnchantingTableCmdExecutor;
import main.java.me.avankziar.mim.spigot.database.MysqlHandler;
import main.java.me.avankziar.mim.spigot.objects.CustomPlayerInventory;
import main.java.me.avankziar.mim.spigot.objects.SyncTask.RunType;
import main.java.me.avankziar.mim.spigot.objects.SyncType;

public class InventoryCloseListener extends BaseListener
{
	public static LinkedHashMap<UUID, Inventory> ownerToInventory = new LinkedHashMap<>();//InventoryOwner, Inventory
	public static LinkedHashMap<UUID, UUID> executorToOwnerInventory = new LinkedHashMap<>();//CmdExecutor, InventoryOwner
	public static LinkedHashMap<UUID, String> executorToInventoryType = new LinkedHashMap<>();//CmdExecutor, InventoryType(CPI, Enderchest etc.)
	//Only useable for Enderchest etc. (CPI not use this)
	public static LinkedHashMap<UUID, GameMode> executorToGameMode = new LinkedHashMap<>(); //CmdExecutor, targetGamemode
	public static LinkedHashMap<UUID, String> executorToSynchroKey = new LinkedHashMap<>(); //CmdExecutor, targetSyncorKey 
	//Only if the person rightclick in a inv on a shulker
	public static LinkedHashMap<UUID, Integer> executorToShulkerSlot = new LinkedHashMap<>(); //CmdExecutor, SlotInOverInventory
	public static LinkedHashMap<UUID, ItemStack> executorToShulkerItem = new LinkedHashMap<>(); //CmdExecutor, ShulkerItemStack
	
	public InventoryCloseListener(MIM plugin)
	{
		super(plugin, BaseListener.Type.INVENTORY_CLOSE);
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onInventoryClose(InventoryCloseEvent event)
	{
		if(inExternInventory(event.getPlayer().getUniqueId()))
		{
			if(removeToExternInventory((Player) event.getPlayer(), event.getInventory().getContents()))
			{
				return;
			}
		}		
		if(EnchantingTableCmdExecutor.inEnchantingTable.contains(event.getPlayer().getUniqueId()))
		{
			EnchantingTableCmdExecutor.inEnchantingTable.remove(event.getPlayer().getUniqueId());
		}
		if(!plugin.getConfigHandler().isEventEnabled(this.bType.getName(), event.getPlayer().getWorld()))
		{
			return;
		}
		Player player = (Player) event.getPlayer();
		doSync(player, SyncType.INVENTORY, RunType.SAVE);
	}
	
	public static boolean inExternInventory(UUID executorPlayer)
	{
		return executorToOwnerInventory.containsKey(executorPlayer);
	}
	
	public static Inventory getExternInventory(UUID targetPlayer)
	{
		if(ownerToInventory.containsKey(targetPlayer))
		{
			return ownerToInventory.get(targetPlayer);
		}
		return null;
	}
	
	public static void addToExternInventory(UUID executorPlayer, UUID targetPlayer,
			Inventory inv, String invType, String subType, GameMode targetMode, String synchroKey)
	{
		ownerToInventory.put(targetPlayer, inv);
		executorToOwnerInventory.put(executorPlayer, targetPlayer);
		executorToInventoryType.put(executorPlayer, invType+";"+subType);
		executorToGameMode.put(executorPlayer, targetMode);
		executorToSynchroKey.put(executorPlayer, synchroKey);
	}
	
	public static boolean removeToExternInventory(Player executor, ItemStack[] shulkerInventoryOnly)
	{
		UUID executorPlayer = executor.getUniqueId();
		//Player is in a ShulkerSubInventory
		if(executorToShulkerSlot.containsKey(executorPlayer))
		{
			final int slot = executorToShulkerSlot.get(executorPlayer);
			final ItemStack is = executorToShulkerItem.get(executorPlayer);
			if(!(is.getItemMeta() instanceof BlockStateMeta))
			{
				return false;
			}
			BlockStateMeta im = (BlockStateMeta) is.getItemMeta();
	        if(!(im.getBlockState() instanceof ShulkerBox))
	        {
	        	return false;
	        }
	        ShulkerBox shulker = (ShulkerBox) im.getBlockState();
	        shulker.getInventory().clear();
	        shulker.getInventory().addItem(shulkerInventoryOnly);
	        im.setBlockState(shulker);
	        is.setItemMeta(im);
	        final UUID targetPlayer = executorToOwnerInventory.get(executorPlayer);
			final Inventory inv = ownerToInventory.get(targetPlayer);
			inv.setItem(slot, is);
			executor.openInventory(inv);
			executorToShulkerSlot.remove(executorPlayer);
			executorToShulkerItem.remove(executorPlayer);
			return true;
		}
		final UUID targetPlayer = executorToOwnerInventory.get(executorPlayer);
		final Inventory inv = ownerToInventory.get(targetPlayer); 
		final String invType = executorToInventoryType.get(executorPlayer).split(";")[0];
		final String subType = executorToInventoryType.get(executorPlayer).split(";")[1];
		final GameMode targetMode = executorToGameMode.get(executorPlayer);
		final String synchroKey = executorToSynchroKey.get(executorPlayer);
		executorToOwnerInventory.remove(executorPlayer);
		executorToInventoryType.remove(executorPlayer);
		executorToGameMode.remove(executorPlayer);
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
			ownerToInventory.remove(targetPlayer);
			Player target = Bukkit.getPlayer(targetPlayer);
			switch(invType)
			{
			default:
				return false;
			case "INV":
				if(target != null)
				{
					target.getInventory().setStorageContents(inv.getContents());
					target.updateInventory();
				} else
				{
					try (Connection conn = MIM.getPlugin().getMysqlSetup().getConnection();)
					{
						String sql = "UPDATE `" + MysqlHandler.Type.PLAYERDATA.getValue()
							+ "` SET `inventory_content` = ?"
							+ " WHERE `synchro_key` = ? AND `game_mode` = ? AND `player_uuid` = ?";
						PreparedStatement ps = conn.prepareStatement(sql);
				        ps.setString(1, MIM.getPlugin().getBase64Api().toBase64Array(inv.getContents()));
				        
				        ps.setString(2, synchroKey);
				        ps.setString(3, targetMode.toString());
				        ps.setString(4, targetPlayer.toString());
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
							+ " WHERE `synchro_key` = ? AND `game_mode` = ? AND `player_uuid` = ?";
						PreparedStatement ps = conn.prepareStatement(sql);
				        ps.setString(1, MIM.getPlugin().getBase64Api().toBase64Array(armor.toArray(new ItemStack[armor.size()])));
				        ps.setString(2, MIM.getPlugin().getBase64Api().toBase64(offhand));
				        
				        ps.setString(3, synchroKey);
				        ps.setString(4, targetMode.toString());
				        ps.setString(5, targetPlayer.toString());	
						int u = ps.executeUpdate();
						MysqlHandler.addRows(MysqlHandler.QueryType.UPDATE, u);
					} catch (SQLException e)
					{
						MIM.log.log(Level.WARNING, "SQLException! Could not update a PlayerData Object!", e);
					}
					break;
				}
			case "EC": //Enderchest
				try (Connection conn = MIM.getPlugin().getMysqlSetup().getConnection();)
				{
					String sql = "UPDATE `" + MysqlHandler.Type.PLAYERDATA.getValue()
						+ "` SET `player_name` = ?,"
						+ " `enderchest_content` = ?"
						+ " WHERE `synchro_key` = ? AND `game_mode` = ? AND `player_uuid` = ?";
					PreparedStatement ps = conn.prepareStatement(sql);
			        ps.setString(1, subType);
			        ps.setString(2, MIM.getPlugin().getBase64Api().toBase64Array(inv.getContents()));
			        
			        ps.setString(3, synchroKey);
			        ps.setString(4, targetMode.toString());
			        ps.setString(5, targetPlayer.toString());		
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
						"`cpi_name` = ? AND `owner_name`", cpi.getUniqueName(), cpi.getOwnerUUID().toString());
				break;
			}
		}
		return true;
	}
}