package main.java.me.avankziar.mim.spigot.cmd;

import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.mim.general.ChatApi;
import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.cmdtree.ArgumentConstructor;
import main.java.me.avankziar.mim.spigot.cmdtree.ArgumentModule;
import main.java.me.avankziar.mim.spigot.cmdtree.CommandConstructor;
import main.java.me.avankziar.mim.spigot.database.MysqlHandler;
import main.java.me.avankziar.mim.spigot.handler.CustomPlayerInventoryHandler;
import main.java.me.avankziar.mim.spigot.objects.CustomPlayerInventory;
import net.md_5.bungee.api.chat.ClickEvent;

public class CustomPlayerInventoryCmdExecutor implements CommandExecutor
{
	private MIM plugin;
	private String cpiUniquename;
	private CommandConstructor cc;
	
	public CustomPlayerInventoryCmdExecutor(MIM plugin, CommandConstructor cc, String cpiUniquename)
	{
		this.plugin = plugin;
		this.cc = cc;
		this.cpiUniquename = cpiUniquename;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) 
	{
		if (!(sender instanceof Player)) 
		{
			MIM.log.info("/%cmd% is only for Player!".replace("%cmd%", cc.getName()));
			return false;
		}
		Player player = (Player) sender;
		if(cc == null)
		{
			return false;
		}
		if(args.length == 0)
		{
			if(!player.hasPermission(cc.getPermission()))
			{
				player.spigot().sendMessage(ChatApi.tctl(plugin.getYamlHandler().getLang().getString("NoPermission")));
				return false;
			}
			baseCommands(player); //Base and Info Command
			return true;
		}
		int length = args.length-1;
		ArrayList<ArgumentConstructor> aclist = cc.subcommands;
		for(int i = 0; i <= length; i++)
		{
			for(ArgumentConstructor ac : aclist)
			{
				if(args[i].equalsIgnoreCase(ac.getName()))
				{
					if(length >= ac.minArgsConstructor && length <= ac.maxArgsConstructor)
					{
						if(player.hasPermission(ac.getPermission()))
						{
							ArgumentModule am = plugin.getArgumentMap().get(ac.getPath());
							if(am != null)
							{
								try
								{
									am.run(sender, args);
								} catch (IOException e)
								{
									e.printStackTrace();
								}
							} else
							{
								MIM.log.info("ArgumentModule from ArgumentConstructor %ac% not found! ERROR!"
										.replace("%ac%", ac.getName()));
								player.spigot().sendMessage(ChatApi.tctl(
										"ArgumentModule from ArgumentConstructor %ac% not found! ERROR!"
										.replace("%ac%", ac.getName())));
								return false;
							}
							return false;
						} else
						{
							player.spigot().sendMessage(ChatApi.tctl(plugin.getYamlHandler().getLang().getString("NoPermission")));
							return false;
						}
					} else
					{
						aclist = ac.subargument;
						break;
					}
				}
			}
		}
		player.spigot().sendMessage(ChatApi.clickEvent(plugin.getYamlHandler().getLang().getString("InputIsWrong"),
				ClickEvent.Action.RUN_COMMAND, MIM.infoCommand));
		return false;
	}
	
	public void baseCommands(final Player player)
	{
		CustomPlayerInventory cpi = new CustomPlayerInventory(cpiUniquename);
		if(!cpi.isActive())
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CPI.NotActiveOrDontExist")));
			return;
		}
		cpi = (CustomPlayerInventory) plugin.getMysqlHandler().getData(MysqlHandler.Type.CUSTOMPLAYERINVENTORY,
				"`cpi_name` = ? AND `owner_uuid` = ?", cpiUniquename, player.getUniqueId().toString());
		if(cpi == null)
		{
			String n = null;
			cpi = new CustomPlayerInventory(cpiUniquename, player.getUniqueId(), 0, 0, 0, n);
			if(cpi.usePredefineCustomInventory())
			{
				//ADDME PredefineCustomInventory.
			}
			int targetRow = cpi.getPermissionRowAmount(player);
			if(targetRow <= 0)
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CPI.DoNotHaveAccess")));
				return;
			}
			if(cpi.getMaxbuyedRowAmount() == 0)
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CPI.DoNotHaveAccess")));
				return;
			}
		}
		new CustomPlayerInventoryHandler(cpi).openInventory(player);
	}
}