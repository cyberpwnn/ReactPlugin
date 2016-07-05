package org.cyberpwn.react.sampler;

import org.bukkit.ChatColor;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.util.ValueType;

public class SamplePHPhoton extends Sample
{
	public SamplePHPhoton(SampleController sampleController)
	{
		super(sampleController, "SamplePHPhoton", ValueType.DOUBLE, "HIST", "Placeholder");
		minDelay = 13500;
		maxDelay = 13500;
		idealDelay = 13500;
		target = "Placeholder";
		explaination = "Placeholder";
	}
	
	public void onTick()
	{
		
	}
	
	public void onStart()
	{
		value.setNumber(1);
	}
	
	public String formatted(boolean acc)
	{
		return "P";
	}
	
	public ChatColor color()
	{
		return ChatColor.BLUE;
	}
}
