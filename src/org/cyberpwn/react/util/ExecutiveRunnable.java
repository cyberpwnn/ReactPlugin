package org.cyberpwn.react.util;

public class ExecutiveRunnable<T> implements Runnable
{
	private T next;
	
	public void run(T next)
	{
		this.next = next;
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