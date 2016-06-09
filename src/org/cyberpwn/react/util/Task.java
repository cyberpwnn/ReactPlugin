package org.cyberpwn.react.util;

import org.cyberpwn.react.React;

public class Task implements Runnable
{
	private React pl;
	private int[] task;
	
	public Task(React pl, int interval)
	{
		this.pl = pl;
		this.task = new int[] { pl.scheduleSyncRepeatingTask(0, interval, this) };
	}
	
	@Override
	public void run()
	{
		
	}
	
	public void cancel()
	{
		pl.cancelTask(task[0]);
	}
}
