package org.cyberpwn.react.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.cyberpwn.react.React;

public class Verbose
{
	public static GList<Player> mrx = new GList<Player>();
	
	public static void x(String s, String msg)
	{
		if(React.isVerbose())
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + s + ChatColor.AQUA + " >> " + ChatColor.GREEN + msg);
		}
		
		if(!mrx.isEmpty())
		{
			for(Player i : mrx)
			{
				i.sendMessage(ChatColor.LIGHT_PURPLE + s + ChatColor.AQUA + " >> " + ChatColor.GREEN + msg);
			}
		}
	}
}
