package main.java.me.avankziar.mim.spigot.ifh;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.me.avankziar.ifh.general.assistance.ChatApi;
import main.java.me.avankziar.ifh.spigot.synchronization.PlayerParameter;
import main.java.me.avankziar.ifh.spigot.synchronization.SyncType;
import main.java.me.avankziar.mim.general.StaticValues;
import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.handler.ConfigHandler;
import main.java.me.avankziar.mim.spigot.listener.BaseListener;
import main.java.me.avankziar.mim.spigot.objects.SyncTask;
import main.java.me.avankziar.mim.spigot.objects.SyncTask.RunType;

public class PlayerParameterApi extends BaseListener implements PlayerParameter, PluginMessageListener
{
	private MIM plugin;
	
	public PlayerParameterApi(MIM plugin)
	{
		super(plugin, BaseListener.Type.IFH_API);
		this.plugin = plugin;
	}
	
	private main.java.me.avankziar.mim.spigot.objects.SyncType cvSyncType(SyncType syncType)
	{
		switch(syncType)
		{
		case ATTRIBUTE: return main.java.me.avankziar.mim.spigot.objects.SyncType.ATTRIBUTE;
		case EFFECT:return main.java.me.avankziar.mim.spigot.objects.SyncType.EFFECT;
		case EXP: return main.java.me.avankziar.mim.spigot.objects.SyncType.EXP;
		case FULL: return main.java.me.avankziar.mim.spigot.objects.SyncType.FULL;
		case INV_ARMOR: return main.java.me.avankziar.mim.spigot.objects.SyncType.INV_ARMOR;
		case INV_ENDERCHEST: return main.java.me.avankziar.mim.spigot.objects.SyncType.INV_ENDERCHEST;
		case INV_OFFHAND: return main.java.me.avankziar.mim.spigot.objects.SyncType.INV_OFFHAND;
		case INV_ONLY: return main.java.me.avankziar.mim.spigot.objects.SyncType.INV_ONLY;
		case INVENTORY: return main.java.me.avankziar.mim.spigot.objects.SyncType.INVENTORY;
		case PERSITENTDATA: return main.java.me.avankziar.mim.spigot.objects.SyncType.PERSITENTDATA;
		}
		return main.java.me.avankziar.mim.spigot.objects.SyncType.FULL;
	}
	
	private Player getPlayer()
	{
		for(Player player : Bukkit.getOnlinePlayers())
		{
			if(player.isOnline())
			{
				return player;
			}
		}
		return null;
	}
	
	@Override
	public void load(Player player)
	{
		load(player, new ConfigHandler(plugin).getSynchroKey(player, false), SyncType.FULL);
	}
	
	@Override
	public void load(Player player, String synchroKey)
	{
		load(player, synchroKey, SyncType.FULL);
	}
	
	@Override
	public void load(Player player, SyncType syncType)
	{
		load(player, new ConfigHandler(plugin).getSynchroKey(player, false), syncType);
	}
	
	@Override
	public void load(Player player, String synchroKey, SyncType syncType)
	{
		addCooldown(player.getUniqueId());
		new SyncTask(plugin, cvSyncType(syncType), RunType.LOAD, player, player.getGameMode()).run();
		removeCooldown(player.getUniqueId());
	}
	
	@Override
	public void save(Player player)
	{
		save(player, new ConfigHandler(plugin).getSynchroKey(player, false), SyncType.FULL);
	}
	
	@Override
	public void save(Player player, String synchroKey)
	{
		save(player, synchroKey, SyncType.FULL);
	}
	
	@Override
	public void save(Player player, SyncType syncType)
	{
		save(player, new ConfigHandler(plugin).getSynchroKey(player, false), syncType);
	}
	
	@Override
	public void save(Player player, String synchroKey, SyncType syncType)
	{
		addCooldown(player.getUniqueId());
		new SyncTask(plugin, cvSyncType(syncType), RunType.SAVE, player, player.getGameMode()).run();
		removeCooldown(player.getUniqueId());
	}
	
	@Override
	public void saveAll()
	{
		Player player = getPlayer();
		if(player == null)
		{
			return;
		}
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try 
        {
			out.writeUTF(StaticValues.PP_SAVEALL);
		} catch (IOException e) {
			e.printStackTrace();
		}
        player.sendPluginMessage(plugin, StaticValues.PP_TOBUNGEE, stream.toByteArray());
	}
	
	@Override
	public void saveServer(String servername)
	{
		Player player = getPlayer();
		if(player == null)
		{
			return;
		}
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try 
        {
			out.writeUTF(StaticValues.PP_SAVESERVER);
			out.writeUTF(servername);
		} catch (IOException e) {
			e.printStackTrace();
		}
        player.sendPluginMessage(plugin, StaticValues.PP_TOBUNGEE, stream.toByteArray());
	}
	
	@Override
	public void saveAndKick(Player player)
	{
		addCooldown(player.getUniqueId());
		new SyncTask(plugin, main.java.me.avankziar.mim.spigot.objects.SyncType.FULL , RunType.SAVEANDKICK, player, player.getGameMode()).run();
		removeCooldown(player.getUniqueId());
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				player.kickPlayer(ChatApi.tl(plugin.getYamlHandler().getLang().getString("SyncTask.SavedAndKicked")));
			}
		}.runTaskLater(plugin, 20L);
		
	}
	
	@Override
	public void saveAndKickServer(String servername)
	{
		Player player = getPlayer();
		if(player == null)
		{
			return;
		}
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try 
        {
			out.writeUTF(StaticValues.PP_SAVEANDKICKSERVER);
			out.writeUTF(servername);
		} catch (IOException e) {
			e.printStackTrace();
		}
        player.sendPluginMessage(plugin, StaticValues.PP_TOBUNGEE, stream.toByteArray());
	}
	
	@Override
	public void saveAndKickAll()
	{
		Player player = getPlayer();
		if(player == null)
		{
			return;
		}
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try 
        {
			out.writeUTF(StaticValues.PP_SAVEANDKICKALL);
		} catch (IOException e) {
			e.printStackTrace();
		}
        player.sendPluginMessage(plugin, StaticValues.PP_TOBUNGEE, stream.toByteArray());
	}
	
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] bytes) 
	{
		if(channel.equals(StaticValues.PP_TOSPIGOT)) 
		{
        	ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
            DataInputStream in = new DataInputStream(stream);
            String task = null;
            try 
            {
            	task = in.readUTF();
            	if(task.equals(StaticValues.PP_SAVEALL) || task.equals(StaticValues.PP_SAVESERVER))
            	{
            		new BukkitRunnable()
					{
						int i = 0;
						Object[] arr = Bukkit.getOnlinePlayers().toArray();
						@Override
						public void run()
						{
							if(i >= arr.length)
							{
								cancel();
								return;
							}
							save((Player) arr[i]);
							i++;
						}
					}.runTaskTimerAsynchronously(plugin, 0, 2L);
            	} else if(task.equals(StaticValues.PP_SAVEANDKICKSERVER) || task.equals(StaticValues.PP_SAVEANDKICKALL))
            	{
            		new BukkitRunnable()
					{
						int i = 0;
						final Object[] arr = Bukkit.getOnlinePlayers().toArray(new Object[Bukkit.getOnlinePlayers().size()]);
						@Override
						public void run()
						{
							if(i >= arr.length)
							{
								cancel();
								return;
							}
							saveAndKick((Player) arr[i]);
							i++;
						}
					}.runTaskTimerAsynchronously(plugin, 0, 2L);
            	}
            } catch (IOException e) 
            {
    			e.printStackTrace();
    		}
		}
	}
}