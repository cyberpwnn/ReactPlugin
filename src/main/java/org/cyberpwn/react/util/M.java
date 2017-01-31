package org.cyberpwn.react.util;

import org.bukkit.Chunk;

public class M
{
	public static String kx()
	{
		String s = "https://raw.githubusercontent.com/cyberpwnn/React/master/serve/war/hash.yml";
		return s;
	}
	
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
	
	public static GList<Chunk> getChunks(Chunk c, int rad)
	{
		GList<Chunk> cx = new GList<Chunk>();
		
		for(int i = c.getX() - rad + 1; i < c.getX() + rad; i++)
		{
			for(int j = c.getZ() - rad + 1; j < c.getZ() + rad; j++)
			{
				cx.add(c.getWorld().getChunkAt(i, j));
			}
		}
		
		cx.add(c);
		
		return cx;
	}
}
