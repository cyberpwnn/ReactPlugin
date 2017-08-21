package com.volmit.react.util;

public class TICK
{
	public static long last = 0;
	public static long tick = 0;
	public static long length = 0;
	public static double tps = 20;
	public static double choke = 0;
	
	public static void tick()
	{
		tick++;
		
		if(last == 0)
		{
			last = M.ms();
			tps = 20;
			
			return;
		}
		
		length = (M.ms() - last) < 50 ? 50 : M.ms() - last;
		last = M.ms();
		choke = 50.0 / length;
		tps = choke * 20.0;
	}
}
