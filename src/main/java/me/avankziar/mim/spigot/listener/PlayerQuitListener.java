package main.java.me.avankziar.mim.spigot.listener;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.database.MysqlHandler;
import main.java.me.avankziar.mim.spigot.handler.ClearAndResetHandler;
import main.java.me.avankziar.mim.spigot.handler.PlayerDataHandler;
import main.java.me.avankziar.mim.spigot.objects.PlayerData;
import main.java.me.avankziar.mim.spigot.objects.SyncType;

public class PlayerQuitListener extends BaseListener
{
	public PlayerQuitListener(MIM plugin)
	{
		super(plugin, BaseListener.Type.PLAYER_QUIT);
	}
	
	@EventHandler
	public void onPlayerQuit(final PlayerQuitEvent event)
	{
		final Player player = event.getPlayer();
		final PlayerData pd = getPlayerData(player);
		final World world = event.getPlayer().getWorld();
		/*if(!plugin.getConfigHandler().isEventEnabled(this.bType.getName(), world))
		{
			return;
		}*/
		if(plugin.getConfigHandler().isClearAndResetByQuit(world))
		{
			ClearAndResetHandler.clearAndReset(SyncType.FULL, player);
			return;
		}
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.PLAYERDATA,
				"`player_uuid` = ? AND `synchro_key` = ? AND `game_mode` = ?",
				pd.getUUID().toString(), pd.getSynchroKey(), pd.getGameMode().toString()))
		{
			plugin.getMysqlHandler().create(MysqlHandler.Type.PLAYERDATA, pd);
		} else
		{
			plugin.getMysqlHandler().updateData(MysqlHandler.Type.PLAYERDATA, pd,
					"`player_uuid` = ? AND `synchro_key` = ? AND `game_mode` = ?",
					pd.getUUID().toString(), pd.getSynchroKey(), pd.getGameMode().toString());
		}
	}
	
	private PlayerData getPlayerData(Player player)
	{
		String synchroKey = MIM.getPlugin().getConfigHandler().getSynchroKey(player, false);
		final GameMode gm = player.getGameMode();
		PlayerData pd = new PlayerData();
		LinkedHashMap<Attribute, Double> attributes = new LinkedHashMap<>();
		for(Attribute at : PlayerDataHandler.attributeList)
		{
			if(player.getAttribute(at) != null)
			{
				attributes.put(at, player.getAttribute(at).getBaseValue());
			}
		}
		ArrayList<PotionEffect> pe = new ArrayList<>();
		for(PotionEffect eff : player.getActivePotionEffects())
		{
			pe.add(eff);
		}
		pd.setId(0);
		pd.setUUID(player.getUniqueId());
		pd.setName(player.getName());
		pd.setSynchroKey(synchroKey);
		pd.setGameMode(gm);
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
		pd.setFlying(player.getAllowFlight());
		pd.setGlowing(player.isGlowing());
		pd.setGravity(player.hasGravity());
		pd.setInvisible(player.isInvisible());
		pd.setInvulnerable(MIM.getPlugin().getYamlHandler().getConfig().getBoolean("SaveOption.Invulnerable", false) ? player.isInvulnerable() : false);
		pd.setActiveEffects(pe);
		pd.setEntityCategory(player.getCategory());
		pd.setArrowsInBody(player.getArrowsInBody());
		pd.setMaximumAir(player.getMaximumAir());
		pd.setRemainingAir(player.getRemainingAir());
		pd.setCustomName(player.getCustomName());
		pd.setPersistentData(PlayerDataHandler.getPersitentData(player));
		return pd;
	}
}