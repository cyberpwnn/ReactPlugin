package org.cyberpwn.react.sampler;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.cyberpwn.react.React;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.GMap;
import org.cyberpwn.react.util.HandledEvent;
import org.cyberpwn.react.util.InstabilityCause;
import org.cyberpwn.react.util.Lag;
import org.cyberpwn.react.util.ValueType;

public class SampleHopperTransfersPerSecond extends Sample implements Listener
{
	private int loadedTick;
	private GMap<Chunk, Integer> chunkMap;
	private Chunk chunk;
	private long last;
	
	public SampleHopperTransfersPerSecond(SampleController sampleController)
	{
		super(sampleController, "SampleHopperTransfersPerSecond", ValueType.DOUBLE, "HOP/S", "Hopper Transfers per Second");
		minDelay = 20;
		maxDelay = 20;
		idealDelay = 20;
		last = 0;
		target = "Lower is better. However this will vary.";
		explaination = L.SAMPLER_WORLD_HOPPER;
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
		return F.f(getValue().getInteger()) + ChatColor.AQUA + " HOP/S";
	}
	
	@Override
	public ChatColor color()
	{
		return ChatColor.BLUE;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockFromTo(InventoryMoveItemEvent e)
	{
		new HandledEvent()
		{
			
			@Override
			public void execute()
			{
				loadedTick++;
				
				if(e.getSource().getHolder() instanceof Hopper)
				{
					Block hopper = ((Hopper) e.getSource().getHolder()).getBlock();
					Lag.report(hopper.getLocation(), InstabilityCause.HOPPER, 67);
				}
			}
		};
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
		return ChatColor.DARK_BLUE;
	}
}
