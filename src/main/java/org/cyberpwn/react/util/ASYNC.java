package org.cyberpwn.react.util;

public abstract class ASYNC
{
	public ASYNC()
	{
		FAU.async(new Runnable()
		{
			@Override
			public void run()
			{
				async();
			}
		});
	}
	
	public abstract void async();
}
