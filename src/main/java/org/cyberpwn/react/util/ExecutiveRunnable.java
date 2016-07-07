package org.cyberpwn.react.util;

public class ExecutiveRunnable<T> implements Runnable
{
	private T next;
	private Boolean cancelled;
	
	public void run(T next)
	{
		this.cancelled = false;
		this.next = next;
		run();
	}
	
	public void cancel()
	{
		cancelled = true;
	}
	
	public Boolean isCancelled()
	{
		return cancelled;
	}
	
	@Override
	public void run()
	{
		
	}
	
	public T next()
	{
		return next;
	}
}