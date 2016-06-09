package org.cyberpwn.react.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.cyberpwn.react.React;
import org.cyberpwn.react.object.GList;

public class Verbose
{
	public static GList<Player> mx = new GList<Player>();
	
	public static void x(String s, String msg)
	{
		if(React.verbose)
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + s + ChatColor.AQUA + " >> " + ChatColor.GREEN + msg);
		}
		
		if(!mx.isEmpty())
		{
			for(Player i : mx)
			{
				i.sendMessage(ChatColor.LIGHT_PURPLE + s + ChatColor.AQUA + " >> " + ChatColor.GREEN + msg);
			}
		}
	}
}
