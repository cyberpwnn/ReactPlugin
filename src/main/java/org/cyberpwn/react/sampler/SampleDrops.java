package org.cyberpwn.react.sampler;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.ASYNC;
import org.cyberpwn.react.util.F;
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
	
	@Override
	public void onTick()
	{
		new ASYNC()
		{
			@Override
			public void async()
			{
				getValue().setNumber(drops);
				drops = 0;
				final int[] cpt = new int[] {0};
				
				for(World i : sampleController.getReact().getServer().getWorlds())
				{
					drops += i.getEntitiesByClass(Item.class).size();
					cpt[0] += i.getLoadedChunks().length;
				}
				
				cpt[0] /= (idealDelay + 1);
			}
		};
	}
	
	@Override
	public void onStart()
	{
		value.setNumber(0);
	}
	
	@Override
	public String formatted(boolean acc)
	{
		return F.f(getValue().getInteger()) + ChatColor.AQUA + " Drops";
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
