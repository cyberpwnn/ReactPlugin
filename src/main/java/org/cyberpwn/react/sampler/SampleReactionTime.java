package org.cyberpwn.react.sampler;

import org.bukkit.ChatColor;
import org.cyberpwn.react.React;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.Metrics;
import org.cyberpwn.react.util.Metrics.Graph;
import org.cyberpwn.react.util.ValueType;

public class SampleReactionTime extends Sample
{
	private double max;
	private GList<Long> average;
	
	public SampleReactionTime(SampleController sampleController)
	{
		super(sampleController, "SampleReactionTime", ValueType.DOUBLE, "RCT", "The ammount of miliseconds it takes for React to tick.");
		minDelay = 1;
		maxDelay = 1;
		idealDelay = 1;
		max = 0;
		target = "Lower is better. Remember, 50ms is an entire tick.";
		explaination = L.SAMPLER_GENERAL_REACTIONTIME;
		
		average = new GList<Long>();
	}
	
	@Override
	public void onTick()
	{
		average.add(sampleController.getReactionTime());
		
		if(average.size() > 20)
		{
			average.remove(0);
		}
		
		long ams = 0;
		
		for(Long i : average)
		{
			ams += i;
		}
		
		ams /= average.size();
		
		if(ams > max)
		{
			max = ams;
		}
		
		value.setNumber(ams);
	}
	
	public double getMax()
	{
		return max;
	}
	
	public double getPercent()
	{
		return value.getDouble() / max;
	}
	
	@Override
	public void onStart()
	{
		value.setNumber(1);
	}
	
	@Override
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
	}
	
	@Override
	public int getMetricsValue()
	{
		return (int) (getValue().getDouble() / 1000000.0);
	}
	
	@Override
	public String formatted(boolean acc)
	{
		return "APM: " + F.f((int) (React.afn.getAverage() * 20 * 60));
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
}
