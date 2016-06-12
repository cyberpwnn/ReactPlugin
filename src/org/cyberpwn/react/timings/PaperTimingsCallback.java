package org.cyberpwn.react.timings;

import org.cyberpwn.react.util.GBook;
import org.cyberpwn.react.util.GMap;

public class PaperTimingsCallback implements Runnable
{
	private GMap<String, TimingsReport> reports;
	private GBook all;
	private String hh;
	private Double ms;
	
	public void run(GMap<String, TimingsReport> reports, GBook k, String hh, Double ms)
	{
		this.reports = reports;
		all = k;
		this.hh = hh;
		this.ms = ms;
		
		run();
	}
	
	@Override
	public void run()
	{
		
	}
	
	public GMap<String, TimingsReport> getReports()
	{
		return reports;
	}
	
	public GBook getAll()
	{
		return all;
	}
	
	public String getHh()
	{
		return hh;
	}
	
	public Double getMs()
	{
		return ms;
	}
}
