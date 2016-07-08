package org.cyberpwn.react.sampler;

import org.bukkit.ChatColor;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.ValueType;

public class SampleTimings extends Sample
{
	private String name;
	private Double ms;
	
	public SampleTimings(SampleController sampleController)
	{
		super(sampleController, "SampleTimings", ValueType.DOUBLE, "TIMINGS", "View the hardest Hitting Timings element.");
		minDelay = 20;
		maxDelay = 20;
		idealDelay = 20;
		target = "Take this relativley, not compared to anything externally.";
		explaination = L.SAMPLER_GENERAL_TIMINGS;
	}
	
	public void onTick()
	{
		ms = getSampleController().getReact().getTimingsController().getMs();
		name = getSampleController().getReact().getTimingsController().getHh();
	}
	
	public void onStart()
	{
		value.setNumber(1);
	}
	
	public String formatted(boolean acc)
	{
		if(!getSampleController().getReact().getTimingsController().supported())
		{
			if(getSampleController().getReact().getTimingsController().getHh() == null)
			{
				return getSampleController().getReact().getTimingsController().ss();
			}
			
			return getSampleController().getReact().getTimingsController().getHh();
		}
		
		try
		{
			return ChatColor.UNDERLINE + name + ChatColor.LIGHT_PURPLE + " " + F.f(ms, 2) + "ms (" + F.pc(ms / 50.0, 0) + ")";
		}
		
		catch(Exception e)
		{
			return "No Data";
		}
	}
	
	public ChatColor color()
	{
		return ChatColor.LIGHT_PURPLE;
	}
	
	public ChatColor darkColor()
	{
		return ChatColor.DARK_PURPLE;
	}
}
