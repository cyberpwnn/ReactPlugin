package org.cyberpwn.react.sampler;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.ASYNC;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.HandledEvent;
import org.cyberpwn.react.util.InstabilityCause;
import org.cyberpwn.react.util.Lag;
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
	
	@Override
	public void onTick()
	{
		new ASYNC()
		{
			@Override
			public void async()
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
		};
	}
	
	@Override
	public void onStart()
	{
		sampleController.getReact().register(this);
		value.setNumber(1);
	}
	
	@Override
	public void onStop()
	{
		sampleController.getReact().unRegister(this);
	}
	
	@Override
	public String formatted(boolean acc)
	{
		return F.f(getValue().getInteger()) + ChatColor.DARK_RED + " CHK/S";
	}
	
	@Override
	public ChatColor color()
	{
		return Info.COLOR_ERR;
	}
	
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent e)
	{
		new HandledEvent()
		{
			
			@Override
			public void execute()
			{
				loadedTick++;
				Lag.report(e.getChunk().getBlock(8, 128, 8).getLocation(), InstabilityCause.CHUNKS, 500);
			}
		};
	}
	
	public int getLoadedTick()
	{
		return loadedTick;
	}
	
	public GList<Double> getAverage()
	{
		return average;
	}
	
	@Override
	public ChatColor darkColor()
	{
		return ChatColor.DARK_RED;
	}
}
