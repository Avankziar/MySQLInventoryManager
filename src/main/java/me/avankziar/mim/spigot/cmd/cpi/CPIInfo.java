package main.java.me.avankziar.mim.spigot.cmd.cpi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.mim.general.ChatApi;
import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.cmdtree.ArgumentConstructor;
import main.java.me.avankziar.mim.spigot.cmdtree.ArgumentModule;
import main.java.me.avankziar.mim.spigot.cmdtree.BaseConstructor;
import main.java.me.avankziar.mim.spigot.database.MysqlHandler;
import main.java.me.avankziar.mim.spigot.objects.CustomPlayerInventory;

public class CPIInfo extends ArgumentModule
{
	private MIM plugin;
	private String cpiUniquename;
	
	public CPIInfo(ArgumentConstructor ac, String cpiUniquename)
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
			String n = null;
			cpi = new CustomPlayerInventory(cpiUniquename, player.getUniqueId(), 0, 0, 0, n);
		}
		List<String> info = plugin.getYamlHandler().getLang().getStringList("CPI.InfoArray");
		ArrayList<String> infonew = new ArrayList<>();
		String cr1 = getCost(plugin, cpi, 1);
		String cr2 = getCost(plugin, cpi, 2);
		String cr3 = getCost(plugin, cpi, 3);
		String cr4 = getCost(plugin, cpi, 4);
		String cr5 = getCost(plugin, cpi, 5);
		String cr6 = getCost(plugin, cpi, 6);
		String list = "";
		int i = 0;
		for(Material mat : CustomPlayerInventory.listItem.get(cpi.getUniqueName()))
		{
			if(i == 0)
			{
				list += mat.toString();
			} else
			{
				list += ", "+mat.toString();
			}
			i++;
		}
		for(String s : info)
		{
			String a = s
					.replace("%cpi%", cpi.getUniqueName())
					.replace("%permrowamount%", String.valueOf(cpi.getPermissionRowAmount(player)))
					.replace("%maxbuyedrow%", String.valueOf(cpi.getMaxbuyedRowAmount()))
					.replace("%costrowone%", cr1)
					.replace("%costrowtwo%", cr2)
					.replace("%costrowthree%", cr3)
					.replace("%costrowfour%", cr4)
					.replace("%costrowfive%", cr5)
					.replace("%costrowsix%", cr6)
					.replace("%liststatus%", cpi.getListStatus().toString())
					.replace("%list%", list)
					;
			infonew.add(a);
		}
		for(String s : infonew)
		{
			player.sendMessage(ChatApi.tl(s));
		}
	}
	
	public static String getCost(MIM plugin, CustomPlayerInventory cpi, int row)
	{
		String s = null;
		int i = 0;
		for(String a : cpi.getCosts(row))
		{
			if(a.contains("FREE"))
			{
				s = plugin.getYamlHandler().getLang().getString("CPI.CostReplacer.Free");
				break;
			} else if(a.contains("MONEY"))
			{
				String[] split = a.split(";");
				if(split.length != 3 || plugin.getEconomy() == null)
				{
					continue;
				}
				if(!plugin.getEconomy().existsCurrency(split[2]))
				{
					continue;
				}
				if(i == 0)
				{
					s += plugin.getEconomy().format(Integer.parseInt(split[1]), plugin.getEconomy().getCurrency(split[2]));
				} else 
				{
					s += ", "+plugin.getEconomy().format(Integer.parseInt(split[1]), plugin.getEconomy().getCurrency(split[2]));
				}
				i++;
			} else if(a.contains("MATERIAL"))
			{
				String[] split = a.split(";");
				if(split.length != 3)
				{
					continue;
				}
				int amount = 0;
				Material mat = Material.AIR;
				try
				{
					mat = Material.valueOf(split[1]);
					amount = Integer.parseInt(split[1]);
				} catch(Exception e)
				{
					 continue;
				}
				if(i == 0)
				{
					s += mat.toString()+" x "+amount;
				} else
				{
					s += ", "+mat.toString()+" x "+amount;
				}
				i++;
			} else if(a.contains("EXP"))
			{
				String[] split = a.split(";");
				if(split.length != 2)
				{
					continue;
				}
				int amount = 0;
				try
				{
					amount = Integer.parseInt(split[1]);
				} catch(Exception e)
				{
					 continue;
				}
				if(i == 0)
				{
					s += amount+" Exp";
				} else
				{
					s += ", "+amount+" Exp";
				}
				i++;
			}
		}
		return s == null ? plugin.getYamlHandler().getLang().getString("CPI.CostReplacer.Free") : s;
	}
}