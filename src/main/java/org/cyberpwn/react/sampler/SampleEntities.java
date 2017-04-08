package org.cyberpwn.react.sampler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.ASYNC;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.ValueType;

public class SampleEntities extends Sample implements Listener
{
	private int entities;
	
	public SampleEntities(SampleController sampleController)
	{
		super(sampleController, "SampleEntities", ValueType.DOUBLE, "ENTS", "Sample Entities");
		minDelay = 100;
		maxDelay = 100;
		idealDelay = 100;
		entities = 0;
		target = "Lower is better. However this will vary.";
		explaination = L.SAMPLER_WORLD_ENTITIES;
	}
	
	@Override
	public void onTick()
	{
		new ASYNC()
		{
			@Override
			public void async()
			{
				getValue().setNumber(entities);
				entities = 0;
				final int[] cpt = new int[] {0};
				
				for(World i : sampleController.getReact().getServer().getWorlds())
				{
					entities += i.getEntities().size();
					cpt[0] += i.getLoadedChunks().length;
				}
				
				cpt[0] /= (idealDelay + 1);
			}
		};
	}
	
	@Override
	public void onStart()
	{
		int ents = 0;
		
		for(World i : Bukkit.getWorlds())
		{
			ents += i.getEntities().size();
		}
		
		value.setNumber(ents);
	}
	
	@Override
	public String formatted(boolean acc)
	{
		return F.f(getValue().getInteger()) + ChatColor.AQUA + " Entities";
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
