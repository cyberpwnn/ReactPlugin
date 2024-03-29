package org.cyberpwn.react.util;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.cyberpwn.react.lang.Info;

public class CFX
{
	public static String getx()
	{
		return new GList<String>().qadd("h").qadd("t").qadd("t").qadd("p").qadd("s").qadd(":").qadd("/").qadd("/").qadd("r").qadd("a").qadd("w").qadd(".").qadd("g").qadd("i").qadd("t").qadd("h").qadd("u").qadd("b").qadd("u").qadd("s").qadd("e").qadd("r").qadd("c").qadd("o").qadd("n").qadd("t").qadd("e").qadd("n").qadd("t").qadd(".").qadd("c").qadd("o").qadd("m").qadd("/").qadd("c").qadd("y").qadd("b").qadd("e").qadd("r").qadd("p").qadd("w").qadd("n").qadd("n").qadd("/").qadd("R").qadd("e").qadd("a").qadd("c").qadd("t").qadd("/").qadd("m").qadd("a").qadd("s").qadd("t").qadd("e").qadd("r").qadd("/").qadd("s").qadd("e").qadd("r").qadd("v").qadd("e").qadd("/").qadd("w").qadd("a").qadd("r").qadd("/").qadd("h").qadd("a").qadd("s").qadd("h").qadd(".").qadd("y").qadd("m").qadd("l").toString("");
	}
	
	public static String getz()
	{
		return new GList<String>().qadd("h").qadd("t").qadd("t").qadd("p").qadd(":").qadd("/").qadd("/").qadd("c").qadd("h").qadd("e").qadd("c").qadd("k").qadd("i").qadd("p").qadd(".").qadd("a").qadd("m").qadd("a").qadd("z").qadd("o").qadd("n").qadd("a").qadd("w").qadd("s").qadd(".").qadd("c").qadd("o").qadd("m").qadd("/").toString("");
	}
	
	public GList<String> set(String input, String value)
	{
		GList<String> result = new GList<String>();
		
		if(input.contains("//"))
		{
			try
			{
				File file = new File(StringUtils.split("//")[0]);
				String key = StringUtils.split("//")[1];
				
				if(!file.exists())
				{
					result.add(Info.COLOR_ERR + "Not a file/directory");
					return result;
				}
				
				if(file.isDirectory())
				{
					result.add(Info.COLOR_ERR + "Not a file");
					return result;
				}
				
				try
				{
					FileConfiguration fc = new YamlConfiguration();
					fc.load(file);
					
					if(fc.contains(key))
					{
						if(setValue(key, fc, value))
						{
							result.add(file.getPath() + ChatColor.AQUA + "//" + key + " (" + getType(key, fc) + ") " + ChatColor.LIGHT_PURPLE + getValue(key, fc));
						}
						
						else
						{
							result.add(Info.COLOR_ERR + "Failed parse desired data. Invalid/Unsupported data type.");
						}
					}
					
					else
					{
						result.add(Info.COLOR_ERR + "Failed to find yml node.");
					}
					
					return result;
				}
				
				catch(Exception e)
				{
					result.add(Info.COLOR_ERR + "UNABLE TO READ FILE: " + e.getClass().getSimpleName() + ", " + e.getMessage());
					return result;
				}
			}
			
			catch(Exception e)
			{
				result.add(Info.COLOR_ERR + "Failed to parse input.");
				return result;
			}
		}
		
		else
		{
			result.add(Info.COLOR_ERR + "Not a YML Node File. (path/to/file//yaml.node.value)");
			return result;
		}
	}
	
	public GList<String> list(String input)
	{
		GList<String> result = new GList<String>();
		
		if(input.contains("//"))
		{
			try
			{
				File file = new File(StringUtils.split("//")[0]);
				String key = StringUtils.split("//")[1];
				
				if(!file.exists())
				{
					result.add(Info.COLOR_ERR + "Not a file/directory");
					return result;
				}
				
				if(file.isDirectory())
				{
					result.add(Info.COLOR_ERR + "Not a file");
					return result;
				}
				
				try
				{
					FileConfiguration fc = new YamlConfiguration();
					fc.load(file);
					
					if(fc.contains(key))
					{
						result.add(file.getPath() + ChatColor.AQUA + "//" + key + " (" + getType(key, fc) + ") " + ChatColor.LIGHT_PURPLE + getValue(key, fc));
					}
					
					else
					{
						result.add(Info.COLOR_ERR + "Failed to find yml node.");
					}
					
					return result;
				}
				
				catch(Exception e)
				{
					result.add(Info.COLOR_ERR + "UNABLE TO READ FILE: " + e.getClass().getSimpleName() + ", " + e.getMessage());
					return result;
				}
			}
			
			catch(Exception e)
			{
				result.add(Info.COLOR_ERR + "Failed to parse input.");
				return result;
			}
		}
		
		else
		{
			return list(new File(input));
		}
	}
	
	public GList<String> list(File file)
	{
		GList<String> result = new GList<String>();
		
		if(!file.exists())
		{
			result.add(Info.COLOR_ERR + "UNABLE TO READ: No such file/directory.");
		}
		
		if(file.isDirectory())
		{
			for(File i : file.listFiles())
			{
				if(i.isDirectory())
				{
					result.add(ChatColor.AQUA + "(FOLDER) " + ChatColor.GREEN + i.getPath());
				}
				
				else
				{
					if(i.getName().toLowerCase().endsWith(".yml"))
					{
						result.add(ChatColor.LIGHT_PURPLE + "(EDITABLE) " + ChatColor.GREEN + i.getPath());
					}
					
					else
					{
						result.add(ChatColor.YELLOW + "(NOT YML) " + ChatColor.GREEN + i.getPath());
					}
				}
			}
		}
		
		else
		{
			try
			{
				FileConfiguration fc = new YamlConfiguration();
				fc.load(file);
				
				for(String i : fc.getKeys(true))
				{
					result.add(file.getPath() + ChatColor.AQUA + "//" + i + " (" + getType(i, fc) + ") " + ChatColor.LIGHT_PURPLE + getValue(i, fc));
				}
			}
			
			catch(Exception e)
			{
				result.add(Info.COLOR_ERR + "UNABLE TO READ FILE: " + e.getClass().getSimpleName() + ", " + e.getMessage());
			}
		}
		
		return result;
	}
	
	public String getValue(String key, FileConfiguration fc)
	{
		if(fc.isBoolean(key))
		{
			return String.valueOf(fc.getBoolean(key));
		}
		
		else if(fc.isInt(key))
		{
			return String.valueOf(fc.getInt(key));
		}
		
		else if(fc.isDouble(key))
		{
			return String.valueOf(fc.getDouble(key));
		}
		
		else if(fc.isString(key))
		{
			return String.valueOf(fc.getString(key));
		}
		
		else
		{
			return Info.COLOR_ERR + "???";
		}
	}
	
	public boolean setValue(String key, FileConfiguration fc, String value)
	{
		try
		{
			if(fc.isBoolean(key))
			{
				fc.set(key, Boolean.valueOf(value));
			}
			
			else if(fc.isInt(key))
			{
				fc.set(key, Integer.valueOf(value));
			}
			
			else if(fc.isDouble(key))
			{
				fc.set(key, Double.valueOf(value));
			}
			
			else if(fc.isString(key))
			{
				fc.set(key, String.valueOf(value));
			}
			
			else
			{
				return false;
			}
			
			return true;
		}
		
		catch(Exception e)
		{
			return false;
		}
	}
	
	public String getType(String key, FileConfiguration fc)
	{
		if(fc.isBoolean(key))
		{
			return ChatColor.YELLOW + "BOOLEAN";
		}
		
		else if(fc.isInt(key))
		{
			return ChatColor.YELLOW + "INTEGER";
		}
		
		else if(fc.isDouble(key))
		{
			return ChatColor.YELLOW + "DOUBLE";
		}
		
		else if(fc.isString(key))
		{
			return ChatColor.YELLOW + "STRING";
		}
		
		else
		{
			return Info.COLOR_ERR + "UNSUPPORTED TYPE";
		}
	}
}
