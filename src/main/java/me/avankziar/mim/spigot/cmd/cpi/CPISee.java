package main.java.me.avankziar.mim.spigot.cmd.cpi;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.mim.general.ChatApi;
import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.assistance.Utility;
import main.java.me.avankziar.mim.spigot.cmdtree.ArgumentConstructor;
import main.java.me.avankziar.mim.spigot.cmdtree.ArgumentModule;
import main.java.me.avankziar.mim.spigot.cmdtree.BaseConstructor;
import main.java.me.avankziar.mim.spigot.database.MysqlHandler;
import main.java.me.avankziar.mim.spigot.handler.CustomPlayerInventoryHandler;
import main.java.me.avankziar.mim.spigot.objects.CustomPlayerInventory;

public class CPISee extends ArgumentModule
{
	private MIM plugin;
	private String cpiUniquename;
	
	public CPISee(ArgumentConstructor ac, String cpiUniquename)
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
		String othername = args[1];
		UUID uuid = Utility.convertNameToUUID(othername);
		if(uuid == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("PlayerNotExist")));
			return;
		}
		CustomPlayerInventory cpi = new CustomPlayerInventory(cpiUniquename);
		if(!cpi.isActive())
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CPI.NotActiveOrDontExist")));
			return;
		}
		cpi = (CustomPlayerInventory) plugin.getMysqlHandler().getData(MysqlHandler.Type.CUSTOMPLAYERINVENTORY,
				"`cpi_name` = ? AND `owner_uuid` = ?", cpiUniquename, uuid.toString());
		if(cpi == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CPI.DoNotHaveAccess")));
			return;
		}
		new CustomPlayerInventoryHandler(cpi).openInventory(player, othername);
	}
}