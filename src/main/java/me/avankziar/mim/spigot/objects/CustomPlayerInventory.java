package main.java.me.avankziar.mim.spigot.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import main.java.me.avankziar.mim.spigot.MIM;
import main.java.me.avankziar.mim.spigot.database.MysqlHandable;
import main.java.me.avankziar.mim.spigot.database.MysqlHandler;

public class CustomPlayerInventory implements MysqlHandable
{
	public enum ListStatus
	{
		WHITELIST, BLACKLIST;
	}
	
	public enum CountType
	{
		ADDUP, HIGHEST;
	}
	
	public static LinkedHashMap<String, ArrayList<Material>> listItem = new LinkedHashMap<>();
	
	private String uniqueName; //Identifier for the customplayerinventory file
	private UUID ownerUUID; //The uuid of the player, which owns the inv
	private int actualRowAmount; //The actualRow of the inv.
	private int targetRowAmount; //The targetRow of the inv. 
								//If actualRow > targetrow, after closing the Items over the limit are dropped.
								//If actualRow < targetrow, before open, the inventory will growing on the targetrow amount.
	private int maxbuyedRowAmount; //The maximum buyed Row amount.
	private ItemStack[] inventory; //The Inventory
	
	public CustomPlayerInventory(){}
	
	public CustomPlayerInventory(String uniqueName) //Only Access for the YamlInformation
	{
		setUniqueName(uniqueName);
	}
	
	public static void initListItem()
	{
		listItem = new LinkedHashMap<>();
		for(Entry<String, YamlConfiguration> es : MIM.getPlugin().getYamlHandler().getCustomPlayerInventory().entrySet())
		{
			ArrayList<Material> al = new ArrayList<>();
			for(String s : es.getValue().getStringList("List.Material"))
			{
				try
				{
					Material m = Material.valueOf(s);
					if(!al.contains(m))
					{
						al.add(m);
					}
				} catch(Exception e)
				{
					continue;
				}
			}
			listItem.put(es.getKey(), al);
		}
	}
	
	public String getUniqueName()
	{
		return uniqueName;
	}

	public void setUniqueName(String uniqueName)
	{
		this.uniqueName = uniqueName;
	}

	private YamlConfiguration getFile()
	{
		if(getUniqueName() == null)
		{
			return null;
		}
		return MIM.getPlugin().getYamlHandler().getCustomPlayerInventory(getUniqueName());
	}
	
	public String getInventoryName()
	{
		YamlConfiguration y = getFile();
		return y == null ? "Custom Player Inventory %player%" : y.getString("InventoryName", "Custom Player Inventory %player%");
	}
	
	public String getShulkerInventoryName()
	{
		YamlConfiguration y = getFile();
		return y == null ? "Custom Player Shulker Inventory %player%" : y.getString("InventoryShulkerName", "Custom Player Shulker Inventory %player%");
	}
	
	public boolean canMaterialAccessInventory(ItemStack is)
	{
		YamlConfiguration y = getFile();
		if(y == null || is == null || !listItem.containsKey(getUniqueName()))
		{
			return false;
		}
		ListStatus ls;
		try
		{
			ls = ListStatus.valueOf(y.getString("List.Status", "BLACKLIST"));
		} catch(Exception e)
		{
			return false;
		}
		return ls == ListStatus.WHITELIST ? listItem.get(getUniqueName()).contains(is.getType()) : !listItem.get(getUniqueName()).contains(is.getType());
	}
	
	public boolean isActive()
	{
		YamlConfiguration y = getFile();
		return y == null ? false : y.getBoolean("IsActive", false);
	}
	
	public boolean usePredefineCustomInventory()
	{
		YamlConfiguration y = getFile();
		return y == null ? false : y.getBoolean("UsePredefineCustomInventory", false);
	}
	
	public String usedPredefineCustomInventory()
	{
		YamlConfiguration y = getFile();
		return y == null ? "pciname" : y.getString("UsedPredefineCustomInventory", "pciname");
	}
	
	public String getCommandPath()
	{
		return "Command";
	}
	
	public boolean canOpenShulkerInInventory(Player player)
	{
		YamlConfiguration y = getFile();
		return y == null ? false : y.getBoolean("ShulkerOpenInInventoryPermission", false);
	}
	
	public int getPermissionRowAmount(Player player)
	{
		YamlConfiguration y = getFile();
		if(y == null)
		{
			return 0;
		}
		CountType ct = CountType.valueOf(y.getString("CountPermissionType", "HIGHEST"));
		int c = 0;
		switch(ct)
		{
		case ADDUP:
			for(int i = 6; i > 0; i--)
			{
				if(player.hasPermission(y.getString("CountPermission", "mim.customplayerinv.count.default.")+i))
				{
					c += i;
				}
			}
			break;
		case HIGHEST:
			for(int i = 6; i > 0; i--)
			{
				if(player.hasPermission(y.getString("CountPermission", "mim.customplayerinv.count.default.")+i))
				{
					c = i;
					break;
				}
			}
			break;
		}
		return c > 6 ? 6 : c;
	}
	
	public List<String> getCosts(int row)
	{
		YamlConfiguration y = getFile();
		return y == null ? null : y.getStringList("CostPerRow."+row);
	}
	
	public ListStatus getListStatus()
	{
		YamlConfiguration y = getFile();
		return ListStatus.valueOf(y.getString("List.Status", "BLACKLIST"));
	}
	
	public CustomPlayerInventory(String uniqueName, UUID ownerUUID,
			int actualRowAmount, int targetRowAmount, int maxbuyedRowAmount, ItemStack[] inventory)
	{
		setUniqueName(uniqueName);
		setOwnerUUID(ownerUUID);
		setActualRowAmount(actualRowAmount);
		setTargetRowAmount(targetRowAmount);
		setMaxbuyedRowAmount(maxbuyedRowAmount);
		setInventory(inventory);
	}
	
	public CustomPlayerInventory(String uniqueName, UUID ownerUUID,
			int actualRowAmount, int targetRowAmount, int maxbuyedRowAmount, String inventory)
	{
		setUniqueName(uniqueName);
		setOwnerUUID(ownerUUID);
		setActualRowAmount(actualRowAmount);
		setTargetRowAmount(targetRowAmount);
		setMaxbuyedRowAmount(maxbuyedRowAmount);
		ArrayList<ItemStack> invc = new ArrayList<>();
		if(inventory != null)
		{
			for(Object o : MIM.getPlugin().getBase64Provider().fromBase64Array(inventory))
			{
				invc.add((ItemStack) o);
			}
			setInventory(invc.toArray(new ItemStack[invc.size()]));
		} else
		{
			setInventory(new ItemStack[actualRowAmount]);
		}
	}

	public UUID getOwnerUUID()
	{
		return ownerUUID;
	}

	public void setOwnerUUID(UUID ownerUUID)
	{
		this.ownerUUID = ownerUUID;
	}

	public int getActualRowAmount()
	{
		return actualRowAmount;
	}

	public void setActualRowAmount(int actualRowAmount)
	{
		this.actualRowAmount = actualRowAmount;
	}

	public int getTargetRowAmount()
	{
		return targetRowAmount;
	}

	public void setTargetRowAmount(int targetRowAmount)
	{
		this.targetRowAmount = targetRowAmount;
	}

	/**
	 * @return the maxbuyedRowAmount
	 */
	public int getMaxbuyedRowAmount()
	{
		return maxbuyedRowAmount;
	}

	/**
	 * @param maxbuyedRowAmount the maxbuyedRowAmount to set
	 */
	public void setMaxbuyedRowAmount(int maxbuyedRowAmount)
	{
		this.maxbuyedRowAmount = maxbuyedRowAmount;
	}

	public ItemStack[] getInventory()
	{
		return inventory;
	}

	public void setInventory(ItemStack[] inventory)
	{
		this.inventory = inventory;
	}
	
	@Override
	public boolean create(Connection conn, String tablename)
	{
		try
		{
			String sql = "INSERT INTO `" + tablename
					+ "`(`cpi_name`, `owner_uuid`,"
					+ " `actual_row_amount`, `target_row_amount`, `maxbuyed_row_amount`,"
					+ " `inventory_content`) " 
					+ "VALUES("
					+ "?, ?, "
					+ "?, ?, ?,"
					+ "?)";
			PreparedStatement ps = conn.prepareStatement(sql);
	        ps.setString(1, getUniqueName());
	        ps.setString(2, getOwnerUUID().toString());
	        ps.setInt(3, getActualRowAmount());
	        ps.setInt(4, getTargetRowAmount());
	        ps.setInt(5, getMaxbuyedRowAmount());
	        ps.setString(6, MIM.getPlugin().getBase64Provider().toBase64Array(getInventory()));
	        
	        int i = ps.executeUpdate();
	        MysqlHandler.addRows(MysqlHandler.QueryType.INSERT, i);
	        return true;
		} catch (SQLException e)
		{
			this.log(Level.WARNING, "SQLException! Could not create a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return false;
	}

	@Override
	public boolean update(Connection conn, String tablename, String whereColumn, Object... whereObject)
	{
		try 
		{
			String sql = "UPDATE `" + tablename
				+ "` SET "
				+ " `actual_row_amount` = ?, `target_row_amount` = ?, `maxbuyed_row_amount` = ?,"
				+ " `inventory_content` = ?" 
				+ " WHERE "+whereColumn;
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, getActualRowAmount());
	        ps.setInt(2, getTargetRowAmount());
	        ps.setInt(3, getMaxbuyedRowAmount());
	        ps.setString(4, MIM.getPlugin().getBase64Provider().toBase64Array(getInventory()));
	        
			int i = 5;
			for(Object o : whereObject)
			{
				ps.setObject(i, o);
				i++;
			}			
			int u = ps.executeUpdate();
			MysqlHandler.addRows(MysqlHandler.QueryType.UPDATE, u);
			return true;
		} catch (SQLException e)
		{
			this.log(Level.WARNING, "SQLException! Could not update a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return false;
	}

	@Override
	public ArrayList<Object> get(Connection conn, String tablename, String orderby, String limit, String whereColumn, Object... whereObject)
	{
		try
		{
			String sql = "SELECT * FROM `" + tablename
				+ "` WHERE "+whereColumn+" ORDER BY "+orderby+limit;
			PreparedStatement ps = conn.prepareStatement(sql);
			int i = 1;
			for(Object o : whereObject)
			{
				ps.setObject(i, o);
				i++;
			}
			
			ResultSet rs = ps.executeQuery();
			MysqlHandler.addRows(MysqlHandler.QueryType.READ, rs.getMetaData().getColumnCount());
			ArrayList<Object> al = new ArrayList<>();
			while (rs.next()) 
			{
				al.add(new CustomPlayerInventory(
						rs.getString("cpi_name"),
						UUID.fromString(rs.getString("owner_uuid")),
						rs.getInt("actual_row_amount"),
						rs.getInt("target_row_amount"),
						rs.getInt("maxbuyed_row_amount"),
						rs.getString("inventory_content")
						));
			}
			return al;
		} catch (SQLException e)
		{
			this.log(Level.WARNING, "SQLException! Could not get a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return new ArrayList<>();
	}
	
	public static ArrayList<CustomPlayerInventory> convert(ArrayList<Object> arrayList)
	{
		ArrayList<CustomPlayerInventory> l = new ArrayList<>();
		for(Object o : arrayList)
		{
			if(o instanceof CustomPlayerInventory)
			{
				l.add((CustomPlayerInventory) o);
			}
		}
		return l;
	}
}
