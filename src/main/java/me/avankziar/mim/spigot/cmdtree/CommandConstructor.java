package main.java.me.avankziar.mim.spigot.cmdtree;

import java.util.ArrayList;

import main.java.me.avankziar.mim.spigot.MIM;

public class CommandConstructor extends BaseConstructor
{
    public ArrayList<ArgumentConstructor> subcommands;
    public ArrayList<String> tablist;

	public CommandConstructor(CommandExecuteType cet, String path, boolean canConsoleAccess,
    		ArgumentConstructor...argumentConstructors)
    {
		super(cet, 
				MIM.getPlugin().getYamlHandler().getCommands().getString(path+".Name"),
				path,
				MIM.getPlugin().getYamlHandler().getCommands().getString(path+".Permission"),
				MIM.getPlugin().getYamlHandler().getCommands().getString(path+".Suggestion"),
				MIM.getPlugin().getYamlHandler().getCommands().getString(path+".CommandString"),
				MIM.getPlugin().getYamlHandler().getCommands().getString(path+".HelpInfo"),
				canConsoleAccess);
        this.subcommands = new ArrayList<>();
        this.tablist = new ArrayList<>();
        for(ArgumentConstructor ac : argumentConstructors)
        {
        	this.subcommands.add(ac);
        	this.tablist.add(ac.getName());
        }
        MIM.getPlugin().getCommandTree().add(this);
    }
}