package main.java.me.avankziar.mim.spigot.listener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import main.java.me.avankziar.mim.general.StaticValues;
import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.cmd.OnlineCmdExecutor;
import main.java.me.avankziar.mim.spigot.permission.Bypass;

public class OnlineListener implements PluginMessageListener
{
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] bytes) 
	{
		if(channel.equals(StaticValues.ONLINE_TOSPIGOT)) 
		{
        	ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
            DataInputStream in = new DataInputStream(stream);
            String task = null;
            try 
            {
            	task = in.readUTF();
            	if(task.equals(StaticValues.ONLINE_REQUEST))
            	{
            		String requesterUUID = in.readUTF();
            		String ownServer = in.readUTF();
            		int amount = in.readInt();
            		LinkedHashMap<String, ArrayList<String>> servermap = new LinkedHashMap<>();
            		for(int i = 0; i < amount; i++)
            		{
            			String[] split = in.readUTF().split(";");
            			String server = split[0];
                		if(servermap.containsKey(server))
                		{
                			ArrayList<String> list = servermap.get(server);
                			for(int j = 1; j < split.length; j++)
                			{
                				list.add(split[j]);
                			}
                			servermap.put(server, list);
                		} else
                		{
                			ArrayList<String> list = new ArrayList<>();
                			for(int j = 1; j < split.length; j++)
                			{
                				list.add(split[j]);
                			}
                			servermap.put(server, list);
                		}
            		}
            		Player request = Bukkit.getPlayer(UUID.fromString(requesterUUID));
            		if(request != null)
            		{
            			if(request.hasPermission(Bypass.get(Bypass.Permission.ONLINE_GROUP)))
            			{
            				OnlineCmdExecutor.onlineExpert(player, ownServer, servermap);
            			} else
            			{
            				OnlineCmdExecutor.onlineSimple(player, ownServer, servermap);
            			}
            		}
            	}
            } catch (IOException e) 
            {
    			e.printStackTrace();
    		}
		}
	}
	
	public static void sendRequest(Player player)
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try 
        {
			out.writeUTF(StaticValues.WHOIS_SENDREQUEST);
			out.writeUTF(player.getUniqueId().toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
        player.sendPluginMessage(MIM.getPlugin(), StaticValues.WHOIS_TOBUNGEE, stream.toByteArray());
	}
}