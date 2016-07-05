package org.cyberpwn.react.reflect;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public class CitizensPluginConnector extends PluginConnector
{
	public CitizensPluginConnector()
	{
		super("Citizens");
	}

	public boolean isNPC(Entity e)
	{
		if(!exists())
		{
			return false;
		}

		Boolean b = false;
		Plugin c = Bukkit.getPluginManager().getPlugin(pluginName);
		
		try
		{
			Object npcr = c.getClass().getMethod("getNPCRegistry").invoke(c);
			b = (Boolean) npcr.getClass().getMethod("isNPC", Entity.class).invoke(npcr, e);
		} 
		
		catch(Exception ex)
		{
			
		}
		
		return b;
	}
}
