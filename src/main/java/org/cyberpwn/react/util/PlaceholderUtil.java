package org.cyberpwn.react.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import me.clip.placeholderapi.PlaceholderAPI;

/**
 * Placeholder utils
 * 
 * @author cyberpwn
 */
public class PlaceholderUtil
{
	/**
	 * Handle
	 * 
	 * @param p
	 *            the player
	 * @param s
	 *            the string
	 * @return the placeholder result or null
	 */
	public static String handle(Player p, String s)
	{
		if(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null)
		{
			return PlaceholderAPI.setPlaceholders(p, s);
		}
		
		return s;
	}
}