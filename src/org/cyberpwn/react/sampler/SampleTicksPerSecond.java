package org.cyberpwn.react.sampler;

import org.bukkit.ChatColor;
import org.cyberpwn.react.api.SpikeEvent;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.object.GList;
import org.cyberpwn.react.object.GTime;
import org.cyberpwn.react.object.ValueType;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.Metrics;
import org.cyberpwn.react.util.Metrics.Graph;

public class SampleTicksPerSecond extends Sample
{
	private long lastInterval;
	private GList<Double> samples;
	private int tickThreshold;
	private int sampleRadius;
	
	public SampleTicksPerSecond(SampleController sampleController)
	{
		super(sampleController, "SampleTicksPerSecond", ValueType.DOUBLE, "TPS", "Ticks per second");
		
		lastInterval = sampleController.getTick();
		minDelay = 1;
		maxDelay = 1;
		idealDelay = 1;
		sampleRadius = 24;
		tickThreshold = 20;
		samples = new GList<Double>();
		target = "Higher is better. 20 is perfect, above 18 is good.";
		explaination = "Twenty times a second the server 'ticks'. This is where all the action happens on the cpu. If this number is lower than 17 you will start to expirience server lag.";
	}
	
	public double getPercent()
	{
		return (20.0 - getValue().getDouble()) / 20.0;
	}
	
	public double percentUsed()
	{
		Double ms = getSampleController().getReact().getTimingsController().getMs();
				
		if(ms != null)
		{
			return ms / 50.0;
		}
		
		return -1;
	}
	
	public void onTick()
	{
		if(timeSinceLastTick() < 40l)
		{
			return;
		}
		
		if(timeSinceLastTick() > (tickThreshold * 50))
		{
			spiked(timeSinceLastTick(), System.currentTimeMillis());
		}
		
		long ticks = sampleController.getTick() - lastInterval;
		long tickTime = timeSinceLastTick() / ticks;
		double ticksPerSecond = (1000.0 / ((double) tickTime)) > 20.0 ? 20.0 : (1000.0 / ((double) tickTime));
		double tps = 0;
		samples.add(ticksPerSecond > 20.0 ? 20.0 : ticksPerSecond);
		
		while(samples.size() > sampleRadius)
		{
			samples.remove(0);
		}
		
		for(Double i : samples)
		{
			tps += i;
		}
		
		tps /= samples.size();
		
		if(new Double(tps).isNaN())
		{
			return;
		}
		
		value.setNumber(tps);
		lastInterval = sampleController.getTick();
	}
	
	public void spiked(long since, long current)
	{
		sampleController.getReact().getServer().getPluginManager().callEvent(new SpikeEvent(new GTime(current), new GTime(since)));
	}
	
	public void onMetricsPlot(Graph graph)
	{
		graph.addPlotter(new Metrics.Plotter(getMetricsValue() + " TPS")
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
		return (int) Math.round(getValue().getDouble());
	}
	
	public void onStart()
	{
		value.setNumber(20);
	}
	
	public String formatted()
	{
		String k = "";
		
		if(percentUsed() > 0)
		{
			k = " (" + F.pc(percentUsed(), 0) + ")";
		}
		
		if(value.getDouble() < getSampleController().getReact().getActionController().getActionInstabilityCause().getConfiguration().getDouble(getSampleController().getReact().getActionController().getActionInstabilityCause().getCodeName() + ".low.tps"))
		{
			return ChatColor.UNDERLINE + "" + ChatColor.BOLD + F.f(getValue().getDouble(), 1) + ChatColor.RESET + ChatColor.GREEN + " TPS" + k;
		}
		
		return F.f(getValue().getDouble(), 1) + " TPS" + k;
	}
	
	public ChatColor color()
	{
		return ChatColor.GREEN;
	}
}
