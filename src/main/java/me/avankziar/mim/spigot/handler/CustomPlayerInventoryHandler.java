package main.java.me.avankziar.mim.spigot.handler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import main.java.me.avankziar.mim.general.ChatApi;
import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.database.MysqlHandler;
import main.java.me.avankziar.mim.spigot.objects.CustomPlayerInventory;

public class CustomPlayerInventoryHandler
{
	private MIM plugin;
	private CustomPlayerInventory cpi;
	public static LinkedHashMap<UUID, CustomPlayerInventory> cpiInInventory = new LinkedHashMap<>();
	public static LinkedHashMap<UUID, Integer> playerInShulkerInInventory = new LinkedHashMap<>(); //Int is slotnumber in the cpi
	public static LinkedHashMap<UUID, ItemStack> shulkerInInventoryData = new LinkedHashMap<>();
	
	public CustomPlayerInventoryHandler(CustomPlayerInventory cpi)
	{	
		this.plugin = MIM.getPlugin();
		this.cpi = cpi;
	}
	
	public CustomPlayerInventoryHandler()
	{
		this.plugin = MIM.getPlugin();
	}
	
	private boolean inUse()
	{
		for(Entry<UUID, CustomPlayerInventory> es : cpiInInventory.entrySet())
		{
			if(es.getValue().getUniqueName().equals(cpi.getUniqueName())
					&& es.getValue().getOwnerUUID().toString().equals(cpi.getOwnerUUID().toString()))
			{
				return true;
			}
		}
		return false;
	}
	
	public void openInventory(Player player)
	{
		if(cpi == null || cpi.getUniqueName() == null || cpi.getOwnerUUID() == null)
		{
			return;
		}
		if(inUse())
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CPI.InventoryIsAlreadyInUse")));
			return;
		}
		int actualRow = cpi.getActualRowAmount();
		int targetRow = player.getUniqueId().toString().equals(cpi.getOwnerUUID().toString()) 
				? cpi.getPermissionRowAmount(player) : cpi.getTargetRowAmount();
		int maxbuyedRow = cpi.getMaxbuyedRowAmount();
		Inventory inv = null;
		if(actualRow == targetRow) 
		{
			inv = Bukkit.createInventory(null, actualRow, cpi.getInventoryName());
			inv.addItem(cpi.getInventory());
		} else if(actualRow < targetRow)
		{
			if(targetRow == maxbuyedRow || targetRow > maxbuyedRow)
			{
				cpi.setActualRowAmount(maxbuyedRow);
				inv = Bukkit.createInventory(null, maxbuyedRow, cpi.getInventoryName());
				inv.addItem(cpi.getInventory());
			} else if(targetRow < maxbuyedRow)
			{
				cpi.setActualRowAmount(targetRow);
				inv = Bukkit.createInventory(null, targetRow, cpi.getInventoryName());
				inv.addItem(cpi.getInventory());
			}
			plugin.getMysqlHandler().updateData(MysqlHandler.Type.CUSTOMPLAYERINVENTORY, cpi, 
					"`cpi_name` = ? AND `owner_name`", cpi.getUniqueName(), cpi.getOwnerUUID().toString());
		} else if(actualRow > targetRow)
		{
			if(actualRow == maxbuyedRow)
			{
				cpi.setActualRowAmount(targetRow);
				inv = Bukkit.createInventory(null, targetRow, cpi.getInventoryName());
				ArrayList<ItemStack> toomuch = new ArrayList<>();
				for(int i = 0; i < 54; i++)
				{
					if(cpi.getInventory()[i] == null)
					{
						continue;
					}
					if(i < targetRow*9)
					{
						inv.addItem(cpi.getInventory()[i]);
					} else
					{
						toomuch.add(cpi.getInventory()[i]);
					}
				}
				for(ItemStack is : toomuch)
				{
					player.getWorld().dropItemNaturally(player.getLocation(), is);
				}				
			} else if(actualRow < maxbuyedRow)
			{
				cpi.setActualRowAmount(targetRow);
				inv = Bukkit.createInventory(null, targetRow, cpi.getInventoryName());
				ArrayList<ItemStack> toomuch = new ArrayList<>();
				for(int i = 0; i < 54; i++)
				{
					if(cpi.getInventory()[i] == null)
					{
						continue;
					}
					if(i < targetRow*9)
					{
						inv.addItem(cpi.getInventory()[i]);
					} else
					{
						toomuch.add(cpi.getInventory()[i]);
					}
				}
				for(ItemStack is : toomuch)
				{
					player.getWorld().dropItemNaturally(player.getLocation(), is);
				}
			} else if(actualRow > maxbuyedRow)
			{
				if(maxbuyedRow == targetRow || maxbuyedRow > targetRow)
				{
					cpi.setActualRowAmount(targetRow);
					inv = Bukkit.createInventory(null, targetRow, cpi.getInventoryName());
					ArrayList<ItemStack> toomuch = new ArrayList<>();
					for(int i = 0; i < 54; i++)
					{
						if(cpi.getInventory()[i] == null)
						{
							continue;
						}
						if(i < targetRow*9)
						{
							inv.addItem(cpi.getInventory()[i]);
						} else
						{
							toomuch.add(cpi.getInventory()[i]);
						}
					}
					for(ItemStack is : toomuch)
					{
						player.getWorld().dropItemNaturally(player.getLocation(), is);
					}
				} else if(maxbuyedRow < targetRow)
				{
					cpi.setActualRowAmount(maxbuyedRow);
					inv = Bukkit.createInventory(null, maxbuyedRow, cpi.getInventoryName());
					ArrayList<ItemStack> toomuch = new ArrayList<>();
					for(int i = 0; i < 54; i++)
					{
						if(cpi.getInventory()[i] == null)
						{
							continue;
						}
						if(i < maxbuyedRow*9)
						{
							inv.addItem(cpi.getInventory()[i]);
						} else
						{
							toomuch.add(cpi.getInventory()[i]);
						}
					}
					for(ItemStack is : toomuch)
					{
						if(is == null)
						{
							continue;
						}
						player.getWorld().dropItemNaturally(player.getLocation(), is);
					}
				}
			}
			plugin.getMysqlHandler().updateData(MysqlHandler.Type.CUSTOMPLAYERINVENTORY, cpi, 
					"`cpi_name` = ? AND `owner_name`", cpi.getUniqueName(), cpi.getOwnerUUID().toString());
		}
		cpiInInventory.put(player.getUniqueId(), cpi);
		player.openInventory(inv);
	}
	
	public void closeInventory(Player player, final ItemStack[] inv)
	{
		if(!cpiInInventory.containsKey(player.getUniqueId()))
		{
			return;
		}
		CustomPlayerInventory cpi = cpiInInventory.get(player.getUniqueId());
		if(cpi == null || cpi.getUniqueName() == null || cpi.getOwnerUUID() == null)
		{
			return;
		}
		cpi.setInventory(inv);
		plugin.getMysqlHandler().updateData(MysqlHandler.Type.CUSTOMPLAYERINVENTORY, cpi, 
				"`cpi_name` = ? AND `owner_name`", cpi.getUniqueName(), cpi.getOwnerUUID().toString());
		cpiInInventory.remove(player.getUniqueId());
	}
	
	public void openShulkerInInventory(Player player, int slot, ItemStack shulker, ShulkerBox shulkerbox)
	{
		playerInShulkerInInventory.put(player.getUniqueId(), slot);
		shulkerInInventoryData.put(player.getUniqueId(), shulker);
		Inventory inv = Bukkit.createInventory(null, 27, cpi.getShulkerInventoryName());
        inv.setContents(shulkerbox.getInventory().getContents());
        player.openInventory(inv);
	}
	
	public void closeShulkerInInventory(Player player, ItemStack[] contents)
	{
		int slot = playerInShulkerInInventory.get(player.getUniqueId());
		ItemStack is = shulkerInInventoryData.get(player.getUniqueId());
		if(!(is.getItemMeta() instanceof BlockStateMeta))
		{
			return;
		}
		BlockStateMeta im = (BlockStateMeta) is.getItemMeta();
        if(!(im.getBlockState() instanceof ShulkerBox))
        {
        	return;
        }
        ShulkerBox shulker = (ShulkerBox) im.getBlockState();
        shulker.getInventory().clear();
        shulker.getInventory().addItem(contents);
        im.setBlockState(shulker);
        is.setItemMeta(im);
        Inventory inv = Bukkit.createInventory(null, cpi.getActualRowAmount(), cpi.getInventoryName());
		inv.addItem(cpi.getInventory());
		inv.setItem(slot, is);
		player.openInventory(inv);
        playerInShulkerInInventory.remove(player.getUniqueId());
	}
	
	public void dropInventory(Player player)
	{
		if(cpi == null || cpi.getUniqueName() == null || cpi.getOwnerUUID() == null)
		{
			return;
		}
		if(inUse())
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CPI.InventoryIsAlreadyInUse")));
			return;
		}
		if(cpi.getInventory() == null)
		{
			return;
		}
		cpiInInventory.put(player.getUniqueId(), cpi);
		int i = 0;
		for(ItemStack is : cpi.getInventory())
		{
			if(is == null)
			{
				continue;
			}
			player.getWorld().dropItemNaturally(player.getLocation(), is);
			i++;
		}
		cpi.setInventory(new ItemStack[cpi.getInventory().length]);
		if(i == 0)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CPI.HaveNothingToDrop")));
		} else
		{
			plugin.getMysqlHandler().updateData(MysqlHandler.Type.CUSTOMPLAYERINVENTORY, cpi, 
					"`cpi_name` = ? AND `owner_uuid` = ?", cpi.getUniqueName(), cpi.getOwnerUUID().toString());
		}
		cpiInInventory.remove(player.getUniqueId());
	}
}
