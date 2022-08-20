package main.java.me.avankziar.mim.spigot.handler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.database.MysqlHandler;
import main.java.me.avankziar.mim.spigot.gui.GUI.PersistentType;
import main.java.me.avankziar.mim.spigot.objects.PersistentData;
import main.java.me.avankziar.mim.spigot.objects.PredefinePlayerState;
import main.java.me.avankziar.mim.spigot.objects.SyncType;

public class PredefinePlayerStateHandler
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
	public static void save(SyncType syncType, final Player player, String statename, String synchroKey)
	{
		PredefinePlayerState pps = (PredefinePlayerState) MIM.getPlugin().getMysqlHandler().getData(MysqlHandler.Type.PREDEFINEPLAYERSTATE,
				"`state_name` = ? AND `synchro_key` = ?",
				statename, synchroKey);
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
		if(pps != null)
		{
			pps.setSynchroKey(synchroKey);
			pps.setStateName(statename);
			pps.setInventoryStorageContents(player.getInventory().getStorageContents());
			pps.setArmorContents(player.getInventory().getArmorContents());
			pps.setOffHand(player.getInventory().getItemInOffHand());
			pps.setEnderchestContents(player.getEnderChest().getContents());
			pps.setFoodLevel(player.getFoodLevel());
			pps.setSaturation(player.getSaturation());
			pps.setSaturatedRegenRate(player.getSaturatedRegenRate());
			pps.setUnsaturatedRegenRate(player.getUnsaturatedRegenRate());
			pps.setStarvationRate(player.getStarvationRate());
			pps.setExhaustion(player.getExhaustion());
			pps.setAttributes(attributes);
			pps.setHealth(player.getHealth());
			pps.setAbsorptionAmount(player.getAbsorptionAmount());
			pps.setExpTowardsNextLevel(player.getExp());
			pps.setExpLevel(player.getLevel());
			pps.setWalkSpeed(player.getWalkSpeed());
			pps.setFlySpeed(player.getFlySpeed());
			pps.setFireTicks(player.getFireTicks());
			pps.setFreezeTicks(player.getFreezeTicks());
			pps.setGlowing(player.isGlowing());
			pps.setGravity(player.hasGravity());
			pps.setActiveEffects(pe);
			pps.setEntityCategory(player.getCategory());
			pps.setArrowsInBody(player.getArrowsInBody());
			pps.setMaximumAir(player.getMaximumAir());
			pps.setRemainingAir(player.getRemainingAir());
			pps.setPersistentData(getPersitentData(player));
			if(syncType != SyncType.FULL)
			{
				save(syncType, player, pps);
				return;
			}
			MIM.getPlugin().getMysqlHandler().updateData(MysqlHandler.Type.PREDEFINEPLAYERSTATE, pps,
					"`state_name` = ? AND `synchro_key` = ?",
				statename, synchroKey);
		} else
		{
			pps = new PredefinePlayerState(0, synchroKey, statename,
					player.getInventory().getStorageContents(), player.getInventory().getArmorContents(),
					player.getInventory().getItemInOffHand(), player.getEnderChest().getContents(),
					player.getFoodLevel(), player.getSaturation(), player.getSaturatedRegenRate(),
					player.getUnsaturatedRegenRate(), player.getStarvationRate(), player.getExhaustion(),
					attributes, player.getHealth(), player.getAbsorptionAmount(), 
					player.getExp(), player.getLevel(),
					player.getWalkSpeed(), player.getFlySpeed(), player.getFireTicks(), player.getFreezeTicks(),
					player.getAllowFlight(), player.isGlowing(), player.hasGravity(), player.isInvisible(), player.isInvulnerable(),
					pe, player.getCategory(), player.getArrowsInBody(), 
					player.getMaximumAir(), player.getRemainingAir(), getPersitentData(player));
			MIM.getPlugin().getMysqlHandler().create(MysqlHandler.Type.PREDEFINEPLAYERSTATE, pps);
		}
	}
	
	private static void save(SyncType syncType, Player player, PredefinePlayerState pps)
	{
		switch(syncType)
		{
		default:
			return;
		case ATTRIBUTE:
			try (Connection conn = MIM.getPlugin().getMysqlSetup().getConnection();)
			{
				String sql = "UPDATE `" + MysqlHandler.Type.PREDEFINEPLAYERSTATE.getValue()
					+ "` SET `food_level` = ?, `saturation` = ?, `saturated_regen_rate` = ?,"
					+ " `unsaturated_regen_rate` = ?, `starvation_rate` = ?, `exhaustion` = ?,"
					+ " `attributes` = ?, `health` = ?, `absorption_amount` = ?,"
					+ " `walk_speed` = ?, `fly_speed` = ?, `fire_ticks` = ?,"
					+ " `freeze_ticks` = ?, `glowing` = ?, `gravity` = ?,"
					+ " `entity_category` = ?, `arrows_in_body` = ?, `maximum_air` = ?,"
					+ " `remaining_air` = ?"
					+ " WHERE `synchro_key` = ? AND `state_name` = ?";
				PreparedStatement ps = conn.prepareStatement(sql);
		        ps.setInt(1, pps.getFoodLevel());
		        ps.setFloat(2, pps.getSaturation());
		        ps.setInt(3, pps.getSaturatedRegenRate());
		        ps.setInt(4, pps.getUnsaturatedRegenRate());
		        ps.setInt(5, pps.getStarvationRate());
		        ps.setFloat(6, pps.getExhaustion());
		        StringBuilder at = new StringBuilder();
		        for(Entry<Attribute, Double> e : pps.getAttributes().entrySet())
		        {
		        	at.append(e.getKey().toString()+";"+e.getValue().doubleValue()+"@");
		        }
		        ps.setString(7, at.toString());
		        ps.setDouble(8, pps.getHealth());
		        ps.setDouble(9, pps.getAbsorptionAmount());
		        ps.setFloat(10, pps.getWalkSpeed());
		        ps.setFloat(11, pps.getFlySpeed());
		        ps.setInt(12, pps.getFireTicks());
		        ps.setInt(13, pps.getFreezeTicks());
		        ps.setBoolean(14, pps.isGlowing());
		        ps.setBoolean(15, pps.isGravity());
		        ps.setString(16, pps.getEntityCategory().toString());
		        ps.setInt(17, pps.getArrowsInBody());
		        ps.setInt(18, pps.getMaximumAir());
		        ps.setInt(19, pps.getRemainingAir());
		        
		        ps.setString(20, pps.getSynchroKey());
		        ps.setString(21, player.getUniqueId().toString());		
				int u = ps.executeUpdate();
				MysqlHandler.addRows(MysqlHandler.QueryType.UPDATE, u);
			} catch (SQLException e)
			{
				MIM.log.log(Level.WARNING, "SQLException! Could not update a PlayerData Object!", e);
			}
			return;
		case EXP:
			try (Connection conn = MIM.getPlugin().getMysqlSetup().getConnection();)
			{
				String sql = "UPDATE `" + MysqlHandler.Type.PREDEFINEPLAYERSTATE.getValue()
					+ "` SET "
					+ " `exp_towards_next_level` = ?, `exp_level` = ?"
					+ " WHERE `synchro_key` = ? AND `state_name` = ?";
				PreparedStatement ps = conn.prepareStatement(sql);
		        ps.setFloat(1, pps.getExpTowardsNextLevel());
		        ps.setInt(2, pps.getExpLevel());
		        
		        ps.setString(3, pps.getSynchroKey());
		        ps.setString(4, pps.getStateName());		
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
				String sql = "UPDATE `" + MysqlHandler.Type.PREDEFINEPLAYERSTATE.getValue()
					+ "` SET "
					+ " `inventory_content` = ?, `armor_content` = ?, `off_hand` = ?, `enderchest_content` = ?"
					+ " WHERE `synchro_key` = ? AND `state_name` = ?";
				PreparedStatement ps = conn.prepareStatement(sql);
		        ps.setString(1, MIM.getPlugin().getBase64Api().toBase64Array(pps.getInventoryStorageContents()));
		        ps.setString(2, MIM.getPlugin().getBase64Api().toBase64Array(pps.getArmorContents()));
		        ps.setString(3, MIM.getPlugin().getBase64Api().toBase64(pps.getOffHand()));
		        ps.setString(4, MIM.getPlugin().getBase64Api().toBase64Array(pps.getEnderchestContents()));
		        
		        ps.setString(5, pps.getSynchroKey());
		        ps.setString(6, pps.getStateName());		
				int u = ps.executeUpdate();
				MysqlHandler.addRows(MysqlHandler.QueryType.UPDATE, u);
			} catch (SQLException e)
			{
				MIM.log.log(Level.WARNING, "SQLException! Could not update a PlayerData Object!", e);
			}
			return;
		case EFFECT:
			try (Connection conn = MIM.getPlugin().getMysqlSetup().getConnection();)
			{
				String sql = "UPDATE `" + MysqlHandler.Type.PREDEFINEPLAYERSTATE.getValue()
					+ "` SET "
					+ " `potion_effects` = ?"
					+ " WHERE `synchro_key` = ? AND `state_name` = ?";
				PreparedStatement ps = conn.prepareStatement(sql);
		        ps.setString(1, MIM.getPlugin().getBase64Api().toBase64Array(pps.getActiveEffects().toArray(
		        		new PotionEffect[pps.getActiveEffects().size()])));
		        
		        ps.setString(2, pps.getSynchroKey());
		        ps.setString(3, player.getUniqueId().toString());		
				int u = ps.executeUpdate();
				MysqlHandler.addRows(MysqlHandler.QueryType.UPDATE, u);
			} catch (SQLException e)
			{
				MIM.log.log(Level.WARNING, "SQLException! Could not update a PlayerData Object!", e);
			}
			return;
		case PERSITENTDATA:
			try (Connection conn = MIM.getPlugin().getMysqlSetup().getConnection();)
			{
				String sql = "UPDATE `" + MysqlHandler.Type.PREDEFINEPLAYERSTATE.getValue()
					+ "` SET "
					+ " `persistent_data` = ?"
					+ " WHERE `synchro_key` = ? AND `state_name` = ?";
				PreparedStatement ps = conn.prepareStatement(sql);
		        StringBuilder pds = new StringBuilder();
		        for(PersistentData per : pps.getPersistentData())
		        {
		        	pds.append(per.getNamespaced()+";"+per.getKey()+";"+per.getPersistentType().toString()+";"+per.getPersistentValue()+"@");
		        }
		        ps.setString(1, pds.toString());
		        
		        ps.setString(2, pps.getSynchroKey());
		        ps.setString(3, player.getUniqueId().toString());		
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
	public static void load(SyncType syncType, Player player, String statename)
	{
		String synchroKey = MIM.getPlugin().getConfigHandler().getSynchroKey(player, false);
		GameMode gm = player.getGameMode();
		PredefinePlayerState pps = (PredefinePlayerState) MIM.getPlugin().getMysqlHandler().getData(MysqlHandler.Type.PREDEFINEPLAYERSTATE,
				"`state_uuid` = ? AND `synchro_key` = ? AND `game_mode` = ?",
				statename, synchroKey, gm.toString());
		if(pps == null)
		{
			return;
		}
		if(syncType != SyncType.FULL)
		{
			load(syncType, player, pps);
			return;
		}
		player.getInventory().setStorageContents(pps.getInventoryStorageContents());
		player.getInventory().setArmorContents(pps.getArmorContents());
		player.getInventory().setItemInOffHand(pps.getOffHand());
		player.getEnderChest().setContents(pps.getEnderchestContents());
		player.setFoodLevel(pps.getFoodLevel());
		player.setSaturation(pps.getSaturation());
		player.setSaturatedRegenRate(pps.getSaturatedRegenRate());
		player.setUnsaturatedRegenRate(pps.getUnsaturatedRegenRate());
		player.setStarvationRate(pps.getStarvationRate());
		player.setExhaustion(pps.getExhaustion());
		for(Attribute a : attributeList)
		{
			if(pps.getAttributes().containsKey(a))
			{
				player.getAttribute(a).setBaseValue(pps.getAttributes().get(a));
			}
		}
		player.setHealth(pps.getHealth());
		player.setAbsorptionAmount(pps.getAbsorptionAmount());
		player.setExp(pps.getExpTowardsNextLevel());
		player.setLevel(pps.getExpLevel());
		player.setWalkSpeed(pps.getWalkSpeed());
		player.setFlySpeed(pps.getFlySpeed());
		player.setFireTicks(pps.getFireTicks());
		player.setFreezeTicks(pps.getFreezeTicks());
		player.setGlowing(pps.isGlowing());
		player.setGravity(pps.isGravity());
		for(PotionEffect pe : pps.getActiveEffects())
		{
			player.addPotionEffect(pe);
		}
		player.setArrowsInBody(pps.getArrowsInBody());
		player.setMaximumAir(pps.getMaximumAir());
		player.setRemainingAir(pps.getRemainingAir());
		setPersitentData(player, pps.getPersistentData());
	}
	
	private static void load(SyncType syncType, Player player, PredefinePlayerState pps)
	{
		switch(syncType)
		{
		default:
			return;
		case ATTRIBUTE:
			player.setFoodLevel(pps.getFoodLevel());
			player.setSaturation(pps.getSaturation());
			player.setSaturatedRegenRate(pps.getSaturatedRegenRate());
			player.setUnsaturatedRegenRate(pps.getUnsaturatedRegenRate());
			player.setStarvationRate(pps.getStarvationRate());
			player.setExhaustion(pps.getExhaustion());
			for(Attribute a : attributeList)
			{
				if(pps.getAttributes().containsKey(a))
				{
					player.getAttribute(a).setBaseValue(pps.getAttributes().get(a));
				}
			}
			player.setHealth(pps.getHealth());
			player.setAbsorptionAmount(pps.getAbsorptionAmount());
			player.setWalkSpeed(pps.getWalkSpeed());
			player.setFlySpeed(pps.getFlySpeed());
			player.setFireTicks(pps.getFireTicks());
			player.setFreezeTicks(pps.getFreezeTicks());
			player.setGlowing(pps.isGlowing());
			player.setGravity(pps.isGravity());
			player.setArrowsInBody(pps.getArrowsInBody());
			player.setMaximumAir(pps.getMaximumAir());
			player.setRemainingAir(pps.getRemainingAir());
		case EXP:
			player.setExp(pps.getExpTowardsNextLevel());
			player.setLevel(pps.getExpLevel());
			return;
		case INVENTORY:
			player.getInventory().setStorageContents(pps.getInventoryStorageContents());
			player.getInventory().setArmorContents(pps.getArmorContents());
			player.getInventory().setItemInOffHand(pps.getOffHand());
			player.getEnderChest().setContents(pps.getEnderchestContents());
			return;
		case EFFECT:
			for(PotionEffect pe : pps.getActiveEffects())
			{
				player.addPotionEffect(pe);
			}
			return;
		case PERSITENTDATA:
			setPersitentData(player, pps.getPersistentData());
			return;
		}
	}
}