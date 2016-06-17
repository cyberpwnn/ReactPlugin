package org.cyberpwn.react.util;

import org.bukkit.Location;
import org.bukkit.Material;

public class W
{
	public static Location fall(Location from)
	{
		int height = from.getBlockY();
		
		for(int i = height; i > 0; i--)
		{
			int check = i - 1;
			
			Material type = new Location(from.getWorld(), from.getBlockX(), check, from.getBlockZ()).getBlock().getType();
			
			if(!(type.equals(Material.AIR) || type.equals(Material.WATER) || type.equals(Material.STATIONARY_WATER) || type.equals(Material.LAVA) || type.equals(Material.STATIONARY_LAVA)))
			{
				return new Location(from.getWorld(), from.getBlockX(), check + 1, from.getBlockZ());
			}
		}
		
		return null;
	}
}
