package main.java.me.avankziar.mim.spigot.cmd;

import java.util.LinkedHashMap;

import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.Player;

import main.java.me.avankziar.mim.general.ChatApi;
import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.assistance.MatchApi;
import main.java.me.avankziar.mim.spigot.cmdtree.CommandConstructor;
import main.java.me.avankziar.mim.spigot.database.MysqlHandler;
import main.java.me.avankziar.mim.spigot.objects.PlayerData;

public class AttributeCmdExecutor implements CommandExecutor
{
	private MIM plugin;
	private static CommandConstructor cc;
	
	public AttributeCmdExecutor(MIM plugin, CommandConstructor cc)
	{
		this.plugin = plugin;
		AttributeCmdExecutor.cc = cc;
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
		if(args.length == 2)
		{
			String synchroKey = MIM.getPlugin().getConfigHandler().getSynchroKey(player, false);
			PlayerData pd = (PlayerData) MIM.getPlugin().getMysqlHandler().getData(MysqlHandler.Type.PLAYERDATA,
    				"`player_uuid` = ? AND `synchro_key` = ? AND `game_mode` = ?",
    				player.getUniqueId().toString(), synchroKey,
    				player.getGameMode().toString());
			String attribute = args[0];
			String value = args[1];
			switch(attribute)
			{
			default:
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Attribute.ValueNotExist")
						.replace("%attribute%", attribute)));
				return false;
			case "foodlevel":
				if(!MatchApi.isInteger(value))
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Attribute.ValueNotExist")
							.replace("%attribute%", attribute)
							.replace("%value%", "Integer")
							.replace("%example%", "-9 -> 0 -> 9")));
					return false;
				}
				int i = Integer.parseInt(value);
				pd.setFoodLevel(i);
				break;
			case "saturation":
				if(!MatchApi.isFloat(value))
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Attribute.ValueNotExist")
							.replace("%attribute%", attribute)
							.replace("%value%", "Float")
							.replace("%example%", "-9.0 -> 0.0 -> 9.0")));
					return false;
				}
				float f = Float.parseFloat(value);
				pd.setSaturation(f);
				break;
			case "saturatedRegenRate":
				if(!MatchApi.isInteger(value))
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Attribute.ValueNotExist")
							.replace("%attribute%", attribute)
							.replace("%value%", "Integer")
							.replace("%example%", "-9 -> 0 -> 9")));
					return false;
				}
				i = Integer.parseInt(value);
				pd.setSaturatedRegenRate(i);
				break;
			case "unsaturatedRegenRate":
				if(!MatchApi.isInteger(value))
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Attribute.ValueNotExist")
							.replace("%attribute%", attribute)
							.replace("%value%", "Integer")
							.replace("%example%", "-9 -> 0 -> 9")));
					return false;
				}
				i = Integer.parseInt(value);
				pd.setUnsaturatedRegenRate(i);
				break;
			case "starvationRate":
				if(!MatchApi.isInteger(value))
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Attribute.ValueNotExist")
							.replace("%attribute%", attribute)
							.replace("%value%", "Integer")
							.replace("%example%", "-9 -> 0 -> 9")));
					return false;
				}
				i = Integer.parseInt(value);
				pd.setStarvationRate(i);
				break;
			case "exhaustion":
				if(!MatchApi.isFloat(value))
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Attribute.ValueNotExist")
							.replace("%attribute%", attribute)
							.replace("%value%", "Float")
							.replace("%example%", "-9.0 -> 0.0 -> 9.0")));
					return false;
				}
				f = Float.parseFloat(value);
				pd.setExhaustion(f);
				break;
			case "generic_armor":
				if(!MatchApi.isDouble(value))
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Attribute.ValueNotExist")
							.replace("%attribute%", attribute)
							.replace("%value%", "Double")
							.replace("%example%", "-9.0 -> 0.0 -> 9.0")));
					return false;
				}
				double d = Double.parseDouble(value);
				LinkedHashMap<Attribute, Double> map = pd.getAttributes();
				if(map == null)
				{
					map = new LinkedHashMap<>();
				}
				map.put(Attribute.GENERIC_ARMOR, d);
				pd.setAttributes(map);
				break;
			case "generic_armor_toughness":
				if(!MatchApi.isDouble(value))
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Attribute.ValueNotExist")
							.replace("%attribute%", attribute)
							.replace("%value%", "Double")
							.replace("%example%", "-9.0 -> 0.0 -> 9.0")));
					return false;
				}
				d = Double.parseDouble(value);
				map = pd.getAttributes();
				if(map == null)
				{
					map = new LinkedHashMap<>();
				}
				map.put(Attribute.GENERIC_ARMOR_TOUGHNESS, d);
				pd.setAttributes(map);
				break;
			case "generic_attack_damage":
				if(!MatchApi.isDouble(value))
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Attribute.ValueNotExist")
							.replace("%attribute%", attribute)
							.replace("%value%", "Double")
							.replace("%example%", "-9.0 -> 0.0 -> 9.0")));
					return false;
				}
				d = Double.parseDouble(value);
				map = pd.getAttributes();
				if(map == null)
				{
					map = new LinkedHashMap<>();
				}
				map.put(Attribute.GENERIC_ATTACK_DAMAGE, d);
				pd.setAttributes(map);
				break;
			case "generic_attack_knockback":
				if(!MatchApi.isDouble(value))
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Attribute.ValueNotExist")
							.replace("%attribute%", attribute)
							.replace("%value%", "Double")
							.replace("%example%", "-9.0 -> 0.0 -> 9.0")));
					return false;
				}
				d = Double.parseDouble(value);
				map = pd.getAttributes();
				if(map == null)
				{
					map = new LinkedHashMap<>();
				}
				map.put(Attribute.GENERIC_ATTACK_KNOCKBACK, d);
				pd.setAttributes(map);
				break;
			case "generic_attack_speed":
				if(!MatchApi.isDouble(value))
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Attribute.ValueNotExist")
							.replace("%attribute%", attribute)
							.replace("%value%", "Double")
							.replace("%example%", "-9.0 -> 0.0 -> 9.0")));
					return false;
				}
				d = Double.parseDouble(value);
				map = pd.getAttributes();
				if(map == null)
				{
					map = new LinkedHashMap<>();
				}
				map.put(Attribute.GENERIC_ATTACK_SPEED, d);
				pd.setAttributes(map);
				break;
			case "generic_knockback_resistance":
				if(!MatchApi.isDouble(value))
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Attribute.ValueNotExist")
							.replace("%attribute%", attribute)
							.replace("%value%", "Double")
							.replace("%example%", "-9.0 -> 0.0 -> 9.0")));
					return false;
				}
				d = Double.parseDouble(value);
				map = pd.getAttributes();
				if(map == null)
				{
					map = new LinkedHashMap<>();
				}
				map.put(Attribute.GENERIC_KNOCKBACK_RESISTANCE, d);
				pd.setAttributes(map);
				break;
			case "generic_luck":
				if(!MatchApi.isDouble(value))
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Attribute.ValueNotExist")
							.replace("%attribute%", attribute)
							.replace("%value%", "Double")
							.replace("%example%", "-9.0 -> 0.0 -> 9.0")));
					return false;
				}
				d = Double.parseDouble(value);
				map = pd.getAttributes();
				if(map == null)
				{
					map = new LinkedHashMap<>();
				}
				map.put(Attribute.GENERIC_LUCK, d);
				pd.setAttributes(map);
				break;
			case "generic_max_health":
				if(!MatchApi.isDouble(value))
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Attribute.ValueNotExist")
							.replace("%attribute%", attribute)
							.replace("%value%", "Double")
							.replace("%example%", "-9.0 -> 0.0 -> 9.0")));
					return false;
				}
				d = Double.parseDouble(value);
				map = pd.getAttributes();
				if(map == null)
				{
					map = new LinkedHashMap<>();
				}
				map.put(Attribute.GENERIC_MAX_HEALTH, d);
				pd.setAttributes(map);
				break;
			case "health":
				if(!MatchApi.isDouble(value))
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Attribute.ValueNotExist")
							.replace("%attribute%", attribute)
							.replace("%value%", "Double")
							.replace("%example%", "-9.0 -> 0.0 -> 9.0")));
					return false;
				}
				d = Double.parseDouble(value);
				map = pd.getAttributes();
				if(map == null)
				{
					map = new LinkedHashMap<>();
				}
				double maxhealth = map.get(Attribute.GENERIC_MAX_HEALTH);
				if(maxhealth < d)
				{
					d = maxhealth;
				}
				pd.setHealth(d);
				break;
			case "absorptionAmount":
				if(!MatchApi.isDouble(value))
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Attribute.ValueNotExist")
							.replace("%attribute%", attribute)
							.replace("%value%", "Double")
							.replace("%example%", "-9.0 -> 0.0 -> 9.0")));
					return false;
				}
				d = Double.parseDouble(value);
				pd.setAbsorptionAmount(d);
				break;
			case "expTowardsNextLevel":
				if(!MatchApi.isFloat(value))
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Attribute.ValueNotExist")
							.replace("%attribute%", attribute)
							.replace("%value%", "Float")
							.replace("%example%", "-9.0 -> 0.0 -> 9.0")));
					return false;
				}
				f = Float.parseFloat(value);
				pd.setExpTowardsNextLevel(f);
				break;
			case "expLevel":
				if(!MatchApi.isInteger(value))
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Attribute.ValueNotExist")
							.replace("%attribute%", attribute)
							.replace("%value%", "Integer")
							.replace("%example%", "-9 -> 0 -> 9")));
					return false;
				}
				i = Integer.parseInt(value);
				pd.setExpLevel(i);
				break;
			case "totalExperience":
				if(!MatchApi.isInteger(value))
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Attribute.ValueNotExist")
							.replace("%attribute%", attribute)
							.replace("%value%", "Integer")
							.replace("%example%", "-9 -> 0 -> 9")));
					return false;
				}
				i = Integer.parseInt(value);
				pd.setTotalExperience(i);
				break;
			case "walkSpeed":
				if(!MatchApi.isFloat(value))
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Attribute.ValueNotExist")
							.replace("%attribute%", attribute)
							.replace("%value%", "Float")
							.replace("%example%", "-9.0 -> 0.0 -> 9.0")));
					return false;
				}
				f = Float.parseFloat(value);
				pd.setWalkSpeed(f);
				break;
			case "flySpeed":
				if(!MatchApi.isFloat(value))
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Attribute.ValueNotExist")
							.replace("%attribute%", attribute)
							.replace("%value%", "Float")
							.replace("%example%", "-9.0 -> 0.0 -> 9.0")));
					return false;
				}
				f = Float.parseFloat(value);
				pd.setFlySpeed(f);
				break;
			case "glowing":
				if(!MatchApi.isBoolean(value))
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Attribute.ValueNotExist")
							.replace("%attribute%", attribute)
							.replace("%value%", "Boolean")
							.replace("%example%", "true / false")));
					return false;
				}
				boolean b = Boolean.parseBoolean(value);
				pd.setGlowing(b);
				break;
			case "gravity":
				if(!MatchApi.isBoolean(value))
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Attribute.ValueNotExist")
							.replace("%attribute%", attribute)
							.replace("%value%", "Boolean")
							.replace("%example%", "true / false")));
					return false;
				}
				b = Boolean.parseBoolean(value);
				pd.setGravity(b);
				break;
			case "invisible":
				if(!MatchApi.isBoolean(value))
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Attribute.ValueNotExist")
							.replace("%attribute%", attribute)
							.replace("%value%", "Boolean")
							.replace("%example%", "true / false")));
					return false;
				}
				b = Boolean.parseBoolean(value);
				pd.setInvisible(b);
				break;
			case "invulnerable":
				if(!MatchApi.isBoolean(value))
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Attribute.ValueNotExist")
							.replace("%attribute%", attribute)
							.replace("%value%", "Boolean")
							.replace("%example%", "true / false")));
					return false;
				}
				b = Boolean.parseBoolean(value);
				pd.setInvulnerable(b);
				break;
			case "entityCategory":
				EntityCategory et;
				try
				{
					et = EntityCategory.valueOf(value);
				} catch(Exception e)
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Attribute.ValueNotExist")
							.replace("%attribute%", attribute)
							.replace("%value%", "EntityCategory")
							.replace("%example%", EntityCategory.ARTHROPOD.toString())));
					return false;
				}
				pd.setEntityCategory(et);
				break;
			case "maximumAir":
				if(!MatchApi.isInteger(value))
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Attribute.ValueNotExist")
							.replace("%attribute%", attribute)
							.replace("%value%", "Integer")
							.replace("%example%", "-9 -> 0 -> 9")));
					return false;
				}
				i = Integer.parseInt(value);
				pd.setMaximumAir(i);
				break;
			}
			plugin.getMysqlHandler().updateData(MysqlHandler.Type.PLAYERDATA, pd,
					"`player_uuid` = ? AND `synchro_key` = ? AND `game_mode` = ?",
    				player.getUniqueId().toString(), synchroKey,
    				player.getGameMode().toString());
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Attribute.ValueIsSet")
					.replace("%attribute%", attribute)
					.replace("%value%", value)));
			return true;
		} else
		{
			sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("DidYouMean")));
			sender.sendMessage(ChatApi.tl(cc.getSuggestion()));
			return false;
		}
	}
}