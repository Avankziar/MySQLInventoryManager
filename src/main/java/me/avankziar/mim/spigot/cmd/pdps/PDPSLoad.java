package main.java.me.avankziar.mim.spigot.cmd.pdps;

import java.io.IOException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.ifh.general.assistance.ChatApi;
import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.cmdtree.ArgumentConstructor;
import main.java.me.avankziar.mim.spigot.cmdtree.ArgumentModule;
import main.java.me.avankziar.mim.spigot.cmdtree.BaseConstructor;
import main.java.me.avankziar.mim.spigot.handler.ConfigHandler;
import main.java.me.avankziar.mim.spigot.handler.PredefinePlayerStateHandler;
import main.java.me.avankziar.mim.spigot.objects.SyncType;

public class PDPSLoad extends ArgumentModule
{
	private MIM plugin;
	
	public PDPSLoad(ArgumentConstructor ac)
	{
		super(ac);
		this.plugin = BaseConstructor.getPlugin();
	}
	
	@Override
	public void run(CommandSender sender, String[] args) throws IOException
	{
		if(!(sender instanceof Player))
		{
			return;
		}
		Player player = (Player) sender;
		String synchroKey = new ConfigHandler(plugin).getSynchroKey(player);
		String statename = args[1];
		PredefinePlayerStateHandler.load(SyncType.FULL, player, statename);
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("PredefinePlayerState.Loaded")
				.replace("%statename%", statename)
				.replace("%synchrokey%", synchroKey)));
	}
}