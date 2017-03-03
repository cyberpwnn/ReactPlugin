package org.cyberpwn.react.util;

import java.io.IOException;
import org.cyberpwn.react.timings.TimingsReport;

public class TimingsPackage
{
	private GList<TimingsElement> elements;
	
	public TimingsPackage()
	{
		elements = new GList<TimingsElement>();
	}
	
	public String getData() throws IOException
	{
		return toString();
	}
	
	public void fromData(String comp) throws IOException
	{
		fromString(comp);
	}
	
	@Override
	public String toString()
	{
		return elements.toString(":::");
	}
	
	public void fromString(String s)
	{
		elements.clear();
		
		for(String i : s.split(":::"))
		{
			elements.add(new TimingsElement(i));
		}
	}
	
	public void handle(GMap<String, TimingsReport> reports)
	{
		elements.clear();
		
		for(String i : reports.k())
		{
			for(String j : reports.get(i).getData().k())
			{
				elements.add(new TimingsElement("[" + i + "]: " + j, reports.get(i).getData().get(j).getMs(), reports.get(i).getData().get(j).getCount(), reports.get(i).getData().get(j).getViolations()));
			}
		}
	}
}
