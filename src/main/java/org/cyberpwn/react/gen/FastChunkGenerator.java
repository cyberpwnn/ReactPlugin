package org.cyberpwn.react.gen;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

public class FastChunkGenerator extends ChunkGenerator
{
	@Override
	public boolean canSpawn(World world, int x, int z)
	{
		return true;
	}
	
	public int xyzToByte(int x, int y, int z)
	{
		return (x * 16 + z) * 128 + y;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public byte[] generate(World world, Random random, int x, int z)
	{
		byte[] result = new byte[32768];
		
		int y = 0;
		
		for(int cx = 0; cx < 16; cx++)
		{
			for(int cz = 0; cz < 16; cz++)
			{
				result[xyzToByte(cx, y, cz)] = (byte) Material.SMOOTH_BRICK.getId();
			}
		}
		
		return result;
	}
	
	@Override
	public List<BlockPopulator> getDefaultPopulators(World world)
	{
		return Arrays.asList((BlockPopulator) new FastPopulator());
	}
}
