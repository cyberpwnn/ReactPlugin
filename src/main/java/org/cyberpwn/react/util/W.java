package org.cyberpwn.react.util;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class W
{
	public static GList<Block> getFaces(Block b)
	{
		GList<Block> blocks = new GList<Block>();

		blocks.add(b.getRelative(BlockFace.UP));
		blocks.add(b.getRelative(BlockFace.DOWN));
		blocks.add(b.getRelative(BlockFace.NORTH));
		blocks.add(b.getRelative(BlockFace.SOUTH));
		blocks.add(b.getRelative(BlockFace.EAST));
		blocks.add(b.getRelative(BlockFace.WEST));
		
		return blocks;
	}
	
	public static boolean skyn(Location l)
	{
		for(int i = 255; i > 0; i--)
		{
			Material m = l.getWorld().getBlockAt(new Location(l.getWorld(), l.getX(), i, l.getZ())).getType();
			
			if(m.isSolid())
			{
				if(i > l.getY())
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
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
	
	public static GList<Chunk> crad(Chunk c, int rad)
	{
		GList<Chunk> cx = new GList<Chunk>();
		
		for(int i = c.getX() - rad; i < c.getX() + rad; i++)
		{
			for(int j = c.getZ() - rad; j < c.getZ() + rad; j++)
			{
				if(c.getWorld().isChunkLoaded(i, j))
				{
					cx.add(c.getWorld().getChunkAt(i, j));
				}
			}
		}
		
		return cx.qadd(c);
	}
}
