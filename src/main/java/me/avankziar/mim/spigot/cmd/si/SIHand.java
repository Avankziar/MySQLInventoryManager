package main.java.me.avankziar.mim.spigot.cmd.si;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import main.java.me.avankziar.ifh.general.assistance.ChatApi;
import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.assistance.Utility;
import main.java.me.avankziar.mim.spigot.cmdtree.ArgumentConstructor;
import main.java.me.avankziar.mim.spigot.cmdtree.ArgumentModule;
import main.java.me.avankziar.mim.spigot.cmdtree.BaseConstructor;
import main.java.me.avankziar.mim.spigot.database.MysqlHandler;
import main.java.me.avankziar.mim.spigot.handler.ConfigHandler;
import main.java.me.avankziar.mim.spigot.objects.WaitingItems;

public class SIHand extends ArgumentModule
{
	private MIM plugin;
	
	public SIHand(ArgumentConstructor ac)
	{
		super(ac);
		this.plugin = BaseConstructor.getPlugin();
	}
	
	@Override
	public void run(CommandSender sender, String[] args) throws IOException
	{
		sendItem(plugin, sender, args, "hand");
	}
	
	public static void sendItem(MIM plugin, CommandSender sender, String[] args, String type)
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage("Cmd only for Players!");
			return;
		}
		Player player = (Player) sender;
		final String othername = args[1];
		UUID uuid = Utility.convertNameToUUID(othername);
		if(uuid == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("PlayerNotExist")));
			return;
		}
		if(uuid.equals(player.getUniqueId()))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("SendItem.CannotSendItemsToYourself")));
			return;
		}
		StringBuilder sb = new StringBuilder();
		for(int i = 2; i < args.length; i++)
		{
			sb.append(args[i]);
			if(i+1 < args.length)
			{
				sb.append(" ");
			}
		}
		String synchroKey = new ConfigHandler(plugin).getSynchroKey(player);
		ItemStack[] iar = null;
		ArrayList<ItemStack> list = new ArrayList<>();
		switch(type)
		{
		default:
		case "hand":
			final ItemStack is = player.getInventory().getItemInMainHand().clone();
			if(is == null || is.getType() == Material.AIR)
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("SendItem.NoItemInHand")));
				return;
			}
			player.getInventory().getItemInMainHand().setAmount(0);
			list.add(is);
			break;
		case "material":
			final ItemStack matis = player.getInventory().getItemInMainHand().clone();
			if(matis == null || matis.getType() == Material.AIR)
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("SendItem.NoItemInHand")));
				return;
			}
			Material mat = matis.getType();
			final ItemStack[] psc = player.getInventory().getStorageContents();
			player.getInventory().remove(mat);
			for(ItemStack i : psc)
			{
				if(i != null && mat == i.getType())
				{
					list.add(i);
				}
			}
			break;
		case "inv":
			final ItemStack[] psci = player.getInventory().getStorageContents();
			final ItemStack[] armor = player.getInventory().getArmorContents();
			final ItemStack offhand = player.getInventory().getItemInOffHand();
			player.getInventory().clear();
			player.getInventory().setArmorContents(armor);
			player.getInventory().setItemInOffHand(offhand);
			for(ItemStack i : psci)
			{
				list.add(i);
			}
			break;
		}
		iar = list.toArray(new ItemStack[list.size()]);
		WaitingItems wi = new WaitingItems(0, synchroKey, uuid, player.getName(), sb.toString(), System.currentTimeMillis(), iar);
		plugin.getMysqlHandler().create(MysqlHandler.Type.WAITINGITEMS, wi);
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("SendItem.Send")
				.replace("%player%", othername)
				.replace("%reason%", sb.toString())));
	}
}