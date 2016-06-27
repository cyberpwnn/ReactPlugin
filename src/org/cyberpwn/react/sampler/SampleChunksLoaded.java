package org.cyberpwn.react.sampler;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.ValueType;

public class SampleChunksLoaded extends Sample implements Listener
{
	public SampleChunksLoaded(SampleController sampleController)
	{
		super(sampleController, "SampleChunksLoaded", ValueType.DOUBLE, "CHUNKS", "Count of chunks loaded.");
		minDelay = 5;
		maxDelay = 5;
		idealDelay = 5;
		target = "Lower is better. However this will vary.";
		explaination = L.SAMPLER_CHUNK_LOADED;
	}
	
	public void onTick()
	{
		
	}
	
	public void onStart()
	{
		int chunksLoaded = 0;
		
		for(World i : sampleController.getReact().getServer().getWorlds())
		{
			chunksLoaded += i.getLoadedChunks().length;
		}
		
		value.setNumber(chunksLoaded);
		getSampleController().getReact().register(this);
	}
	
	public String formatted()
	{
		return F.f(getValue().getInteger()) + " CHKS";
	}
	
	public ChatColor color()
	{
		return Info.COLOR_ERR;
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onChunk(ChunkLoadEvent e)
	{
		value.setNumber(value.getInteger() + 1);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onChunk(ChunkUnloadEvent e)
	{
		value.setNumber(value.getInteger() - 1);
	}
}
