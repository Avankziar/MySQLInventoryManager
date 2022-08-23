package main.java.me.avankziar.mim.spigot.cmd.si;

import java.io.IOException;

import org.bukkit.command.CommandSender;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.cmdtree.ArgumentConstructor;
import main.java.me.avankziar.mim.spigot.cmdtree.ArgumentModule;
import main.java.me.avankziar.mim.spigot.cmdtree.BaseConstructor;

public class SIMaterial extends ArgumentModule
{
	private MIM plugin;
	
	public SIMaterial(ArgumentConstructor ac)
	{
		super(ac);
		this.plugin = BaseConstructor.getPlugin();
	}
	
	@Override
	public void run(CommandSender sender, String[] args) throws IOException
	{
		SIHand.sendItem(plugin, sender, args, "material");
	}
}