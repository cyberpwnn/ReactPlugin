package com.volmit.react.sample;

import com.volmit.react.util.Trend;

public interface Sampler
{
	public String getName();
	
	public String getDescription();
	
	public void onPreSample();
	
	public void onSample(SampledValue alloc);
	
	public void onPostSample(SampledValue alloc);
	
	public SampledValue getValue();
	
	public SampledValue getRawValue();
	
	public Trend getValueTrend();
	
	public int getRollThreshold();
	
	public void setRollThreshold(int threshold);
}
