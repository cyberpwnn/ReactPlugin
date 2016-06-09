package org.cyberpwn.react.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.cyberpwn.react.React;
import org.cyberpwn.react.cluster.Configurable;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.object.GList;
import org.cyberpwn.react.object.GMap;
import org.cyberpwn.react.object.GTimeBank;
import org.cyberpwn.react.object.PlayerData;

public class DataController extends Controller
{
	private File dataFolder;
	private GMap<UUID, PlayerData> pd;
	private GTimeBank tb;
	
	public DataController(React react)
	{
		super(react);
		
		dataFolder = react.getDataFolder();
		tb = new GTimeBank();
		pd = new GMap<UUID, PlayerData>();
	}
	
	public PlayerData gpd(Player player)
	{
		if(pd.containsKey(player.getUniqueId()))
		{
			return pd.get(player.getUniqueId());
		}
		
		PlayerData p = new PlayerData(player.getUniqueId());
		load("playerdata", p);
		pd.put(player.getUniqueId(), p);
		return p;
	}
	
	public void spd(Player p)
	{
		if(pd.containsKey(p.getUniqueId()))
		{
			save("playerdata", pd.get(p.getUniqueId()));
		}
	}
	
	public void start()
	{
		File fx = new File(new File(dataFolder, "cache"), "history.cch");
		
		if(fx.exists())
		{
			Object o = deserialize(fx);
			
			if(o != null)
			{
				tb = (GTimeBank) o;
			}
			
			else
			{
				serialize(fx, tb);
			}
		}
		
		else
		{
			verifyFile(fx);
			serialize(fx, tb);
		}
		
		for(Player i : react.onlinePlayers())
		{
			if(i.hasPermission(Info.PERM_MONITOR))
			{
				gpd(i);
			}
		}
	}
	
	public void stop()
	{
		File fx = new File(new File(dataFolder, "cache"), "history.cch");
		verifyFile(fx);
		serialize(fx, tb);
		
		for(UUID i : pd.k())
		{
			save("playerdata", pd.get(i));
		}
	}
	
	public void save(String category, Configurable c)
	{
		try
		{
			File file = null;
			
			if(category == null)
			{
				file = new File(dataFolder, c.getCodeName() + ".yml");
			}
			
			else
			{
				file = new File(new File(dataFolder, category), c.getCodeName() + ".yml");
			}
			
			if(!file.exists() && file.isDirectory())
			{
				file.delete();
			}
			
			if(!file.exists())
			{
				c.onNewConfig();
				verifyFile(file);
			}
			
			saveFileConfig(file, c.getConfiguration().toYaml());
		}
		
		catch(Exception e)
		{
			f("============ DATA FAILURE ============");
			f("A file has failed to save it's data to");
			f("your server. If this persists, please ");
			f("contact support on spigot or github.  ");
			f("TF: " + ChatColor.YELLOW + category);
			f("CC: " + ChatColor.YELLOW + c.getCodeName());
			f("EX: " + ChatColor.YELLOW + e.getClass().getSimpleName());
			f("TG: " + ChatColor.YELLOW + e.getStackTrace()[0].getMethodName() + "(" + e.getStackTrace()[0].getLineNumber() + ")");
			f("============ ============ ============");
		}
	}
	
	public void load(String category, Configurable c)
	{
		try
		{
			File file = null;
			
			if(category == null)
			{
				file = new File(dataFolder, c.getCodeName() + ".yml");
			}
			
			else
			{
				file = new File(new File(dataFolder, category), c.getCodeName() + ".yml");
			}
			
			if(!file.exists() && file.isDirectory())
			{
				file.delete();
			}
			
			if(!file.exists())
			{
				c.onNewConfig();
				verifyFile(file);
				saveFileConfig(file, c.getConfiguration().toYaml());
			}
			
			loadConfigurableSettings(file, c);
			c.onReadConfig();
		}
		
		catch(Exception e)
		{
			f("============ DATA FAILURE ============");
			f("A file has failed to load it's data to");
			f("your server. If this persists, please ");
			f("contact support on spigot or github.  ");
			f("TF: " + ChatColor.YELLOW + category);
			f("CC: " + ChatColor.YELLOW + c.getCodeName());
			f("EX: " + ChatColor.YELLOW + e.getClass().getSimpleName());
			f("TG: " + ChatColor.YELLOW + e.getStackTrace()[0].getMethodName() + "(" + e.getStackTrace()[0].getLineNumber() + ")");
			f("TC: " + ChatColor.YELLOW + e.getStackTrace()[0].getClassName() + "(" + e.getStackTrace()[0].getLineNumber() + ")");
			f("============ ============ ============");
			e.printStackTrace();
		}
	}
	
	public void loadConfigurableSettings(File file, Configurable c)
	{
		c.onNewConfig();
		FileConfiguration fc = loadFileConfig(file);
		
		for(String i : fc.getKeys(true))
		{
			if(fc.isBoolean(i))
			{
				c.getConfiguration().set(i, fc.getBoolean(i));
			}
			
			if(fc.isDouble(i))
			{
				c.getConfiguration().set(i, fc.getDouble(i));
			}
			
			if(fc.isInt(i))
			{
				c.getConfiguration().set(i, fc.getInt(i));
			}
			
			if(fc.isString(i))
			{
				c.getConfiguration().set(i, fc.getString(i));
			}
			
			if(fc.isList(i))
			{
				c.getConfiguration().set(i, new GList<String>(fc.getStringList(i)));
			}
		}
		
		for(String i : c.getConfiguration().getData().k())
		{
			fc.set(i, c.getConfiguration().getAbstract(i));
		}
		
		saveFileConfig(file, fc);
	}
	
	public FileConfiguration loadFileConfig(File file)
	{
		FileConfiguration fc = new YamlConfiguration();
		
		try
		{
			fc.load(file);
		}
		
		catch(Exception e)
		{
			f("============ DATA FAILURE ============");
			f("A file has failed to load it's data to");
			f("your server. If this persists, please ");
			f("contact support on spigot or github.  ");
			f("TF: " + ChatColor.YELLOW + file.getName());
			f("EX: " + ChatColor.YELLOW + e.getClass().getSimpleName());
			f("TG: " + ChatColor.YELLOW + e.getStackTrace()[0].getMethodName() + "(" + e.getStackTrace()[0].getLineNumber() + ")");
			f("============ ============ ============");
			e.printStackTrace();
		}
		
		return fc;
	}
	
	public void saveFileConfig(File file, FileConfiguration fc)
	{
		try
		{
			fc.save(file);
		}
		
		catch(Exception e)
		{
			f("============ DATA FAILURE ============");
			f("A file has failed to save it's data to");
			f("your server. If this persists, please ");
			f("contact support on spigot or github.  ");
			f("TF: " + ChatColor.YELLOW + file.getName());
			f("EX: " + ChatColor.YELLOW + e.getClass().getSimpleName());
			f("TG: " + ChatColor.YELLOW + e.getStackTrace()[0].getMethodName() + "(" + e.getStackTrace()[0].getLineNumber() + ")");
			f("============ ============ ============");
		}
	}
	
	public void verifyFile(File file)
	{
		if(!file.exists())
		{
			file.getParentFile().mkdirs();
			
			try
			{
				file.createNewFile();
			}
			
			catch(Exception e)
			{
				f("============ DATA FAILURE ============");
				f("A file has failed to save it's data to");
				f("your server. If this persists, please ");
				f("contact support on spigot or github.  ");
				f("TF: " + ChatColor.YELLOW + file.getName());
				f("EX: " + ChatColor.YELLOW + e.getClass().getSimpleName());
				f("TG: " + ChatColor.YELLOW + e.getStackTrace()[0].getMethodName() + "(" + e.getStackTrace()[0].getLineNumber() + ")");
				f("============ ============ ============");
			}
		}
	}
	
	public void serialize(File file, Object obj)
	{
		try
		{
			verifyFile(file);
			FileOutputStream fos = new FileOutputStream(file);
			GZIPOutputStream gzo = new GZIPOutputStream(fos);
			ObjectOutputStream oos = new ObjectOutputStream(gzo);
			
			oos.writeObject(obj);
			oos.close();
		}
		
		catch(Exception e)
		{
			f("============ DATA FAILURE ============");
			f("A file has failed to save it's data to");
			f("your server. If this persists, please ");
			f("contact support on spigot or github.  ");
			f("TF: " + ChatColor.YELLOW + file.getName());
			f("EX: " + ChatColor.YELLOW + e.getClass().getSimpleName());
			f("TG: " + ChatColor.YELLOW + e.getStackTrace()[0].getMethodName() + "(" + e.getStackTrace()[0].getLineNumber() + ")");
			f("============ ============ ============");
		}
	}
	
	public Object deserialize(File file)
	{
		if(!file.exists())
		{
			return null;
		}
		
		ObjectInputStream ois = null;
		
		try
		{
			FileInputStream fis = new FileInputStream(file);
			GZIPInputStream gzi = new GZIPInputStream(fis);
			ois = new ObjectInputStream(gzi);
			
			Object o = ois.readObject();
			ois.close();
			
			return o;
		}
		
		catch(IOException e)
		{
			e.printStackTrace();
			try
			{
				ois.close();
			}
			
			catch(IOException e1)
			{
				
			}
		}
		
		catch(ClassNotFoundException e)
		{
			try
			{
				ois.close();
				file.delete();
				verifyFile(file);
			}
			
			catch(IOException e1)
			{
				
			}
		}
		
		return null;
	}
	
	public File getDataFolder()
	{
		return dataFolder;
	}
	
	public GTimeBank getTb()
	{
		return tb;
	}
}
