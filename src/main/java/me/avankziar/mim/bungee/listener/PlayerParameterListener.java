package main.java.me.avankziar.mim.bungee.listener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import main.java.me.avankziar.mim.bungee.MIM;
import main.java.me.avankziar.mim.general.StaticValues;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerParameterListener implements Listener	
{
	private MIM plugin;
	
	public PlayerParameterListener(MIM plugin)
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
        if (!event.getTag().equalsIgnoreCase(StaticValues.PP_TOBUNGEE))
        {
            return;
        }
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
        String task = in.readUTF();
        String server = null;
        if(task.equals(StaticValues.PP_SAVEALL))
        {
        	//Do Nothing
        } else if(task.equals(StaticValues.PP_SAVESERVER))
        {
        	server = in.readUTF();
        } else if(task.equals(StaticValues.PP_SAVEANDKICKALL))
        {
        	//Do Nothing
        } else if(task.equals(StaticValues.PP_SAVEANDKICKSERVER))
        {
        	server = in.readUTF();
        }
        send(task, server);
	}
	
	private void send(String stv, String server)
	{
		ByteArrayOutputStream streamout = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(streamout);
        try {
        	out.writeUTF(stv);
		} catch (IOException e) 
        {
			e.printStackTrace();
		}
        if(server == null)
        {
        	for(ServerInfo si : plugin.getProxy().getServers().values())
        	{
        		if(si.getPlayers().size() > 0)
        		{
        			si.sendData(StaticValues.PP_TOSPIGOT, streamout.toByteArray());
        		}
        	}
        } else
        {
        	ServerInfo si = plugin.getProxy().getServerInfo(server);
            if(si != null)
            {
            	si.sendData(StaticValues.PP_TOSPIGOT, streamout.toByteArray());
            }
        }
	}
}