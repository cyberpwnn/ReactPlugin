package org.cyberpwn.react.sampler;

import org.bukkit.ChatColor;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.Metrics;
import org.cyberpwn.react.util.ValueType;
import org.cyberpwn.react.util.Metrics.Graph;

public class SampleMemoryAllocationsPerSecond extends Sample
{
	private long lastSample;
	private GList<Long> samples;
	
	public SampleMemoryAllocationsPerSecond(SampleController sampleController)
	{
		super(sampleController, "SampleMemoryAllocationsPerSecond", ValueType.DOUBLE, "MAH/S", "Memory Allocations (MB) per second.");
		
		lastSample = sampleController.getSampleMemoryUsed().getMemoryUsed();
		samples = new GList<Long>().qadd(lastSample);
		minDelay = 1;
		maxDelay = 1;
		idealDelay = 1;
		target = "Lower is better.";
		explaination = L.SAMPLER_MEMORY_MAHS;
	}
	
	public void onMetricsPlot(Graph graph)
	{
		graph.addPlotter(new Metrics.Plotter((getMetricsValue() / 10) + "0 MAH/S")
		{
			@Override
			public int getValue()
			{
				return 1;
			}
		});
	}
	
	public int getMetricsValue()
	{
		return getValue().getInteger();
	}
	
	@Override
	public void onTick()
	{
		long currentSample = sampleController.getSampleMemoryUsed().getMemoryUsed();
		
		if(lastSample < currentSample)
		{
			samples.add(currentSample - lastSample);
			
			if(samples.size() > 20)
			{
				samples.remove(0);
			}
		}
		
		long total = 0l;
		
		for(Long i : samples)
		{
			total += i;
		}
		
		value.setNumber(((total) / 1024 / 1024));
		
		lastSample = currentSample;
	}
	
	@Override
	public void onStart()
	{
		value.setNumber(0);
	}
	
	public Double getPercent()
	{
		return (getValue().getDouble() / (getSampleController().getSampleMemoryUsed().getAverage().doubleValue() * 8.0));
	}
	
	public String formatted()
	{
		return F.f(getValue().getLong()) + ChatColor.YELLOW + " MAH/S";
	}
	
	public ChatColor color()
	{
		return ChatColor.GOLD;
	}
}
