package main.java.me.avankziar.mim.spigot.cmdtree;

import java.io.IOException;

import org.bukkit.command.CommandSender;

import main.java.me.avankziar.mim.spigot.MIM;

public abstract class ArgumentModule
{
	public ArgumentConstructor argumentConstructor;

    public ArgumentModule(ArgumentConstructor argumentConstructor)
    {
       this.argumentConstructor = argumentConstructor;
       MIM.getPlugin().getArgumentMap().put(argumentConstructor.getPath(), this);
    }
    
    //This method will process the command.
    public abstract void run(CommandSender sender, String[] args) throws IOException;

}
