package org.cyberpwn.react.sampler;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.cyberpwn.react.cluster.ClusterConfig;
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
	
	@Override
	public void onTick()
	{
		new ASYNC()
		{
			@Override
			public void async()
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
		return F.f(getValue().getInteger()) + ChatColor.DARK_RED + " CGEN/S";
	}
	
	@Override
	public void onNewConfig(ClusterConfig cc)
	{
		super.onNewConfig(cc);
		
		cc.set("component.limit", 42);
	}
	
	@Override
	public ChatColor color()
	{
		return Info.COLOR_ERR;
	}
	
	@Override
	public ChatColor darkColor()
	{
		return ChatColor.DARK_RED;
	}
	
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent e)
	{
		new HandledEvent()
		{
			
			@Override
			public void execute()
			{
				if(e.isNewChunk())
				{
					Lag.report(e.getChunk().getBlock(8, 128, 8).getLocation(), InstabilityCause.CHUNKS, 1000);
					loadedTick++;
				}
			}
		};
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
