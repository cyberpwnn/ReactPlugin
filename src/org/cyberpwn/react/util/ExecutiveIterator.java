package org.cyberpwn.react.util;

import java.util.Iterator;

import org.cyberpwn.react.object.GList;

public class ExecutiveIterator<T>
{
	private Iterator<T> it;
	
	public ExecutiveIterator(final Long lim, GList<T> data, final ExecutiveRunnable<T> runnable, final Runnable finish)
	{
		this.it = data.iterator();
		
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