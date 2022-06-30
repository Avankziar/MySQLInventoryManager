package main.java.me.avankziar.mim.spigot.cmd;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import main.java.me.avankziar.mim.general.ChatApi;
import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.assistance.Utility;
import main.java.me.avankziar.mim.spigot.cmdtree.CommandConstructor;
import main.java.me.avankziar.mim.spigot.database.MysqlHandler;
import main.java.me.avankziar.mim.spigot.listener.InventoryCloseListener;
import main.java.me.avankziar.mim.spigot.objects.PlayerData;
import main.java.me.avankziar.mim.spigot.permission.Bypass;

public class InventorySeeCmdExecutor implements CommandExecutor
{
	private MIM plugin;
	private static CommandConstructor cc;
	
	public InventorySeeCmdExecutor(MIM plugin, CommandConstructor cc)
	{
		this.plugin = plugin;
		InventorySeeCmdExecutor.cc = cc;
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
		if(args.length == 1)
		{
			if(!sender.hasPermission(Bypass.get(Bypass.Permission.INVENTORYSEE_OTHERPLAYER)))
			{
				sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("NoPermission")));
				return false;
			}
			String synchroKey = MIM.getPlugin().getConfigHandler().getSynchroKey(player, false);
			String othername = args[0];
			UUID otheruuid = Utility.convertNameToUUID(othername);
			if(otheruuid == null)
			{
				sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("PlayerNotExist")));
				return false;
			}
			Player other = Bukkit.getPlayer(otheruuid);
			Inventory inv = other != null ? other.getInventory() : null;
			if(inv == null)
			{
				PlayerData pd = (PlayerData) MIM.getPlugin().getMysqlHandler().getData(MysqlHandler.Type.PLAYERDATA,
						"`player_uuid` = ? AND `synchro_key` = ? AND `game_mode` = ?",
						otheruuid.toString(), synchroKey, player.getGameMode().toString());
				if(pd == null)
				{
					sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("PlayerNotExist")));
					return false;
				}
				inv = Bukkit.createInventory(player, 9*4, othername+"`s Inventory");
				inv.setContents(pd.getInventoryStorageContents());
			} else
			{
				inv = Bukkit.createInventory(player, 9*4, othername+"`s Inventory");
				inv.setContents(other.getInventory().getStorageContents());
			}
			InventoryCloseListener.addToExternInventory(player.getUniqueId(), otheruuid, inv,
					"INV", "INV", player.getGameMode(), synchroKey);
			player.openInventory(inv);
			sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Openable.InventoryOther")
					.replace("%player%", othername)));
			return true;
		} else
		{
			sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("DidYouMean")));
			sender.sendMessage(ChatApi.tl(cc.getSuggestion()));
			return false;
		}
	}
}