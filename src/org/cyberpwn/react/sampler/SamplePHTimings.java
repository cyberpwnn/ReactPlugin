package org.cyberpwn.react.sampler;

import org.bukkit.ChatColor;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.object.ValueType;
import org.cyberpwn.react.util.Task;

public class SamplePHTimings extends Sample
{
	private boolean anim;
	
	public SamplePHTimings(SampleController sampleController)
	{
		super(sampleController, "SamplePHTimings", ValueType.DOUBLE, "HIST", "Compares the difference between more players over memory and stability over 24 hours.");
		minDelay = 13500;
		maxDelay = 13500;
		anim = false;
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
		
		new Task(getSampleController().getReact(), 8)
		{
			public void run()
			{
				anim = !anim;
			}
		};
	}
	
	public String formatted()
	{
		String s = "";
		ChatColor c = anim ? ChatColor.LIGHT_PURPLE : ChatColor.DARK_PURPLE;
		
		if(!getSampleController().getReact().getTimingsController().supported())
		{
			if(getSampleController().getReact().getTimingsController().sxs() != null)
			{
				s = c + " (" + getSampleController().getReact().getTimingsController().sxs() + ")";
			}
		}
		
		return "T" + s;
	}
	
	public ChatColor color()
	{
		return ChatColor.LIGHT_PURPLE;
	}
}
