package main.java.me.avankziar.mim.bungee.listener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.UUID;

import main.java.me.avankziar.mim.bungee.MIM;
import main.java.me.avankziar.mim.general.StaticValues;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class OnlineListener implements Listener	
{
	private MIM plugin;
	
	public OnlineListener(MIM plugin)
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
        if (!event.getTag().equalsIgnoreCase(StaticValues.ONLINE_TOBUNGEE))
        {
            return;
        }
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
        String task = in.readUTF();
        if(task.equals(StaticValues.ONLINE_REQUEST))
        {
        	String requestUUID = in.readUTF();
        	sendRequest(requestUUID);
        }
	}
	
	private void sendRequest(String requestUUID)
	{
		Collection<ProxiedPlayer> cp = plugin.getProxy().getPlayers();
		ProxiedPlayer target = plugin.getProxy().getPlayer(UUID.fromString(requestUUID));
		if(target == null)
		{
			return;
		}
		ByteArrayOutputStream streamout = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(streamout);
        try {
        	out.writeUTF(StaticValues.ONLINE_REQUEST);
        	out.writeUTF(requestUUID);
        	out.writeUTF(target.getServer().getInfo().getName());
        	LinkedHashMap<String, ArrayList<String>> servermap = new LinkedHashMap<>();
        	for(ProxiedPlayer pp : cp)
        	{
        		String server = pp.getServer().getInfo().getName();
        		if(servermap.containsKey(server))
        		{
        			ArrayList<String> list = servermap.get(server);
        			list.add(pp.getUniqueId().toString());
        			servermap.put(server, list);
        		} else
        		{
        			ArrayList<String> list = new ArrayList<>();
        			list.add(pp.getUniqueId().toString());
        			servermap.put(server, list);
        		}
        	}
        	out.writeInt(servermap.size());
        	for(Entry<String, ArrayList<String>> entry : servermap.entrySet())
        	{
        		String a = entry.getKey();
        		for(String s : entry.getValue())
        		{
        			a += ";"+s;
        		}
        		out.writeUTF(a);
        	}
		} catch (IOException e) 
        {
			e.printStackTrace();
		}
        target.getServer().sendData(StaticValues.ONLINE_TOSPIGOT, streamout.toByteArray());
	}
}