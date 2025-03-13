package me.avankziar.mim.spigot.handler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class Base64Handler
{
	public static String getBase64Inventory(ItemStack[] inventory) 
	{
        try (ByteArrayOutputStream output = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(output)) 
        {
            dataOutput.writeObject(inventory);
            return Base64.getEncoder().encodeToString(output.toByteArray());
        } catch (IOException e) 
        {
            e.printStackTrace();
        }
        return "";
    }
	
	public static String getBase64Item(ItemStack inventory) 
	{
        try (ByteArrayOutputStream output = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(output)) 
        {
            dataOutput.writeObject(inventory);
            return Base64.getEncoder().encodeToString(output.toByteArray());
        } catch (IOException e) 
        {
            e.printStackTrace();
        }
        return "";
    }
	
	public static String getBase64PotionEffect(PotionEffect[] potionEffect) 
	{
        try (ByteArrayOutputStream output = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(output)) 
        {
            dataOutput.writeObject(potionEffect);
            return Base64.getEncoder().encodeToString(output.toByteArray());
        } catch (IOException e) 
        {
            e.printStackTrace();
        }
        return "";
    }

    public static ItemStack[] fromBase64Inventory(String data) 
    {
        try (ByteArrayInputStream input = new ByteArrayInputStream(Base64.getDecoder().decode(data));
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(input)) 
        {
            return (ItemStack[]) dataInput.readObject();
        } catch (IOException | ClassNotFoundException e) 
        {
            e.printStackTrace();
        }
        return new ItemStack[0];
    }
    
    public static ItemStack fromBase64Item(String data) 
    {
        try (ByteArrayInputStream input = new ByteArrayInputStream(Base64.getDecoder().decode(data));
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(input)) 
        {
            return (ItemStack) dataInput.readObject();
        } catch (IOException | ClassNotFoundException e) 
        {
            e.printStackTrace();
        }
        return new ItemStack(Material.ACACIA_BOAT);
    }
    
    public static PotionEffect[] fromBase64PotionEffect(String data) 
    {
        try (ByteArrayInputStream input = new ByteArrayInputStream(Base64.getDecoder().decode(data));
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(input)) 
        {
            return (PotionEffect[]) dataInput.readObject();
        } catch (IOException | ClassNotFoundException e) 
        {
            e.printStackTrace();
        }
        return new PotionEffect[0];
    }
}