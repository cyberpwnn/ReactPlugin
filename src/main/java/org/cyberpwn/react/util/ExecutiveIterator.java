package org.cyberpwn.react.util;

import java.util.Iterator;

public class ExecutiveIterator<T>
{
	private Iterator<T> it;
	
	public ExecutiveIterator(final Double lim, GList<T> data, final ExecutiveRunnable<T> runnable, final Runnable finish)
	{
		this(lim, data.iterator(), runnable, finish);
	}
	
	public ExecutiveIterator(final Double lim, Iterator<T> data, final ExecutiveRunnable<T> runnable, final Runnable finish)
	{
		this.it = data;
		
		new Task(0)
		{
			@Override
			public void run()
			{
				Long ns = M.ns();
				
				while(it.hasNext() && System.nanoTime() - ns < (lim * 1000000.0))
				{
					runnable.run(it.next());
					
					try
					{
						if(runnable.isCancelled())
						{
							cancel();
						}
					}
					
					catch(Exception ee)
					{
						
					}
				}
				
				if(!it.hasNext())
				{
					finish.run();
					cancel();
				}
			}
		};
	}
}