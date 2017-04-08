package org.cyberpwn.react.api;

import org.cyberpwn.react.React;
import org.cyberpwn.react.queue.Execution;
import org.cyberpwn.react.queue.ParallelThread;
import org.cyberpwn.react.util.F;
import net.md_5.bungee.api.ChatColor;

public class U
{
	public static React i()
	{
		return React.instance();
	}
	
	public static void queue(Execution e)
	{
		i().getPoolManager().queue(e);
	}
	
	public static double getTPS(int thread)
	{
		return getThreads()[thread].getInfo().getTicksPerSecondAverage();
	}
	
	public static int getThreadCount()
	{
		return i().getPoolManager().getSize();
	}
	
	public static ParallelThread[] getThreads()
	{
		return i().getPoolManager().getThreads();
	}
	
	public static String status(boolean acc)
	{
		String st = "";
		
		for(ParallelThread i : getThreads())
		{
			String stat = "";
			double tps = i.getInfo().getTicksPerSecondAverage();
			int id = i.getInfo().getId();
			int q = i.getInfo().getQueuedSize();
			
			stat = ChatColor.LIGHT_PURPLE + F.f(tps, 2) + ChatColor.GRAY + "[" + (acc ? F.f(q) : id) + "]";
			st += stat + " ";
		}
		
		return st;
	}
}
