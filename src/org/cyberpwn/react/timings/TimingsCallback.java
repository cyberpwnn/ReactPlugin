package org.cyberpwn.react.timings;

import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.object.GBook;
import org.cyberpwn.react.object.GMap;

public class TimingsCallback implements Runnable
{
	private GMap<String, TimingsReport> reports;
	private GBook all;
	private String hh;
	private Double ms;
	private ClusterConfig cc;
	
	public void run(GMap<String, TimingsReport> reports, GBook k, String hh, Double ms, ClusterConfig cc)
	{
		this.reports = reports;
		all = k;
		this.hh = hh;
		this.ms = ms;
		this.cc = cc;
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
	
	public ClusterConfig getConfig()
	{
		return cc;
	}
}
