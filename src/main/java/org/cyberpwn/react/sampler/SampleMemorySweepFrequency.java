package org.cyberpwn.react.sampler;

import org.bukkit.ChatColor;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.Average;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.Metrics;
import org.cyberpwn.react.util.Metrics.Graph;
import org.cyberpwn.react.util.ValueType;

public class SampleMemorySweepFrequency extends Sample
{
	private long lastSample;
	private double ticks;
	private Average avg;
	private double last;
	
	public SampleMemorySweepFrequency(SampleController sampleController)
	{
		super(sampleController, "SampleMemorySweepFrequency", ValueType.DOUBLE, "SPMS", "Memory (RAM) Sweeps per Minute");
		
		lastSample = sampleController.getSampleMemoryUsed().getMemoryUsed();
		minDelay = 1;
		maxDelay = 1;
		idealDelay = 1;
		avg = new Average(12);
		target = "Lower is better. However this number will drasticaly vary from server to server.";
		explaination = L.SAMPLER_MEMORY_GC;
	}
	
	public void onMetricsPlot(Graph graph)
	{
		graph.addPlotter(new Metrics.Plotter(getMetricsValue() + " SPMS")
		{
			@Override
			public int getValue()
			{
				return get().getInteger();
			}
		});
	}
	
	public int getMetricsValue()
	{
		return getValue().getInteger();
	}
	
	public void onTick()
	{
		long currentSample = sampleController.getSampleMemoryUsed().getMemoryUsed();
		
		if(lastSample > currentSample)
		{
			if(ticks <= 0)
			{
				ticks++;
			}
			
			last = 1200 / ticks;
			ticks = 0;
		}
		
		else
		{
			ticks++;
		}
		
		avg.put(last);
		value.setNumber(avg.getAverage());
		
		lastSample = currentSample;
	}
	
	public void onStart()
	{
		value.setNumber(0);
	}
	
	public String formatted(boolean acc)
	{
		return F.f(getValue().getLong()) + ChatColor.YELLOW + " GC/M";
	}
	
	public ChatColor color()
	{
		return ChatColor.GOLD;
	}
	
	public ChatColor darkColor()
	{
		return ChatColor.GOLD;
	}
}
