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
import org.cyberpwn.react.util.HandledEvent;
import org.cyberpwn.react.util.InstabilityCause;
import org.cyberpwn.react.util.Lag;
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
	
	@Override
	public void onTick()
	{
		value.setNumber(loadedTick);
		loadedTick = 0;
	}
	
	@Override
	public void onStart()
	{
		sampleController.getReact().register(this);
		value.setNumber(1);
	}
	
	@Override
	public void onStop()
	{
		sampleController.getReact().unRegister(this);
	}
	
	@Override
	public String formatted(boolean acc)
	{
		return F.f(getValue().getInteger()) + ChatColor.DARK_RED + " LIQ/S";
	}
	
	@Override
	public ChatColor color()
	{
		return Info.COLOR_ERR;
	}
	
	@EventHandler
	public void onBlockPhysics(BlockPhysicsEvent e)
	{
		new HandledEvent()
		{
			
			@Override
			public void execute()
			{
				if(e.getBlock().getType().equals(Material.WATER) || e.getBlock().getType().equals(Material.LAVA))
				{
					Lag.report(e.getBlock().getLocation(), InstabilityCause.LIQUID, 150);
					loadedTick++;
				}
			}
		};
	}
	
	@Override
	public ChatColor darkColor()
	{
		return ChatColor.DARK_RED;
	}
}
