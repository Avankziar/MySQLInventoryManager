package main.java.me.avankziar.mim.spigot.cmd.wi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import main.java.me.avankziar.ifh.general.assistance.ChatApi;
import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.assistance.MatchApi;
import main.java.me.avankziar.mim.spigot.cmdtree.ArgumentConstructor;
import main.java.me.avankziar.mim.spigot.cmdtree.ArgumentModule;
import main.java.me.avankziar.mim.spigot.cmdtree.BaseConstructor;
import main.java.me.avankziar.mim.spigot.database.MysqlHandler;
import main.java.me.avankziar.mim.spigot.objects.WaitingItems;

public class WIAccept extends ArgumentModule
{
	private MIM plugin;
	private ArgumentConstructor ac;
	
	public WIAccept(ArgumentConstructor ac)
	{
		super(ac);
		this.plugin = BaseConstructor.getPlugin();
		this.ac = ac;
	}
	
	@Override
	public void run(CommandSender sender, String[] args) throws IOException
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage("Cmd only for Players!");
			return;
		}
		Player player = (Player) sender;
		if(!MatchApi.isInteger(args[1]))
		{
			sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("DidYouMean")));
			sender.sendMessage(ChatApi.tl(ac.getSuggestion()));
			return;
		}
		final int id = Integer.parseInt(args[1]);
		WaitingItems wi = (WaitingItems) plugin.getMysqlHandler().getData(MysqlHandler.Type.WAITINGITEMS,
				"`id` = ?", id);
		if(wi == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("WaitingItems.DontExist")));
			return;
		}
		if(!wi.getReceiver().equals(player.getUniqueId()))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("WaitingItems.NotYours")));
			return;
		}
		HashMap<Integer, ItemStack> map = player.getInventory().addItem(wi.getItems());
		if(map == null || map.isEmpty())
		{
			plugin.getMysqlHandler().deleteData(MysqlHandler.Type.WAITINGITEMS, "`id` = ?", id);
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("WaitingItems.IsDistributed")));
		} else
		{
			ArrayList<ItemStack> list = new ArrayList<>();
			list.addAll(map.values());
			ItemStack[] arr = list.toArray(new ItemStack[list.size()]);
			wi.setItems(arr);
			plugin.getMysqlHandler().updateData(MysqlHandler.Type.WAITINGITEMS, wi, "`id` = ?", id);
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("WaitingItems.IsHalfDistributed")));
		}		
	}
}