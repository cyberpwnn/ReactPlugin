package org.cyberpwn.react.sampler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.ValueType;

public class SampleMonitoredPlugins extends Sample
{
	public SampleMonitoredPlugins(SampleController sampleController)
	{
		super(sampleController, "SampleMonitoredPlugins", ValueType.DOUBLE, "PLG", "Monitored Plugins");
		
		minDelay = 200;
		maxDelay = 200;
		idealDelay = 200;
		target = "This notes how many plugins react is monitoring";
		explaination = L.SAMPLER_GENERAL_PLUGINS;
	}
	
	public void onTick()
	{
		getValue().setNumber(Bukkit.getPluginManager().getPlugins().length);
	}
	
	public void onStart()
	{
		value.setNumber(0);
	}
	
	public String formatted(boolean acc)
	{
		return F.f(getValue().getInteger()) + " PLG";
	}
	
	public ChatColor color()
	{
		return ChatColor.AQUA;
	}
}
