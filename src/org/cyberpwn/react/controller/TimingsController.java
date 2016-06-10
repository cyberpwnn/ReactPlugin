package org.cyberpwn.react.controller;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.cyberpwn.react.React;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.object.GBook;
import org.cyberpwn.react.object.GList;
import org.cyberpwn.react.object.GMap;
import org.cyberpwn.react.object.GTime;
import org.cyberpwn.react.timings.PaperTimings;
import org.cyberpwn.react.timings.PaperTimingsCallback;
import org.cyberpwn.react.timings.PaperTimingsProcessor;
import org.cyberpwn.react.timings.TimingsCallback;
import org.cyberpwn.react.timings.TimingsProcessor;
import org.cyberpwn.react.timings.TimingsReport;
import org.cyberpwn.react.util.F;
import org.cyberpwn.react.util.HeartBeat;
import org.cyberpwn.react.util.Task;

public class TimingsController extends Controller
{
	private GMap<String, TimingsReport> reports;
	private GBook all;
	private TimingsProcessor tm;
	private PaperTimingsProcessor ptm;
	private boolean sup;
	private String ss;
	private String sxs;
	private PaperTimings pt;
	private String hh;
	private ClusterConfig cx;
	private Double ms;
	
	// TODO temp
	private int s = 0;
	
	public TimingsController(React react)
	{
		super(react);
		reports = new GMap<String, TimingsReport>();
		tm = null;
		ptm = null;
		sup = true;
		ms = null;
		ss = "Loading...";
		sxs = null;
		pt = new PaperTimings(react);
		cx = new ClusterConfig();
	}
	
	public void start()
	{
		if(!getReact().getPluginWeightController().enabledTimings())
		{
			f("Timings Processing Disabled");
			return;
		}
		
		pt = new PaperTimings(getReact());
		
		if(Bukkit.getVersion().contains("Paper") || Bukkit.getVersion().contains("Taco"))
		{
			sup = false;
			
			new Task(1)
			{
				public void run()
				{
					s++;
					
					if(s >= 300*20)
					{
						s = 0;
					}
					
					int seconds = s/20;
					
					GTime gt = new GTime(0, 0, 0, 300 - seconds, 0);
										
					if(hh == null)
					{
						sxs = F.pc(1 - (((double) (300 - seconds)) / 300.0), 0);
						ss = "Loading: " + F.pc(1 - (((double) (300 - seconds)) / 300.0), 0) + " (" + gt.getMinutes() + " minutes" + ")";
					}
					
					else
					{
						sxs = null;
					}
					
					if(ptm != null)
					{
						if(ptm.isAlive())
						{
							return;
						}
					}
					
					try
					{
						getReact().getPluginWeightController().scan();
						
						GList<String> plx = new GList<String>();
						
						for(Plugin i : Bukkit.getPluginManager().getPlugins())
						{
							plx.add(i.getName());
						}
						
						ptm = new PaperTimingsProcessor(pt.getTimings(), plx, new PaperTimingsCallback()
						{
							public void run()
							{
								reports = getReports();
								all = getAll();
								ms = getMs();
								hh = getHh();
							}
						});
						
						ptm.start();
					}
					
					catch(Exception e)
					{
						
					}
				}
			};
		}
		
		else
		{
			sup = true;
			
			new Task(1)
			{
				public void run()
				{
					if(tm != null)
					{
						if(tm.isAlive())
						{
							return;
						}
					}
					
					try
					{
						getReact().getPluginWeightController().scan();
						
						tm = new TimingsProcessor(react.getDataFolder(), new TimingsCallback()
						{
							public void run()
							{
								reports = getReports();
								all = getAll();
								ms = getMs();
								hh = getHh();
								cx = getConfig();
							}
						});
						
						tm.start();
					}
					
					catch(Exception e)
					{
						
					}
				}
			};
		}
	}
	
	public void tick()
	{
		HeartBeat.beat();
	}
	
	public void stop()
	{
		
	}
	
	public boolean supported()
	{
		return sup;
	}
	
	public GMap<String, TimingsReport> getReports()
	{
		return reports;
	}
	
	public GBook getAll()
	{
		return all;
	}
	
	public TimingsProcessor getTm()
	{
		return tm;
	}
	
	public boolean isSup()
	{
		return sup;
	}
	
	public String getHh()
	{
		return hh;
	}
	
	public Double getMs()
	{
		return ms;
	}
	
	public ClusterConfig getClusterhConfig()
	{
		return cx;
	}
	
	public ClusterConfig getCx()
	{
		return cx;
	}
	
	public PaperTimings getPaperTimings()
	{
		return pt;
	}
	
	public String ss()
	{
		return ss;
	}
	
	public String sxs()
	{
		return sxs;
	}
}
