package main.java.me.avankziar.mim.spigot.cmd.clear;

import java.io.IOException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.mim.spigot.cmdtree.ArgumentConstructor;
import main.java.me.avankziar.mim.spigot.cmdtree.ArgumentModule;
import main.java.me.avankziar.mim.spigot.listener.BaseListener;
import main.java.me.avankziar.mim.spigot.objects.SyncType;

public class ClearSub extends ArgumentModule
{
	private SyncType synctype;
	
	public ClearSub(ArgumentConstructor ac, SyncType synctype)
	{
		super(ac);
		this.synctype = synctype;
	}
	
	@Override
	public void run(CommandSender sender, String[] args) throws IOException
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage("Cmd only for Players!");
			return;
		}
		BaseListener.clearAndReset((Player) sender, args, synctype);
	}
}