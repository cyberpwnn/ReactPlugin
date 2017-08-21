package com.volmit.react.core;

import com.volmit.react.util.F;

public class SampledValue
{
	public double value;
	
	public SampledValue(double value)
	{
		this.value = value;
	}
	
	public int getInt()
	{
		return (int) value;
	}
	
	public double getDouble()
	{
		return value;
	}
	
	public long getLong()
	{
		return (long) value;
	}
	
	public String formatInt()
	{
		return F.f(getInt());
	}
	
	public String formatLong()
	{
		return F.f(getLong());
	}
	
	public String formatDouble(int dec)
	{
		return F.f(getDouble(), dec);
	}
	
	public String formatPercent(int dec)
	{
		return F.pc(getDouble(), dec);
	}
	
	public String formatPercent()
	{
		return F.pc(getDouble());
	}
	
	public void setInt(int i)
	{
		value = i;
	}
	
	public void setLong(long l)
	{
		value = l;
	}
	
	public void setDouble(double d)
	{
		value = d;
	}
}
