package org.cyberpwn.react.sampler;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.object.ValueType;
import org.cyberpwn.react.util.F;

public class SampleChunksLoaded extends Sample
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
		int chunksLoaded = 0;
		
		for(World i : sampleController.getReact().getServer().getWorlds())
		{
			chunksLoaded += i.getLoadedChunks().length;
		}
		
		value.setNumber(chunksLoaded);
	}
	
	public void onStart()
	{
		value.setNumber(1);
	}
	
	public String formatted()
	{
		return F.f(getValue().getInteger()) + " CHKS";
	}
	
	public ChatColor color()
	{
		return ChatColor.RED;
	}
}
