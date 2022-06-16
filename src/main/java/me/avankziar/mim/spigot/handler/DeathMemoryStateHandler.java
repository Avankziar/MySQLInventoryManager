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
import main.java.me.avankziar.mim.spigot.objects.DeathMemoryState;
import main.java.me.avankziar.mim.spigot.objects.PersistentData;
import main.java.me.avankziar.mim.spigot.objects.SyncType;

public class DeathMemoryStateHandler
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
	 * Saves the DeathMemoryState in the mysql. Async usage!
	 * @param player
	 */
	public static void save(SyncType syncType, final Player player)
	{
		String synchroKey = MIM.getPlugin().getConfigHandler().getSynchroKey(player, false);
		GameMode gm = player.getGameMode();
		int count = MIM.getPlugin().getMysqlHandler().getCount(MysqlHandler.Type.DEATHMEMORYSTATE, 
				"`player_uuid` = ? AND `synchro_key` = ? AND `game_mode` = ?",
				player.getUniqueId().toString(), synchroKey, gm.toString());
		DeathMemoryState dms = new DeathMemoryState();
		if(count >= new ConfigHandler(MIM.getPlugin()).getMaximalAmountDeathMemoryStatePerPlayer(player.getWorld()))
		{
			ArrayList<DeathMemoryState> list = DeathMemoryState.convert(
					MIM.getPlugin().getMysqlHandler().getList(MysqlHandler.Type.DEATHMEMORYSTATE, "`time_stamp` ASC", 0, 1,
					"`player_uuid` = ? AND `synchro_key` = ? AND `game_mode` = ?",
					player.getUniqueId().toString(), synchroKey, gm.toString()));
			for(DeathMemoryState dmst : list)
			{
				if(dmst != null)
				{
					dms = dmst;
					break;
				}
			}
		}
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
		if(dms != null)
		{
			if(syncType != SyncType.FULL)
			{
				save(syncType, player, dms);
				return;
			}
			dms.setTimeStamp(System.currentTimeMillis());
			dms.setInventoryStorageContents(player.getInventory().getStorageContents());
			dms.setArmorContents(player.getInventory().getArmorContents());
			dms.setOffHand(player.getInventory().getItemInOffHand());
			dms.setFoodLevel(player.getFoodLevel());
			dms.setSaturation(player.getSaturation());
			dms.setSaturatedRegenRate(player.getSaturatedRegenRate());
			dms.setUnsaturatedRegenRate(player.getUnsaturatedRegenRate());
			dms.setStarvationRate(player.getStarvationRate());
			dms.setExhaustion(player.getExhaustion());
			dms.setAttributes(attributes);
			dms.setHealth(player.getHealth());
			dms.setAbsorptionAmount(player.getAbsorptionAmount());
			dms.setExpTowardsNextLevel(player.getExp());
			dms.setExpLevel(player.getLevel());
			dms.setWalkSpeed(player.getWalkSpeed());
			dms.setFlySpeed(player.getFlySpeed());
			dms.setFireTicks(player.getFireTicks());
			dms.setFreezeTicks(player.getFreezeTicks());
			dms.setGlowing(player.isGlowing());
			dms.setGravity(player.hasGravity());
			dms.setActiveEffects(pe);
			dms.setEntityCategory(player.getCategory());
			dms.setArrowsInBody(player.getArrowsInBody());
			dms.setMaximumAir(player.getMaximumAir());
			dms.setRemainingAir(player.getRemainingAir());
			dms.setCustomName(player.getCustomName());
			dms.setPersistentData(getPersitentData(player));
			MIM.getPlugin().getMysqlHandler().updateData(MysqlHandler.Type.DEATHMEMORYSTATE, dms,
					"`player_uuid` = ? AND `synchro_key` = ? AND `game_mode` = ?",
				player.getUniqueId().toString(), synchroKey, gm.toString());
		}
	}
	
	private static void save(SyncType syncType, Player player, DeathMemoryState dms)
	{
		switch(syncType)
		{
		default:
			return;
		case ATTRIBUTE:
			try (Connection conn = MIM.getPlugin().getMysqlSetup().getConnection();)
			{
				String sql = "UPDATE `" + MysqlHandler.Type.DEATHMEMORYSTATE.getValue()
					+ "` SET `food_level` = ?, `saturation` = ?, `saturated_regen_rate` = ?,"
					+ " `unsaturated_regen_rate` = ?, `starvation_rate` = ?, `exhaustion` = ?,"
					+ " `attributes` = ?, `health` = ?, `absorption_amount` = ?,"
					+ " `walk_speed` = ?, `fly_speed` = ?, `fire_ticks` = ?,"
					+ " `freeze_ticks` = ?, `glowing` = ?, `gravity` = ?,"
					+ " `entity_category` = ?, `arrows_in_body` = ?, `maximum_air` = ?,"
					+ " `remaining_air` = ?, `custom_name` = ?,"
					+ " WHERE `synchro_key` = ? AND `game_mode` = ? AND `player_uuid` = ?";
				PreparedStatement ps = conn.prepareStatement(sql);
		        ps.setInt(1, dms.getFoodLevel());
		        ps.setFloat(2, dms.getSaturation());
		        ps.setInt(3, dms.getSaturatedRegenRate());
		        ps.setInt(4, dms.getUnsaturatedRegenRate());
		        ps.setInt(5, dms.getStarvationRate());
		        ps.setFloat(6, dms.getExhaustion());
		        StringBuilder at = new StringBuilder();
		        for(Entry<Attribute, Double> e : dms.getAttributes().entrySet())
		        {
		        	at.append(e.getKey().toString()+";"+e.getValue().doubleValue()+"@");
		        }
		        ps.setString(7, at.toString());
		        ps.setDouble(8, dms.getHealth());
		        ps.setDouble(9, dms.getAbsorptionAmount());
		        ps.setFloat(10, dms.getWalkSpeed());
		        ps.setFloat(11, dms.getFlySpeed());
		        ps.setInt(12, dms.getFireTicks());
		        ps.setInt(13, dms.getFreezeTicks());
		        ps.setBoolean(14, dms.isGlowing());
		        ps.setBoolean(15, dms.isGravity());
		        ps.setString(16, dms.getEntityCategory().toString());
		        ps.setInt(17, dms.getArrowsInBody());
		        ps.setInt(18, dms.getMaximumAir());
		        ps.setInt(19, dms.getRemainingAir());
		        
		        ps.setString(20, dms.getSynchroKey());
		        ps.setString(21, dms.getGameMode().toString());
		        ps.setString(22, player.getUniqueId().toString());		
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
				String sql = "UPDATE `" + MysqlHandler.Type.DEATHMEMORYSTATE.getValue()
					+ "` SET "
					+ " `exp_towards_next_level` = ?, `exp_level` = ?"
					+ " WHERE `synchro_key` = ? AND `game_mode` = ? AND `player_uuid` = ?";
				PreparedStatement ps = conn.prepareStatement(sql);
		        ps.setFloat(1, dms.getExpTowardsNextLevel());
		        ps.setInt(2, dms.getExpLevel());
		        
		        ps.setString(3, dms.getSynchroKey());
		        ps.setString(4, dms.getGameMode().toString());
		        ps.setString(5, player.getUniqueId().toString());		
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
				String sql = "UPDATE `" + MysqlHandler.Type.DEATHMEMORYSTATE.getValue()
					+ "` SET `player_name` = ?,"
					+ " `inventory_content` = ?, `armor_content` = ?, `off_hand` = ?"
					+ " WHERE `synchro_key` = ? AND `game_mode` = ? AND `player_uuid` = ?";
				PreparedStatement ps = conn.prepareStatement(sql);
		        ps.setString(1, MIM.getPlugin().getBase64Api().toBase64Array(dms.getInventoryStorageContents()));
		        ps.setString(2, MIM.getPlugin().getBase64Api().toBase64Array(dms.getArmorContents()));
		        ps.setString(3, MIM.getPlugin().getBase64Api().toBase64(dms.getOffHand()));
		        
		        ps.setString(4, dms.getSynchroKey());
		        ps.setString(5, dms.getGameMode().toString());
		        ps.setString(6, player.getUniqueId().toString());		
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
				String sql = "UPDATE `" + MysqlHandler.Type.DEATHMEMORYSTATE.getValue()
					+ "` SET "
					+ " `potion_effects` = ?"
					+ " WHERE `synchro_key` = ? AND `game_mode` = ? AND `player_uuid` = ?";
				PreparedStatement ps = conn.prepareStatement(sql);
		        ps.setString(1, MIM.getPlugin().getBase64Api().toBase64Array(dms.getActiveEffects().toArray(
		        		new PotionEffect[dms.getActiveEffects().size()])));
		        
		        ps.setString(2, dms.getSynchroKey());
		        ps.setString(3, dms.getGameMode().toString());
		        ps.setString(4, player.getUniqueId().toString());		
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
				String sql = "UPDATE `" + MysqlHandler.Type.DEATHMEMORYSTATE.getValue()
					+ "` SET "
					+ " `persistent_data` = ?"
					+ " WHERE `synchro_key` = ? AND `game_mode` = ? AND `player_uuid` = ?";
				PreparedStatement ps = conn.prepareStatement(sql);
		        StringBuilder pds = new StringBuilder();
		        for(PersistentData per : dms.getPersistentData())
		        {
		        	pds.append(per.getNamespaced()+";"+per.getKey()+";"+per.getPersistentType().toString()+";"+per.getPersistentValue()+"@");
		        }
		        ps.setString(1, pds.toString());
		        
		        ps.setString(2, dms.getSynchroKey());
		        ps.setString(3, dms.getGameMode().toString());
		        ps.setString(4, player.getUniqueId().toString());		
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
	public static void load(SyncType syncType, Player player, int id)
	{
		DeathMemoryState dms = (DeathMemoryState) MIM.getPlugin().getMysqlHandler().getData(MysqlHandler.Type.DEATHMEMORYSTATE,
				"`id` = ?",
				id);
		if(dms == null)
		{
			return;
		}//ADDME noch den fall einbauen, dass der Spieler nicht on ist. Bungeecord?
		if(syncType != SyncType.FULL)
		{
			load(syncType, player, dms);
			return;
		}
		player.getInventory().setStorageContents(dms.getInventoryStorageContents());
		player.getInventory().setArmorContents(dms.getArmorContents());
		player.getInventory().setItemInOffHand(dms.getOffHand());
		player.setFoodLevel(dms.getFoodLevel());
		player.setSaturation(dms.getSaturation());
		player.setSaturatedRegenRate(dms.getSaturatedRegenRate());
		player.setUnsaturatedRegenRate(dms.getUnsaturatedRegenRate());
		player.setStarvationRate(dms.getStarvationRate());
		player.setExhaustion(dms.getExhaustion());
		for(Attribute a : attributeList)
		{
			if(dms.getAttributes().containsKey(a))
			{
				player.getAttribute(a).setBaseValue(dms.getAttributes().get(a));
			}
		}
		player.setHealth(dms.getHealth());
		player.setAbsorptionAmount(dms.getAbsorptionAmount());
		player.setExp(dms.getExpTowardsNextLevel());
		player.setLevel(dms.getExpLevel());
		player.setWalkSpeed(dms.getWalkSpeed());
		player.setFlySpeed(dms.getFlySpeed());
		player.setFireTicks(dms.getFireTicks());
		player.setFreezeTicks(dms.getFreezeTicks());
		player.setGlowing(dms.isGlowing());
		player.setGravity(dms.isGravity());
		for(PotionEffect pe : dms.getActiveEffects())
		{
			player.addPotionEffect(pe);
		}
		player.setArrowsInBody(dms.getArrowsInBody());
		player.setMaximumAir(dms.getMaximumAir());
		player.setRemainingAir(dms.getRemainingAir());
		setPersitentData(player, dms.getPersistentData());
	}
	
	private static void load(SyncType syncType, Player player, DeathMemoryState dms)
	{
		switch(syncType)
		{
		default:
			return;
		case ATTRIBUTE:
			player.setFoodLevel(dms.getFoodLevel());
			player.setSaturation(dms.getSaturation());
			player.setSaturatedRegenRate(dms.getSaturatedRegenRate());
			player.setUnsaturatedRegenRate(dms.getUnsaturatedRegenRate());
			player.setStarvationRate(dms.getStarvationRate());
			player.setExhaustion(dms.getExhaustion());
			for(Attribute a : attributeList)
			{
				if(dms.getAttributes().containsKey(a))
				{
					player.getAttribute(a).setBaseValue(dms.getAttributes().get(a));
				}
			}
			player.setHealth(dms.getHealth());
			player.setAbsorptionAmount(dms.getAbsorptionAmount());
			player.setWalkSpeed(dms.getWalkSpeed());
			player.setFlySpeed(dms.getFlySpeed());
			player.setFireTicks(dms.getFireTicks());
			player.setFreezeTicks(dms.getFreezeTicks());
			player.setGlowing(dms.isGlowing());
			player.setGravity(dms.isGravity());
			player.setArrowsInBody(dms.getArrowsInBody());
			player.setMaximumAir(dms.getMaximumAir());
			player.setRemainingAir(dms.getRemainingAir());
		case EXP:
			player.setExp(dms.getExpTowardsNextLevel());
			player.setLevel(dms.getExpLevel());
			return;
		case INVENTORY:
			player.getInventory().setStorageContents(dms.getInventoryStorageContents());
			player.getInventory().setArmorContents(dms.getArmorContents());
			player.getInventory().setItemInOffHand(dms.getOffHand());
			return;
		case EFFECT:
			for(PotionEffect pe : dms.getActiveEffects())
			{
				player.addPotionEffect(pe);
			}
			return;
		case PERSITENTDATA:
			setPersitentData(player, dms.getPersistentData());
			return;
		}
	}
}
