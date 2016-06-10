package org.cyberpwn.react.network;

import org.cyberpwn.react.React;
import org.cyberpwn.react.sampler.Samplable;
import org.cyberpwn.react.util.GMap;

public class ReactData
{
	private GMap<String, Double> samples;
	
	public ReactData()
	{
		this.samples = new GMap<String, Double>();
	}
	
	public void sample(React react)
	{
		for(Samplable s : react.getSampleController().getSamples().keySet())
		{
			samples.put(s.getName().toLowerCase(), s.get().getDouble());
		}
	}
	
	public GMap<String, Double> getSamples()
	{
		return samples;
	}
}
