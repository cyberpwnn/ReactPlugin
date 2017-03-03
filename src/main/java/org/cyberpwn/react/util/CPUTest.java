package org.cyberpwn.react.util;

import java.util.Random;

public class CPUTest
{
	public static long singleThreaded(int ms)
	{
		long m = 0;
		long ns = System.nanoTime();
		
		Random r = new Random(1234554321);
		
		while(System.nanoTime() - ns < (ms * 1000000))
		{
			Math.sqrt(1 + r.nextDouble() * (r.nextDouble() * 1000));
			Math.cbrt(1 + r.nextDouble() * (r.nextDouble() * 1000));
			Math.pow(1010 * r.nextDouble(), r.nextDouble() / 1.1);
			m++;
		}
		
		return m / ms;
	}
	
	public static long multiThreaded(int ms, int threads)
	{
		long[] total = {0};
		int[] th = {threads};
		
		for(int i = 0; i < threads; i++)
		{
			new ASYNC()
			{
				@Override
				public void async()
				{
					long v = singleThreaded(ms);
					total[0] += v;
					th[0]--;
				}
			};
		}
		
		long msd = ms * 2;
		
		while(th[0] > 0)
		{
			try
			{
				Thread.sleep(1);
				msd--;
				
				if(msd < 0)
				{
					break;
				}
			}
			
			catch(InterruptedException e)
			{
				
			}
		}
		
		return (long) ((double) total[0]);
	}
}
