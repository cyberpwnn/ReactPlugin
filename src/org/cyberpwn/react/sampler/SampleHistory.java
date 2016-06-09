package org.cyberpwn.react.sampler;

import org.bukkit.ChatColor;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.object.ValueType;

public class SampleHistory extends Sample
{
	private boolean first;
	
	public SampleHistory(SampleController sampleController)
	{
		super(sampleController, "SampleHistory", ValueType.DOUBLE, "HIST", "Compares the difference between more players over memory and stability over 24 hours.");
		minDelay = 13500;
		first = true;
		maxDelay = 13500;
		idealDelay = 13500;
		target = "View this on the lower quadrant";
		explaination = "View this on the lower right quadrant on the map.";
	}
	
	public void onTick()
	{
		if(first)
		{
			first = false;
			return;
		}
		
		getSampleController().getReact().getDataController().getTb().push("stability", getSampleController().getSampleStability().getValue().getDouble());
		getSampleController().getReact().getDataController().getTb().push("players", getSampleController().getReact().onlinePlayers().length);
		getSampleController().getReact().getDataController().getTb().push("memory", getSampleController().getSampleMemoryUsed().getValue().getDouble());
		getSampleController().getReact().getMonitorController().getMapper().updateSlow(getSampleController().getReact().getDataController().getTb());
	}
	
	public void onStart()
	{
		value.setNumber(1);
	}
	
	public String formatted()
	{
		return "MAP ONLY";
	}
	
	public ChatColor color()
	{
		return ChatColor.RED;
	}
}
