package org.cyberpwn.react.util;

import java.util.Iterator;

public class ExecutiveIterator<T>
{
	private Iterator<T> it;
	
	public ExecutiveIterator(final Long lim, GList<T> data, final ExecutiveRunnable<T> runnable, final Runnable finish)
	{
		this(lim, data.iterator(), runnable, finish);
	}
	
	public ExecutiveIterator(final Long lim, Iterator<T> data, final ExecutiveRunnable<T> runnable, final Runnable finish)
	{
		this.it = data;
		
		new Task(0)
		{
			public void run()
			{
				Long ms = M.ms();
				
				while(it.hasNext() && System.currentTimeMillis() - ms < lim)
				{
					runnable.run(it.next());
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