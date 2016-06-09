package org.cyberpwn.react.api;

import org.cyberpwn.react.event.SampledEvent;
import org.cyberpwn.react.object.GTime;

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
