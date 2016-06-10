package org.cyberpwn.react.sampler;

import org.bukkit.ChatColor;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.Average;
import org.cyberpwn.react.util.CPUTest;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.ValueType;

public class SampleCPUScore extends Sample
{
	private Average avg;
	
	public SampleCPUScore(SampleController sampleController)
	{
		super(sampleController, "SampleCPUScore", ValueType.DOUBLE, "CPU SCORE", "Measure your cpu score relativley with previous measurments or with another cpu completley.");
		minDelay = 200;
		maxDelay = 200;
		idealDelay = 200;
		target = "Take this relativley, not compared to anything externally.";
		explaination = L.SAMPLER_GENERAL_CPUSCORE;
		
		avg = new Average(8);
	}
	
	public void onTick()
	{
		avg.put(CPUTest.test(1));
		value.setNumber(avg.getAverage());
	}
	
	public void onStart()
	{
		value.setNumber(1);
	}
	
	public String formatted()
	{
		return F.f(value.getLong()) + ChatColor.DARK_GREEN + " CPU SCORE";
	}
	
	public ChatColor color()
	{
		return ChatColor.GREEN;
	}
}
