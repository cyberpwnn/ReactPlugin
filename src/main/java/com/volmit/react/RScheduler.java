package com.volmit.react;

import com.volmit.react.util.Execution;
import com.volmit.react.util.GList;

public class RScheduler extends Thread
{
	private GList<Execution> sched;
	private GList<Execution> pched;
	
	public RScheduler()
	{
		super("RScheduler6");
		sched = new GList<Execution>();
		pched = new GList<Execution>();
	}
	
	public void r(Execution e)
	{
		React.pool.queue(e);
	}
	
	@Override
	public void run()
	{
		while(!interrupted())
		{
			for(Execution i : pched)
			{
				r(i);
			}
			
			for(Execution i : sched)
			{
				r(i);
			}
			
			sched.clear();
			
			try
			{
				sleep(50);
			}
			
			catch(InterruptedException e)
			{
				return;
			}
		}
	}
	
	public void schedRepeat(Execution e)
	{
		pched.add(e);
	}
	
	public void sched(Execution e)
	{
		sched.add(e);
	}
}
