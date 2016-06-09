package org.cyberpwn.react.object;

public class Value
{
	private Double number;
	
	public Value(Integer n)
	{
		setNumber(n);
	}
	
	public Value(Long n)
	{
		setNumber(n);
	}
	
	public Value(Double n)
	{
		setNumber(n);
	}
	
	public void setNumber(Integer n)
	{
		this.number = n.doubleValue();
	}
	
	public void setNumber(Long n)
	{
		this.number = n.doubleValue();
	}
	
	public void setNumber(Double n)
	{
		this.number = n;
	}
	
	public Integer getInteger()
	{
		return number.intValue();
	}
	
	public Long getLong()
	{
		return number.longValue();
	}
	
	public Double getDouble()
	{
		return number.doubleValue();
	}
}
