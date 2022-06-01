package main.java.me.avankziar.mim.spigot.cmd.cpi;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import main.java.me.avankziar.ifh.general.economy.account.AccountCategory;
import main.java.me.avankziar.ifh.general.economy.account.AccountManagementType;
import main.java.me.avankziar.ifh.general.economy.action.EconomyAction;
import main.java.me.avankziar.ifh.general.economy.action.OrdererType;
import main.java.me.avankziar.ifh.spigot.economy.account.Account;
import main.java.me.avankziar.ifh.spigot.economy.currency.EconomyCurrency;
import main.java.me.avankziar.mim.general.ChatApi;
import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.assistance.Utility;
import main.java.me.avankziar.mim.spigot.cmdtree.ArgumentConstructor;
import main.java.me.avankziar.mim.spigot.cmdtree.ArgumentModule;
import main.java.me.avankziar.mim.spigot.cmdtree.BaseConstructor;
import main.java.me.avankziar.mim.spigot.database.MysqlHandler;
import main.java.me.avankziar.mim.spigot.objects.CustomPlayerInventory;
import main.java.me.avankziar.mim.spigot.permission.Bypass;

public class CPIBuy extends ArgumentModule
{
	private MIM plugin;
	private String cpiUniquename;
	
	public CPIBuy(ArgumentConstructor ac, String cpiUniquename)
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
		UUID other = player.getUniqueId();
		if(args.length >= 2)
		{
			other = Utility.convertNameToUUID(args[1]);
			if(other == null)
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("PlayerNotExist")));
				return;
			}
		}
		CustomPlayerInventory cpi = new CustomPlayerInventory(cpiUniquename);
		if(!cpi.isActive())
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CPI.NotActiveOrDontExist")));
			return;
		}
		cpi = (CustomPlayerInventory) plugin.getMysqlHandler().getData(MysqlHandler.Type.CUSTOMPLAYERINVENTORY,
				"`cpi_name` = ? AND `owner_uuid` = ?", cpiUniquename, other.toString());
		if(cpi == null)
		{
			String n = null;
			cpi = new CustomPlayerInventory(cpiUniquename, player.getUniqueId(), 0, 0, 0, n);
		}
		if(cpi.usePredefineCustomInventory())
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CPI.NotActiveOrDontExist")));
			return;
		}
		if(cpi.getMaxbuyedRowAmount() >= 6)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CPI.YouHaveAlreadyBuyedRowSix")));
			return;
		}
		if(other.toString().equals(player.getUniqueId().toString()))
		{
			int permrow = cpi.getPermissionRowAmount(player);
			if(cpi.getMaxbuyedRowAmount()+1 > permrow)
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CPI.YouCannotBuyWhereYouHaveNoPermission")));
				return;
			}
		} else
		{
			if(!player.hasPermission(Bypass.get(Bypass.Permission.CUSTOMPLAYERINVENTORY_BUY)))
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CPI.YouCannotBuyAAnotherPlayerARow")));
				return;
			}
		}
		if(!player.hasPermission(Bypass.get(Bypass.Permission.CUSTOMPLAYERINVENTORY_BUY)))
		{
			boolean isFree = false;
			for(String a : cpi.getCosts(cpi.getMaxbuyedRowAmount()+1))
			{
				if(a.contains("FREE"))
				{
					isFree = true;
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
					EconomyCurrency ec = plugin.getEconomy().getCurrency(split[2]);
					Account ac = plugin.getEconomy().getDefaultAccount(player.getUniqueId(), AccountCategory.MAIN);
					if(ac == null || !plugin.getEconomy().canManageAccount(ac, player.getUniqueId(), AccountManagementType.CAN_WITHDRAW))
					{
						player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CPI.YouHaveNoAccountToWithdrawTheCost")
								.replace("%format%", plugin.getEconomy().format(Double.parseDouble(split[1]), ec))));
						return;
					}
					if(ac.getBalance() < Double.parseDouble(split[1]))
					{
						player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CPI.YouHaveNoEnoughMoneyAmount")
								.replace("%format%", plugin.getEconomy().format(Double.parseDouble(split[1]), ec))));
						return;
					}
				} else if(a.contains("MATERIAL"))
				{
					String[] split = a.split(";");
					if(split.length != 3)
					{
						continue;
					}
					int needed = 0;
					int amount = 0;
					Material mat = Material.AIR;
					try
					{
						mat = Material.valueOf(split[1]);
						amount = Integer.parseInt(split[1]);
						needed = amount;
					} catch(Exception e)
					{
						 continue;
					}
					if(amount < 0)
					{
						continue;
					}
					for(ItemStack is : player.getInventory().getStorageContents())
					{
						if(mat != is.getType())
						{
							continue;
						}
						if(is.getItemMeta().hasDisplayName() || is.getItemMeta().hasLore())
						{
							continue;
						}
						amount = amount - is.getAmount();
					}
					if(amount > 0)
					{
						player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CPI.YouHaveNoEnoughMaterials")
								.replace("%actual%", String.valueOf(needed-amount))
								.replace("%needed%", String.valueOf(needed))
								.replace("%mat%", mat.toString())));
						return;
					}
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
					int texp = getTotalExperience(player);
					if(texp < amount)
					{
						player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CPI.YouHaveNoEnoughExp")
								.replace("%actual%", String.valueOf(texp-amount))
								.replace("%needed%", String.valueOf(texp))));
						return;
					}
				}
			}
			if(!isFree)
			{
				for(String a : cpi.getCosts(cpi.getMaxbuyedRowAmount()+1))
				{
					if(a.contains("MONEY"))
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
						EconomyCurrency ec = plugin.getEconomy().getCurrency(split[2]);
						Account ac = plugin.getEconomy().getDefaultAccount(player.getUniqueId(), AccountCategory.MAIN);
						Account v = plugin.getEconomy().getDefaultAccount(player.getUniqueId(), AccountCategory.VOID);
						if(ac == null)
						{
							player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CPI.YouHaveNoAccountToWithdrawTheCost")
									.replace("%format%", plugin.getEconomy().format(Double.parseDouble(split[1]), ec))));
							return;
						}
						EconomyAction ea = null;
						if(v != null)
						{
							ea = plugin.getEconomy().transaction(ac, v, Double.parseDouble(split[1]),
									OrdererType.PLAYER, player.getUniqueId().toString(),
									plugin.getYamlHandler().getLang().getString("CPI.ActionLogCategory"),
									plugin.getYamlHandler().getLang().getString("CPI.ActionLogComment"));
						} else
						{
							ea = plugin.getEconomy().withdraw(ac, Double.parseDouble(split[1]),
									OrdererType.PLAYER, player.getUniqueId().toString(),
									plugin.getYamlHandler().getLang().getString("CPI.ActionLogCategory"),
									plugin.getYamlHandler().getLang().getString("CPI.ActionLogComment"));
						}
						if(!ea.isSuccess())
						{
							player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CPI.YouHaveNoAccountToWithdrawTheCost")
									.replace("%format%", plugin.getEconomy().format(Double.parseDouble(split[1]), ec))));
							return;
						}
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
						if(amount < 0)
						{
							continue;
						}
						for(ItemStack is : player.getInventory().getStorageContents())
						{
							if(amount <= 0)
							{
								break;
							}
							if(mat != is.getType())
							{
								continue;
							}
							if(is.getItemMeta().hasDisplayName() || is.getItemMeta().hasLore())
							{
								continue;
							}
							if(is.getAmount() < amount)
							{
								is.setAmount(0);
								amount = amount - is.getAmount();
							} else if(is.getAmount() == amount)
							{
								is.setAmount(0);
								amount = 0;
							} else if(is.getAmount() > amount)
							{
								is.setAmount(is.getAmount()-amount);
								amount = 0;
							}
						}
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
						int texp = getTotalExperience(player)-amount;
						setTotalExperience(player, texp);
					}
				}
				String cost = CPIInfo.getCost(plugin, cpi, cpi.getMaxbuyedRowAmount()+1);
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CPI.YouPaidForTheNextRow")
						.replace("%row%", String.valueOf(cpi.getMaxbuyedRowAmount()+1))
						.replace("%cpi%", cpi.getUniqueName())
						.replace("%cost%", cost)));
			} else
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CPI.YouGetTheNextRow")
						.replace("%row%", String.valueOf(cpi.getMaxbuyedRowAmount()+1))
						.replace("%cpi%", cpi.getUniqueName())));
			}
			cpi.setMaxbuyedRowAmount(cpi.getMaxbuyedRowAmount()+1);
			plugin.getMysqlHandler().updateData(MysqlHandler.Type.CUSTOMPLAYERINVENTORY, cpi, 
					"`cpi_name` = ? AND `owner_uuid` = ?", cpiUniquename, other.toString());
		}
	}
	
	//Thank you, https://www.spigotmc.org/threads/how-to-calculate-exp-and-levels.510074/#post-4184467
	public int getTotalExperience(Player player) 
	{
	    int experience = 0;
	    int level = player.getLevel();
	    if(level >= 0 && level <= 15) 
	    {
	        experience = (int) Math.ceil(Math.pow(level, 2) + (6 * level));
	        int requiredExperience = 2 * level + 7;
	        double currentExp = Double.parseDouble(Float.toString(player.getExp()));
	        experience += Math.ceil(currentExp * requiredExperience);
	        return experience;
	    } else if(level > 15 && level <= 30) 
	    {
	        experience = (int) Math.ceil((2.5 * Math.pow(level, 2) - (40.5 * level) + 360));
	        int requiredExperience = 5 * level - 38;
	        double currentExp = Double.parseDouble(Float.toString(player.getExp()));
	        experience += Math.ceil(currentExp * requiredExperience);
	        return experience;
	    } else 
	    {
	        experience = (int) Math.ceil(((4.5 * Math.pow(level, 2) - (162.5 * level) + 2220)));
	        int requiredExperience = 9 * level - 158;
	        double currentExp = Double.parseDouble(Float.toString(player.getExp()));
	        experience += Math.ceil(currentExp * requiredExperience);
	        return experience;
	    }
	}

	public void setTotalExperience(Player player, int xp) 
	{
	    //Levels 0 through 15
	    if(xp >= 0 && xp < 351) 
	    {
	        //Calculate Everything
	        int a = 1; int b = 6; int c = -xp;
	        int level = (int) (-b + Math.sqrt(Math.pow(b, 2) - (4 * a * c))) / (2 * a);
	        int xpForLevel = (int) (Math.pow(level, 2) + (6 * level));
	        int remainder = xp - xpForLevel;
	        int experienceNeeded = (2 * level) + 7;
	        float experience = (float) remainder / (float) experienceNeeded;
	        experience = round(experience, 2);

	        //Set Everything
	        player.setLevel(level);
	        player.setExp(experience);
	        //Levels 16 through 30
	    } else if(xp >= 352 && xp < 1507) 
	    {
	        //Calculate Everything
	        double a = 2.5; double b = -40.5; int c = -xp + 360;
	        double dLevel = (-b + Math.sqrt(Math.pow(b, 2) - (4 * a * c))) / (2 * a);
	        int level = (int) Math.floor(dLevel);
	        int xpForLevel = (int) (2.5 * Math.pow(level, 2) - (40.5 * level) + 360);
	        int remainder = xp - xpForLevel;
	        int experienceNeeded = (5 * level) - 38;
	        float experience = (float) remainder / (float) experienceNeeded;
	        experience = round(experience, 2);

	        //Set Everything
	        player.setLevel(level);
	        player.setExp(experience);
	        //Level 31 and greater
	    } else 
	    {
	        //Calculate Everything
	        double a = 4.5; double b = -162.5; int c = -xp + 2220;
	        double dLevel = (-b + Math.sqrt(Math.pow(b, 2) - (4 * a * c))) / (2 * a);
	        int level = (int) Math.floor(dLevel);
	        int xpForLevel = (int) (4.5 * Math.pow(level, 2) - (162.5 * level) + 2220);
	        int remainder = xp - xpForLevel;
	        int experienceNeeded = (9 * level) - 158;
	        float experience = (float) remainder / (float) experienceNeeded;
	        experience = round(experience, 2);

	        //Set Everything
	        player.setLevel(level);
	        player.setExp(experience);
	    }
	}

	@SuppressWarnings("deprecation")
	private float round(float d, int decimalPlace) 
	{
	    BigDecimal bd = new BigDecimal(Float.toString(d));
	    bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_DOWN);
	    return bd.floatValue();
	}
}