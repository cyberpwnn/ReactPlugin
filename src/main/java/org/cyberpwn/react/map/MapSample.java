package org.cyberpwn.react.map;

public class MapSample
{
	private final double memoryPercent;
	private final double stability;
	private final double mahsPercent;
	private final double tps;
	
	public MapSample(double memoryPercent, double stability, double mahsPercent, double tps)
	{
		this.memoryPercent = memoryPercent;
		this.stability = stability;
		this.mahsPercent = mahsPercent;
		this.tps = tps;
	}
	
	public double getTps()
	{
		return tps;
	}
	
	public double getMemoryPercent()
	{
		return memoryPercent;
	}
	
	public double getStability()
	{
		return stability;
	}
	
	public double getMahsPercent()
	{
		return mahsPercent;
	}
}
