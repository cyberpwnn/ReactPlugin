package org.cyberpwn.react.sampler;

import org.bukkit.ChatColor;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.Metrics;
import org.cyberpwn.react.util.ValueType;
import org.cyberpwn.react.util.Metrics.Graph;

public class SampleReactionTime extends Sample
{
	private double max;
	private Double opn;
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
	
	public void onTick()
	{
		if(opn != null)
		{
			value.setNumber(opn * 1000000);
			return;
		}
		
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
	
	public void onStart()
	{
		value.setNumber(1);
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
	}
	
	public void setOpn(Double v)
	{
		this.opn = v;
	}
	
	public int getMetricsValue()
	{
		return (int) (getValue().getDouble() / 1000000.0);
	}
	
	public String formatted(boolean acc)
	{
		return F.fd(getValue().getDouble() / 1000000.0, 2) + "ms";
	}
	
	public ChatColor color()
	{
		return ChatColor.GREEN;
	}
}
