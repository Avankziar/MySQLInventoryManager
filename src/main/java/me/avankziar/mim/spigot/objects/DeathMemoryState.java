package main.java.me.avankziar.mim.spigot.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityCategory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.database.MysqlHandable;
import main.java.me.avankziar.mim.spigot.database.MysqlHandler;
import main.java.me.avankziar.mim.spigot.gui.GUI.PersistentType;

public class DeathMemoryState implements MysqlHandable
{
	private long timeStamp;
	private int id;
	private String synchroKey; //Key to synchro on different server & worlds
	private GameMode gameMode; //Second "key"
	private UUID uuid;
	//Inventory
	private ItemStack[] inventoryContents; //getStorageContents() - Returns hotbar & normal slots
	//PlayerInventory
	private ItemStack[] armorContents; //getArmorContents()
	private ItemStack offHand; //getItemInOffHand
	//HumanEntity
	private int foodLevel; //getFoodLevel
	private float saturation; //getSaturation
	private int saturatedRegenRate; //getSaturatedRegenRate
	private int unsaturatedRegenRate; //getUnsaturatedRegenRate
	private int starvationRate; //getStarvationRate()
	private float exhaustion; //getExhaustion
	private LinkedHashMap<Attribute, Double> attributes; //getAttribute
	private double health; //getHealth || Only set if MaxHealth is setted!
	private double absorptionAmount; //getAbsorptionAmount
	private float expTowardsNextLevel; //Player#getExp
	private int expLevel; //getLevel
	private float walkSpeed; //getWalkSpeed
	private float flySpeed; //getFlySpeed
	private int fireTicks; //getFireTicks
	private int freezeTicks; //getFreezeTicks
	private boolean glowing; //isGlowing
	private boolean gravity; //hasGravity
	private ArrayList<PotionEffect> activeEffects; //getActivePotionEffects
	private EntityCategory entityCategory; //getCategory || Interessant für Schadensverz. BANN etc.
	//LivingEntity
	private int arrowsInBody; //getArrowsInBody
	private int maximumAir; //getMaximumAir
	private int remainingAir; //getRemainingAir
	//Nameable
	private String customName;
	//PersistentDataHolder
	private ArrayList<PersistentData> persistentData;	
	//NamespaceKey(Namespace, Key), PersistentType, value);
	
	public DeathMemoryState(){}
	
	public DeathMemoryState(int id, long timeStamp, String synchroKey, GameMode gameMode, UUID uuid,
			ItemStack[] inventoryContents, ItemStack[] armorContents, ItemStack offHand, 
			int foodLevel, float saturation, int saturatedRegenRate, int unsaturatedRegenRate, 
			int starvationRate, float exhaustion, LinkedHashMap<Attribute, Double> attributes, double health, 
			double absorptionAmount, float expTowardsNextLevel, int expLevel, float walkSpeed,
			float flySpeed, int fireTicks, int freezeTicks, boolean glowing, boolean gravity, ArrayList<PotionEffect> activeEffects,
			EntityCategory entityCategory, 
			int arrowsInBody, int maximumAir, int remainingAir,
			String customName,
			ArrayList<PersistentData> persistentData)
	{
		setId(id);
		setTimeStamp(timeStamp);
		setSynchroKey(synchroKey);
		setGameMode(gameMode);
		setUUID(uuid);
		setInventoryStorageContents(inventoryContents);
		setArmorContents(armorContents);
		setOffHand(offHand);
		setFoodLevel(foodLevel);
		setSaturation(saturation);
		setSaturatedRegenRate(unsaturatedRegenRate);
		setUnsaturatedRegenRate(unsaturatedRegenRate);
		setStarvationRate(starvationRate);
		setExhaustion(exhaustion);
		setAttributes(attributes);
		setHealth(health);
		setAbsorptionAmount(absorptionAmount);
		setExpTowardsNextLevel(expTowardsNextLevel);
		setExpLevel(expLevel);
		setWalkSpeed(walkSpeed);
		setFlySpeed(flySpeed);
		setFireTicks(fireTicks);
		setFreezeTicks(freezeTicks);
		setGlowing(glowing);
		setGravity(gravity);
		setActiveEffects(activeEffects);
		setEntityCategory(entityCategory);
		setArrowsInBody(arrowsInBody);
		setMaximumAir(maximumAir);
		setRemainingAir(remainingAir);
		setCustomName(customName);
		setPersistentData(persistentData);
	}
	
	public DeathMemoryState(int id, long timeStamp, String synchroKey, GameMode gameMode, UUID uuid,
			String inventoryContents, String armorContents, String offHand, 
			int foodLevel, float saturation, int saturatedRegenRate, int unsaturatedRegenRate, 
			int starvationRate, float exhaustion, String attributes, double health, 
			double absorptionAmount, float expTowardsNextLevel, int expLevel, float walkSpeed,
			float flySpeed, int fireTicks, int freezeTicks, boolean glowing, boolean gravity, String activeEffects,
			EntityCategory entityCategory, 
			int arrowsInBody, int maximumAir, int remainingAir,
			String customName,
			String persistentData)
	{
		setId(id);
		setTimeStamp(timeStamp);
		setSynchroKey(synchroKey);
		setGameMode(gameMode);
		setUUID(uuid);
		MIM plugin = MIM.getPlugin();
		ArrayList<ItemStack> invc = new ArrayList<>();
		for(Object o : plugin.getBase64Api().fromBase64Array(inventoryContents))
		{
			invc.add((ItemStack) o);
		}
		setInventoryStorageContents(invc.toArray(new ItemStack[invc.size()]));
		ArrayList<ItemStack> arc = new ArrayList<>();
		for(Object o : plugin.getBase64Api().fromBase64Array(armorContents))
		{
			arc.add((ItemStack) o);
		}
		setArmorContents(arc.toArray(new ItemStack[arc.size()]));
		setOffHand((ItemStack) plugin.getBase64Api().fromBase64(offHand));
		setFoodLevel(foodLevel);
		setSaturation(saturation);
		setSaturatedRegenRate(unsaturatedRegenRate);
		setUnsaturatedRegenRate(unsaturatedRegenRate);
		setStarvationRate(starvationRate);
		setExhaustion(exhaustion);
		LinkedHashMap<Attribute, Double> at = new LinkedHashMap<>();
		for(String s : attributes.split("@"))
		{
			String[] a = s.split(";");
			Attribute att = Attribute.valueOf(a[0]);
			Double d = Double.parseDouble(a[1]);
			at.put(att, d);
		}
		setAttributes(at);
		setHealth(health);
		setAbsorptionAmount(absorptionAmount);
		setExpTowardsNextLevel(expTowardsNextLevel);
		setExpLevel(expLevel);
		setWalkSpeed(walkSpeed);
		setFlySpeed(flySpeed);
		setFireTicks(fireTicks);
		setFreezeTicks(freezeTicks);
		setGlowing(glowing);
		setGravity(gravity);
		ArrayList<PotionEffect> pe = new ArrayList<>();
		for(Object o : plugin.getBase64Api().fromBase64Array(activeEffects))
		{
			pe.add((PotionEffect) o);
		}
		setActiveEffects(pe);
		setEntityCategory(entityCategory);
		setArrowsInBody(arrowsInBody);
		setMaximumAir(maximumAir);
		setRemainingAir(remainingAir);
		setCustomName(customName);
		ArrayList<PersistentData> pd = new ArrayList<>();
		for(String s : persistentData.split("@"))
		{
			String[] p = s.split(";");
			String namespaced = p[0];
			String key = p[1];
			PersistentType pt = PersistentType.valueOf(p[2]);
			String v = p[3];
			pd.add(new PersistentData(namespaced, key, pt, v));
		}
		setPersistentData(pd);
	}
	
	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getSynchroKey()
	{
		return synchroKey;
	}

	public void setSynchroKey(String synchroKey)
	{
		this.synchroKey = synchroKey;
	}

	public GameMode getGameMode()
	{
		return gameMode;
	}

	public void setGameMode(GameMode gameMode)
	{
		this.gameMode = gameMode;
	}

	public UUID getUUID()
	{
		return uuid;
	}

	public void setUUID(UUID uuid)
	{
		this.uuid = uuid;
	}

	public ItemStack[] getInventoryStorageContents()
	{
		return inventoryContents;
	}

	public void setInventoryStorageContents(ItemStack[] inventoryContents)
	{
		this.inventoryContents = inventoryContents;
	}

	public ItemStack[] getArmorContents()
	{
		return armorContents;
	}

	public void setArmorContents(ItemStack[] armorContents)
	{
		this.armorContents = armorContents;
	}

	public ItemStack getOffHand()
	{
		return offHand;
	}

	public void setOffHand(ItemStack offHand)
	{
		this.offHand = offHand;
	}

	public int getFoodLevel()
	{
		return foodLevel;
	}

	public void setFoodLevel(int foodLevel)
	{
		this.foodLevel = foodLevel;
	}

	public float getSaturation()
	{
		return saturation;
	}

	public void setSaturation(float saturation)
	{
		this.saturation = saturation;
	}

	public int getSaturatedRegenRate()
	{
		return saturatedRegenRate;
	}

	public void setSaturatedRegenRate(int saturatedRegenRate)
	{
		this.saturatedRegenRate = saturatedRegenRate;
	}

	public int getUnsaturatedRegenRate()
	{
		return unsaturatedRegenRate;
	}

	public void setUnsaturatedRegenRate(int unsaturatedRegenRate)
	{
		this.unsaturatedRegenRate = unsaturatedRegenRate;
	}

	public int getStarvationRate()
	{
		return starvationRate;
	}

	public void setStarvationRate(int starvationRate)
	{
		this.starvationRate = starvationRate;
	}

	public float getExhaustion()
	{
		return exhaustion;
	}

	public void setExhaustion(float exhaustion)
	{
		this.exhaustion = exhaustion;
	}

	public LinkedHashMap<Attribute, Double> getAttributes()
	{
		return attributes;
	}

	public void setAttributes(LinkedHashMap<Attribute, Double> attributes)
	{
		this.attributes = attributes;
	}

	public double getHealth()
	{
		return health;
	}

	public void setHealth(double health)
	{
		this.health = health;
	}

	public double getAbsorptionAmount()
	{
		return absorptionAmount;
	}

	public void setAbsorptionAmount(double absorptionAmount)
	{
		this.absorptionAmount = absorptionAmount;
	}

	public float getExpTowardsNextLevel()
	{
		return expTowardsNextLevel;
	}

	public void setExpTowardsNextLevel(float expTowardsNextLevel)
	{
		this.expTowardsNextLevel = expTowardsNextLevel;
	}

	public int getExpLevel()
	{
		return expLevel;
	}

	public void setExpLevel(int expLevel)
	{
		this.expLevel = expLevel;
	}

	public float getWalkSpeed()
	{
		return walkSpeed;
	}

	public void setWalkSpeed(float walkSpeed)
	{
		this.walkSpeed = walkSpeed;
	}

	public float getFlySpeed()
	{
		return flySpeed;
	}

	public void setFlySpeed(float flySpeed)
	{
		this.flySpeed = flySpeed;
	}

	public int getFireTicks()
	{
		return fireTicks;
	}

	public void setFireTicks(int fireTicks)
	{
		this.fireTicks = fireTicks;
	}

	public int getFreezeTicks()
	{
		return freezeTicks;
	}

	public void setFreezeTicks(int freezeTicks)
	{
		this.freezeTicks = freezeTicks;
	}

	public boolean isGlowing()
	{
		return glowing;
	}

	public void setGlowing(boolean glowing)
	{
		this.glowing = glowing;
	}

	public boolean isGravity()
	{
		return gravity;
	}

	public void setGravity(boolean gravity)
	{
		this.gravity = gravity;
	}

	public ArrayList<PotionEffect> getActiveEffects()
	{
		return activeEffects;
	}

	public void setActiveEffects(ArrayList<PotionEffect> activeEffects)
	{
		this.activeEffects = activeEffects;
	}

	public EntityCategory getEntityCategory()
	{
		return entityCategory;
	}

	public void setEntityCategory(EntityCategory entityCategory)
	{
		this.entityCategory = entityCategory;
	}

	public int getArrowsInBody()
	{
		return arrowsInBody;
	}

	public void setArrowsInBody(int arrowsInBody)
	{
		this.arrowsInBody = arrowsInBody;
	}

	public int getMaximumAir()
	{
		return maximumAir;
	}

	public void setMaximumAir(int maximumAir)
	{
		this.maximumAir = maximumAir;
	}

	public int getRemainingAir()
	{
		return remainingAir;
	}

	public void setRemainingAir(int remainingAir)
	{
		this.remainingAir = remainingAir;
	}

	public String getCustomName()
	{
		return customName;
	}

	public void setCustomName(String customName)
	{
		this.customName = customName;
	}

	public ArrayList<PersistentData> getPersistentData()
	{
		return persistentData;
	}

	public void setPersistentData(ArrayList<PersistentData> persistentData)
	{
		this.persistentData = persistentData;
	}

	public long getTimeStamp()
	{
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp)
	{
		this.timeStamp = timeStamp;
	}
	
	@Override
	public boolean create(Connection conn, String tablename)
	{
		try
		{
			String sql = "INSERT INTO `" + tablename
					+ "`(`player_uuid`,"
					+ " `synchro_key`, `game_mode`,"
					+ " `inventory_content`, `armor_content`, `off_hand`, `enderchest_content`,"
					+ " `food_level`, `saturation`, `saturated_regen_rate`, `unsaturated_regen_rate`,"
					+ " `starvation_rate`, `exhaustion`, `attributes`, `health`, `absorption_amount`,"
					+ " `exp_towards_next_level`, `exp_level`, `total_experience`,"
					+ " `walk_speed`, `fly_speed`, `fire_ticks`, `freeze_ticks`, `glowing`, `gravity`,"
					+ " `potion_effects`, `entity_category`, `arrows_in_body`, `maximum_air`, `remaining_air`, `custom_name`,"
					+ " `persistent_data`, `time_stamp`) " 
					+ "VALUES(?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
	        ps.setString(1, getUUID().toString());
	        ps.setString(3, getSynchroKey());
	        ps.setString(4, getGameMode().toString());
	        ps.setString(5, MIM.getPlugin().getBase64Api().toBase64Array(getInventoryStorageContents()));
	        ps.setString(6, MIM.getPlugin().getBase64Api().toBase64Array(getArmorContents()));
	        ps.setString(7, MIM.getPlugin().getBase64Api().toBase64(getOffHand()));
	        ps.setInt(9, getFoodLevel());
	        ps.setFloat(10, getSaturation());
	        ps.setInt(11, getSaturatedRegenRate());
	        ps.setInt(12, getUnsaturatedRegenRate());
	        ps.setInt(13, getStarvationRate());
	        ps.setFloat(14, getExhaustion());
	        StringBuilder at = new StringBuilder();
	        for(Entry<Attribute, Double> e : getAttributes().entrySet())
	        {
	        	at.append(e.getKey().toString()+";"+e.getValue().doubleValue()+"@");
	        }
	        ps.setString(15, at.toString());
	        ps.setDouble(16, getHealth());
	        ps.setDouble(17, getAbsorptionAmount());
	        ps.setFloat(18, getExpTowardsNextLevel());
	        ps.setInt(19, getExpLevel());
	        ps.setFloat(21, getWalkSpeed());
	        ps.setFloat(22, getFlySpeed());
	        ps.setInt(23, getFireTicks());
	        ps.setInt(24, getFreezeTicks());
	        ps.setBoolean(25, isGlowing());
	        ps.setBoolean(26, isGravity());
	        ps.setString(27, MIM.getPlugin().getBase64Api().toBase64Array(getActiveEffects().toArray(new PotionEffect[getActiveEffects().size()])));
	        ps.setString(28, getEntityCategory().toString());
	        ps.setInt(29, getArrowsInBody());
	        ps.setInt(30, getMaximumAir());
	        ps.setInt(31, getRemainingAir());
	        StringBuilder pd = new StringBuilder();
	        for(PersistentData per : getPersistentData())
	        {
	        	pd.append(per.getNamespaced()+";"+per.getKey()+";"+per.getPersistentType().toString()+";"+per.getPersistentValue()+"@");
	        }
	        ps.setString(32, pd.toString());
	        ps.setLong(34, getTimeStamp());
	        
	        int i = ps.executeUpdate();
	        MysqlHandler.addRows(MysqlHandler.QueryType.INSERT, i);
	        return true;
		} catch (SQLException e)
		{
			this.log(Level.WARNING, "SQLException! Could not create a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return false;
	}

	@Override
	public boolean update(Connection conn, String tablename, String whereColumn, Object... whereObject)
	{
		try 
		{
			String sql = "UPDATE `" + tablename
				+ "` SET `player_uuid` = ?,"
				+ " `synchro_key` = ?, `game_mode` = ?,"
				+ " `inventory_content` = ?, `armor_content` = ?, `off_hand` = ?,"
				+ " `food_level` = ?, `saturation` = ?, `saturated_regen_rate` = ?, `unsaturated_regen_rate` = ?,"
				+ " `starvation_rate` = ?, `exhaustion` = ?, `attributes` = ?, `health` = ?, `absorption_amount` = ?,"
				+ " `exp_towards_next_level` = ?, `exp_level` = ?,"
				+ " `walk_speed` = ?, `fly_speed` = ?, `fire_ticks` = ?, `freeze_ticks` = ?, `glowing` = ?, `gravity` = ?,"
				+ " `potion_effects` = ?, `entity_category` = ?, `arrows_in_body` = ?, `maximum_air` = ?, `remaining_air` = ?, `custom_name` = ?,"
				+ " `persistent_data` = ?, `time_stamp` = ?" 
				+ " WHERE "+whereColumn;
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, getUUID().toString());
	        ps.setString(3, getSynchroKey());
	        ps.setString(4, getGameMode().toString());
	        ps.setString(5, MIM.getPlugin().getBase64Api().toBase64Array(getInventoryStorageContents()));
	        ps.setString(6, MIM.getPlugin().getBase64Api().toBase64Array(getArmorContents()));
	        ps.setString(7, MIM.getPlugin().getBase64Api().toBase64(getOffHand()));
	        ps.setInt(9, getFoodLevel());
	        ps.setFloat(10, getSaturation());
	        ps.setInt(11, getSaturatedRegenRate());
	        ps.setInt(12, getUnsaturatedRegenRate());
	        ps.setInt(13, getStarvationRate());
	        ps.setFloat(14, getExhaustion());
	        StringBuilder at = new StringBuilder();
	        for(Entry<Attribute, Double> e : getAttributes().entrySet())
	        {
	        	at.append(e.getKey().toString()+";"+e.getValue().doubleValue()+"@");
	        }
	        ps.setString(15, at.toString());
	        ps.setDouble(16, getHealth());
	        ps.setDouble(17, getAbsorptionAmount());
	        ps.setFloat(18, getExpTowardsNextLevel());
	        ps.setInt(19, getExpLevel());
	        ps.setFloat(21, getWalkSpeed());
	        ps.setFloat(22, getFlySpeed());
	        ps.setInt(23, getFireTicks());
	        ps.setInt(24, getFreezeTicks());
	        ps.setBoolean(25, isGlowing());
	        ps.setBoolean(26, isGravity());
	        ps.setString(27, MIM.getPlugin().getBase64Api().toBase64Array(getActiveEffects().toArray(new PotionEffect[getActiveEffects().size()])));
	        ps.setString(28, getEntityCategory().toString());
	        ps.setInt(29, getArrowsInBody());
	        ps.setInt(30, getMaximumAir());
	        ps.setInt(31, getRemainingAir());
	        StringBuilder pd = new StringBuilder();
	        for(PersistentData per : getPersistentData())
	        {
	        	pd.append(per.getNamespaced()+";"+per.getKey()+";"+per.getPersistentType().toString()+";"+per.getPersistentValue()+"@");
	        }
	        ps.setString(32, pd.toString());
	        ps.setLong(34, getTimeStamp());
			int i = 35;
			for(Object o : whereObject)
			{
				ps.setObject(i, o);
				i++;
			}			
			int u = ps.executeUpdate();
			MysqlHandler.addRows(MysqlHandler.QueryType.UPDATE, u);
			return true;
		} catch (SQLException e)
		{
			this.log(Level.WARNING, "SQLException! Could not update a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return false;
	}

	@Override
	public ArrayList<Object> get(Connection conn, String tablename, String orderby, String limit, String whereColumn, Object... whereObject)
	{
		try
		{
			String sql = "SELECT * FROM `" + tablename
				+ "` WHERE "+whereColumn+" ORDER BY "+orderby+limit;
			PreparedStatement ps = conn.prepareStatement(sql);
			int i = 1;
			for(Object o : whereObject)
			{
				ps.setObject(i, o);
				i++;
			}
			
			ResultSet rs = ps.executeQuery();
			MysqlHandler.addRows(MysqlHandler.QueryType.READ, rs.getMetaData().getColumnCount());
			ArrayList<Object> al = new ArrayList<>();
			while (rs.next()) 
			{
				al.add(new DeathMemoryState(
						rs.getInt("id"),
						rs.getLong("time_stamp"),
						rs.getString("synchro_key"),
						GameMode.valueOf(rs.getString("game_mode")),
						UUID.fromString(rs.getString("player_uuid")),
						rs.getString("inventory_content"),
						rs.getString("armor_content"),
						rs.getString("off_hand"),
						rs.getInt("food_level"),
						rs.getFloat("saturation"),
						rs.getInt("saturated_regen_rate"),
						rs.getInt("unsaturated_regen_rate"),
						rs.getInt("starvation_rate"),
						rs.getFloat("exhaustion"),
						rs.getString("attributes"),
						rs.getDouble("health"),
						rs.getDouble("absorption_amount"),
						rs.getFloat("exp_towards_next_level"),
						rs.getInt("exp_level"),
						rs.getFloat("walk_speed"),
						rs.getFloat("fly_speed"),
						rs.getInt("fire_ticks"),
						rs.getInt("freeze_ticks"),
						rs.getBoolean("glowing"),
						rs.getBoolean("gravity"),
						rs.getString("potion_effects"),
						EntityCategory.valueOf(rs.getString("entity_category")),
						rs.getInt("arrows_in_body"),
						rs.getInt("maximum_air"),
						rs.getInt("remaining_air"),
						rs.getString("custom_name"),
						rs.getString("persistent_data")
						));
			}
			return al;
		} catch (SQLException e)
		{
			this.log(Level.WARNING, "SQLException! Could not get a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return new ArrayList<>();
	}
	
	public static ArrayList<DeathMemoryState> convert(ArrayList<Object> arrayList)
	{
		ArrayList<DeathMemoryState> l = new ArrayList<>();
		for(Object o : arrayList)
		{
			if(o instanceof DeathMemoryState)
			{
				l.add((DeathMemoryState) o);
			}
		}
		return l;
	}
}