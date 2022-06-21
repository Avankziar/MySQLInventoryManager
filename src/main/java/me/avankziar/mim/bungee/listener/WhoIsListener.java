package main.java.me.avankziar.mim.bungee.listener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;

import main.java.me.avankziar.mim.bungee.MIM;
import main.java.me.avankziar.mim.general.StaticValues;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class WhoIsListener implements Listener	
{
	private MIM plugin;
	
	public WhoIsListener(MIM plugin)
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
        if (!event.getTag().equalsIgnoreCase(StaticValues.WHOIS_TOBUNGEE))
        {
            return;
        }
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
        String task = in.readUTF();
        if(task.equals(StaticValues.WHOIS_SENDREQUEST))
        {
        	String requestUUID = in.readUTF();
        	String targetUUID = in.readUTF();
        	String targetname = in.readUTF();
        	ProxiedPlayer target = plugin.getProxy().getPlayer(UUID.fromString(targetUUID));
        	if(target == null)
        	{
        		sendAnswerOffline(requestUUID, targetUUID, targetname);
        	} else
        	{
        		sendRequest(requestUUID, targetUUID, targetname);
        	}
        } else if(task.equals(StaticValues.WHOIS_ANSWERONLINE))
        {
        	String requestUUID = in.readUTF();
        	String targetUUID = in.readUTF();
        	String targetname = in.readUTF();
        	int pdid = in.readInt();
        	String world = in.readUTF();
        	double x = in.readDouble();
        	double y = in.readDouble();
        	double z = in.readDouble();
        	float yaw = in.readFloat();
        	float pitch = in.readFloat();
        	ProxiedPlayer target = plugin.getProxy().getPlayer(UUID.fromString(targetUUID));
        	String server = target.getServer().getInfo().getName();
        	String ip = ((InetSocketAddress)target.getSocketAddress()).getAddress().toString();
        	sendAnswerOnline(requestUUID, targetUUID, targetname, ip, pdid, server, world, x, y, z, yaw, pitch);
        }
	}
	
	private void sendRequest(String requestUUID, String targetUUID, String targetname)
	{
		ByteArrayOutputStream streamout = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(streamout);
        try {
        	out.writeUTF(StaticValues.WHOIS_SENDREQUEST);
        	out.writeUTF(requestUUID);
        	out.writeUTF(targetUUID);
        	out.writeUTF(targetname);
		} catch (IOException e) 
        {
			e.printStackTrace();
		}
        ProxiedPlayer target = plugin.getProxy().getPlayer(UUID.fromString(targetUUID));
        if(target != null)
        {
        	target.getServer().sendData(StaticValues.WHOIS_TOSPIGOT, streamout.toByteArray());
        }
	}
	
	private void sendAnswerOffline(String requestUUID, String targetUUID, String targetname)
	{
		ByteArrayOutputStream streamout = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(streamout);
        try {
        	out.writeUTF(StaticValues.WHOIS_ANSWEROFFLINE);
        	out.writeUTF(requestUUID);
        	out.writeUTF(targetUUID);
        	out.writeUTF(targetname);
		} catch (IOException e) 
        {
			e.printStackTrace();
		}
        ProxiedPlayer requester = plugin.getProxy().getPlayer(UUID.fromString(requestUUID));
        if(requester != null)
        {
        	requester.getServer().sendData(StaticValues.WHOIS_TOSPIGOT, streamout.toByteArray());
        }
	}
	
	private void sendAnswerOnline(String requestUUID, String targetUUID, String targetname,
			String ip, int pdid, String server, String world, double x, double y, double z, float yaw, float pitch)
	{
		ByteArrayOutputStream streamout = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(streamout);
        try {
        	out.writeUTF(StaticValues.WHOIS_ANSWEROFFLINE);
        	out.writeUTF(requestUUID);
        	out.writeUTF(targetUUID);
        	out.writeUTF(targetname);
        	out.writeUTF(ip);
        	out.writeInt(pdid);
        	out.writeUTF(server);
        	out.writeUTF(world);
        	out.writeDouble(x);
        	out.writeDouble(y);
        	out.writeDouble(z);
        	out.writeFloat(yaw);
        	out.writeFloat(pitch);
		} catch (IOException e) 
        {
			e.printStackTrace();
		}
        ProxiedPlayer requester = plugin.getProxy().getPlayer(UUID.fromString(requestUUID));
        if(requester != null)
        {
        	requester.getServer().sendData(StaticValues.WHOIS_TOSPIGOT, streamout.toByteArray());
        }
	}
}