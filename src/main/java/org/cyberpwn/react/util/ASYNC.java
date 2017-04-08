package org.cyberpwn.react.util;

import org.cyberpwn.react.React;
import org.cyberpwn.react.queue.Execution;

public abstract class ASYNC
{
	public static int k = 0;
	
	public ASYNC()
	{
		if(React.STOPPING)
		{
			return;
		}
		
		if(React.instance().getPoolManager() == null)
		{
			async();
			return;
		}
		
		React.instance().getPoolManager().queue(new Execution()
		{
			@Override
			public void run()
			{
				async();
			}
		});
	}
	
	public abstract void async();
}
