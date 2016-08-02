package org.cyberpwn.react.util;

import java.io.Serializable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlockModification implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String world;
	private int x;
	private int y;
	private int z;
	private int id;
	private byte data;
	
	@SuppressWarnings("deprecation")
	public BlockModification(Block b)
	{
		this.world = b.getWorld().getName();
		this.x = b.getLocation().getBlockX();
		this.y = b.getLocation().getBlockY();
		this.z = b.getLocation().getBlockZ();
		this.id = b.getTypeId();
		this.data = b.getData();
	}
	
	public Location getLocation()
	{
		return new Location(Bukkit.getWorld(world), x, y, z);
	}
	
	@SuppressWarnings("deprecation")
	public void apply()
	{
		Location l = getLocation();
		l.getBlock().setType(Material.getMaterial(id));
		l.getBlock().setData(data);
	}
}
