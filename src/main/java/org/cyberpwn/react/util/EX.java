package org.cyberpwn.react.util;

import org.cyberpwn.react.React;

public abstract class EX
{
	public EX()
	{
		if(!React.instance().getConfiguration().getBoolean("startup.multicore") || !React.shouldMulticore())
		{
			try
			{
				execute();
			}
			
			catch(Exception e)
			{
				
			}
			
			return;
		}
		
		React.instance().getTaskManager().getExecutor().execute(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					execute();
				}
				
				catch(Exception e)
				{
					
				}
			}
		});
	}
	
	public abstract void execute();
}
