package main.java.me.avankziar.mim.spigot.ifh.provider;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import main.java.me.avankziar.ifh.spigot.serializer.Base64;

public class Base64Provider implements Base64
{	
	@Override
	public Object fromBase64(String data)
	{
		try 
		{
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            Object o = dataInput.readObject();
            dataInput.close();
            return o;
        } catch (IOException e)
		{
			e.printStackTrace();
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
    	return null;
	}

	@Override
	public Object[] fromBase64Array(String data)
	{
		ArrayList<Object> list = new ArrayList<>();
		try 
		{
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            int size = (int) dataInput.readInt();
            
            for (int i = 0; i < size; i++) 
            {
            	Object o = dataInput.readObject();
            	list.add(o);
            }
            
            dataInput.close();
            return list.toArray(new Object[list.size()]);
        } catch (ClassNotFoundException e) 
        {
        	e.printStackTrace();
        } catch (IOException e)
		{
			e.printStackTrace();
		}
        return null;
	}

	@Override
	public String toBase64(Object o)
	{
		try 
		{
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(o);
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) 
		{
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
	}

	@Override
	public String toBase64Array(Object[] o)
	{
		try 
        {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeInt(o.length);
            for(int i = 0; i < o.length; i++)
            {
            	dataOutput.writeObject(o[i]);
            }           
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) 
        {
            throw new IllegalStateException("Unable to save itemstacks.", e);
        }
	}
}
