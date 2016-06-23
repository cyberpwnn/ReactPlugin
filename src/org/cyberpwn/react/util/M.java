package org.cyberpwn.react.util;

import org.bukkit.Chunk;

public class M
{
	public static double dof(double base, double range)
	{
		if(base == 0)
		{
			return 0;
		}
		
		return ((range - base) / base);
	}
	
	public static double tps(long ns, int rad)
	{
		return (20.0 * (ns / 50000000.0)) / rad;
	}
	
	public static int ticksFromNS(long ns)
	{
		return (int) (ns / 50000000.0);
	}
	
	public static long ns()
	{
		return System.nanoTime();
	}
	
	public static long ms()
	{
		return System.currentTimeMillis();
	}
	
	public static double avg(GList<Double> doubles)
	{
		double a = 0.0;
		
		for(double i : doubles)
		{
			a += i;
		}
		
		return a / doubles.size();
	}
	
	public static void lim(GList<Double> doubles, int limit)
	{
		while(doubles.size() > limit)
		{
			doubles.remove(0);
		}
	}
	
	public static GList<Chunk> getChunks(Chunk center, int rad)
	{
		GList<Chunk> ck = new GList<Chunk>();
		
		int nx = center.getX() - rad;
		int nz = center.getZ() - rad;
		
		for(int i = nx; i < nx + (rad * 2); i++)
		{
			for(int j = nz; j < nz + (rad * 2); j++)
			{
				ck.add(center.getWorld().getChunkAt(i, j));
			}
		}
		
		return ck;
	}
}
