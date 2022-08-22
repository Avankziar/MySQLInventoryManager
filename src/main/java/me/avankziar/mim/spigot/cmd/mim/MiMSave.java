package main.java.me.avankziar.mim.spigot.cmd.mim;

import java.io.IOException;

import org.bukkit.command.CommandSender;

import main.java.me.avankziar.mim.general.ChatApi;
import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.cmdtree.ArgumentConstructor;
import main.java.me.avankziar.mim.spigot.cmdtree.ArgumentModule;
import main.java.me.avankziar.mim.spigot.cmdtree.BaseConstructor;

public class MiMSave extends ArgumentModule
{
	private MIM plugin;
	
	public MiMSave(ArgumentConstructor ac)
	{
		super(ac);
		this.plugin = BaseConstructor.getPlugin();
	}
	
	@Override
	public void run(CommandSender sender, String[] args) throws IOException
	{
		String server = null;
		if(args.length >= 2)
		{
			server = args[1];
		}
		if(server != null)
		{
			plugin.getPlayerParameterProvider().saveServer(server);
			sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("SyncTask.SaveServer").replace("%server%", server)));
			return;
		} else
		{
			plugin.getPlayerParameterProvider().saveAll();
			sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("SyncTask.SaveAll")));
			return;
		}
	}
}