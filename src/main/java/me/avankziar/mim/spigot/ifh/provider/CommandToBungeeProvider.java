package main.java.me.avankziar.mim.spigot.ifh.provider;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import main.java.me.avankziar.ifh.spigot.tobungee.commands.CommandToBungee;
import main.java.me.avankziar.mim.general.StaticValues;
import main.java.me.avankziar.mim.spigot.MIM;

public class CommandToBungeeProvider implements CommandToBungee
{
	private MIM plugin;
	
	public CommandToBungeeProvider(MIM plugin)
	{
		this.plugin = plugin;
	}
	
	private Player getPlayer()
	{
		for(Player player : Bukkit.getOnlinePlayers())
		{
			if(player.isOnline())
			{
				return player;
			}
		}
		return null;
	}

	@Override
	public void executeAsConsole(String command)
	{
		Player player = getPlayer();
		if(player == null)
		{
			return;
		}
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try 
        {
			out.writeUTF(StaticValues.CMDTB_ASCONSOLE);
			out.writeUTF(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
        player.sendPluginMessage(plugin, StaticValues.CMDTB_TOBUNGEE, stream.toByteArray());
	}

	@Override
	public void executeAsConsoleForAllPlayers(String command)
	{
		Player player = getPlayer();
		if(player == null)
		{
			return;
		}
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try 
        {
			out.writeUTF(StaticValues.CMDTB_ASCONSOLE_FORALLPLAYERS);
			out.writeUTF(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
        player.sendPluginMessage(plugin, StaticValues.CMDTB_TOBUNGEE, stream.toByteArray());
	}

	@Override
	public void executeAsConsoleForAllPlayers(String command, String playerReplacer)
	{
		Player player = getPlayer();
		if(player == null)
		{
			return;
		}
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try 
        {
			out.writeUTF(StaticValues.CMDTB_ASCONSOLE_FORALLPLAYERSREPLACER);
			out.writeUTF(command);
			out.writeUTF(playerReplacer);
		} catch (IOException e) {
			e.printStackTrace();
		}
        player.sendPluginMessage(plugin, StaticValues.CMDTB_TOBUNGEE, stream.toByteArray());
	}

	@Override
	public void executeAsPlayer(String command, UUID playerUUID)
	{
		Player player = getPlayer();
		if(player == null)
		{
			return;
		}
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try 
        {
			out.writeUTF(StaticValues.CMDTB_ASPLAYER);
			out.writeUTF(command);
			out.writeUTF(playerUUID.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
        player.sendPluginMessage(plugin, StaticValues.CMDTB_TOBUNGEE, stream.toByteArray());
	}

	@Override
	public void executeAsPlayerForAllPlayers(String command)
	{
		Player player = getPlayer();
		if(player == null)
		{
			return;
		}
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try 
        {
			out.writeUTF(StaticValues.CMDTB_ASPLAYER_FORALLPLAYERS);
			out.writeUTF(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
        player.sendPluginMessage(plugin, StaticValues.CMDTB_TOBUNGEE, stream.toByteArray());
	}

	@Override
	public void executeAsPlayerForAllPlayers(String command, String playerReplacer)
	{
		Player player = getPlayer();
		if(player == null)
		{
			return;
		}
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try 
        {
			out.writeUTF(StaticValues.CMDTB_ASPLAYER_FORALLPLAYERSREPLACER);
			out.writeUTF(command);
			out.writeUTF(playerReplacer);
		} catch (IOException e) {
			e.printStackTrace();
		}
        player.sendPluginMessage(plugin, StaticValues.CMDTB_TOBUNGEE, stream.toByteArray());
	}
}