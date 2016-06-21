package org.cyberpwn.react.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

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
	
	public static void r(Entity e)
	{
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
		
		e.remove();
	}
}
