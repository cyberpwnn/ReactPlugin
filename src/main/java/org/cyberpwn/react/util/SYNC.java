package org.cyberpwn.react.util;

public abstract class SYNC
{
	public SYNC()
	{
		FAU.sync(new Runnable()
		{
			@Override
			public void run()
			{
				sync();
			}
		});
	}
	
	public abstract void sync();
}
