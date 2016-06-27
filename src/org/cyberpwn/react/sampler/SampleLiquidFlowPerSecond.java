package org.cyberpwn.react.sampler;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.ValueType;

public class SampleLiquidFlowPerSecond extends Sample implements Listener
{
	private int loadedTick;
	
	public SampleLiquidFlowPerSecond(SampleController sampleController)
	{
		super(sampleController, "SampleLiquidFlowPerSecond", ValueType.DOUBLE, "LIQ/S", "Liquid Flows Per Second");
		minDelay = 20;
		maxDelay = 20;
		idealDelay = 20;
		target = "Lower is better. However this will vary.";
		explaination = L.SAMPLER_WORLD_LIQUIDFLOW;
	}
	
	public void onTick()
	{
		value.setNumber(loadedTick);
		loadedTick = 0;
	}
	
	public void onStart()
	{
		sampleController.getReact().register(this);
		value.setNumber(1);
	}
	
	public void onStop()
	{
		sampleController.getReact().unRegister(this);
	}
	
	public String formatted(boolean acc)
	{
		return F.f(getValue().getInteger()) + ChatColor.DARK_RED + " LIQ/S";
	}
	
	public ChatColor color()
	{
		return Info.COLOR_ERR;
	}
	
	@EventHandler
	public void onBlockPhysics(BlockPhysicsEvent e)
	{
		if(e.getBlock().getType().equals(Material.WATER) || e.getBlock().getType().equals(Material.LAVA))
		{
			loadedTick++;
		}
	}
}
