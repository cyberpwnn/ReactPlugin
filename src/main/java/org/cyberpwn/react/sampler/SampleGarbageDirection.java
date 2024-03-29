package org.cyberpwn.react.sampler;

import org.bukkit.ChatColor;
import org.cyberpwn.react.api.ReactAPI;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.ASYNC;
import org.cyberpwn.react.util.Metrics;
import org.cyberpwn.react.util.Metrics.Graph;
import org.cyberpwn.react.util.ValueType;

public class SampleGarbageDirection extends Sample
{
	private long lastSample;
	private long direction;
	private long overhead;
	private int kmx;
	
	public SampleGarbageDirection(SampleController sampleController)
	{
		super(sampleController, "SampleGarbageDirection", ValueType.DOUBLE, "+-", "The Direction of Memory Garbage.");
		
		lastSample = sampleController.getSampleMemoryUsed().getMemoryUsed();
		minDelay = 1;
		maxDelay = 1;
		idealDelay = 1;
		overhead = 1;
		direction = 1;
		kmx = 0;
		target = "Lower is better. However this number will drasticaly vary from server to server.";
		explaination = L.SAMPLER_MEMORY_GARBAGEDIRECTION;
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
		new ASYNC()
		{
			@Override
			public void async()
			{
				kmx++;
				
				if(kmx > 10)
				{
					long currentSample = (long) ReactAPI.getMemoryGarbage();
					
					if(lastSample != currentSample)
					{
						direction = currentSample - lastSample;
					}
					
					else
					{
						direction = 0;
					}
					
					lastSample = currentSample;
					
					if(direction != 0)
					{
						if(direction < 0)
						{
							overhead = -direction;
						}
					}
					
					kmx = 0;
				}
			}
		};
	}
	
	public void onStart()
	{
		value.setNumber(0);
	}
	
	public String formatted(boolean acc)
	{
		String s = ChatColor.GOLD + "" + lastSample + "M ";
		
		if(direction != 0)
		{
			if(direction > 0)
			{
				s = s + Info.COLOR_ERR + "+" + direction;
			}
			
			else
			{
				s = s + ChatColor.GREEN + "-" + -direction;
			}
		}
		
		else
		{
			s = s + "+0";
		}
		
		return s + ChatColor.YELLOW + " Garbage";
	}
	
	public ChatColor color()
	{
		return ChatColor.GOLD;
	}

	public long getLastSample()
	{
		return lastSample;
	}

	public long getDirection()
	{
		return direction;
	}

	public long getOverhead()
	{
		return overhead;
	}
	
	public ChatColor darkColor()
	{
		return ChatColor.GOLD;
	}
}
