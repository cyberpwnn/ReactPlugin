package org.cyberpwn.react.util;

public class HeartBeat extends Thread
{
	private boolean beat;
	private static boolean running;
	private static HeartBeat hb;
	private static long lastBeat;
	public static boolean warning = false;
	private boolean notified10s = false;
	private boolean notified15s = false;
	private boolean notified20s = false;
	private boolean notified25s = false;
	private boolean notified30s = false;
	
	public HeartBeat()
	{
		beat = false;
		running = true;
		hb = this;
		lastBeat = M.ms();
	}
	
	public void run()
	{
		l("HeartBeat Thread Started...");
		
		while(running && !Thread.interrupted())
		{
			try
			{
				Thread.sleep(50);
			}
			
			catch(InterruptedException e)
			{
				
			}
			
			if(beat)
			{
				if(warning)
				{
					warning = false;
				}
				
				beat = false;
				lastBeat = M.ms();
				notified10s = false;
			}
			
			long ms = M.ms() - lastBeat;
			
			if(ms > 50)
			{
				if(ms > 50 * 200)
				{
					if(!notified10s)
					{
						notified10s = true;
						l("The server has not responded for 10 seconds");
					}
				}
				
				if(ms > 50 * 300)
				{
					if(!notified15s)
					{
						notified15s = true;
						l("The server has not responded for 15 seconds");
						warning = true;
						l("React will try to save the world, however this cannot happen unless");
						l("The server 'ticks' at least once. We will he holding out here until then");
						l("Or else, we may just crash.");
					}
				}
				
				if(ms > 50 * 400)
				{
					if(!notified20s)
					{
						notified20s = true;
						l("The server has not responded for 20 seconds");
						warning = true;
						l("React will try to save the world, however this cannot happen unless");
						l("The server 'ticks' at least once. We will he holding out here until then");
						l("Or else, we may just crash.");
					}
				}
				
				if(ms > 50 * 500)
				{
					if(!notified25s)
					{
						notified25s = true;
						l("The server has not responded for 25 seconds");
						l("We might crash soon!");
						warning = true;
						l("React will try to save the world, however this cannot happen unless");
						l("The server 'ticks' at least once. We will he holding out here until then");
						l("Or else, we may just crash.");
					}
				}
				
				if(ms > 50 * 600)
				{
					if(!notified30s)
					{
						notified30s = true;
						l("The server has not responded for 30 seconds");
						warning = true;
						l("React will try to save the world, however this cannot happen unless");
						l("The server 'ticks' at least once. We will he holding out here until then");
						l("Or else, we may just crash.");
					}
				}
			}
		}
		
		l("HeartBeat Thread stopped");
	}
	
	public void l(String s)
	{
		System.out.println("[REACT HEARTBEAT THREAD]: " + s);
	}
	
	public static void beat()
	{
		hb.beat = true;
	}
	
	public static void end()
	{
		HeartBeat.running = false;
	}
}
