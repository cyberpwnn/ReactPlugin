package org.cyberpwn.react.timings;

import org.cyberpwn.react.util.GMap;
import org.cyberpwn.react.util.Severity;

public class TimingsReport
{
	private GMap<String, Severity> problems;
	private GMap<String, TimingsObject> data;
	
	public TimingsReport()
	{
		this.problems = new GMap<String, Severity>();
		this.data = new GMap<String, TimingsObject>();
	}
	
	public void put(String s, TimingsObject o)
	{
		data.put(s, o);
	}
	
	public void put(String s, Severity o)
	{
		problems.put(s, o);
	}
	
	public GMap<String, Severity> getProblems()
	{
		return problems;
	}
	
	public void setProblems(GMap<String, Severity> problems)
	{
		this.problems = problems;
	}
	
	public GMap<String, TimingsObject> getData()
	{
		return data;
	}
	
	public void setData(GMap<String, TimingsObject> data)
	{
		this.data = data;
	}
}
