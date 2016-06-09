package org.cyberpwn.react.nms;

import org.bukkit.Bukkit;

public class NMS
{
	private static NMS instance;
	private final AbstractNMS nms;
	
	private NMS()
	{
		AbstractNMS nmsx = null;
		
		if(Bukkit.getBukkitVersion().startsWith("1.10"))
		{
			nmsx = new NMS110();
		}
		
		if(Bukkit.getBukkitVersion().startsWith("1.9"))
		{
			if(Bukkit.getBukkitVersion().startsWith("1.9.4"))
			{
				nmsx = new NMS194();
			}
			
			else
			{
				nmsx = new NMS192();
			}
		}
		
		if(Bukkit.getBukkitVersion().startsWith("1.8"))
		{
			nmsx = new NMS18();
		}
		
		if(Bukkit.getBukkitVersion().startsWith("1.7"))
		{
			nmsx = new NMS17();
		}
		
		nms = nmsx;
	}
	
	public static AbstractNMS instance()
	{
		if(instance == null)
		{
			instance = new NMS();
		}
		
		return instance.nms;
	}
}
