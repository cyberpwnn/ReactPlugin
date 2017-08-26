package com.volmit.timings;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import com.volmit.react.util.GList;

public class StackTraceProbe
{
	private Thread t;
	private GList<PluginReport> plugins;
	
	public StackTraceProbe(Thread t)
	{
		this.t = t;
		plugins = new GList<PluginReport>();
	}
	
	public void scan()
	{
		for(StackTraceElement i : t.getStackTrace())
		{
			try
			{
				Plugin blame = getPlugin(i);
				
				if(blame != null)
				{
					boolean k = false;
					
					for(PluginReport j : plugins)
					{
						if(j.getPlugin().equals(blame))
						{
							k = true;
							j.getElements().add(i);
							break;
						}
					}
					
					if(!k)
					{
						plugins.add(new PluginReport(blame, new GList<StackTraceElement>().qadd(i)));
					}
				}
			}
			
			catch(ClassNotFoundException e)
			{
				
			}
		}
	}
	
	public Plugin getPlugin(StackTraceElement e) throws ClassNotFoundException
	{
		Class<?> clazz = Class.forName(e.getClassName());
		ClassLoader loader = clazz.getClassLoader();
		
		for(Plugin i : Bukkit.getPluginManager().getPlugins())
		{
			Class<?> pclazz = Class.forName(i.getDescription().getMain());
			ClassLoader ploader = pclazz.getClassLoader();
			
			if(ploader.equals(loader))
			{
				return i;
			}
		}
		
		return null;
	}
	
	public void clear()
	{
		plugins.clear();
	}
	
	public GList<PluginReport> getReports()
	{
		return plugins;
	}
}
