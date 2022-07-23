package main.java.me.avankziar.mim.spigot.handler;

import java.util.ArrayList;

import org.bukkit.Bukkit;
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
	
	public void openInventory(Player player, String playername)
	{
		if(cpi == null || cpi.getUniqueName() == null || cpi.getOwnerUUID() == null)
		{
			return;
		}
		int actualRow = cpi.getActualRowAmount();
		int targetRow = player.getUniqueId().toString().equals(cpi.getOwnerUUID().toString()) 
				? cpi.getPermissionRowAmount(player) : cpi.getTargetRowAmount();
		int maxbuyedRow = cpi.getMaxbuyedRowAmount();
		Inventory inv = InventoryCloseListener.getExternInventory(cpi.getOwnerUUID(), "CPI", cpi.getUniqueName());
		if(inv == null) 
		{
			if(actualRow == targetRow) 
			{
				inv = addItem(Bukkit.createInventory(null, actualRow*9, cpi.getInventoryName().replace("%player%", playername)),
						cpi.getInventory());
			} else if(actualRow < targetRow)
			{
				if(targetRow == maxbuyedRow || targetRow > maxbuyedRow)
				{
					cpi.setActualRowAmount(maxbuyedRow);
					inv = addItem(Bukkit.createInventory(null, maxbuyedRow*9, cpi.getInventoryName().replace("%player%", playername)),
							cpi.getInventory());
				} else if(targetRow < maxbuyedRow)
				{
					cpi.setActualRowAmount(targetRow);
					inv = addItem(Bukkit.createInventory(null, targetRow*9, cpi.getInventoryName().replace("%player%", playername)),
							cpi.getInventory());
				}
				plugin.getMysqlHandler().updateData(MysqlHandler.Type.CUSTOMPLAYERINVENTORY, cpi, 
						"`cpi_name` = ? AND `owner_uuid` = ?", cpi.getUniqueName(), cpi.getOwnerUUID().toString());
			} else if(actualRow > targetRow)
			{
				if(actualRow == maxbuyedRow)
				{
					cpi.setActualRowAmount(targetRow);
					inv = Bukkit.createInventory(null, targetRow*9, cpi.getInventoryName().replace("%player%", playername));
					ArrayList<ItemStack> toomuch = new ArrayList<>();
					for(int i = 0; i < 54; i++)
					{
						if(cpi.getInventory()[i] == null)
						{
							continue;
						}
						if(i < targetRow*9)
						{
							inv.setItem(i, cpi.getInventory()[i]);
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
					inv = Bukkit.createInventory(null, targetRow*9, cpi.getInventoryName().replace("%player%", playername));
					ArrayList<ItemStack> toomuch = new ArrayList<>();
					for(int i = 0; i < 54; i++)
					{
						if(cpi.getInventory()[i] == null)
						{
							continue;
						}
						if(i < targetRow*9)
						{
							inv.setItem(i, cpi.getInventory()[i]);
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
						inv = Bukkit.createInventory(null, targetRow*9, cpi.getInventoryName().replace("%player%", playername));
						ArrayList<ItemStack> toomuch = new ArrayList<>();
						for(int i = 0; i < 54; i++)
						{
							if(cpi.getInventory()[i] == null)
							{
								continue;
							}
							if(i < targetRow*9)
							{
								inv.setItem(i, cpi.getInventory()[i]);
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
						inv = Bukkit.createInventory(null, maxbuyedRow*9, cpi.getInventoryName().replace("%player%", playername));
						ArrayList<ItemStack> toomuch = new ArrayList<>();
						for(int i = 0; i < 54; i++)
						{
							if(cpi.getInventory()[i] == null)
							{
								continue;
							}
							if(i < maxbuyedRow*9)
							{
								inv.setItem(i, cpi.getInventory()[i]);
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
						"`cpi_name` = ? AND `owner_uuid` = ?", cpi.getUniqueName(), cpi.getOwnerUUID().toString());
			}
		}
		InventoryCloseListener.addToExternInventory(player.getUniqueId(), cpi.getOwnerUUID(), inv,
				"CPI", cpi.getUniqueName(), player.getGameMode(), cpi.getUniqueName());
		player.openInventory(inv);
	}
	
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
	
	private Inventory addItem(Inventory inventory, ItemStack[] is)
	{
		Inventory inv = inventory;
		for(int i = 0; i < inv.getSize(); i++)
		{
			if(i < is.length && is[i] != null)
			{
				inv.setItem(i, is[i]);
			}
		}
		return inv;
	}
}
