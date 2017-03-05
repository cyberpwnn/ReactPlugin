package org.cyberpwn.react.sampler;

import org.bukkit.ChatColor;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.Metrics;
import org.cyberpwn.react.util.Metrics.Graph;
import org.cyberpwn.react.util.ValueType;

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
	
	@Override
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
	
	@Override
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
	
	@Override
	public int getMetricsValue()
	{
		return (int) (getValue().getDouble() * 100);
	}
	
	@Override
	public void onStart()
	{
		value.setNumber(1);
	}
	
	@Override
	public String formatted(boolean acc)
	{
		return getSampleController().getSampleTicksPerSecond().getLoad(acc);
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
