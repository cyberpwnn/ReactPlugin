package org.cyberpwn.react.nms;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.cyberpwn.react.React;

public class PacketUtil
{
	public enum V
	{
		R17, R18, R19, R110;
	}
	
	public static V getVersion()
	{
		if(Bukkit.getBukkitVersion().startsWith("1.10"))
		{
			return V.R110;
		}
		
		if(Bukkit.getBukkitVersion().startsWith("1.9"))
		{
			return V.R19;
		}
		
		if(Bukkit.getBukkitVersion().startsWith("1.8"))
		{
			return V.R18;
		}
		
		return V.R17;
	}
	
	public static void sendTitle(Player player, Integer in, Integer stay, Integer out, String title, String subTitle)
	{
		if(getVersion().equals(V.R17))
		{
			return;
		}
		
		try
		{
			NMS.instance().packetTitle(player, title, subTitle, in, out, stay);
		}
		
		catch(Exception e)
		{
			React.fail(e, "Failed to send title packet to player:" + player.getName() + "stay: " + stay + " in:" + in + " out:" + out + " >> " + title + " <> " + subTitle);
		}
	}
	
	public static void clearTitle(Player player)
	{
		if(getVersion().equals(V.R17))
		{
			return;
		}
		try
		{
			NMS.instance().clearTitle(player);
		}
		
		catch(Exception e)
		{
			React.fail(e, "Failed to send cleartitle packet to player:" + player.getName());
		}
	}
	
	public static void sendActionBar(Player player, String message)
	{
		if(getVersion().equals(V.R17))
		{
			return;
		}
		
		try
		{
			NMS.instance().packetActionTitle(player, message);
		}
		
		catch(Exception e)
		{
			React.fail(e, "Failed to send actionbar packet to player:" + player.getName() + " msg:" + message);
		}
	}
}
