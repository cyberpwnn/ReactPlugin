package org.cyberpwn.react.nms;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.cyberpwn.react.util.Default;

public class NMS17 implements AbstractNMS
{
	@Override
	public boolean isCapable(String version)
	{
		return version.startsWith("1.7");
	}
	
	@Override
	public boolean isCapable()
	{
		return isCapable(Bukkit.getBukkitVersion());
	}
	
	@Override
	public int ping(Player player)
	{
		try
		{
			String bukkitversion = Bukkit.getServer().getClass().getPackage().getName().substring(23);
			Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit." + bukkitversion + ".entity.CraftPlayer");
			Object handle = craftPlayer.getMethod("getHandle").invoke(player);
			Integer ping = (Integer) handle.getClass().getDeclaredField("ping").get(handle);
			
			return ping.intValue();
		}
		
		catch(Exception e)
		{
			
		}
		
		return -1;
	}
	
	@Override
	public void packetTitle(Player player, String title, String subTitle)
	{
		packetTitle(player, title);
		packetSubTitle(player, subTitle);
	}
	
	@Override
	public void packetTitle(Player player, String title, String subTitle, String actionTitle)
	{
		packetTitle(player, title);
		packetSubTitle(player, subTitle);
		packetActionTitle(player, actionTitle);
	}
	
	@Override
	public void packetTitle(Player player, String title, String subTitle, int in, int out, int stay)
	{
		packetTitle(player, title, in, out, stay);
		packetSubTitle(player, subTitle, in, out, stay);
	}
	
	@Override
	public void packetTitle(Player player, String title, String subTitle, String actionTitle, int in, int out, int stay)
	{
		packetTitle(player, title, in, out, stay);
		packetSubTitle(player, subTitle, in, out, stay);
		packetActionTitle(player, actionTitle);
	}
	
	@Override
	public void packetTitle(Player player, String title)
	{
		packetTitle(player, title, Default.TITLE_FADE_IN, Default.TITLE_FADE_OUT, Default.TITLE_FADE_STAY);
	}
	
	@Override
	public void packetSubTitle(Player player, String title)
	{
		packetSubTitle(player, title, Default.TITLE_FADE_IN, Default.TITLE_FADE_OUT, Default.TITLE_FADE_STAY);
	}
	
	@Override
	public void packetTitle(Player player, String title, int in, int out, int stay)
	{
		
	}
	
	@Override
	public void packetSubTitle(Player player, String title, int in, int out, int stay)
	{
		
	}
	
	@Override
	public void packetActionTitle(Player player, String title)
	{
		
	}
	
	public String getEntityName(Entity e)
	{
		return null;
	}
	
	@Override
	public void clearTitle(Player player)
	{
		
	}
	
	@Override
	public void relight(Location location)
	{
		
	}
}
