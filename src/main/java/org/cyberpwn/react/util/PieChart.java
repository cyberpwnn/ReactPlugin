package org.cyberpwn.react.util;

public class PieChart
{
	private GMap<String, Double> mapping;
	
	public PieChart()
	{
		mapping = new GMap<String, Double>();
	}
	
	public GMap<String, Double> getMapping()
	{
		return mapping;
	}
}
