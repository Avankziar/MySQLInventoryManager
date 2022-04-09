package main.java.me.avankziar.mim.spigot.objects;

import main.java.me.avankziar.mim.spigot.gui.GUI.PersistentType;

public class PersistentData
{
	private String namespaced;
	private String key;
	private PersistentType persistentType;
	private String persistentValue;
	
	public PersistentData(String namespaced, String key, PersistentType persistentType, String persistentValue)
	{
		setNamespaced(namespaced);
		setKey(key);
		setPersistentType(persistentType);
		setPersistentValue(persistentValue);
	}

	public String getNamespaced()
	{
		return namespaced;
	}

	public void setNamespaced(String namespaced)
	{
		this.namespaced = namespaced;
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public PersistentType getPersistentType()
	{
		return persistentType;
	}

	public void setPersistentType(PersistentType persistentType)
	{
		this.persistentType = persistentType;
	}

	public String getPersistentValue()
	{
		return persistentValue;
	}

	public void setPersistentValue(String persistentValue)
	{
		this.persistentValue = persistentValue;
	}
}