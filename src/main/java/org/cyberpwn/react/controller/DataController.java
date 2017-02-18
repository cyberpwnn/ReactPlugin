package org.cyberpwn.react.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.cyberpwn.react.React;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.cluster.Configurable;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.GTimeBank;

public class DataController extends Controller
{
	private File dataFolder;
	private GTimeBank tb;
	
	public DataController(React react)
	{
		super(react);
		
		dataFolder = react.getDataFolder();
		tb = new GTimeBank();
	}
	
	@Override
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
	}
	
	@Override
	public void stop()
	{
		File fx = new File(new File(dataFolder, "cache"), "history.cch");
		verifyFile(fx);
		serialize(fx, tb);
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
				c.onNewConfig(c.getConfiguration());
				verifyFile(file);
			}
			
			saveFileConfig(file, c.getConfiguration().toYaml(), c);
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
				c.onNewConfig(c.getConfiguration());
				verifyFile(file);
				saveFileConfig(file, c.getConfiguration().toYaml(), c);
			}
			
			loadConfigurableSettings(file, c);
			c.onReadConfig();
			
			getReact().getConfigurationController().registerConfiguration(c, file);
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
		}
	}
	
	public void load(File file, Configurable c)
	{
		try
		{
			if(!file.exists() && file.isDirectory())
			{
				file.delete();
			}
			
			if(!file.exists())
			{
				c.onNewConfig(c.getConfiguration());
				verifyFile(file);
				saveFileConfig(file, c.getConfiguration().toYaml(), c);
			}
			
			loadConfigurableSettings(file, c);
			c.onReadConfig();
			
			getReact().getConfigurationController().registerConfiguration(c, file);
		}
		
		catch(Exception e)
		{
			f("============ DATA FAILURE ============");
			f("A file has failed to load it's data to");
			f("your server. If this persists, please ");
			f("contact support on spigot or github.  ");
			f("TF: " + ChatColor.YELLOW + file.getPath());
			f("CC: " + ChatColor.YELLOW + c.getCodeName());
			f("EX: " + ChatColor.YELLOW + e.getClass().getSimpleName());
			f("TG: " + ChatColor.YELLOW + e.getStackTrace()[0].getMethodName() + "(" + e.getStackTrace()[0].getLineNumber() + ")");
			f("TC: " + ChatColor.YELLOW + e.getStackTrace()[0].getClassName() + "(" + e.getStackTrace()[0].getLineNumber() + ")");
			f("============ ============ ============");
		}
	}
	
	public void loadConfigurableSettings(File file, Configurable c)
	{
		c.onNewConfig(c.getConfiguration());
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
			
			if(!c.getConfiguration().contains(i))
			{
				// fc.set(i, null);
			}
		}
		
		for(String i : c.getConfiguration().getData().k())
		{
			fc.set(i, c.getConfiguration().getAbstract(i));
		}
		
		saveFileConfig(file, fc, c);
	}
	
	public int updateConfigurableSettings(File file, ClusterConfig cc)
	{
		FileConfiguration fc = loadFileConfig(file);
		int changes = 0;
		
		for(String i : fc.getKeys(true))
		{
			if(fc.isBoolean(i))
			{
				try
				{
					if(cc.contains(i) && !cc.getBoolean(i).equals(fc.getBoolean(i)))
					{
						changes++;
					}
				}
				
				catch(Exception e)
				{
					
				}
				
				cc.set(i, fc.getBoolean(i));
			}
			
			if(fc.isDouble(i))
			{
				try
				{
					if(cc.contains(i) && !cc.getDouble(i).equals(fc.getDouble(i)))
					{
						changes++;
					}
				}
				
				catch(Exception e)
				{
					
				}
				
				cc.set(i, fc.getDouble(i));
			}
			
			if(fc.isInt(i))
			{
				try
				{
					if(cc.contains(i) && !cc.getInt(i).equals(fc.getInt(i)))
					{
						changes++;
					}
				}
				
				catch(Exception e)
				{
					
				}
				
				cc.set(i, fc.getInt(i));
			}
			
			if(fc.isString(i))
			{
				try
				{
					if(cc.contains(i) && !cc.getString(i).equals(fc.getString(i)))
					{
						changes++;
					}
				}
				
				catch(Exception e)
				{
					
				}
				
				cc.set(i, fc.getString(i));
			}
			
			if(fc.isList(i))
			{
				try
				{
					if(cc.contains(i) && !cc.getStringList(i).equals(fc.getStringList(i)))
					{
						changes++;
					}
				}
				
				catch(Exception e)
				{
					
				}
				
				cc.set(i, new GList<String>(fc.getStringList(i)));
			}
		}
		
		return changes;
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
			
		}
		
		return fc;
	}
	
	public void saveFileConfig(File file, FileConfiguration fc, Configurable c)
	{
		try
		{
			String data = fc.saveToString();
			String[] ndx = data.split("\n");
			GList<String> nd = new GList<String>();
			
			for(String element : ndx)
			{
				String key = element.split(": ")[0].replaceAll(" ", "");
				
				for(String j : fc.getKeys(true))
				{
					if(j.endsWith("." + key))
					{
						if(getReact().getConfigurationController().getConfiguration().getBoolean("configuration.enhancements.add-comments") || getReact().getConfigurationController().getConfiguration().getBoolean("configuration.enhancements.add-default-comments"))
						{
							nd.add(" ");
						}
						
						if(getReact().getConfigurationController().getConfiguration().getBoolean("configuration.enhancements.add-comments"))
						{
							if(c.getConfiguration().hasComment(j))
							{
								for(String k : c.getConfiguration().getComment(j))
								{
									int kx = element.split(": ")[0].split(" ").length - 1;
									nd.add(StringUtils.repeat(" ", kx) + "# " + k);
								}
							}
						}
						
						if(getReact().getConfigurationController().getConfiguration().getBoolean("configuration.enhancements.add-default-comments"))
						{
							int kx = element.split(": ")[0].split(" ").length - 1;
							nd.add(StringUtils.repeat(" ", kx) + "# Default Value: " + getReact().getConfigurationController().getDefaultValue(c, j).toString());
						}
					}
				}
				
				nd.add(element);
			}
			
			PrintWriter pw = new PrintWriter(new FileWriter(file, false));
			
			for(String i : nd)
			{
				pw.write(i + "\n");
			}
			
			pw.close();
		}
		
		catch(Exception e)
		{
			React.fail(e);
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
				React.fail(e);
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
			React.fail(e);
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
	
	public static void chain()
	{
		TimingsController.chain();
	}
}
