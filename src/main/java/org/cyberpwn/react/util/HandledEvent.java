package org.cyberpwn.react.util;

public abstract class HandledEvent
{
	public HandledEvent()
	{
		try
		{
			execute();
		}
		
		catch(Throwable e)
		{
			
		}
	}
	
	public abstract void execute();
}
