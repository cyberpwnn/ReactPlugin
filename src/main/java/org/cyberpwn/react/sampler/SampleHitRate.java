package org.cyberpwn.react.sampler;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.HitRateCache;
import org.cyberpwn.react.util.InstabilityCause;
import org.cyberpwn.react.util.Metrics;
import org.cyberpwn.react.util.Metrics.Graph;
import org.cyberpwn.react.util.ValueType;

public class SampleHitRate extends Sample
{
	private HitRateCache cache;
	
	public SampleHitRate(SampleController sampleController)
	{
		super(sampleController, "SampleHitRate", ValueType.DOUBLE, "HIT", "React Hit Rate");
		
		minDelay = 20;
		maxDelay = 20;
		idealDelay = 20;
		target = "Monitors the hit rate of how much react is detecting. There is no correct number here.";
		explaination = L.SAMPLER_GENERAL_HITRATE;
	}
	
	public void onTick()
	{
		
	}
	
	public int getHit(InstabilityCause i)
	{
		if(i == null)
		{
			return 0;
		}
		
		return cache.get(i);
	}
	
	public void onMetricsPlot(Graph graph)
	{
		for(final InstabilityCause i : InstabilityCause.values())
		{
			graph.addPlotter(new Metrics.Plotter(i.toString())
			{
				@Override
				public int getValue()
				{
					return getHit(i);
				}
			});
		}
	}
	
	public int getMetricsValue()
	{
		return getValue().getInteger();
	}
	
	public void onStart()
	{
		cache = new HitRateCache(getSampleController().getReact());
		value.setNumber(0);
	}
	
	public String getTextSmall()
	{
		String hits = "";
		int ghit = 0;
		
		for(InstabilityCause i : InstabilityCause.values())
		{
			ghit += getHit(i);
		}
		
		if(ghit < 1)
		{
			hits = Info.COLOR_ERR + "No Data.";
			return hits;
		}
		
		hits = ChatColor.DARK_RED + "Unknown: " + ChatColor.DARK_AQUA + F.pc(getHit(InstabilityCause.LAG) / (double) ghit, 0);
		hits = hits + "\n" + ChatColor.DARK_RED + "Known: " + ChatColor.DARK_AQUA + F.pc((ghit - getHit(InstabilityCause.LAG)) / (double) ghit, 0);
		return hits;
	}
	
	@SuppressWarnings("deprecation")
	public String getText()
	{
		String hits = "";
		int ghit = 0;
		
		for(InstabilityCause i : InstabilityCause.values())
		{
			ghit += getHit(i);
		}
		
		if(ghit < 1)
		{
			hits = Info.COLOR_ERR + "No Data.\n" + ChatColor.BLACK + "This data records percentages of how often react detects lag sources versus unknown. No data means react has no found any issues yet!";
			return hits;
		}
		
		hits = ChatColor.DARK_RED + "Unknown: " + ChatColor.DARK_AQUA + F.pc(getHit(InstabilityCause.LAG) / (double) ghit, 0);
		hits = hits + "\n" + ChatColor.DARK_RED + "Known: " + ChatColor.DARK_AQUA + F.pc((ghit - getHit(InstabilityCause.LAG)) / (double) ghit, 0);
		
		for(InstabilityCause i : InstabilityCause.values())
		{
			if(!i.equals(InstabilityCause.LAG) && getHit(i) > 0)
			{
				hits = hits + "\n  " + StringUtils.capitalise(i.toString().toLowerCase().replace('_', ' ')) + ": " + ChatColor.DARK_AQUA + F.pc(getHit(i) / (double) ghit, 0);
			}
		}
		
		return hits;
	}
	
	public void hit(InstabilityCause i)
	{
		cache.hit(i);
	}
	
	public void onStop()
	{
		cache.save();
	}
	
	public String formatted(boolean acc)
	{
		return F.f(getValue().getDouble(), 1) + " HT";
	}
	
	public ChatColor color()
	{
		return ChatColor.GREEN;
	}
}
