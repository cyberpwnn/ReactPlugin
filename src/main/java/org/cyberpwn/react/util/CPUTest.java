package org.cyberpwn.react.util;

public class CPUTest
{
	public static long singleThreaded(int ms)
	{
		long m = 0;
		long ns = System.nanoTime();
		
		while(System.nanoTime() - ns < (ms * 1000000))
		{
			Math.sqrt(1 + Math.random() * (Math.random() * 1000));
			Math.cbrt(1 + Math.random() * (Math.random() * 1000));
			Math.pow(1010 * Math.random(), Math.random() / 1.1);
			m++;
		}
		
		return m / ms;
	}
}
