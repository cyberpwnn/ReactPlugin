package org.cyberpwn.react.sampler;

import org.bukkit.ChatColor;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.PlayerCache;
import org.cyberpwn.react.util.ValueType;

public class SampleMemoryPerPlayer extends Sample
{
	private PlayerCache cache;
	
	public SampleMemoryPerPlayer(SampleController sampleController)
	{
		super(sampleController, "SampleMemoryPerPlayer", ValueType.DOUBLE, "MB/P", "Ram Per Player");
		minDelay = 100;
		maxDelay = 100;
		idealDelay = 100;
		cache = new PlayerCache();
		target = "Lower is better. However this will vary.";
		explaination = L.SAMPLER_MEMORY_PLAYERS;
	}
	
	public void onTick()
	{
		cache.sample(sampleController.getReact());
		
		value.setNumber(cache.pull());
	}
	
	public void onStart()
	{
		sampleController.getReact().getDataController().load("cache", cache);
		value.setNumber(1);
	}
	
	public void onStop()
	{
		sampleController.getReact().getDataController().save("cache", cache);
	}
	
	public long base()
	{
		return (long) cache.pull(0);
	}
	
	public double getPercent()
	{
		return cache.pull() / cache.maxEver();
	}
	
	public double getPercentOfMem()
	{
		return cache.pull() / getSampleController().getSampleMemoryUsed().getValue().getLong();
	}
	
	public String formatted(boolean acc)
	{
		return F.f(getValue().getInteger(), 1) + ChatColor.DARK_RED + " MB/P";
	}
	
	public ChatColor color()
	{
		return Info.COLOR_ERR;
	}
	
	public ChatColor darkColor()
	{
		return ChatColor.DARK_RED;
	}
}
