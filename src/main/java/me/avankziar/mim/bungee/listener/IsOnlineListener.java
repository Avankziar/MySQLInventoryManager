package main.java.me.avankziar.mim.bungee.listener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import main.java.me.avankziar.mim.bungee.MIM;
import main.java.me.avankziar.mim.general.StaticValues;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class IsOnlineListener implements Listener	
{
	private MIM plugin;
	
	public IsOnlineListener(MIM plugin)
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
        if (!event.getTag().equalsIgnoreCase(StaticValues.ISONLINE_TOBUNGEE))
        {
            return;
        }
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
        String task = in.readUTF();
        if(task.equals(StaticValues.ISONLINE))
        {
        	String mode = in.readUTF();
        	String requestuuid = in.readUTF();
        	String targetuuid = in.readUTF();
        	boolean isOnline = false;
        	String requestserver = "";
        	String targetserver = "";
        	ProxiedPlayer request = plugin.getProxy().getPlayer(UUID.fromString(requestuuid));
        	ProxiedPlayer target = plugin.getProxy().getPlayer(UUID.fromString(targetuuid));
        	if(request != null)
        	{
        		requestserver = request.getServer().getInfo().getName();
        	}
        	if(target != null)
        	{
        		isOnline = true;
        		targetserver = target.getServer().getInfo().getName();
        	}
        	ByteArrayOutputStream streamout = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(streamout);
            try {
            	out.writeUTF(StaticValues.ISONLINE);
            	out.writeUTF(mode);
            	out.writeUTF(requestuuid);
            	out.writeUTF(targetuuid);
            	out.writeBoolean(isOnline);
            	out.writeUTF(requestserver);
            	out.writeUTF(targetserver);
    		} catch (IOException e) 
            {
    			e.printStackTrace();
    		}
        	request.getServer().sendData(StaticValues.ISONLINE_TOSPIGOT, streamout.toByteArray());
        }
	}
}