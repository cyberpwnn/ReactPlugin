package com.volmit.react;

import com.volmit.react.core.SampledValue;
import com.volmit.react.sample.ReactSampler;
import com.volmit.react.sample.Sampler;
import com.volmit.react.util.GList;
import com.volmit.react.util.TICK;

public class SampleController
{
	private GList<Sampler> samplers;
	
	public SampleController()
	{
		samplers = new GList<Sampler>();
	}
	
	public void start()
	{
		registerSampler(new ReactSampler("TPS", 1)
		{
			@Override
			public void onSample(SampledValue alloc)
			{
				alloc.setDouble(TICK.tps);
			}
			
			@Override
			public void onConfigure()
			{
				setRollThreshold(20);
			}
		});
	}
	
	public Sampler getSampler(String name)
	{
		for(Sampler i : samplers)
		{
			if(i.getName().equals(name))
			{
				return i;
			}
		}
		
		return null;
	}
	
	public void tick()
	{
		for(Sampler i : samplers)
		{
			i.onPreSample();
		}
	}
	
	public void registerSampler(Sampler s)
	{
		samplers.add(s);
	}
}
