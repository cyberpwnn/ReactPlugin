package org.cyberpwn.react.sampler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.lang.Info;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.ASYNC;
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

	@Override
	public void onTick()
	{
		if(first)
		{
			first = false;
			return;
		}

		new ASYNC()
		{
			@Override
			public void async()
			{
				getSampleController().getReact().getDataController().getTb().push("stability", getSampleController().getSampleStability().getValue().getDouble());
				getSampleController().getReact().getDataController().getTb().push("players", (double) getSampleController().getReact().onlinePlayers().length / (double) Bukkit.getServer().getMaxPlayers());
				getSampleController().getReact().getDataController().getTb().push("memory", getSampleController().getSampleMemoryUsed().getPercentAverage());
			}
		};
	}

	@Override
	public void onStart()
	{
		value.setNumber(1);
	}

	@Override
	public String formatted(boolean acc)
	{
		return "MAP ONLY";
	}

	@Override
	public ChatColor color()
	{
		return Info.COLOR_ERR;
	}

	@Override
	public ChatColor darkColor()
	{
		return ChatColor.DARK_RED;
	}
}
