package org.cyberpwn.react.util;

import java.io.Serializable;

public class GTimeBank implements Serializable
{
	private static final long serialVersionUID = 1L;
	private final int version;
	private GMap<String, GList<Double>> data;
	
	public GTimeBank()
	{
		this.data = new GMap<String, GList<Double>>();
		this.version = 1;
	}
	
	public GList<Double> get(String name)
	{
		return data.get(name);
	}
	
	public void push(String name, double value)
	{
		if(data == null)
		{
			this.data = new GMap<String, GList<Double>>();
		}
		
		if(name == null)
		{
			return;
		}
		
		if(!data.containsKey(name))
		{
			data.put(name, new GList<Double>());
		}
		
		data.get(name).add(value);
		
		if(data.get(name).size() > 128)
		{
			data.get(name).remove(0);
		}
	}
	
	public GMap<String, GList<Double>> getData()
	{
		return data;
	}
	
	public void setData(GMap<String, GList<Double>> data)
	{
		this.data = data;
	}
	
	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}
	
	public int getVersion()
	{
		return version;
	}
}
