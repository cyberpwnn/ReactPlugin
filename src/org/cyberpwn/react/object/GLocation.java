package org.cyberpwn.react.object;

import java.io.Serializable;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class GLocation implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private double x;
	private double y;
	private double z;
	private int blockX;
	private int blockY;
	private int blockZ;
	private float yaw;
	private float pitch;
	private String world;
	
	public GLocation(Location location)
	{
		x = location.getX();
		y = location.getY();
		z = location.getZ();
		blockX = location.getBlockX();
		blockY = location.getBlockY();
		blockZ = location.getBlockZ();
		yaw = location.getYaw();
		pitch = location.getPitch();
		world = location.getWorld().getName();
	}
	
	public Location toLocation()
	{
		if(Bukkit.getServer().getWorld(world) == null)
		{
			world = Bukkit.getServer().getWorlds().get(0).getName();
		}
		
		return new Location(Bukkit.getServer().getWorld(world), x, y, z, yaw, pitch);
	}
	
	public double getX()
	{
		return x;
	}
	
	public void setX(double x)
	{
		this.x = x;
	}
	
	public double getY()
	{
		return y;
	}
	
	public void setY(double y)
	{
		this.y = y;
	}
	
	public double getZ()
	{
		return z;
	}
	
	public void setZ(double z)
	{
		this.z = z;
	}
	
	public int getBlockX()
	{
		return blockX;
	}
	
	public int getBlockY()
	{
		return blockY;
	}
	
	public int getBlockZ()
	{
		return blockZ;
	}
	
	public float getYaw()
	{
		return yaw;
	}
	
	public void setYaw(float yaw)
	{
		this.yaw = yaw;
	}
	
	public float getPitch()
	{
		return pitch;
	}
	
	public void setPitch(float pitch)
	{
		this.pitch = pitch;
	}
	
	public String getWorld()
	{
		return world;
	}
	
	public void setWorld(String world)
	{
		this.world = world;
	}
}
