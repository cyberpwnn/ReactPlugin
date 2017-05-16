package org.cyberpwn.react.sampler;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.ValueType;

public class SampleTiles extends Sample implements Listener
{
	private int entities;
	
	public SampleTiles(SampleController sampleController)
	{
		super(sampleController, "SampleTiles", ValueType.DOUBLE, "TLES", "Sample Tile Entities");
		minDelay = 100;
		maxDelay = 100;
		idealDelay = 100;
		entities = 0;
		target = "Lower is better. However this will vary.";
		explaination = L.SAMPLER_WORLD_TILEENTITIES;
	}
	
	@Override
	public void onTick()
	{
		getValue().setNumber(entities);
		entities = 0;
		
		for(World i : sampleController.getReact().getServer().getWorlds())
		{
			for(Chunk c : i.getLoadedChunks())
			{
				entities += c.getTileEntities().length;
			}
		}
	}
	
	@Override
	public void onStart()
	{
		value.setNumber(0);
	}
	
	@Override
	public String formatted(boolean acc)
	{
		return F.f(getValue().getInteger()) + ChatColor.AQUA + " Tiles";
	}
	
	@Override
	public ChatColor color()
	{
		return ChatColor.BLUE;
	}
	
	@Override
	public ChatColor darkColor()
	{
		return ChatColor.DARK_BLUE;
	}
}
