package org.cyberpwn.react.sampler;

import org.bukkit.ChatColor;
import org.cyberpwn.react.api.PostGCEvent;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.GTime;
import org.cyberpwn.react.util.Metrics;
import org.cyberpwn.react.util.ValueType;
import org.cyberpwn.react.util.Metrics.Graph;

public class SampleMemoryUsed extends Sample
{
	private long lastSample;
	private GList<Long> average;
	
	public SampleMemoryUsed(SampleController sampleController)
	{
		super(sampleController, "SampleMemoryUsed", ValueType.LONG, "MEM", "Used Memory (RAM)");
		
		lastSample = getMemoryUsed();
		minDelay = 1;
		average = new GList<Long>().qadd(1l);
		maxDelay = 1;
		idealDelay = 1;
		target = "Lower is better, however it will not impact you unless it is dangerously high (your max memory)";
		explaination = L.SAMPLER_MEMORY_USED;
	}
	
	@Override
	public void onTick()
	{
		long currentSample = getMemoryUsed();
		
		if(lastSample > currentSample)
		{
			value.setNumber(currentSample / 1024 / 1024);
			sampleController.getReact().getServer().getPluginManager().callEvent(new PostGCEvent(new GTime(System.currentTimeMillis()), ((lastSample - currentSample) / 1024 / 1024), new GTime(timeSinceLastTick())));
		}
		
		lastSample = currentSample;
		
		average.add(value.getLong());
		
		if(average.size() > 120)
		{
			average.remove(0);
		}
	}
	
	@Override
	public void onStart()
	{
		value.setNumber(getMemoryUsed() / 1024 / 1024);
	}
	
	public Long getAverage()
	{
		long mb = 0;
		
		for(long i : average)
		{
			mb += i;
		}
		
		mb /= average.size();
		
		return mb;
	}
	
	public void onMetricsPlot(Graph graph)
	{
		graph.addPlotter(new Metrics.Plotter((((int) (getPercent() * 100)) / 10) + "0%")
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
	
	public Long getMemoryUsed()
	{
		return Runtime.getRuntime().maxMemory() - getMemoryFree();
	}
	
	public Long getMemoryMax()
	{
		return Runtime.getRuntime().maxMemory();
	}
	
	public Long getMemoryAllocated()
	{
		return Runtime.getRuntime().totalMemory();
	}
	
	public Long getMemoryFree()
	{
		return Runtime.getRuntime().freeMemory() + (Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory());
	}
	
	public Double getPercent()
	{
		return (getValue().getLong().doubleValue()) / (getMemoryMax().doubleValue() / 1024.0 / 1024.0);
	}
	
	public Double getPercentAverage()
	{
		return (getAverage().doubleValue()) / (getMemoryMax().doubleValue() / 1024.0 / 1024.0);
	}
	
	public String formatted(boolean acc)
	{
		if(getPercent() > getSampleController().getReact().getActionController().getActionInstabilityCause().getConfiguration().getDouble(getSampleController().getReact().getActionController().getActionInstabilityCause().getCodeName() + ".high.memory.percent"))
		{
			return ChatColor.UNDERLINE + "" + ChatColor.UNDERLINE + F.mem(getValue().getLong()) + " (" + F.pc(getValue().getLong(), (getMemoryMax() / 1024 / 1024)) + ")" + ChatColor.RESET;
		}
		
		return F.mem(getValue().getLong()) + " (" + F.pc(getValue().getLong(), (getMemoryMax() / 1024 / 1024)) + ")";
	}
	
	public ChatColor color()
	{
		return ChatColor.GOLD;
	}
}
