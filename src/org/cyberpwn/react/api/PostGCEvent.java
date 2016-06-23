package org.cyberpwn.react.api;

import org.cyberpwn.react.util.GTime;

public class PostGCEvent extends SampledEvent
{
	private final long size;
	private final GTime time;
	
	public PostGCEvent(GTime timeStamp, long size, GTime time)
	{
		super(timeStamp);
		
		this.size = size;
		this.time = time;
	}
	
	public long getSize()
	{
		return size;
	}
	
	public GTime getTime()
	{
		return time;
	}
}
