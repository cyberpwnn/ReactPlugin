package org.cyberpwn.react.cluster;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.GMap;

public class ClusterConfig
{
	public enum ClusterDataType
	{
		INTEGER, DOUBLE, BOOLEAN, STRING, STRING_LIST;
	}
	
	private GMap<String, Cluster> data;
	private GMap<String, String> comments;
	
	public ClusterConfig()
	{
		this.data = new GMap<String, Cluster>();
		this.comments = new GMap<String, String>();
	}
	
	public ClusterConfig copy()
	{
		return new ClusterConfig().qset(this.data);
	}
	
	public ClusterConfig qset(GMap<String, Cluster> data)
	{
		this.data = data;
		return this;
	}
	
	public void set(FileConfiguration fc)
	{
		for(String i : fc.getKeys(true))
		{
			if(fc.isBoolean(i))
			{
				set(i, fc.getBoolean(i));
			}
			
			else if(fc.isString(i))
			{
				set(i, fc.getString(i));
			}
			
			else if(fc.isInt(i))
			{
				set(i, fc.getInt(i));
			}
			
			else if(fc.isDouble(i))
			{
				set(i, fc.isDouble(i));
			}
			
			else if(fc.isLong(i))
			{
				set(i, fc.getLong(i));
			}
			
			else if(fc.isList(i))
			{
				List<?> o = fc.getList(i);
				GList<String> list = new GList<String>();
				
				for(Object j : o)
				{
					list.add(j.toString());
				}
				
				set(i, list);
			}
		}
	}
	
	public FileConfiguration toYaml()
	{
		FileConfiguration fc = new YamlConfiguration();
		
		for(String i : data.keySet())
		{
			fc.set(i, getAbstract(i));
		}
		
		return fc;
	}
	
	public void set(String key, int value)
	{
		data.put(key, new ClusterInteger(key, value));
	}
	
	public void set(String key, double value)
	{
		data.put(key, new ClusterDouble(key, value));
	}
	
	public void set(String key, boolean value)
	{
		data.put(key, new ClusterBoolean(key, value));
	}
	
	public void set(String key, String value)
	{
		data.put(key, new ClusterString(key, value));
	}
	
	public void set(String key, GList<String> value)
	{
		data.put(key, new ClusterStringList(key, value));
	}
	
	public void setComment(String key, String comment)
	{
		comments.put(key, comment);
	}
	
	public void set(String key, int value, String comment)
	{
		Cluster c = new ClusterInteger(key, value);
		setComment(key, comment);
		data.put(key, c);
	}
	
	public void set(String key, double value, String comment)
	{
		Cluster c = new ClusterDouble(key, value);
		setComment(key, comment);
		data.put(key, c);
		}
	
	public void set(String key, boolean value, String comment)
	{
		Cluster c = new ClusterBoolean(key, value);
		setComment(key, comment);
		data.put(key, c);
	}
	
	public void set(String key, String value, String comment)
	{
		Cluster c = new ClusterString(key, value);
		setComment(key, comment);
		data.put(key, c);
	}
	
	public void set(String key, GList<String> value, String comment)
	{
		Cluster c = new ClusterStringList(key, value);
		setComment(key, comment);
		data.put(key, c);
	}
	
	public Boolean hasComment(String key)
	{
		return comments.containsKey(key);
	}
	
	public GMap<String, String> getComments()
	{
		return comments;
	}

	public GList<String> getComment(String key)
	{
		if(hasComment(key))
		{
			GList<String> g = new GList<String>();
			
			if(comments.get(key).contains("\n"))
			{
				for(String i : comments.get(key).split("\n"))
				{
					g.add(i);
				}
			}
			
			else
			{
				g.add(comments.get(key));
			}
			
			return g;
		}
		
		return new GList<String>();
	}
	
	public Boolean getBoolean(String key)
	{
		if(contains(key) && getType(key).equals(ClusterDataType.BOOLEAN))
		{
			return ((ClusterBoolean) get(key)).get();
		}
		
		return null;
	}
	
	public Integer getInt(String key)
	{
		if(contains(key) && getType(key).equals(ClusterDataType.INTEGER))
		{
			return ((ClusterInteger) get(key)).get();
		}
		
		return null;
	}
	
	public Double getDouble(String key)
	{
		if(contains(key) && getType(key).equals(ClusterDataType.DOUBLE))
		{
			return ((ClusterDouble) get(key)).get();
		}
		
		return null;
	}
	
	public String getString(String key)
	{
		if(contains(key) && getType(key).equals(ClusterDataType.STRING))
		{
			return ((ClusterString) get(key)).get();
		}
		
		return null;
	}
	
	public GList<String> getStringList(String key)
	{
		if(contains(key) && getType(key).equals(ClusterDataType.STRING_LIST))
		{
			return ((ClusterStringList) get(key)).get();
		}
		
		return null;
	}
	
	public boolean contains(String key)
	{
		return data.containsKey(key) && data.get(key) != null;
	}
	
	public void remove(String key)
	{
		data.remove(key);
	}
	
	public Object getAbstract(String key)
	{
		if(getType(key).equals(ClusterDataType.BOOLEAN))
		{
			return getBoolean(key);
		}
		
		else if(getType(key).equals(ClusterDataType.INTEGER))
		{
			return getInt(key);
		}
		
		else if(getType(key).equals(ClusterDataType.DOUBLE))
		{
			return getDouble(key);
		}
		
		else if(getType(key).equals(ClusterDataType.STRING))
		{
			return getString(key);
		}
		
		else if(getType(key).equals(ClusterDataType.STRING_LIST))
		{
			return getStringList(key);
		}
		
		return null;
	}
	
	public Cluster get(String key)
	{
		return data.get(key);
	}
	
	public ClusterDataType getType(String key)
	{
		return get(key).getType();
	}
	
	public GMap<String, Cluster> getData()
	{
		return data;
	}
	
	public void setData(GMap<String, Cluster> data)
	{
		this.data = data;
	}
}
