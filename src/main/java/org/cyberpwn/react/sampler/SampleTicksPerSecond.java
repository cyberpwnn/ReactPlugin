package org.cyberpwn.react.sampler;

import org.bukkit.ChatColor;
import org.cyberpwn.react.api.SpikeEvent;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.Average;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.GTime;
import org.cyberpwn.react.util.Metrics;
import org.cyberpwn.react.util.Metrics.Graph;
import org.cyberpwn.react.util.Platform;
import org.cyberpwn.react.util.U;
import org.cyberpwn.react.util.ValueType;

public class SampleTicksPerSecond extends Sample
{
	private long lastInterval;
	private GList<Double> samples;
	private int tickThreshold;
	private int sampleRadius;
	private Average avg;
	private double cpu;
	private Average cpua;
	private Average cpub;
	private int del;
	
	public SampleTicksPerSecond(SampleController sampleController)
	{
		super(sampleController, "SampleTicksPerSecond", ValueType.DOUBLE, "TPS", "Ticks per second");
		
		cpu = -1;
		del = 0;
		pooled = false;
		sleepy = false;
		cpua = new Average(12);
		cpub = new Average(12);
		avg = new Average(2);
		lastInterval = sampleController.getTick();
		minDelay = 1;
		maxDelay = 1;
		idealDelay = 1;
		sampleRadius = 24;
		tickThreshold = 20;
		samples = new GList<Double>();
		target = "Higher is better. 20 is perfect, above 18 is good.";
		explaination = L.SAMPLER_GENERAL_TPS;
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
	
	@Override
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
		
		try
		{
			del++;
			
			if(del > 1)
			{
				cpu = Platform.CPU.getProcessCPULoad() * Platform.CPU.getAvailableProcessors();
				
				if(cpua.getData().size() > 4)
				{
					if(!cpub.getData().contains(cpu))
					{
						cpub.put(cpu);
					}
				}
				
				cpua.put(cpu);
				
				U.cpu = cpub.getAverage();
				del = 0;
			}
		}
		
		catch(Exception e)
		{
			cpu = -1;
		}
		
		value.setNumber(tps);
		avg.put(tps);
		lastInterval = sampleController.getTick();
	}
	
	public String getLoad(boolean acc)
	{
		return F.pc(cpua.getAverage(), acc ? 2 : 0) + ChatColor.GRAY + " (" + ChatColor.YELLOW + U.getTerm() + ChatColor.GRAY + ")";
	}
	
	public void spiked(long since, long current)
	{
		sampleController.getReact().getServer().getPluginManager().callEvent(new SpikeEvent(new GTime(current), new GTime(since)));
	}
	
	@Override
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
	
	@Override
	public int getMetricsValue()
	{
		return (int) Math.round(getValue().getDouble());
	}
	
	@Override
	public void onStart()
	{
		value.setNumber(20);
	}
	
	@Override
	public String formatted(boolean acc)
	{
		String k = "";
		
		if(percentUsed() > 0 && getSampleController().getReact().getTimingsController().enabled())
		{
			k = " (" + F.pc(percentUsed(), 0) + ")";
		}
		
		else
		{
			try
			{
				if(cpu > -1)
				{
					k = " (" + F.pc(cpua.getAverage(), 0) + ")";
				}
			}
			
			catch(Exception e)
			{
				k = "";
			}
		}
		
		try
		{
			if(value.getDouble() < getSampleController().getReact().getActionController().getActionInstabilityCause().getConfiguration().getDouble(getSampleController().getReact().getActionController().getActionInstabilityCause().getCodeName() + ".low.tps"))
			{
				return ChatColor.UNDERLINE + "" + ChatColor.BOLD + F.f(getValue().getDouble(), 1) + ChatColor.RESET + ChatColor.GREEN + " TPS" + k;
			}
		}
		
		catch(Exception e)
		{
			
		}
		
		if(acc)
		{
			return F.fd(getValue().getDouble(), 3) + k;
		}
		
		else
		{
			return F.fd(getValue().getDouble(), 0) + " TPS" + k;
		}
	}
	
	@Override
	public ChatColor color()
	{
		return ChatColor.GREEN;
	}
	
	@Override
	public ChatColor darkColor()
	{
		return ChatColor.DARK_GREEN;
	}
	
	public double getAverage()
	{
		return avg.getAverage();
	}
}
