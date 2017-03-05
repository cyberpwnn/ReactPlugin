package org.cyberpwn.react.util;

import org.cyberpwn.react.React;

public abstract class AsyncQueue
{
	private int maxThreads;
	private int currentThreads;
	private GList<Runnable> queue;
	
	public AsyncQueue(int maxThreads)
	{
		this.maxThreads = maxThreads;
		currentThreads = 0;
		queue = new GList<Runnable>();
	}
	
	public void queue(Runnable r)
	{
		queue.add(r);
	}
	
	public abstract void onComplete();
	
	public void start()
	{
		new ASYNC()
		{
			@Override
			public void async()
			{
				while(!queue.isEmpty())
				{
					if(React.STOPPING)
					{
						break;
					}
					
					chomp();
				}
				
				if(React.STOPPING)
				{
					return;
				}
				
				onComplete();
			}
		};
	}
	
	public void chomp()
	{
		if(maxThreads - currentThreads > 0)
		{
			while(currentThreads < maxThreads && !queue.isEmpty())
			{
				Runnable r = queue.pop();
				
				currentThreads++;
				
				new ASYNC()
				{
					@Override
					public void async()
					{
						r.run();
						currentThreads--;
					}
				};
			}
		}
	}
	
	public int getMaxThreads()
	{
		return maxThreads;
	}
	
	public int getCurrentThreads()
	{
		return currentThreads;
	}
	
	public GList<Runnable> getQueue()
	{
		return queue;
	}
}
