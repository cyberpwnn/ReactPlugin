package org.cyberpwn.react.sampler;

import org.bukkit.ChatColor;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.util.ValueType;

public class SamplePHEntities extends Sample
{
	public SamplePHEntities(SampleController sampleController)
	{
		super(sampleController, "SamplePHEntities", ValueType.DOUBLE, "HIST", "Compares the difference between more players over memory and stability over 24 hours.");
		minDelay = 13500;
		maxDelay = 13500;
		idealDelay = 13500;
		target = "View this on the lower quadrant";
		explaination = "View this on the lower right quadrant on the map.";
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
		return "E";
	}
	
	public ChatColor color()
	{
		return ChatColor.AQUA;
	}
	
	public ChatColor darkColor()
	{
		return ChatColor.BLUE;
	}
}
