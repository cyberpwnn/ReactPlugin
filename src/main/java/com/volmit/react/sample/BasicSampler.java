package com.volmit.react.sample;

import com.volmit.react.util.Average;
import com.volmit.react.util.Trend;

public abstract class BasicSampler implements Sampler
{
	private String name;
	private String description;
	private SampledValue value;
	private SampledValue rawValue;
	private SampledValue allocValue;
	private Average average;
	private Trend trend;
	
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
		SampledValue alloc = allocValue;
		alloc.setDouble(0);
		onSample(alloc);
		onPostSample(alloc);
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
	
	public Trend getTrend()
	{
		return trend;
	}
}
