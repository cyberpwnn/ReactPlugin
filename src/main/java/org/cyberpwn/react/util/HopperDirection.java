package org.cyberpwn.react.util;

import org.bukkit.block.BlockFace;

public enum HopperDirection
{
	DOWN, NORTH, SOUTH, EAST, WEST;
	
	public BlockFace toFace()
	{
		switch(this)
		{
			case DOWN:
				return BlockFace.DOWN;
			case EAST:
				return BlockFace.EAST;
			case NORTH:
				return BlockFace.NORTH;
			case SOUTH:
				return BlockFace.SOUTH;
			case WEST:
				return BlockFace.WEST;
		}
		
		return BlockFace.DOWN;
	}
}
