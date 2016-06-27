package org.cyberpwn.react.sampler;

import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.Task;
import org.cyberpwn.react.util.ValueType;

public class SampleDrops extends Sample
{
	private int drops;
	
	public SampleDrops(SampleController sampleController)
	{
		super(sampleController, "SampleDrops", ValueType.DOUBLE, "DROPS", "Sample Item Drops");
		minDelay = 100;
		maxDelay = 100;
		idealDelay = 100;
		drops = 0;
		target = "Lower is better. However this will vary.";
		explaination = L.SAMPLER_WORLD_DROPS;
	}
	
	public void onTick()
	{
		getValue().setNumber(drops);
		drops = 0;
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
						for(Entity i : it.next().getEntities())
						{
							if(i.getType().equals(EntityType.DROPPED_ITEM))
							{
								drops++;
							}
							
							itx[0]++;
						}
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
		value.setNumber(0);
	}
	
	public String formatted(boolean acc)
	{
		return F.f(getValue().getInteger()) + ChatColor.AQUA + " DROPS";
	}
	
	public ChatColor color()
	{
		return ChatColor.BLUE;
	}
}
