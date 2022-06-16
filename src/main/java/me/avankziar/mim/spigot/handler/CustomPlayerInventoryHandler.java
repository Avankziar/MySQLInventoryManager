package main.java.me.avankziar.mim.spigot.handler;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import main.java.me.avankziar.mim.general.ChatApi;
import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.database.MysqlHandler;
import main.java.me.avankziar.mim.spigot.listener.InventoryCloseListener;
import main.java.me.avankziar.mim.spigot.objects.CustomPlayerInventory;

public class CustomPlayerInventoryHandler
{
	private MIM plugin;
	private CustomPlayerInventory cpi;
	
	public CustomPlayerInventoryHandler(CustomPlayerInventory cpi)
	{	
		this.plugin = MIM.getPlugin();
		this.cpi = cpi;
	}
	
	public CustomPlayerInventoryHandler()
	{
		this.plugin = MIM.getPlugin();
	}
	
	public void openInventory(Player player)
	{
		if(cpi == null || cpi.getUniqueName() == null || cpi.getOwnerUUID() == null)
		{
			return;
		}
		int actualRow = cpi.getActualRowAmount();
		int targetRow = player.getUniqueId().toString().equals(cpi.getOwnerUUID().toString()) 
				? cpi.getPermissionRowAmount(player) : cpi.getTargetRowAmount();
		int maxbuyedRow = cpi.getMaxbuyedRowAmount();
		Inventory inv = InventoryCloseListener.getExternInventory(cpi.getOwnerUUID());
		if(inv == null) 
		{
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
		}
		InventoryCloseListener.addToExternInventory(player.getUniqueId(), cpi.getOwnerUUID(), inv,
				"CPI", cpi.getUniqueName(), player.getGameMode(), cpi.getUniqueName());
		player.openInventory(inv);
	}
	
	/*public void closeInventory(Player player, final ItemStack[] inv)
	{
		if(!inOwnCPIInventory.containsKey(player.getUniqueId()))
		{
			return;
		}
		CustomPlayerInventory cpi = inOwnCPIInventory.get(player.getUniqueId());
		if(cpi == null || cpi.getUniqueName() == null || cpi.getOwnerUUID() == null)
		{
			return;
		}
		cpi.setInventory(inv);
		plugin.getMysqlHandler().updateData(MysqlHandler.Type.CUSTOMPLAYERINVENTORY, cpi, 
				"`cpi_name` = ? AND `owner_name`", cpi.getUniqueName(), cpi.getOwnerUUID().toString());
		inOwnCPIInventory.remove(player.getUniqueId());
	}*/
	
	public void openShulkerInInventory(Player player, int slot, ItemStack shulker, ShulkerBox shulkerbox)
	{
		InventoryCloseListener.executorToShulkerSlot.put(player.getUniqueId(), slot);
		InventoryCloseListener.executorToShulkerItem.put(player.getUniqueId(), shulker);
		Inventory inv = Bukkit.createInventory(null, 27, cpi.getShulkerInventoryName());
        inv.setContents(shulkerbox.getInventory().getContents());
        player.openInventory(inv);
	}
	
	/*public void closeShulkerInInventory(Player player, ItemStack[] contents)
	{
		int slot = inOwnCPIInShulkerInventory.get(player.getUniqueId());
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
        inOwnCPIInShulkerInventory.remove(player.getUniqueId());
	}*/
	
	public void dropInventory(Player player)
	{
		if(cpi == null || cpi.getUniqueName() == null || cpi.getOwnerUUID() == null)
		{
			return;
		}
		if(cpi.getInventory() == null)
		{
			return;
		}
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
	}
}
