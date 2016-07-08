package org.cyberpwn.react.sampler;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.ValueType;

public class SampleTNTPerSecond extends Sample implements Listener
{
	private int loadedTick;
	private GList<Integer> average;
	
	public SampleTNTPerSecond(SampleController sampleController)
	{
		super(sampleController, "SampleTNTPerSecond", ValueType.DOUBLE, "TNT/S", "TNT Primes Per Second");
		minDelay = 20;
		maxDelay = 20;
		idealDelay = 20;
		target = "Lower is better. However this will vary.";
		explaination = L.SAMPLER_WORLD_TNT;
		
		average = new GList<Integer>();
	}
	
	public void onTick()
	{
		int tnt = 0;
		
		average.add(loadedTick);
		loadedTick = 0;
		
		if(average.size() > 10)
		{
			average.remove(0);
		}
		
		for(Integer i : average)
		{
			tnt += i;
		}
		
		tnt /= average.size();
		
		value.setNumber(tnt);
	}
	
	public void onStart()
	{
		sampleController.getReact().register(this);
		value.setNumber(0);
	}
	
	public void onStop()
	{
		sampleController.getReact().unRegister(this);
	}
	
	public String formatted(boolean acc)
	{
		return F.f(getValue().getInteger()) + ChatColor.DARK_RED + " TNT/S";
	}
	
	public ChatColor color()
	{
		return Info.COLOR_ERR;
	}
	
	@EventHandler
	public void onChunkLoad(ExplosionPrimeEvent e)
	{
		loadedTick++;
	}
	
	public ChatColor darkColor()
	{
		return ChatColor.DARK_RED;
	}
}
