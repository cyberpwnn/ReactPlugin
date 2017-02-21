package org.cyberpwn.react.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.cyberpwn.react.React;

public class Keyring
{
	private static GList<File> hosts;
	private String id;
	private String ta;
	
	public Keyring(String id, String ta)
	{
		this.id = id;
		this.ta = ta;
		hosts = new GList<File>();
		hosts.add(new File(new File(React.instance().getDataFolder(), "cache"), "host.keyring"));
		hosts.add(new File(new File(React.instance().getDataFolder().getParentFile().getParentFile(), "cache"), "hmx.rv"));
	}
	
	public void saveKeyring()
	{
		for(File i : hosts)
		{
			try
			{
				save(i);
			}
			
			catch(Exception e)
			{
				
			}
		}
	}
	
	public void loadKeyring()
	{
		for(File i : hosts)
		{
			try
			{
				load(i);
				
				if(!id.equals("%%__USER__%%"))
				{
					break;
				}
			}
			
			catch(Exception e)
			{
				
			}
		}
	}
	
	public void save(File f) throws Exception
	{
		FileConfiguration fc = new YamlConfiguration();
		fc.set("id", id);
		fc.set("ta", ta);
		String d = fc.saveToString();
		FileOutputStream fos = new FileOutputStream(f);
		GZIPOutputStream gzo = new GZIPOutputStream(fos);
		DataOutputStream dos = new DataOutputStream(gzo);
		dos.writeUTF(d);
		dos.close();
	}
	
	public void load(File f) throws Exception
	{
		FileInputStream fin = new FileInputStream(f);
		GZIPInputStream gzi = new GZIPInputStream(fin);
		DataInputStream dis = new DataInputStream(gzi);
		String d = dis.readUTF();
		FileConfiguration fc = new YamlConfiguration();
		fc.loadFromString(d);
		id = fc.getString("id");
		ta = fc.getString("ta");
		dis.close();
	}
	
	public String getId()
	{
		return id;
	}
	
	public String getTa()
	{
		return ta;
	}
}
