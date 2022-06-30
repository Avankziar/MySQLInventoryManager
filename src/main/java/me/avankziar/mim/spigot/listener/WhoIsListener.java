package main.java.me.avankziar.mim.spigot.listener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.me.avankziar.mim.general.StaticValues;
import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.cmd.WhoIsCmdExecutor;
import main.java.me.avankziar.mim.spigot.database.MysqlHandler;
import main.java.me.avankziar.mim.spigot.objects.PlayerData;

public class WhoIsListener implements PluginMessageListener
{	
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] bytes) 
	{
		if(channel.equals(StaticValues.WHOIS_TOSPIGOT)) 
		{
        	ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
            DataInputStream in = new DataInputStream(stream);
            String task = null;
            try 
            {
            	task = in.readUTF();
            	if(task.equals(StaticValues.WHOIS_SENDREQUEST))
            	{
            		String requesterUUID = in.readUTF();
            		String targetUUID = in.readUTF();
            		String targetname = in.readUTF();
            		String synchroKey = MIM.getPlugin().getConfigHandler().getSynchroKey(player, false);
            		Player target = Bukkit.getPlayer(UUID.fromString(targetUUID));
            		PlayerData pd = (PlayerData) MIM.getPlugin().getMysqlHandler().getData(MysqlHandler.Type.PLAYERDATA,
            				"`player_uuid` = ? AND `synchro_key` = ? AND `game_mode` = ?",
            				targetUUID, synchroKey, target.getGameMode().toString());
            		int pdid = pd.getId();
            		String worldname = target.getWorld().getName();
            		double x = target.getLocation().getX();
            		double y = target.getLocation().getY();
            		double z = target.getLocation().getZ();
            		float yaw = target.getLocation().getYaw();
            		float pitch = target.getLocation().getPitch();
            		new BukkitRunnable()
					{
						@Override
						public void run()
						{
							sendAnswer(target, requesterUUID, targetUUID, targetname, pdid, worldname, x, y, z, yaw, pitch);
						}
					}.runTaskLater(MIM.getPlugin(), 20L);
            	} else if(task.equals(StaticValues.WHOIS_ANSWEROFFLINE))
            	{
            		String requesterUUID = in.readUTF();
            		String targetUUID = in.readUTF();
            		String targetname = in.readUTF();
            		WhoIsCmdExecutor.sendWhoIs(Bukkit.getPlayer(UUID.fromString(requesterUUID)),
            				targetUUID, targetname, null, null, false, null, null, 0, 0, 0, 0, 0);
            	} else if(task.equals(StaticValues.WHOIS_ANSWERONLINE))
            	{
            		String requesterUUID = in.readUTF();
            		String targetUUID = in.readUTF();
            		String targetname = in.readUTF();
            		String ip = in.readUTF();
            		int pdid = in.readInt();
            		String server = in.readUTF();
            		String worldname = in.readUTF();
            		double x = in.readDouble();
            		double y = in.readDouble();
            		double z = in.readDouble();
            		float yaw = in.readFloat();
            		float pitch = in.readFloat();
            		boolean isOnline = true;
            		PlayerData pd = (PlayerData) MIM.getPlugin().getMysqlHandler().getData(MysqlHandler.Type.PLAYERDATA, "`id` = ?", pdid);
            		WhoIsCmdExecutor.sendWhoIs(Bukkit.getPlayer(UUID.fromString(requesterUUID)), 
            				targetUUID, targetname, ip, pd, isOnline, server, worldname, x, y, z, yaw, pitch);
            	}
            } catch (IOException e) 
            {
    			e.printStackTrace();
    		}
		}
	}
	
	public static void sendRequest(Player player, UUID targetUUID, String targetname)
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try 
        {
			out.writeUTF(StaticValues.WHOIS_SENDREQUEST);
			out.writeUTF(player.getUniqueId().toString());
			out.writeUTF(targetUUID.toString());
			out.writeUTF(targetname);
		} catch (IOException e) {
			e.printStackTrace();
		}
        player.sendPluginMessage(MIM.getPlugin(), StaticValues.WHOIS_TOBUNGEE, stream.toByteArray());
	}
	
	public static void sendAnswer(Player player, String requesterUUID, String targetUUID, String targetname,
			int pdid, String world, double x, double y, double z, float yaw, float pitch)
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try 
        {
			out.writeUTF(StaticValues.WHOIS_ANSWERONLINE);
			out.writeUTF(requesterUUID);
			out.writeUTF(targetUUID);
			out.writeUTF(targetname);
			out.writeInt(pdid);
			out.writeUTF(world);
			out.writeDouble(x);
			out.writeDouble(y);
			out.writeDouble(z);
			out.writeFloat(yaw);
			out.writeFloat(pitch);
		} catch (IOException e) {
			e.printStackTrace();
		}
        player.sendPluginMessage(MIM.getPlugin(), StaticValues.WHOIS_TOBUNGEE, stream.toByteArray());
	}
}