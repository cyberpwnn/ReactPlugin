package org.cyberpwn.react.util;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.cyberpwn.react.React;
import org.cyberpwn.react.reflect.CitizensPluginConnector;
import org.cyberpwn.react.reflect.MobArenaPluginConnector;

public class E
{
	public static boolean isNPC(Entity e)
	{
		return new CitizensPluginConnector().isNPC(e);
	}
	
	public static void r(Entity e, boolean die)
	{
		if(die)
		{
			d(e);
		}
		
		else
		{
			r(e);
		}
	}
	
	public static void r(Entity e)
	{
		if(new MobArenaPluginConnector().inArena(e.getLocation()))
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
	
	public static void d(Entity e)
	{
		if(new MobArenaPluginConnector().inArena(e.getLocation()))
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
			if(e instanceof LivingEntity)
			{
				((LivingEntity)e).setHealth(0);
			}
		}
		
		catch(Exception ex)
		{
			
		}
	}
}
