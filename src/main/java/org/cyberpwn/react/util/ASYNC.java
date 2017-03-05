package org.cyberpwn.react.util;

import org.cyberpwn.react.React;

public abstract class ASYNC
{
	public ASYNC()
	{
		if(React.STOPPING)
		{
			return;
		}
		
		new Thread()
		{
			@Override
			public void run()
			{
				async();
			}
		}.start();
	}
	
	public abstract void async();
}
