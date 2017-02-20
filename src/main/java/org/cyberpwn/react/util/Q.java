package org.cyberpwn.react.util;

import org.apache.commons.lang.StringUtils;
import org.cyberpwn.react.React;

public abstract class Q
{
	public enum P
	{
		LOWEST,
		LOW,
		NORMAL,
		HIGH,
		HIGHEST;
	}
	
	private P p;
	private String name;
	private boolean skip;
	
	public Q(P p, String name, boolean skip)
	{
		this.p = p;
		this.name = name;
		this.skip = skip;
		
		React.instance().getTaskManager().queue(this);
	}
	
	public abstract void run();
	
	public boolean skippable()
	{
		return skip;
	}
	
	public P getPriority()
	{
		return p;
	}
	
	public P getP()
	{
		return p;
	}
	
	public String getName()
	{
		return name;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public String toString()
	{
		return name + "(" + StringUtils.capitalise(getPriority().toString().toLowerCase()) + " Priority";
	}
}
