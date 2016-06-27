package org.cyberpwn.react.sampler;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.ValueType;

public class SampleChunkLoadPerSecond extends Sample implements Listener
{
	private int loadedTick;
	private GList<Double> average;
	
	public SampleChunkLoadPerSecond(SampleController sampleController)
	{
		super(sampleController, "SampleChunkLoadPerSecond", ValueType.DOUBLE, "CHK/S", "Chunk Loads Per Second");
		minDelay = 20;
		maxDelay = 20;
		idealDelay = 20;
		target = "Lower is better. However this will vary.";
		explaination = L.SAMPLER_CHUNK_LOAD;
		
		average = new GList<Double>();
	}
	
	public void onTick()
	{
		double chunksLoad = 0;
		
		average.add((double) loadedTick);
		loadedTick = 0;
		
		if(average.size() > 10)
		{
			average.remove(0);
		}
		
		for(Double i : average)
		{
			chunksLoad += i;
		}
		
		chunksLoad /= (double) average.size();
		
		value.setNumber(chunksLoad);
	}
	
	public void onStart()
	{
		sampleController.getReact().register(this);
		value.setNumber(1);
	}
	
	public void onStop()
	{
		sampleController.getReact().unRegister(this);
	}
	
	public String formatted()
	{
		return F.f(getValue().getInteger()) + ChatColor.DARK_RED + " CHK/S";
	}
	
	public ChatColor color()
	{
		return Info.COLOR_ERR;
	}
	
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent e)
	{
		loadedTick++;
	}
	
	public int getLoadedTick()
	{
		return loadedTick;
	}
	
	public GList<Double> getAverage()
	{
		return average;
	}
}
