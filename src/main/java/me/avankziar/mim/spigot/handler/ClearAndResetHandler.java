package main.java.me.avankziar.mim.spigot.handler;

import java.util.ArrayList;
import java.util.EnumSet;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import main.java.me.avankziar.mim.spigot.objects.SyncType;

public class ClearAndResetHandler
{
	private static ArrayList<Attribute> attributeList = new ArrayList<>();
	static
	{
		for(Attribute a : new ArrayList<Attribute>(EnumSet.allOf(Attribute.class)))
		{
			if(a != Attribute.HORSE_JUMP_STRENGTH && a != Attribute.ZOMBIE_SPAWN_REINFORCEMENTS
					&& a != Attribute.GENERIC_FOLLOW_RANGE)
			{
				attributeList.add(a);
			}
		}
	}
	
	public static void clearAndReset(SyncType syncType, Player player)
	{
		switch(syncType)
		{
		case FULL:
			player.getInventory().clear();
			//player.getInventory().setArmorContents(pd.getArmorContents());
			//player.getInventory().setItemInOffHand(pd.getOffHand());
			player.getEnderChest().clear();
			player.setFoodLevel(20);
			player.setSaturation(1);
			player.setSaturatedRegenRate(10);
			player.setUnsaturatedRegenRate(80);
			player.setStarvationRate(80);
			player.setExhaustion(0);
			for(Attribute a : attributeList)
			{
				player.getAttribute(a).setBaseValue(player.getAttribute(a).getDefaultValue());
				break;
			}
			player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
			player.setAbsorptionAmount(0);
			player.setExp(0);
			player.setLevel(0);
			player.setWalkSpeed(1);
			player.setFlySpeed(1);
			player.setFireTicks(0);
			player.setFreezeTicks(0);
			player.setGlowing(false);
			player.setGravity(true);
			for(PotionEffect pe : player.getActivePotionEffects())
			{
				player.removePotionEffect(pe.getType());
			}
			player.setArrowsInBody(0);
			player.setMaximumAir(40);
			player.setRemainingAir(40);
			for(NamespacedKey nsk : player.getPersistentDataContainer().getKeys())
			{
				player.getPersistentDataContainer().remove(nsk);
			}
			player.updateInventory();
			return;
		case ATTRIBUTE:
			player.setFoodLevel(20);
			player.setSaturation(1);
			player.setSaturatedRegenRate(10);
			player.setUnsaturatedRegenRate(80);
			player.setStarvationRate(80);
			player.setExhaustion(0);
			for(Attribute a : attributeList)
			{
				player.getAttribute(a).setBaseValue(player.getAttribute(a).getDefaultValue());
				break;
			}
			player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
			player.setAbsorptionAmount(0);
			player.setWalkSpeed(1);
			player.setFlySpeed(1);
			player.setFireTicks(0);
			player.setFreezeTicks(0);
			player.setGlowing(false);
			player.setGravity(true);
			player.setArrowsInBody(0);
			player.setMaximumAir(40);
			player.setRemainingAir(40);
		case EXP:
			player.setExp(0);
			player.setLevel(0);
			return;
		case INVENTORY:
			player.getInventory().clear();
			//player.getInventory().setArmorContents(pd.getArmorContents());
			//player.getInventory().setItemInOffHand(pd.getOffHand());
			player.getEnderChest().clear();
			player.updateInventory();
			return;
		case INV_ONLY:
			final ItemStack[] armor = player.getInventory().getArmorContents();
			final ItemStack offhand = player.getInventory().getItemInOffHand();
			player.getInventory().clear();
			player.getInventory().setArmorContents(armor);
			player.getInventory().setItemInOffHand(offhand);
			player.updateInventory();
			return;
		case INV_OFFHAND:
			player.getInventory().setItemInOffHand(null);
			player.updateInventory();
			return;
		case INV_ARMOR:
			player.getInventory().setArmorContents(null);
			player.updateInventory();
			return;
		case INV_ENDERCHEST:
			player.getEnderChest().clear();
			return;
		case EFFECT:
			for(PotionEffect pe : player.getActivePotionEffects())
			{
				player.removePotionEffect(pe.getType());
			}
			return;
		case PERSITENTDATA:
			for(NamespacedKey nsk : player.getPersistentDataContainer().getKeys())
			{
				player.getPersistentDataContainer().remove(nsk);
			}
			return;
		}
	}
}
