package org.cyberpwn.react.api;

import org.cyberpwn.react.util.GTime;
import org.cyberpwn.react.util.SampledEvent;

public class SpikeEvent extends SampledEvent
{
	private final GTime lockTime;
	
	public SpikeEvent(GTime timeStamp, GTime lockTime)
	{
		super(timeStamp);
		
		this.lockTime = lockTime;
	}
	
	public GTime getLockTime()
	{
		return lockTime;
	}
}
