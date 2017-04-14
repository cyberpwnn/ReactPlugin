package org.cyberpwn.react.util;

public enum LOAD
{
	UNKNOWN(-1),
	IDLE(0),
	LIGHT(1),
	MODERATE(2),
	HEAVY(3),
	INTENSE(4),
	MAX(5);
	
	private int scale;
	
	private LOAD(int scale)
	{
		this.scale = scale;
	}
	
	public int getScale()
	{
		return scale;
	}
	
	public boolean min()
	{
		return UC.getLoad().getScale() <= getScale();
	}
}