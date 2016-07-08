package org.cyberpwn.react.sampler;

import org.bukkit.ChatColor;
import org.cyberpwn.react.React;
import org.cyberpwn.react.controller.PhotonController;
import org.cyberpwn.react.controller.SampleController;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.ValueType;

public class SamplePhoton extends Sample
{
	public SamplePhoton(SampleController sampleController)
	{
		super(sampleController, "SamplePhoton", ValueType.LONG, "PHO", "Photon Data");
		
		minDelay = 100;
		maxDelay = 100;
		idealDelay = 100;
		target = "Lower is better, however it will not impact you unless it is dangerously high (your max memory)";
		explaination = "Photon Data";
	}
	
	@Override
	public void onTick()
	{
		
	}
	
	public String formatted(boolean acc)
	{
		PhotonController p = React.instance().getPhotonController();
		return ChatColor.AQUA + F.f(p.getCache().size()) + ChatColor.BLUE + " Cached " + ChatColor.AQUA + p.getPhotons().size() + ChatColor.BLUE + " Photons " + ChatColor.AQUA + F.pc(1.0 - p.getAccuracy()) + ChatColor.BLUE + " Accuracy " + ChatColor.AQUA + F.f(p.getPower(), 0) + " " + ChatColor.BLUE + "Lum/t";
	}
	
	public ChatColor color()
	{
		return ChatColor.BLUE;
	}
	
	public ChatColor darkColor()
	{
		return ChatColor.DARK_BLUE;
	}
}
