package main.java.me.avankziar.mim.spigot.cmd.wi;

import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.me.avankziar.ifh.general.assistance.ChatApi;
import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.assistance.MatchApi;
import main.java.me.avankziar.mim.spigot.assistance.TimeHandler;
import main.java.me.avankziar.mim.spigot.cmdtree.ArgumentConstructor;
import main.java.me.avankziar.mim.spigot.cmdtree.ArgumentModule;
import main.java.me.avankziar.mim.spigot.cmdtree.BaseConstructor;
import main.java.me.avankziar.mim.spigot.cmdtree.CommandExecuteType;
import main.java.me.avankziar.mim.spigot.cmdtree.CommandSuggest;
import main.java.me.avankziar.mim.spigot.database.MysqlHandler;
import main.java.me.avankziar.mim.spigot.handler.ConfigHandler;
import main.java.me.avankziar.mim.spigot.objects.WaitingItems;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class WIList extends ArgumentModule
{
	private MIM plugin;
	
	public WIList(ArgumentConstructor ac)
	{
		super(ac);
		this.plugin = BaseConstructor.getPlugin();
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
		int p = 0;
		if(args.length == 2 && MatchApi.isInteger(args[1]))
		{
			p = Integer.parseInt(args[1]);
		}
		int page = p;
		new BukkitRunnable()
		{
			
			@Override
			public void run()
			{
				waitingItemsList(plugin, player, page);
			}
		}.runTaskAsynchronously(plugin);
	}
	
	public static void waitingItemsList(MIM plugin, Player player, int page)
	{
		String synchroKey = new ConfigHandler(plugin).getSynchroKey(player);
		int count = plugin.getMysqlHandler().getCount(MysqlHandler.Type.WAITINGITEMS,
				"`player_uuid` = ? AND `synchro_key` = ?", player.getUniqueId().toString(), synchroKey);
		if(count <= 0)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("WaitingItems.NoItemsWaiting")));
			return;
		}
		ArrayList<WaitingItems> list = plugin.getSendItemAPI().getWaitingItems(player.getUniqueId(), synchroKey, page);
		ArrayList<ArrayList<BaseComponent>> bc = new ArrayList<>();
		ArrayList<BaseComponent> bc1 = new ArrayList<>();
		bc1.add(ChatApi.tctl(plugin.getYamlHandler().getLang().getString("WaitingItems.Headline")
				.replace("%amount%", String.valueOf(count))
				.replace("%page%", String.valueOf(page))));
		bc.add(bc1);
		for(WaitingItems wi : list)
		{
			ArrayList<BaseComponent> bc2 = new ArrayList<>();
			bc2.add(ChatApi.tctl(plugin.getYamlHandler().getLang().getString("WaitingItems.LineStart")
					.replace("%time%", TimeHandler.getDateTime(wi.getSendTime()))
					.replace("%sender%", wi.getSender())
					.replace("%reason%", wi.getReason())));
			bc2.add(ChatApi.apiChat(plugin.getYamlHandler().getLang().getString("WaitingItems.LineEnd"),
					ClickEvent.Action.RUN_COMMAND,
					CommandSuggest.get(CommandExecuteType.WAITINGITEMS_ACCEPT)+" "+wi.getId(),
					HoverEvent.Action.SHOW_TEXT, plugin.getYamlHandler().getLang().getString("WaitingItems.Hover")));
			bc.add(bc2);
		}
		for(ArrayList<BaseComponent> bcl : bc)
		{
			TextComponent tx = ChatApi.tc("");
			tx.setExtra(bcl);
			player.spigot().sendMessage(tx);
		}
	}
}