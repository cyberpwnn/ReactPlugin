package org.cyberpwn.react.sampler;

import org.bukkit.ChatColor;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.ValueType;

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
		explaination = L.SAMPLER_GENERAL_HISTORY;
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
	
	public String formatted(boolean acc)
	{
		return "MAP ONLY";
	}
	
	public ChatColor color()
	{
		return Info.COLOR_ERR;
	}
	
	public ChatColor darkColor()
	{
		return ChatColor.DARK_RED;
	}
}
