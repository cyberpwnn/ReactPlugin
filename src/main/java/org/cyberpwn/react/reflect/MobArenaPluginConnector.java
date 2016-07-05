package org.cyberpwn.react.reflect;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

public class MobArenaPluginConnector extends PluginConnector
{
	public MobArenaPluginConnector()
	{
		super("MobArena");
	}
	
	public boolean inArena(Location l)
	{
		if(!exists())
		{
			return false;
		}
		
		Plugin c = Bukkit.getPluginManager().getPlugin("MobArena");
		
		try
		{
			Object master = c.getClass().getMethod("getArenaMaster").invoke(c);
			Object a = master.getClass().getMethod("getArenaAtLocation", Location.class).invoke(master, l);
			
			if(a == null)
			{
				return false;
			}
			
			else
			{
				return true;
			}
		}
		
		catch(Exception ex)
		{
			
		}
				
		return false;
	}
}
