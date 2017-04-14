package org.cyberpwn.react.util;

import org.bukkit.ChatColor;

/**
 * Colored circle spinner
 * 
 * @author cyberpwn
 */
public class PhantomSpinner
{
	private ProgressSpinner s;
	private ProgressSpinner c;
	
	public PhantomSpinner()
	{
		s = new ProgressSpinner();
		c = new ProgressSpinner(ChatColor.AQUA.toString(), ChatColor.AQUA.toString(), ChatColor.AQUA.toString(), ChatColor.DARK_AQUA.toString(), ChatColor.DARK_GRAY.toString(), ChatColor.DARK_GRAY.toString(), ChatColor.DARK_GRAY.toString(), ChatColor.DARK_AQUA.toString());
	}
	
	public String toString()
	{
		return c.toString() + s.toString();
	}
}
