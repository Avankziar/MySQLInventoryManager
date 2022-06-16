package main.java.me.avankziar.mim.bungee.listener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

import main.java.me.avankziar.mim.bungee.MIM;
import main.java.me.avankziar.mim.general.StaticValues;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class CommandToBungeeListener implements Listener	
{
	private MIM plugin;
	
	public CommandToBungeeListener(MIM plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onTeleportMessage(PluginMessageEvent event) throws IOException
	{
		if (event.isCancelled()) 
		{
            return;
        }
        if (!( event.getSender() instanceof Server))
        {
        	return;
        }
        if (!event.getTag().equalsIgnoreCase(StaticValues.CMDTB_TOBUNGEE))
        {
            return;
        }
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
        String task = in.readUTF();
        
        if(task.equals(StaticValues.CMDTB_ASCONSOLE))
        {
        	String cmd = in.readUTF();
        	exeCmd(plugin.getProxy().getConsole(), cmd);
        } else if(task.equals(StaticValues.CMDTB_ASCONSOLE_FORALLPLAYERS))
        {
        	String cmd = in.readUTF();
        	for(int i = 0; i < plugin.getProxy().getPlayers().size(); i++)
        	{
        		exeCmd(plugin.getProxy().getConsole(), cmd);
        	}
        } else if(task.equals(StaticValues.CMDTB_ASCONSOLE_FORALLPLAYERSREPLACER))
        {
        	String cmd = in.readUTF();
        	String repl = in.readUTF();
        	for(ProxiedPlayer pp : plugin.getProxy().getPlayers())
        	{
        		exeCmd(plugin.getProxy().getConsole(), cmd.replace(repl, pp.getName()));
        	}
        } else if(task.equals(StaticValues.CMDTB_ASPLAYER))
        {
        	String cmd = in.readUTF();
        	String uuid = in.readUTF();
        	ProxiedPlayer pp = plugin.getProxy().getPlayer(UUID.fromString(uuid));
        	if(pp == null)
        	{
        		return;
        	}
        	exeCmd(pp, cmd);
        } else if(task.equals(StaticValues.CMDTB_ASPLAYER_FORALLPLAYERS))
        {
        	String cmd = in.readUTF();
        	for(ProxiedPlayer pp : plugin.getProxy().getPlayers())
        	{
        		exeCmd(pp, cmd);
        	}
        } else if(task.equals(StaticValues.CMDTB_ASPLAYER_FORALLPLAYERSREPLACER))
        {
        	String cmd = in.readUTF();
        	String repl = in.readUTF();
        	for(ProxiedPlayer pp : plugin.getProxy().getPlayers())
        	{
        		exeCmd(pp, cmd.replace(repl, pp.getName()));
        	}
        }
	}
	
	private void exeCmd(CommandSender cs, String cmd)
	{
		BungeeCord.getInstance().getPluginManager().dispatchCommand(cs, cmd);
	}
}