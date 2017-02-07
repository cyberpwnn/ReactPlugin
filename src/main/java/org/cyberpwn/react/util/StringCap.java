package org.cyberpwn.react.util;

public class StringCap
{
	private String string;
	private int max;
	private int index;
	
	public StringCap(String string, int max)
	{
		this.string = string;
		this.max = max;
		index = 0;
	}
	
	public void shift(int index)
	{
		this.index = index;
	}
	
	public void shiftYaw(double yaw)
	{
		double v = (yaw / 360) * getString().length();
		shift((int) v);
	}
	
	@Override
	public String toString()
	{
		String base = string;
		String mod = "";
		int shift = Math.abs(new CNum(string.length()).set(index).get());
		
		for(int i = 0; i < max; i++)
		{
			mod += base.charAt((i + shift) % base.length());
		}
		
		return mod;
	}
	
	public String getString()
	{
		return string;
	}
	
	public int getMax()
	{
		return max;
	}
	
	public int getIndex()
	{
		return index;
	}
}
