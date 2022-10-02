package main.java.me.avankziar.mim.spigot.cmd;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.mim.general.ChatApi;
import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.assistance.TimeHandler;
import main.java.me.avankziar.mim.spigot.assistance.Utility;
import main.java.me.avankziar.mim.spigot.cmdtree.CommandConstructor;
import main.java.me.avankziar.mim.spigot.database.MysqlHandler;
import main.java.me.avankziar.mim.spigot.objects.DeathMemoryState;
import main.java.me.avankziar.mim.spigot.permission.Bypass;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class InvDeathListCmdExecutor implements CommandExecutor
{
	private MIM plugin;
	private static CommandConstructor cc;
	private static String deathloadcmd;
	
	public InvDeathListCmdExecutor(MIM plugin, CommandConstructor cc, String deathloadcmd)
	{
		this.plugin = plugin;
		InvDeathListCmdExecutor.cc = cc;
		InvDeathListCmdExecutor.deathloadcmd = deathloadcmd;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) 
	{
		if(cc == null)
		{
			return false;
		}
		if(!(sender instanceof Player))
		{
			sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CommandWorkOnlyForPlayer")));
			return false;
		}
		Player player = (Player) sender;
		if(!player.hasPermission(cc.getPermission()))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("NoPermission")));
			return false;
		}
		UUID otheruuid = null;
		Player other = null;
		if(args.length == 0)
		{
			otheruuid = player.getUniqueId();
			other = player;
		} else if(args.length == 1)
		{
			if(!sender.hasPermission(Bypass.get(Bypass.Permission.INV_DEATHLIST_OTHER)))
			{
				sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("NoPermission")));
				return false;
			}
			String othername = args[0];
			otheruuid = Utility.convertNameToUUID(othername);
			if(otheruuid == null)
			{
				sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("PlayerNotExist")));
				return false;
			}
			other = Bukkit.getPlayer(otheruuid);
			if(other == null)
			{
				sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("PlayerNotOnline")));
				return false;
			}
		} else
		{
			sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("DidYouMean")));
			sender.sendMessage(ChatApi.tl(cc.getSuggestion()));
			return false;
		}
		String synchrokey = MIM.getPlugin().getConfigHandler().getSynchroKey(player);
		int amount = plugin.getMysqlHandler().getCount(MysqlHandler.Type.DEATHMEMORYSTATE,
				"`player_uuid` = ? AND `synchro_key` = ?", otheruuid.toString(), synchrokey);
		ArrayList<DeathMemoryState> list = DeathMemoryState.convert(plugin.getMysqlHandler().getFullList(MysqlHandler.Type.DEATHMEMORYSTATE,
				"`time_stamp` DESC", "`player_uuid` = ? AND `synchro_key` = ?", otheruuid.toString(), synchrokey));
		ArrayList<ArrayList<BaseComponent>> bc = new ArrayList<>();
		ArrayList<BaseComponent> bc1 = new ArrayList<>();
		bc1.add(ChatApi.tctl(plugin.getYamlHandler().getLang().getString("DeathMemoryState.ListHeadline")
				.replace("%player%", player.getName())
				.replace("%amount%", String.valueOf(amount))));
		bc.add(bc1);
		for(DeathMemoryState dms : list)
		{
			ArrayList<BaseComponent> bc2 = new ArrayList<>();
			bc2.add(ChatApi.tctl(plugin.getYamlHandler().getLang().getString("DeathMemoryState.ListLine")
					.replace("%time%", TimeHandler.getDateTime(dms.getTimeStamp()))));
			bc2.add(ChatApi.apiChat(plugin.getYamlHandler().getLang().getString("DeathMemoryState.ListLineEnd"),
					ClickEvent.Action.SUGGEST_COMMAND, deathloadcmd+other.getName()+" "+dms.getId(),
					HoverEvent.Action.SHOW_TEXT, plugin.getYamlHandler().getLang().getString("DeathMemoryState.ListLineEndHover")));
			bc.add(bc2);
		}
		for(ArrayList<BaseComponent> bcar : bc)
		{
			TextComponent tc = ChatApi.tc("");
			tc.setExtra(bcar);
			player.spigot().sendMessage(tc);
		}
		return true;
	}
}