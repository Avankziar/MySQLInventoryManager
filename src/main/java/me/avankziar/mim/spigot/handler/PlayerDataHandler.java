package main.java.me.avankziar.mim.spigot.handler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.logging.Level;

import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.database.MysqlHandler;
import main.java.me.avankziar.mim.spigot.gui.GUI.PersistentType;
import main.java.me.avankziar.mim.spigot.objects.PersistentData;
import main.java.me.avankziar.mim.spigot.objects.PlayerData;
import main.java.me.avankziar.mim.spigot.objects.SyncType;

public class PlayerDataHandler
{
	private static ArrayList<Attribute> attributeList = new ArrayList<>();
	static
	{
		for(Attribute a : new ArrayList<Attribute>(EnumSet.allOf(Attribute.class)))
		{
			if(a != Attribute.HORSE_JUMP_STRENGTH && a != Attribute.ZOMBIE_SPAWN_REINFORCEMENTS)
			{
				attributeList.add(a);
			}
		}
	}
	
	private static ArrayList<PersistentData> getPersitentData(Player player)
	{
		ArrayList<PersistentData> list = new ArrayList<>();
		PersistentDataContainer pdc = player.getPersistentDataContainer();
		for(NamespacedKey n : pdc.getKeys())
		{
			for(PersistentType a : new ArrayList<PersistentType>(EnumSet.allOf(PersistentType.class)))
			{
				PersistentDataType<?, ?> pdt = null;
				switch(a)
				{
				default:
					break;
				case BYTE:
					pdt = PersistentDataType.BYTE;
					if(pdc.has(n, pdt))
					{
						list.add(new PersistentData(n.getNamespace(), n.getKey(), a, String.valueOf((Byte) pdc.get(n, pdt))));
					}
					break;
				case DOUBLE:
					pdt = PersistentDataType.DOUBLE;
					if(pdc.has(n, pdt))
					{
						list.add(new PersistentData(n.getNamespace(), n.getKey(), a, String.valueOf((double) pdc.get(n, pdt))));
					}
					break;
				case FLOAT:
					pdt = PersistentDataType.FLOAT;
					break;
				case INTEGER:
					pdt = PersistentDataType.INTEGER;
					break;
				case LONG:
					pdt = PersistentDataType.LONG;
					break;
				case LONG_ARRAY:
					pdt = PersistentDataType.LONG_ARRAY;
					break;
				case SHORT:
					pdt = PersistentDataType.SHORT;
					break;
				case STRING:
					pdt = PersistentDataType.STRING;
					break;
				}
				
			}
		}
		return list;
	}
	
	private static void setPersitentData(Player player, ArrayList<PersistentData> list)
	{
		PersistentDataContainer pdc = player.getPersistentDataContainer();
		for(PersistentData pd : list)
		{
			@SuppressWarnings("deprecation")
			NamespacedKey n = new NamespacedKey(pd.getNamespaced(), pd.getKey());
			switch(pd.getPersistentType())
			{
			default:
				break;
			case BYTE:
				try
				{
					pdc.set(n, PersistentDataType.BYTE, Byte.valueOf(pd.getPersistentValue()));
				} catch(Exception e){}
				break;
			case DOUBLE:
				try
				{
					pdc.set(n, PersistentDataType.DOUBLE, Double.valueOf(pd.getPersistentValue()));
				} catch(Exception e){}
				break;
			case FLOAT:
				try
				{
					pdc.set(n, PersistentDataType.FLOAT, Float.valueOf(pd.getPersistentValue()));
				} catch(Exception e){}
				break;
			case INTEGER:
				try
				{
					pdc.set(n, PersistentDataType.INTEGER, Integer.valueOf(pd.getPersistentValue()));
				} catch(Exception e){}
				break;
			case LONG:
				try
				{
					pdc.set(n, PersistentDataType.LONG, Long.valueOf(pd.getPersistentValue()));
				} catch(Exception e){}
				break;
			case SHORT:
				try
				{
					pdc.set(n, PersistentDataType.SHORT, Short.valueOf(pd.getPersistentValue()));
				} catch(Exception e){}
				break;
			case STRING:
				try
				{
					pdc.set(n, PersistentDataType.STRING, String.valueOf(pd.getPersistentValue()));
				} catch(Exception e){}
				break;
			}
		}
		return;
	}
	
	/**
	 * Saves the playerData in the mysql. Async usage!
	 * @param player
	 */
	public static void save(SyncType syncType, final Player player)
	{
		String synchroKey = MIM.getPlugin().getConfigHandler().getSynchroKey(player);
		GameMode gm = player.getGameMode();
		PlayerData pd = (PlayerData) MIM.getPlugin().getMysqlHandler().getData(MysqlHandler.Type.PLAYERDATA,
				"`player_uuid` = ? AND `synchro_key` = ? AND `game_mode` = ?",
				player.getUniqueId().toString(), synchroKey, gm.toString());
		LinkedHashMap<Attribute, Double> attributes = new LinkedHashMap<>();
		for(Attribute at : attributeList)
		{
			attributes.put(at, player.getAttribute(at).getBaseValue());
		}
		ArrayList<PotionEffect> pe = new ArrayList<>();
		for(PotionEffect eff : player.getActivePotionEffects())
		{
			pe.add(eff);
		}
		if(pd != null)
		{
			if(syncType != SyncType.FULL)
			{
				save(syncType, player, pd);
				return;
			}
			pd.setName(player.getName());
			pd.setInventoryStorageContents(player.getInventory().getStorageContents());
			pd.setArmorContents(player.getInventory().getArmorContents());
			pd.setOffHand(player.getInventory().getItemInOffHand());
			pd.setEnderchestContents(player.getEnderChest().getContents());
			pd.setFoodLevel(player.getFoodLevel());
			pd.setSaturation(player.getSaturation());
			pd.setSaturatedRegenRate(player.getSaturatedRegenRate());
			pd.setUnsaturatedRegenRate(player.getUnsaturatedRegenRate());
			pd.setStarvationRate(player.getStarvationRate());
			pd.setExhaustion(player.getExhaustion());
			pd.setAttributes(attributes);
			pd.setHealth(player.getHealth());
			pd.setAbsorptionAmount(player.getAbsorptionAmount());
			pd.setExpTowardsNextLevel(player.getExp());
			pd.setExpLevel(player.getLevel());
			pd.setTotalExperience(player.getTotalExperience());
			pd.setWalkSpeed(player.getWalkSpeed());
			pd.setFlySpeed(player.getFlySpeed());
			pd.setFireTicks(player.getFireTicks());
			pd.setFreezeTicks(player.getFreezeTicks());
			pd.setGlowing(player.isGlowing());
			pd.setGravity(player.hasGravity());
			pd.setActiveEffects(pe);
			pd.setEntityCategory(player.getCategory());
			pd.setArrowsInBody(player.getArrowsInBody());
			pd.setMaximumAir(player.getMaximumAir());
			pd.setRemainingAir(player.getRemainingAir());
			pd.setCustomName(player.getCustomName());
			pd.setPersistentData(getPersitentData(player));
			MIM.getPlugin().getMysqlHandler().updateData(MysqlHandler.Type.PLAYERDATA, pd, synchroKey,
					"`player_uuid` = ? AND `synchro_key` = ? AND `game_mode` = ?",
				player.getUniqueId().toString(), synchroKey, gm.toString());
		} else
		{
			pd = new PlayerData(0, synchroKey, gm, player.getUniqueId(), player.getName(),
					player.getInventory().getStorageContents(), player.getInventory().getArmorContents(),
					player.getInventory().getItemInOffHand(), player.getEnderChest().getContents(),
					player.getFoodLevel(), player.getSaturation(), player.getSaturatedRegenRate(),
					player.getUnsaturatedRegenRate(), player.getStarvationRate(), player.getExhaustion(),
					attributes, player.getHealth(), player.getAbsorptionAmount(), 
					player.getExp(), player.getLevel(), player.getTotalExperience(), 
					player.getWalkSpeed(), player.getFlySpeed(), player.getFireTicks(), player.getFreezeTicks(),
					player.isGlowing(), player.hasGravity(), pe, player.getCategory(), player.getArrowsInBody(), 
					player.getMaximumAir(), player.getRemainingAir(), player.getCustomName(), getPersitentData(player),
					MIM.getPlugin().getConfigHandler().getDefaultClearToggle());
			MIM.getPlugin().getMysqlHandler().create(MysqlHandler.Type.PLAYERDATA, pd);
		}
	}
	
	private static void save(SyncType syncType, Player player, PlayerData playerData)
	{
		switch(syncType)
		{
		case FULL:
			return;
		case EXP:
			try (Connection conn = MIM.getPlugin().getMysqlSetup().getConnection();)
			{
				String sql = "UPDATE `" + MysqlHandler.Type.PLAYERDATA.getValue()
					+ "` SET `player_name` = ?,"
					+ " `exp_towards_next_level` = ?, `exp_level` = ?, `total_experience` = ?"
					+ " WHERE `synchro_key` = ? AND `game_mode` = ? AND `player_uuid` = ?";
				PreparedStatement ps = conn.prepareStatement(sql);
		        ps.setString(1, playerData.getName());
		        ps.setFloat(2, playerData.getExpTowardsNextLevel());
		        ps.setInt(3, playerData.getExpLevel());
		        ps.setInt(4, playerData.getTotalExperience());
		        
		        ps.setString(5, playerData.getSynchroKey());
		        ps.setString(6, playerData.getGameMode().toString());
		        ps.setString(7, player.getUniqueId().toString());		
				int u = ps.executeUpdate();
				MysqlHandler.addRows(MysqlHandler.QueryType.UPDATE, u);
			} catch (SQLException e)
			{
				MIM.log.log(Level.WARNING, "SQLException! Could not update a PlayerData Object!", e);
			}
			return;
		case INVENTORY:
			try (Connection conn = MIM.getPlugin().getMysqlSetup().getConnection();)
			{
				String sql = "UPDATE `" + MysqlHandler.Type.PLAYERDATA.getValue()
					+ "` SET `player_name` = ?,"
					+ " `inventory_content` = ?, `armor_content` = ?, `off_hand` = ?, `enderchest_content` = ?"
					+ " WHERE `synchro_key` = ? AND `game_mode` = ? AND `player_uuid` = ?";
				PreparedStatement ps = conn.prepareStatement(sql);
		        ps.setString(1, playerData.getName());
		        ps.setString(2, MIM.getPlugin().getBase64Api().toBase64Array(playerData.getInventoryStorageContents()));
		        ps.setString(3, MIM.getPlugin().getBase64Api().toBase64Array(playerData.getArmorContents()));
		        ps.setString(4, MIM.getPlugin().getBase64Api().toBase64(playerData.getOffHand()));
		        ps.setString(5, MIM.getPlugin().getBase64Api().toBase64Array(playerData.getEnderchestContents()));
		        
		        ps.setString(6, playerData.getSynchroKey());
		        ps.setString(7, playerData.getGameMode().toString());
		        ps.setString(8, player.getUniqueId().toString());		
				int u = ps.executeUpdate();
				MysqlHandler.addRows(MysqlHandler.QueryType.UPDATE, u);
			} catch (SQLException e)
			{
				MIM.log.log(Level.WARNING, "SQLException! Could not update a PlayerData Object!", e);
			}
			return;
		}
	}
	
	/**
	 * Load the playerData on the player. Always sync
	 * @param player
	 */
	public static void load(SyncType syncType, Player player)
	{
		String synchroKey = MIM.getPlugin().getConfigHandler().getSynchroKey(player);
		GameMode gm = player.getGameMode();
		PlayerData pd = (PlayerData) MIM.getPlugin().getMysqlHandler().getData(MysqlHandler.Type.PLAYERDATA,
				"`player_uuid` = ? AND `synchro_key` = ? AND `game_mode` = ?",
				player.getUniqueId().toString(), synchroKey, gm.toString());
		if(pd == null)
		{
			LinkedHashMap<Attribute, Double> attributes = new LinkedHashMap<>();
			for(Attribute at : attributeList)
			{
				attributes.put(at, player.getAttribute(at).getBaseValue());
			}
			ArrayList<PotionEffect> pe = new ArrayList<>();
			for(PotionEffect eff : player.getActivePotionEffects())
			{
				pe.add(eff);
			}
			pd = new PlayerData(0, synchroKey, gm, player.getUniqueId(), player.getName(),
					player.getInventory().getStorageContents(), player.getInventory().getArmorContents(),
					player.getInventory().getItemInOffHand(), player.getEnderChest().getContents(),
					player.getFoodLevel(), player.getSaturation(), player.getSaturatedRegenRate(),
					player.getUnsaturatedRegenRate(), player.getStarvationRate(), player.getExhaustion(),
					attributes, player.getHealth(), player.getAbsorptionAmount(), 
					player.getExp(), player.getLevel(), player.getTotalExperience(), 
					player.getWalkSpeed(), player.getFlySpeed(), player.getFireTicks(), player.getFreezeTicks(),
					player.isGlowing(), player.hasGravity(), pe, player.getCategory(), player.getArrowsInBody(), 
					player.getMaximumAir(), player.getRemainingAir(), player.getCustomName(), getPersitentData(player),
					MIM.getPlugin().getConfigHandler().getDefaultClearToggle());
			MIM.getPlugin().getMysqlHandler().create(MysqlHandler.Type.PLAYERDATA, pd);
			return;
		}
		if(syncType != SyncType.FULL)
		{
			load(syncType, player, pd);
			return;
		}
		player.getInventory().setStorageContents(pd.getInventoryStorageContents());
		player.getInventory().setArmorContents(pd.getArmorContents());
		player.getInventory().setItemInOffHand(pd.getOffHand());
		player.getEnderChest().setContents(pd.getEnderchestContents());
		player.setFoodLevel(pd.getFoodLevel());
		player.setSaturation(pd.getSaturation());
		player.setSaturatedRegenRate(pd.getSaturatedRegenRate());
		player.setUnsaturatedRegenRate(pd.getUnsaturatedRegenRate());
		player.setStarvationRate(pd.getStarvationRate());
		player.setExhaustion(pd.getExhaustion());
		for(Attribute a : attributeList)
		{
			if(pd.getAttributes().containsKey(a))
			{
				player.getAttribute(a).setBaseValue(pd.getAttributes().get(a));
			}
		}
		player.setHealth(pd.getHealth());
		player.setAbsorptionAmount(pd.getAbsorptionAmount());
		player.setExp(pd.getExpTowardsNextLevel());
		player.setLevel(pd.getExpLevel());
		player.setTotalExperience(pd.getTotalExperience());
		player.setWalkSpeed(pd.getWalkSpeed());
		player.setFlySpeed(pd.getFlySpeed());
		player.setFireTicks(pd.getFireTicks());
		player.setFreezeTicks(pd.getFreezeTicks());
		player.setGlowing(pd.isGlowing());
		player.setGravity(pd.isGravity());
		player.setArrowsInBody(pd.getArrowsInBody());
		player.setMaximumAir(pd.getMaximumAir());
		player.setRemainingAir(pd.getRemainingAir());
		setPersitentData(player, pd.getPersistentData());
	}
	
	private static void load(SyncType syncType, Player player, PlayerData playerData)
	{
		switch(syncType)
		{
		case FULL:
			return;
		case EXP:
			try (Connection conn = MIM.getPlugin().getMysqlSetup().getConnection();)
			{
				String sql = "SELECT * FROM `" + MysqlHandler.Type.PLAYERDATA.getValue() 
				+ "` WHERE WHERE `synchro_key` = ? AND `game_mode` = ? AND `player_uuid` = ? ORDER BY `id` Limit 1";
				PreparedStatement ps = conn.prepareStatement(sql);
				
				ps.setString(1, playerData.getSynchroKey());
		        ps.setString(2, playerData.getGameMode().toString());
		        ps.setString(3, player.getUniqueId().toString());
				
				ResultSet rs = ps.executeQuery();
				while (rs.next()) 
				{
					float expt = rs.getFloat("exp_towards_next_level");
					int lev = rs.getInt("exp_level");
					int te = rs.getInt("total_experience");
					player.setExp(expt);
					player.setLevel(lev);
					player.setTotalExperience(te);
				}
			} catch (SQLException e)
			{
				MIM.log.log(Level.WARNING, "SQLException! Could not update a PlayerData Object!", e);
			}
			return;
		case INVENTORY:
			try (Connection conn = MIM.getPlugin().getMysqlSetup().getConnection();)
			{
				String sql = "SELECT * FROM `" + MysqlHandler.Type.PLAYERDATA.getValue() 
				+ "` WHERE WHERE `synchro_key` = ? AND `game_mode` = ? AND `player_uuid` = ? ORDER BY `id` Limit 1";
				PreparedStatement ps = conn.prepareStatement(sql);
				
				ps.setString(1, playerData.getSynchroKey());
		        ps.setString(2, playerData.getGameMode().toString());
		        ps.setString(3, player.getUniqueId().toString());
				
				ResultSet rs = ps.executeQuery();
				while (rs.next()) 
				{
					ArrayList<ItemStack> invc = new ArrayList<>();
					for(Object o : MIM.getPlugin().getBase64Api().fromBase64Array(rs.getString("inventory_content")))
					{
						invc.add((ItemStack) o);
					}
					ArrayList<ItemStack> arc = new ArrayList<>();
					for(Object o : MIM.getPlugin().getBase64Api().fromBase64Array(rs.getString("armor_content")))
					{
						arc.add((ItemStack) o);
					}
					ArrayList<ItemStack> ecc = new ArrayList<>();
					for(Object o : MIM.getPlugin().getBase64Api().fromBase64Array(rs.getString("enderchest_content")))
					{
						ecc.add((ItemStack) o);
					}
					;
					player.getInventory().setStorageContents(invc.toArray(new ItemStack[invc.size()]));
					player.getInventory().setArmorContents(arc.toArray(new ItemStack[arc.size()]));
					player.getInventory().setItemInOffHand((ItemStack) MIM.getPlugin().getBase64Api().fromBase64(rs.getString("off_hand")));
					player.getEnderChest().setContents(ecc.toArray(new ItemStack[ecc.size()]));
				}
			} catch (SQLException e)
			{
				MIM.log.log(Level.WARNING, "SQLException! Could not update a PlayerData Object!", e);
			}
			return;
		}
	}
}
