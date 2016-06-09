package org.cyberpwn.react.sampler;

import org.bukkit.ChatColor;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.object.GList;
import org.cyberpwn.react.object.ValueType;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.Metrics;
import org.cyberpwn.react.util.Metrics.Graph;

public class SampleStability extends Sample
{
	private GList<Double> tps;
	
	public SampleStability(SampleController sampleController)
	{
		super(sampleController, "SampleStability", ValueType.DOUBLE, "STABILITY", "The Stability of the server over time");
		minDelay = 5;
		maxDelay = 5;
		idealDelay = 5;
		tps = new GList<Double>();
		target = "Higher is better. 100% is perfect, above 80% is good.";
		explaination = L.SAMPLER_GENERAL_STABILITY;
	}
	
	public void onTick()
	{
		tps.add(sampleController.getSampleTicksPerSecond().getValue().getDouble());
		
		if(tps.size() > 120)
		{
			tps.remove(0);
		}
		
		double tpx = 0;
		
		for(Double i : tps)
		{
			tpx += i;
		}
		
		tpx /= tps.size();
		tpx = tpx / 20.0;
		
		value.setNumber(tpx);
	}
	
	public void onMetricsPlot(Graph graph)
	{
		graph.addPlotter(new Metrics.Plotter(getMetricsValue() / 10 + "0%")
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
		return (int) (getValue().getDouble() * 100);
	}
	
	public void onStart()
	{
		value.setNumber(1);
	}
	
	public String formatted()
	{
		return F.pc(getValue().getDouble(), 1) + ChatColor.DARK_GREEN + " STABLE";
	}
	
	public ChatColor color()
	{
		return ChatColor.GREEN;
	}
}
