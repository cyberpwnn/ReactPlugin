package org.cyberpwn.react.util;

import org.bukkit.entity.Entity;

public class E
{
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
