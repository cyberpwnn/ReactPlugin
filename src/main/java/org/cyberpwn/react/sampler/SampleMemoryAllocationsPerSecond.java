package org.cyberpwn.react.sampler;

import org.bukkit.ChatColor;
import org.cyberpwn.react.React;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.queue.M;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.Metrics;
import org.cyberpwn.react.util.Metrics.Graph;
import org.cyberpwn.react.util.ValueType;

public class SampleMemoryAllocationsPerSecond extends Sample
{
	private long lastSample;
	private GList<Long> samples;
	private long last;
	private long lastMs;
	
	public SampleMemoryAllocationsPerSecond(SampleController sampleController)
	{
		super(sampleController, "SampleMemoryAllocationsPerSecond", ValueType.DOUBLE, "MAH/S", "Memory Allocations (MB) per second.");
		
		lastSample = sampleController.getSampleMemoryUsed().getMemoryUsed();
		samples = new GList<Long>().qadd(lastSample);
		minDelay = 1;
		maxDelay = 1;
		last = 0;
		lastMs = M.ms();
		idealDelay = 1;
		target = "Lower is better.";
		explaination = L.SAMPLER_MEMORY_MAHS;
	}
	
	@Override
	public void onMetricsPlot(Graph graph)
	{
		graph.addPlotter(new Metrics.Plotter("MAH/S")
		{
			@Override
			public int getValue()
			{
				return value.getInteger();
			}
		});
	}
	
	@Override
	public int getMetricsValue()
	{
		return getValue().getInteger();
	}
	
	@Override
	public void onTick()
	{
		long el = React.instance().getSampleController().getTick() - last;
		last = React.instance().getSampleController().getTick();
		
		long currentSample = sampleController.getSampleMemoryUsed().getMemoryUsed();
		
		if(lastSample < currentSample)
		{
			for(int i = 0; i < el; i++)
			{
				samples.add(currentSample - lastSample);
			}
			
			while(samples.size() > 20)
			{
				samples.remove(0);
			}
		}
		
		long total = 0l;
		
		for(Long i : samples)
		{
			total += i;
		}
		
		long timeSinceLast = M.ms() - lastMs;
		
		if(timeSinceLast < 50)
		{
			timeSinceLast = 50;
		}
		
		double ticks = (double)timeSinceLast / 50.0;
		
		if(ticks < 1)
		{
			ticks = 1;
		}
		
		value.setNumber(((total / ticks) / 1024 / 1024));
		lastMs = M.ms();
		
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
	
	@Override
	public String formatted(boolean acc)
	{
		if(acc)
		{
			return F.f(getValue().getLong()) + ChatColor.YELLOW + " MAH/S";
		}
		
		else
		{
			return F.f(getValue().getLong()) + ChatColor.YELLOW + " MAH/S";
		}
	}
	
	@Override
	public ChatColor color()
	{
		return ChatColor.GOLD;
	}
	
	@Override
	public ChatColor darkColor()
	{
		return ChatColor.GOLD;
	}
}
