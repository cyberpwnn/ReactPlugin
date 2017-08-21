package com.volmit.react.util;

import com.volmit.react.core.Trend;

public class Average
{
	private int limit;
	private GList<Double> data;
	private double average;
	
	public Average(int limit)
	{
		this.limit = limit;
		data = new GList<Double>();
		average = 0.0;
	}
	
	public Trend getTrend()
	{
		double avg = average;
		double max = max();
		int size = data.size();
		double pcb = avg / max;
		
		if(size > 4)
		{
			GList<GList<Double>> dat = data.split();
			double a1 = M.avg(dat.get(0));
			double a2 = M.avg(dat.get(1));
			
			if(Math.abs(a1 - a2) > Math.abs(avg - (min() / max)))
			{
				if(a1 > a2)
				{
					return Trend.DECREASE;
				}
				
				else if(a1 < a2)
				{
					return Trend.INCREASE;
				}
			}
		}
		
		else if(pcb > 0.7 || pcb < 0.3)
		{
			return Trend.SPIKY;
		}
		
		return Trend.SMOOTH;
	}
	
	public double min()
	{
		double k = Double.MAX_VALUE;
		
		for(Double i : data)
		{
			if(i < k)
			{
				k = i;
			}
		}
		
		return k;
	}
	
	public double max()
	{
		double k = Double.MIN_VALUE;
		
		for(Double i : data)
		{
			if(i > k)
			{
				k = i;
			}
		}
		
		return k;
	}
	
	public double last()
	{
		return data.get(data.size() - 1);
	}
	
	public void put(double d)
	{
		data.add(d);
		M.lim(data, limit);
		average = M.avg(data);
	}
	
	public int getLimit()
	{
		return limit;
	}
	
	public void setLimit(int limit)
	{
		this.limit = limit;
	}
	
	public GList<Double> getData()
	{
		return data;
	}
	
	public void setData(GList<Double> data)
	{
		this.data = data;
	}
	
	public double getAverage()
	{
		return average;
	}
}
