package org.cyberpwn.react.util;

import org.cyberpwn.react.React;

public class TaskLater implements Runnable
{
	public TaskLater(int delay)
	{
		React.instance().scheduleSyncTask(delay, this);
	}
	
	@Override
	public void run()
	{
		
	}
}