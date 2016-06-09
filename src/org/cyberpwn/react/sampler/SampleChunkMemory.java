package org.cyberpwn.react.sampler;

import org.bukkit.ChatColor;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.object.ValueType;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.Metrics;
import org.cyberpwn.react.util.Metrics.Graph;

public class SampleChunkMemory extends Sample
{
	public SampleChunkMemory(SampleController sampleController)
	{
		super(sampleController, "SampleChunkMemory", ValueType.DOUBLE, "CHUNKMEM", "Memory used for chunks.");
		minDelay = 1;
		maxDelay = 1;
		idealDelay = 1;
		target = "Lower is better. However this will vary directly with loaded chunks.";
		explaination = L.SAMPLER_CHUNK_MEMORY;
	}
	
	public void onTick()
	{
		value.setNumber((long) (((double) getSampleController().getSampleChunksLoaded().getValue().getInteger()) / 12.8));
		
		if(value.getLong() >= getSampleController().getSampleMemoryUsed().getValue().getLong())
		{
			value.setNumber(getSampleController().getSampleMemoryUsed().getValue().getLong() - 80);
		}
	}
	
	public void onMetricsPlot(Graph graph)
	{
		graph.addPlotter(new Metrics.Plotter(getName())
		{
			@Override
			public int getValue()
			{
				return getMetricsValue();
			}
		});
		
		graph.addPlotter(new Metrics.Plotter(getName() + " Percent")
		{
			@Override
			public int getValue()
			{
				return (int) (getPercent() * 100);
			}
		});
	}
	
	public int getMetricsValue()
	{
		return getValue().getInteger();
	}
	
	public void onStart()
	{
		value.setNumber(1);
	}
	
	public double getPercent()
	{
		return getValue().getDouble() / getSampleController().getSampleMemoryUsed().getValue().getDouble();
	}
	
	public String formatted()
	{
		return F.mem(getValue().getLong()) + " (" + F.pc(getValue().getDouble() / getSampleController().getSampleMemoryUsed().getValue().getDouble(), 0) + ")" + ChatColor.YELLOW + " CMEM";
	}
	
	public ChatColor color()
	{
		return ChatColor.GOLD;
	}
}
