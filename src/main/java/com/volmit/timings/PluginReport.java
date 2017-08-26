package com.volmit.timings;

import org.bukkit.plugin.Plugin;
import com.volmit.react.util.GList;

public class PluginReport
{
	private Plugin plugin;
	private GList<StackTraceElement> elements;
	
	public PluginReport(Plugin plugin, GList<StackTraceElement> elements)
	{
		this.plugin = plugin;
		this.elements = elements;
	}
	
	public Plugin getPlugin()
	{
		return plugin;
	}
	
	public void setPlugin(Plugin plugin)
	{
		this.plugin = plugin;
	}
	
	public GList<StackTraceElement> getElements()
	{
		return elements;
	}
	
	public void setElements(GList<StackTraceElement> elements)
	{
		this.elements = elements;
	}
}
