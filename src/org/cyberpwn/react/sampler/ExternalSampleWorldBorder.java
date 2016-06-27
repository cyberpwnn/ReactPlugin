package org.cyberpwn.react.sampler;

import org.bukkit.ChatColor;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.util.ValueType;

import com.wimbli.WorldBorder.Config;

public class ExternalSampleWorldBorder extends ExternalSample
{
	private boolean filling;
	
	public ExternalSampleWorldBorder(SampleController sampleController)
	{
		super(sampleController, "ExternalSampleWorldBorder", "WorldBorder", ValueType.LONG, "WBJ", "World Border Tasks");
		minDelay = 100;
		maxDelay = 100;
		idealDelay = 100;
		target = "Lower is better. However this will vary.";
		explaination = "Every World Border Fill job is caught here.";
		filling = false;
	}
	
	public void onStart()
	{
		if(Config.fillTask != null && Config.fillTask.valid())
		{
			filling = true;
		}
		
		else
		{
			filling = false;
		}
	}
	
	public void onStop()
	{
		filling = false;
	}
	
	public boolean filling()
	{
		return filling;
	}
	
	public void onTick()
	{
		if(Config.fillTask != null && Config.fillTask.valid())
		{
			filling = true;
		}
		
		else
		{
			filling = false;
		}
	}
	
	public String formatted(boolean acc)
	{
		return filling ? "FILLING" : "IDLE" + ChatColor.AQUA + " WB";
	}
	
	public ChatColor color()
	{
		return ChatColor.BLUE;
	}
}
