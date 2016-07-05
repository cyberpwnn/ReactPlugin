package org.cyberpwn.react.util;

import org.bukkit.plugin.IllegalPluginAccessException;
import org.cyberpwn.react.React;

public class Task implements Runnable
{
	private int[] task;
	private boolean running;
	
	public Task(int interval)
	{
		try
		{
			this.running = true;
			this.task = new int[] { React.instance().scheduleSyncRepeatingTask(0, interval, this) };
		}
		
		catch(IllegalPluginAccessException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void run()
	{
		
	}
	
	public void cancel()
	{
		React.instance().cancelTask(task[0]);
		running = false;
	}

	public boolean isRunning()
	{
		return running;
	}
}
