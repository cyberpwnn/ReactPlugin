package org.cyberpwn.react.sampler;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.Task;
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
	
	public void onTick()
	{
		getValue().setNumber(entities);
		entities = 0;
		final int[] cpt = new int[] { 0 };
		
		for(World i : sampleController.getReact().getServer().getWorlds())
		{
			cpt[0] += i.getLoadedChunks().length;
		}
		
		cpt[0] /= (idealDelay + 1);
		
		for(World i : sampleController.getReact().getServer().getWorlds())
		{
			final Iterator<Chunk> it = new GList<Chunk>(i.getLoadedChunks()).iterator();
			
			new Task(0)
			{
				public void run()
				{
					int[] itx = new int[] { 0 };
					while(it.hasNext() && itx[0] <= cpt[0])
					{
						entities += it.next().getEntities().length;
						itx[0]++;
					}
					
					if(!it.hasNext())
					{
						cancel();
					}
				}
			};
		}
	}
	
	public void onStart()
	{
		int ents = 0;
		
		for(World i : Bukkit.getWorlds())
		{
			ents += i.getEntities().size();
		}
		
		value.setNumber(ents);
	}
	
	public String formatted(boolean acc)
	{
		return F.f(getValue().getInteger()) + ChatColor.AQUA + " ENTS";
	}
	
	public ChatColor color()
	{
		return ChatColor.BLUE;
	}
	
	public ChatColor darkColor()
	{
		return ChatColor.DARK_BLUE;
	}
}
