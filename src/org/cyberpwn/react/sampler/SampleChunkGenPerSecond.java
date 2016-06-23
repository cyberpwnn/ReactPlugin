package org.cyberpwn.react.sampler;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.ValueType;

public class SampleChunkGenPerSecond extends Sample implements Listener
{
	private int loadedTick;
	private GList<Integer> average;
	
	public SampleChunkGenPerSecond(SampleController sampleController)
	{
		super(sampleController, "SampleChunkGenPerSecond", ValueType.DOUBLE, "CGEN/S", "Chunk Generated Per Second");
		minDelay = 20;
		maxDelay = 20;
		idealDelay = 20;
		target = "Lower is better. However this will vary.";
		explaination = L.SAMPLER_CHUNK_GEN;
		
		average = new GList<Integer>();
	}
	
	public void onTick()
	{
		int chunksLoad = 0;
		
		average.add(loadedTick);
		loadedTick = 0;
		
		if(average.size() > 10)
		{
			average.remove(0);
		}
		
		for(Integer i : average)
		{
			chunksLoad += i;
		}
		
		chunksLoad /= average.size();
		
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
		return F.f(getValue().getInteger()) + ChatColor.DARK_RED + " CGEN/S";
	}
	
	public void onNewConfig()
	{
		super.onNewConfig();
		
		cc.set("component.limit", 42);
	}
	
	public ChatColor color()
	{
		return ChatColor.RED;
	}
	
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent e)
	{
		if(e.isNewChunk())
		{
			loadedTick++;
		}
	}
	
	public int getLoadedTick()
	{
		return loadedTick;
	}

	public GList<Integer> getAverage()
	{
		return average;
	}

	@Override
	public boolean isProblematic()
	{
		return value.getInteger() > cc.getInt("component.limit");
	}
	
	@Override
	public String getProblem()
	{
		return "Mass Chunk Gen";
	}
}
