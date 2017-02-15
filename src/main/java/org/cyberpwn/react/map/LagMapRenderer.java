package org.cyberpwn.react.map;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.cyberpwn.react.React;
import org.cyberpwn.react.util.LagMap;

public class LagMapRenderer extends MapRenderer
{
	@SuppressWarnings("deprecation")
	@Override
	public void render(MapView v, MapCanvas c, Player p)
	{
		for(int i = 0; i < 127; i++)
		{
			for(int j = 0; j < 127; j++)
			{
				c.setPixel(i, j, MapPalette.WHITE);
			}
		}
		
		LagMap map = React.instance().getLagMapController().getMap().crop(p.getLocation());
		
		for(Chunk i : p.getWorld().getLoadedChunks())
		{
			Chunk cc = i;
			Chunk vv = p.getLocation().getChunk();
			Biome b = i.getBlock(8, 63, 8).getBiome();
			
			if(b.equals(Biome.OCEAN) || b.equals(Biome.DEEP_OCEAN) || b.equals(Biome.FROZEN_OCEAN))
			{
				c.setPixel(cc.getX() - vv.getX() + 64, cc.getZ() - vv.getZ() + 64, MapPalette.PALE_BLUE);
			}
			
			if(b.equals(Biome.BEACH) || b.equals(Biome.FROZEN_RIVER) || b.equals(Biome.RIVER))
			{
				c.setPixel(cc.getX() - vv.getX() + 64, cc.getZ() - vv.getZ() + 64, MapPalette.BLUE);
			}
			
			if(b.equals(Biome.DESERT) || b.equals(Biome.DESERT_HILLS) || b.equals(Biome.DESERT_MOUNTAINS))
			{
				c.setPixel(cc.getX() - vv.getX() + 64, cc.getZ() - vv.getZ() + 64, MapPalette.LIGHT_BROWN);
			}
			
			if(b.equals(Biome.SWAMPLAND) || b.equals(Biome.SWAMPLAND_MOUNTAINS) || b.toString().contains("TAIGA"))
			{
				c.setPixel(cc.getX() - vv.getX() + 64, cc.getZ() - vv.getZ() + 64, MapPalette.DARK_GREEN);
			}
			
			else
			{
				c.setPixel(cc.getX() - vv.getX() + 64, cc.getZ() - vv.getZ() + 64, MapPalette.LIGHT_GREEN);
			}
		}
		
		for(Location i : map.getMap().k())
		{
			if(map.getMap().get(i) < 100)
			{
				continue;
			}
			
			Chunk cc = i.getChunk();
			Chunk vv = p.getLocation().getChunk();
			c.setPixel(cc.getX() - vv.getX() + 64, cc.getZ() - vv.getZ() + 64, MapPalette.RED);
		}
	}
}
