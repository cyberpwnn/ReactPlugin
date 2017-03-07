package org.cyberpwn.react.sampler;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.cyberpwn.react.React;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.GMap;
import org.cyberpwn.react.util.InstabilityCause;
import org.cyberpwn.react.util.Lag;
import org.cyberpwn.react.util.ValueType;

public class SampleRedstoneUpdatesPerSecond extends Sample implements Listener
{
	private int loadedTick;
	private GMap<Chunk, Integer> chunkMap;
	private Chunk chunk;
	private long last;
	
	public SampleRedstoneUpdatesPerSecond(SampleController sampleController)
	{
		super(sampleController, "SampleRedstoneUpdatesPerSecond", ValueType.DOUBLE, "RED/S", "Redstone Updates per Second");
		minDelay = 20;
		maxDelay = 20;
		idealDelay = 20;
		last = 0;
		target = "Lower is better. However this will vary.";
		explaination = L.SAMPLER_WORLD_REDSTONE;
		chunkMap = new GMap<Chunk, Integer>();
		chunk = null;
	}
	
	@Override
	public void onTick()
	{
		long dur = React.instance().getSampleController().getTick() - last;
		last = React.instance().getSampleController().getTick();
		value.setNumber(loadedTick / ((dur / 20) + 1));
		loadedTick = 0;
		
		int m = 0;
		Chunk chunk = null;
		
		for(Chunk i : chunkMap.k())
		{
			if(chunkMap.get(i) > m)
			{
				m = chunkMap.get(i);
				chunk = i;
			}
		}
		
		this.chunk = chunk;
		chunkMap.clear();
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
		return F.f(getValue().getInteger()) + ChatColor.DARK_RED + " RED/S";
	}
	
	@Override
	public ChatColor color()
	{
		return Info.COLOR_ERR;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockFromTo(BlockRedstoneEvent e)
	{
		loadedTick++;
		Lag.report(e.getBlock().getLocation(), InstabilityCause.REDSTONE, 200);
		
		if(!chunkMap.containsKey(e.getBlock().getChunk()))
		{
			chunkMap.put(e.getBlock().getChunk(), 0);
		}
		
		chunkMap.put(e.getBlock().getChunk(), chunkMap.get(e.getBlock().getChunk()) + 1);
	}
	
	public int getLoadedTick()
	{
		return loadedTick;
	}
	
	public GMap<Chunk, Integer> getChunkMap()
	{
		return chunkMap;
	}
	
	public Chunk getChunk()
	{
		return chunk;
	}
	
	@Override
	public ChatColor darkColor()
	{
		return ChatColor.DARK_RED;
	}
}
