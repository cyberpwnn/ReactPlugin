package org.cyberpwn.react.map;

import org.cyberpwn.react.util.GList;

public class MapData
{
	private final GList<MapSample> samples;
	private final MapSample dummy;
	
	public MapData()
	{
		this.samples = new GList<MapSample>();
		this.dummy = new MapSample(0, 1, 0, 1);
	}
	
	public int size()
	{
		return samples.size();
	}
	
	public void put(MapSample sample)
	{
		if(samples.size() > 128)
		{
			samples.remove(0);
		}
		
		samples.add(sample);
	}
	
	public MapSample get(int x)
	{
		if(samples.size() - 1 < x)
		{
			return dummy;
		}
		
		return samples.get(x);
	}
}
