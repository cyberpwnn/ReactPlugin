package org.cyberpwn.react.object;

import java.io.Serializable;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;

public class GChunk implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private Integer x;
	private Integer z;
	private String world;
	
	public GChunk(Chunk chunk)
	{
		this(chunk.getX(), chunk.getZ(), chunk.getWorld().getName());
	}
	
	public GChunk(Location location)
	{
		this(location.getChunk().getX(), location.getChunk().getZ(), location.getChunk().getWorld().getName());
	}
	
	public GChunk(int x, int z, String world)
	{
		this.x = x;
		this.z = z;
		this.world = world;
	}
	
	public boolean equals(Object o)
	{
		if(o != null)
		{
			if(o instanceof GChunk)
			{
				GChunk gc = (GChunk) o;
				
				if(this.x == gc.x && this.z == gc.z && this.world.equals(gc.world))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean isChunk(Chunk c)
	{
		if(this.x == c.getX() && this.z == c.getZ() && this.world.equals(c.getWorld().getName()))
		{
			return true;
		}
		
		return false;
	}
	
	public Integer getX()
	{
		return x;
	}
	
	public void setX(Integer x)
	{
		this.x = x;
	}
	
	public Integer getZ()
	{
		return z;
	}
	
	public void setZ(Integer z)
	{
		this.z = z;
	}
	
	public String getWorld()
	{
		return world;
	}
	
	public void setWorld(String world)
	{
		this.world = world;
	}
	
	public Chunk toChunk()
	{
		return Bukkit.getServer().getWorld(world).getChunkAt(x, z);
	}
	
	public String toString()
	{
		return "Chunk: " + world + " @ [" + x + "," + z + "]";
	}
}
