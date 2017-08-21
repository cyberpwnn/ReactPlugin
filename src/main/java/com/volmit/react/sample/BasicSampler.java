package com.volmit.react.sample;

import com.volmit.react.core.SampledValue;
import com.volmit.react.core.SleeperPayload;
import com.volmit.react.core.Trend;
import com.volmit.react.util.Average;

public abstract class BasicSampler implements Sampler
{
	private String name;
	private String description;
	private SampledValue value;
	private SampledValue rawValue;
	private SampledValue allocValue;
	private Average average;
	private SleeperPayload sleeperPayload;
	private Trend trend;
	
	@Override
	public SleeperPayload getSleeper()
	{
		return sleeperPayload;
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	@Override
	public String getDescription()
	{
		return description;
	}
	
	@Override
	public void onPreSample()
	{
		if(getSleeper().trigger())
		{
			SampledValue alloc = allocValue;
			alloc.setDouble(0);
			onSample(alloc);
			onPostSample(alloc);
			
			for(int i = 0; i < getSleeper().recycle(); i++)
			{
				onPreSample();
			}
		}
	}
	
	@Override
	public abstract void onSample(SampledValue alloc);
	
	@Override
	public void onPostSample(SampledValue alloc)
	{
		rawValue.setDouble(alloc.getDouble());
		average.put(rawValue.getDouble());
		value.setDouble(average.getAverage());
		trend = average.getTrend();
	}
	
	@Override
	public SampledValue getValue()
	{
		return value;
	}
	
	@Override
	public SampledValue getRawValue()
	{
		return rawValue;
	}
	
	@Override
	public int getRollThreshold()
	{
		return average.getLimit();
	}
	
	@Override
	public void setRollThreshold(int threshold)
	{
		average.setLimit(threshold);
	}
	
	@Override
	public Trend getValueTrend()
	{
		return trend;
	}
}
