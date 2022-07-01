package main.java.me.avankziar.mim.spigot.cmd;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import main.java.me.avankziar.ifh.general.economy.account.AccountCategory;
import main.java.me.avankziar.ifh.general.economy.account.EconomyEntity.EconomyType;
import main.java.me.avankziar.ifh.general.economy.currency.CurrencyType;
import main.java.me.avankziar.ifh.spigot.economy.account.Account;
import main.java.me.avankziar.ifh.spigot.economy.currency.EconomyCurrency;
import main.java.me.avankziar.ifh.spigot.position.ServerLocation;
import main.java.me.avankziar.mim.general.ChatApi;
import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.assistance.TimeHandler;
import main.java.me.avankziar.mim.spigot.assistance.Utility;
import main.java.me.avankziar.mim.spigot.cmd.cpi.CPIBuy;
import main.java.me.avankziar.mim.spigot.cmdtree.CommandConstructor;
import main.java.me.avankziar.mim.spigot.database.MysqlHandler;
import main.java.me.avankziar.mim.spigot.listener.WhoIsListener;
import main.java.me.avankziar.mim.spigot.objects.PersistentData;
import main.java.me.avankziar.mim.spigot.objects.PlayerData;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;

public class WhoIsCmdExecutor implements CommandExecutor
{
	private MIM plugin;
	private static CommandConstructor cc;
	
	public WhoIsCmdExecutor(MIM plugin, CommandConstructor cc)
	{
		this.plugin = plugin;
		WhoIsCmdExecutor.cc = cc;
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
		UUID targetuuid = null;
		String targetname = "";
		if(args.length == 0)
		{
			targetuuid = player.getUniqueId();
			targetname = player.getName();
		} else if(args.length == 1)
		{
			targetname = args[0];
			targetuuid = Utility.convertNameToUUID(targetname);
			if(targetuuid == null)
			{
				sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("PlayerNotExist")));
				return false;
			}
		} else
		{
			sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("DidYouMean")));
			sender.sendMessage(ChatApi.tl(cc.getSuggestion()));
			return false;
		}
		Player target = Bukkit.getPlayer(targetuuid);
		if(target != null)
		{
			PlayerData pd = (PlayerData) MIM.getPlugin().getMysqlHandler().getData(MysqlHandler.Type.PLAYERDATA,
    				"`player_uuid` = ? AND `synchro_key` = ? AND `game_mode` = ?",
    				targetuuid.toString(), MIM.getPlugin().getConfigHandler().getSynchroKey(target, false), target.getGameMode().toString());
			WhoIsCmdExecutor.sendWhoIs(player, 
    				targetuuid.toString(), targetname, target.getAddress().getAddress().toString(),
    				pd, true, null, null, 0.0, 0.0, 0.0, 0F, 0F);
		} else
		{
			//Nachricht an Bungee ob Spieler online ist.
			//Falls ja, muss seine Position, sowie Deathbackloc, die IP und die ID der playerData mitliefern.
			WhoIsListener.sendRequest(player, targetuuid, targetname);
		}
		return true;
	}
	
	public static void sendWhoIs(Player player, String targetuuid, String targetname, 
			String ip, PlayerData pd, boolean isOnline,
			String server, String worldname, double x, double y, double z, float yaw, float pitch)
	{
		UUID uuid = UUID.fromString(targetuuid);
		ArrayList<ArrayList<BaseComponent>> whois = new ArrayList<>();
		TextComponent ss = null;
		for(String s : MIM.getPlugin().getYamlHandler().getLang().getStringList("WhoIs.List"))
		{
			player.sendMessage(s);//TODO
			if(s.contains("%player%"))
			{
				ss = ChatApi.clickEvent(s.replace("%player%", targetname), Action.RUN_COMMAND, "/bminfo "+targetname);
			}
			if(s.contains("%uuid"))
			{
				ss = ChatApi.clickEvent(s.replace("%uuid%", targetuuid), Action.COPY_TO_CLIPBOARD, targetuuid);
			}
			if(s.contains("%money%"))
			{
				if(MIM.getPlugin().getEconomy() != null)
				{
					EconomyCurrency ec = MIM.getPlugin().getEconomy().getDefaultCurrency(CurrencyType.DIGITAL);
					double money = 0;
					for(Account ac : MIM.getPlugin().getEconomy().getAccounts(
							MIM.getPlugin().getEconomy().getEntity(uuid, EconomyType.PLAYER)))
					{
						if(ec == null || ac == null || ac.getCurrency() == null)
						{
							continue;
						}
						if(ac.getCategory() == AccountCategory.TAX || ac.getCategory() == AccountCategory.VOID)
						{
							continue;
						}
						if(!ec.getUniqueName().equals(ac.getCurrency().getUniqueName()))
						{
							continue;
						}
						money += ac.getBalance();
					}
					ss = ChatApi.tctl(s.replace("%money%", MIM.getPlugin().getEconomy().format(money, ec)));
				} else if(MIM.getPlugin().getVaultEconomy() != null)
				{
					ss = ChatApi.tctl(s.replace("%money%", MIM.getPlugin().getVaultEconomy().format(
							MIM.getPlugin().getVaultEconomy().getBalance(Bukkit.getOfflinePlayer(uuid)))
							+" "+MIM.getPlugin().getVaultEconomy().currencyNamePlural()));
				} else
				{
					continue;
				}
			}
			if(s.contains("%totaltime%") || s.contains("%onlinetime%") || s.contains("%afktime%"))
			{
				if(MIM.getPlugin().getPlayerTimes() != null)
				{
					ss = ChatApi.tctl(s.replace("%totaltime%", MIM.getPlugin().getPlayerTimes().format(
							MIM.getPlugin().getPlayerTimes().getTotalTime(uuid), 
							false, isOnline, isOnline, isOnline, false))
						  .replace("%onlinetime%", MIM.getPlugin().getPlayerTimes().format(
							MIM.getPlugin().getPlayerTimes().getTotalTime(uuid), 
							false, isOnline, isOnline, isOnline, false))
						  .replace("%afktime%", MIM.getPlugin().getPlayerTimes().format(
							MIM.getPlugin().getPlayerTimes().getTotalTime(uuid), 
							false, isOnline, isOnline, isOnline, false)));
				} else
				{
					continue;
				}
			}
			if(s.contains("%vacation%"))
			{
				if(MIM.getPlugin().getPlayerTimes() != null)
				{
					if(MIM.getPlugin().getPlayerTimes().isVacacation(uuid))
					{
						ss = ChatApi.tctl(s.replace("%vacation%", MIM.getPlugin().getPlayerTimes().format(
								MIM.getPlugin().getPlayerTimes().getTotalTime(uuid), 
								false, isOnline, isOnline, isOnline, false)));
					} else
					{
						ss = ChatApi.tctl(s.replace("%vacation%", getBoolean(false)));
					}
				} else
				{
					continue;
				}
			}
			if(s.contains("%backpos%"))
			{
				if(MIM.getPlugin().getLastKnownPosition() != null)
				{
					ServerLocation sl = MIM.getPlugin().getLastKnownPosition().getLastKnownPosition(uuid);
					ss = ChatApi.clickEvent(s.replace("%backpos%", sl.toString()),
							Action.RUN_COMMAND, "/tppos "+sl.getServername()+" "+sl.getWorldname()+" "+
									sl.getX()+" "+sl.getY()+" "+sl.getZ()+" "+sl.getYaw()+" "+sl.getPitch());
				} else
				{
					continue;
				}
			}
			if(ss != null)
			{
				ArrayList<BaseComponent> bc = new ArrayList<>();
				bc.add(ss);
				whois.add(bc);
			}
		}
		if(isOnline)
		{
			ss = null;
			for(String s : MIM.getPlugin().getYamlHandler().getLang().getStringList("WhoIs.OnlineList"))
			{
				if(ip != null && s.contains("%ip%"))
				{
					ss = ChatApi.tctl(s.replace("%ip%", ip));
				}
				if(server != null && s.contains("%pos%"))
				{
					ss = ChatApi.clickEvent(s.replace("%pos%", getPos(server, worldname, x, y, z)),
							Action.RUN_COMMAND, "/tp "+targetname);
				}
				if(pd != null)
				{
					if(s.contains("%gm%"))
					{
						ss = ChatApi.tctl(s.replace("%gm%", pd.getGameMode().toString()));
					}
					if(s.contains("%lp%") || s.contains("%lpmax%") || s.contains("%armor%"))
					{
						ss = ChatApi.tctl(s.replace("%lp%", String.valueOf(pd.getHealth()))
											.replace("%lpmax%", String.valueOf(pd.getAttributes().get(Attribute.GENERIC_MAX_HEALTH)))
											.replace("%armor%", String.valueOf(pd.getAttributes().get(Attribute.GENERIC_ARMOR)))
											);
					}
					if(s.contains("%hunger%") || s.contains("%saturation%"))
					{
						ss = ChatApi.tctl(s.replace("%hunger%", String.valueOf(pd.getFoodLevel()))
											.replace("%saturation%", String.valueOf(pd.getSaturation()))
											);
					}
					if(s.contains("%exp%") || s.contains("%level%"))
					{
						ss = ChatApi.tctl(s.replace("%exp%", String.valueOf(getTotalExp(pd.getExpLevel(), pd.getExpTowardsNextLevel())))
											.replace("%level%", String.valueOf(pd.getExpLevel()))
											);
					}
					if(s.contains("%invun%"))
					{
						ss = ChatApi.tctl(s.replace("%invun%", getBoolean(pd.isInvulnerable())));
					}
					if(s.contains("%invis%"))
					{
						ss = ChatApi.tctl(s.replace("%invis%", getBoolean(pd.isInvisible())));
					}
					if(s.contains("%flying%"))
					{
						ss = ChatApi.tctl(s.replace("%flying%", getBoolean(pd.isFlying())));
					}
					if(s.contains("%walkspeed%") || s.contains("%flyspeed%"))
					{
						ss = ChatApi.tctl(s.replace("%walkspeed%", String.valueOf(pd.getWalkSpeed()))
											.replace("%flyspeed%", String.valueOf(pd.getFlySpeed()))
											);
					}
					if(s.contains("%effect%"))
					{
						String effect = "";
						for(PotionEffect pe : pd.getActiveEffects())
						{
							if(pe == null)
							{
								continue;
							}
							int i = pe.getAmplifier()+1;
							effect += pe.getType().getName()+"("+i+")["
							+TimeHandler.getRepeatingTime(Long.parseLong(String.valueOf(pe.getDuration()))*50, "HH:mm:ss")+"], ";
						}
						ss = ChatApi.tctl(s.replace("%effect%", effect != null ? effect : "/"));
					}
					if(s.contains("%perdata%"))
					{
						String perda = null;
						for(PersistentData pda : pd.getPersistentData())
						{
							perda += getPersistentData(pda)+", ";
						}
						ss = ChatApi.tctl(s.replace("%perdata%", perda != null ? perda : "/"));
					}
				}
				if(ss != null)
				{
					ArrayList<BaseComponent> bc = new ArrayList<>();
					bc.add(ss);
					whois.add(bc);
				}
			}
		}
		for(ArrayList<BaseComponent> bc : whois)
		{
			TextComponent tx = ChatApi.tc("");
			tx.setExtra(bc);
			player.spigot().sendMessage(tx);
		}
	}
	
	private static String getPersistentData(PersistentData pd)
	{
		return pd.getNamespaced()+";"+pd.getKey()+"["+pd.getPersistentType().toString()+"-"+pd.getPersistentValue()+"]";
	}
	
	private static String getPos(String server, String world, double x, double z, double y)
	{
		return server+", "+world+" : "+x+" : "+y+" : "+z;
	}
	
	private static String getBoolean(boolean boo)
	{
		return boo ? MIM.getPlugin().getYamlHandler().getLang().getString("Boolean.True")
				: MIM.getPlugin().getYamlHandler().getLang().getString("Boolean.False");
	}
	
	private static int getTotalExp(int level, float exp) 
	{
		return CPIBuy.getTotalExperience(level, exp);
	}
}