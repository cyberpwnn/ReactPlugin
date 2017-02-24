package org.cyberpwn.react.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class RegionFile
{
	private GMap<String, GList<RegionProperty>> map;
	
	public RegionFile()
	{
		map = new GMap<String, GList<RegionProperty>>();
	}
	
	public void load(File file) throws InvalidConfigurationException, IOException
	{
		if(!file.exists())
		{
			return;
		}
		
		map.clear();
		FileInputStream fin = new FileInputStream(file);
		GZIPInputStream gzi = new GZIPInputStream(fin);
		DataInputStream dis = new DataInputStream(gzi);
		String data = dis.readUTF();
		FileConfiguration fc = new YamlConfiguration();
		fc.loadFromString(data);
		dis.close();
		
		for(String i : fc.getKeys(true))
		{
			GList<RegionProperty> p = new GList<RegionProperty>();
			
			for(String j : new GList<String>(fc.getStringList(i)))
			{
				RegionProperty rp = RegionProperty.get(j);
				
				if(rp != null)
				{
					p.add(rp);
				}
			}
			
			map.put(i, p);
		}
	}
	
	public void save(File file) throws IOException
	{
		if(!file.exists())
		{
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		
		FileConfiguration fc = new YamlConfiguration();
		FileOutputStream fos = new FileOutputStream(file);
		GZIPOutputStream gzo = new GZIPOutputStream(fos);
		DataOutputStream dos = new DataOutputStream(gzo);
		
		for(String i : map.k())
		{
			GList<String> pr = new GList<String>();
			
			for(RegionProperty j : map.get(i))
			{
				pr.add(j.toString());
			}
			
			fc.set(i, pr);
		}
		
		dos.writeUTF(fc.saveToString());
		dos.close();
	}
	
	public GMap<String, GList<RegionProperty>> getMap()
	{
		return map;
	}
	
	public GList<RegionProperty> getProperties(String region)
	{
		GList<RegionProperty> p = new GList<RegionProperty>();
		
		if(map.containsKey(region))
		{
			p = map.get(region).copy();
		}
		
		return p;
	}
}
