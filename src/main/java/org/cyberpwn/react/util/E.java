package org.cyberpwn.react.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.cyberpwn.react.React;

public class E
{
	public static boolean isNPC(Entity e)
	{
		if(Bukkit.getPluginManager().getPlugin("Citizens") == null)
		{
			
			return false;
		}

		Boolean b = false;
		Plugin c = Bukkit.getPluginManager().getPlugin("Citizens");
		
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
	
	public static boolean isInArena(Location l)
	{
		if(Bukkit.getPluginManager().getPlugin("MobArena") == null)
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
	
	public static void r(Entity e)
	{
		if(isInArena(e.getLocation()))
		{
			return;
		}
		
		if(e.getType().toString().equals("PLAYER"))
		{
			return;
		}
		
		if(e.getType().toString().equals("COMPLEX_PART"))
		{
			return;
		}
		
		if(e.getType().toString().equals("PAINTING"))
		{
			return;
		}
		
		if(e.getType().toString().equals("PAINTING"))
		{
			return;
		}
		
		if(e.getType().toString().equals("ITEM_FRAME"))
		{
			return;
		}
		
		if(e.getType().toString().equals("WITHER_SKULL"))
		{
			return;
		}
		
		if(e.getType().toString().equals("ARMOR_STAND"))
		{
			return;
		}
		
		if(!React.instance().getWorldController().canTouch(e))
		{
			return;
		}
		
		try
		{
			e.remove();
		}
		
		catch(Exception ex)
		{
			
		}
	}
}
