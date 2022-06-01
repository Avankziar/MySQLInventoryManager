package main.java.me.avankziar.mim.spigot.cmd.cpi;

import java.io.IOException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.mim.general.ChatApi;
import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.cmdtree.ArgumentConstructor;
import main.java.me.avankziar.mim.spigot.cmdtree.ArgumentModule;
import main.java.me.avankziar.mim.spigot.cmdtree.BaseConstructor;
import main.java.me.avankziar.mim.spigot.database.MysqlHandler;
import main.java.me.avankziar.mim.spigot.handler.CustomPlayerInventoryHandler;
import main.java.me.avankziar.mim.spigot.objects.CustomPlayerInventory;

public class CPIDrop extends ArgumentModule
{
	private MIM plugin;
	private String cpiUniquename;
	
	public CPIDrop(ArgumentConstructor ac, String cpiUniquename)
	{
		super(ac);
		this.plugin = BaseConstructor.getPlugin();
		this.cpiUniquename = cpiUniquename;
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
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CPI.DoNotHaveAccess")));
			return;
		}
		if(args.length <= 1)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CPI.AreYouSureToDrop")
					.replace("%cpi%", cpi.getInventoryName())));
			return;
		}
		if(!"confirm".equalsIgnoreCase(args[1]) && !"bestätigen".equalsIgnoreCase(args[1]))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CPI.AreYouSureToDrop")
					.replace("%cpi%", cpi.getInventoryName())));
			return;
		}
		new CustomPlayerInventoryHandler(cpi).dropInventory(player);
	}
}
