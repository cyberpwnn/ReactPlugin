package org.cyberpwn.react.util;

public class SampledEvent extends ReactEvent
{
	private final GTime timeStamp;
	
	public SampledEvent(GTime timeStamp)
	{
		super();
		
		this.timeStamp = timeStamp;
	}
	
	public GTime getTimeStamp()
	{
		return timeStamp;
	}
}
