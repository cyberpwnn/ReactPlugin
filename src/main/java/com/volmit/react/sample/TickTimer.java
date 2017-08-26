package com.volmit.react.sample;

import com.volmit.react.React;
import com.volmit.react.util.M;

public class TickTimer extends Thread
{
	private long startSleep;
	public static long tickTime;
	private boolean sleep;
	
	public TickTimer()
	{
		sleep = false;
		startSleep = 0;
		tickTime = 0;
	}
	
	@Override
	public void run()
	{
		while(!interrupted())
		{
			try
			{
				Thread.sleep(1);
				
				if(!sleep && !React.mainThread.getState().equals(State.RUNNABLE))
				{
					sleep = true;
					startSleep = M.ns();
				}
				
				if(sleep && React.mainThread.getState().equals(State.RUNNABLE))
				{
					sleep = false;
					tickTime = M.ns() - startSleep;
				}
				
				if(!sleep && (M.ns() - startSleep) / 1000000 > 1000)
				{
					React.i.reportSpike();
				}
			}
			
			catch(InterruptedException e)
			{
				
			}
		}
	}
}
