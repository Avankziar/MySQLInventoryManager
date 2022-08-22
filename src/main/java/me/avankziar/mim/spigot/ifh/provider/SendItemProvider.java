package main.java.me.avankziar.mim.spigot.ifh.provider;

import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import main.java.me.avankziar.ifh.spigot.synchronization.SendItem;
import main.java.me.avankziar.mim.spigot.MIM;

public class SendItemProvider implements SendItem
{
	private MIM plugin;
	private static String d = "default";
	private static String n = "/";
	
	public SendItemProvider(MIM plugin2)
	{
		this.plugin = plugin2;
	}
	
	@Override
	public void sendItem(UUID uuid, String sender, ItemStack...itemStack)
	{
		sendItem(uuid, sender, d, n, itemStack);
	}
	
	@Override
	public void sendItem(UUID uuid, String sender, String synchroKey, ItemStack...itemStack)
	{
		sendItem(uuid, sender, synchroKey, n, itemStack);
	}
	
	@Override
	public void sendItem(UUID uuid, String sender, String synchroKey, String reason, ItemStack...itemStack)
	{
		plugin.getSendItemAPI().sendItem(uuid, sender, synchroKey, reason, itemStack);
	}
	
	@Override
	public void sendItem(UUID[] uuid, String sender, ItemStack...itemStack)
	{
		sendItem(uuid, sender, d, n, itemStack);
	}
	
	@Override
	public void sendItem(UUID[] uuid, String sender, String synchroKey, ItemStack...itemStack)
	{
		sendItem(uuid, sender, synchroKey, n, itemStack);
	}
	
	@Override
	public void sendItem(UUID[] uuid, String sender, String synchroKey, String reason, ItemStack...itemStack)
	{
		plugin.getSendItemAPI().sendItem(uuid, synchroKey, itemStack);
	}
	
	@Override
	public void sendItem(String sender, ItemStack...itemStack)
	{
		sendItem(sender, d, n, itemStack);
	}
	
	@Override
	public void sendItem(String sender, String synchroKey, ItemStack...itemStack)
	{
		sendItem(synchroKey, n, itemStack);
	}
	
	@Override
	public void sendItem(String sender, String synchroKey, String reason, ItemStack...itemStack)
	{
		plugin.getSendItemAPI().sendItem(sender, synchroKey, reason, itemStack);
	}
}