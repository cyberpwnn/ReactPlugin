package org.cyberpwn.react.sampler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.lang.L;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.ValueType;

public class SamplePlayers extends Sample
{
	public SamplePlayers(SampleController sampleController)
	{
		super(sampleController, "SamplePlayers", ValueType.DOUBLE, "PLR", "Sample Players");
		
		minDelay = 20;
		maxDelay = 20;
		idealDelay = 20;
		target = "Samples the amount of players online";
		explaination = L.SAMPLER_GENERAL_PLAYERS;
	}
	
	public void onTick()
	{
		value.setNumber(Bukkit.getServer().getOnlinePlayers().size());
	}
	
	public void onStart()
	{
		value.setNumber(0);
	}
	
	public void onStop()
	{
		
	}
	
	public String formatted(boolean acc)
	{
		return F.f(getValue().getDouble(), 1) + " PL";
	}
	
	public ChatColor color()
	{
		return ChatColor.GREEN;
	}
}
