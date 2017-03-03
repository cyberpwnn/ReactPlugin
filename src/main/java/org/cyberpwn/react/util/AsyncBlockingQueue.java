package org.cyberpwn.react.util;

public class AsyncBlockingQueue
{
	private int maxThreads;
	private int currentThreads;
	private GList<Runnable> queue;
	
	public AsyncBlockingQueue(int maxThreads)
	{
		this.maxThreads = maxThreads;
		currentThreads = 0;
		queue = new GList<Runnable>();
	}
	
	public void queue(Runnable r)
	{
		queue.add(r);
	}
	
	public void execute()
	{
		boolean[] f = {false};
		
		new ASYNC()
		{
			@Override
			public void async()
			{
				while(!queue.isEmpty())
				{
					chomp();
				}
				
				f[0] = true;
			}
		};
		
		while(!f[0])
		{
			try
			{
				Thread.sleep(10);
			}
			
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
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
