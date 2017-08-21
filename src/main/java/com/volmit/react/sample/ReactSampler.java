package com.volmit.react.sample;

import com.volmit.react.core.SleeperPayload;

public abstract class ReactSampler extends BasicSampler implements Sampler
{
	public ReactSampler(String name, String description, int intervalTicks)
	{
		super(name, description, new SleeperPayload(intervalTicks * 50, true));
		
		onConfigure();
	}
	
	public ReactSampler(String name, int intervalTicks)
	{
		super(name, name, new SleeperPayload(intervalTicks * 50, true));
		
		onConfigure();
	}
	
	public abstract void onConfigure();
}
