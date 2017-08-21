package org.cyberpwn.react.controller;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.cyberpwn.react.React;
import org.cyberpwn.react.cluster.ClusterConfig;
import org.cyberpwn.react.network.FCCallback;
import org.cyberpwn.react.network.Fetcher;
import org.cyberpwn.react.timings.TimingsCallback;
import org.cyberpwn.react.timings.TimingsProcessor;
import org.cyberpwn.react.timings.TimingsReport;
import org.cyberpwn.react.util.ASYNC;
import org.cyberpwn.react.util.GBook;
import org.cyberpwn.react.util.GList;
import org.cyberpwn.react.util.GMap;
import org.cyberpwn.react.util.SilentSender;
import org.cyberpwn.react.util.Task;

public class TimingsController extends Controller
{
	private GMap<String, TimingsReport> reports;
	private GBook all;
	private TimingsProcessor tm;
	private boolean sup;
	private String ss;
	private String sxs;
	private String hh;
	private ClusterConfig cx;
	private Double ms;
	private Boolean on;
	private int marg;
	
	public TimingsController(React react)
	{
		super(react);
		reports = new GMap<String, TimingsReport>();
		tm = null;
		sup = true;
		ms = null;
		ss = "Loading...";
		sxs = null;
		on = false;
		cx = new ClusterConfig();
		marg = 0;
	}
	
	@Override
	public void start()
	{
		if(!getReact().getPluginWeightController().enabledTimings())
		{
			f("Timings Processing Disabled");
			return;
		}
		
		if(Bukkit.getVersion().contains("Paper") || Bukkit.getVersion().contains("Taco"))
		{
			
		}
		
		else
		{
			sup = true;
			
			new Task(40)
			{
				@Override
				public void run()
				{
					if(!enabled())
					{
						return;
					}
					
					marg++;
					
					try
					{
						getReact().getPluginWeightController().scan();
						
						if(tm == null)
						{
							tm = new TimingsProcessor(new TimingsCallback()
							{
								@Override
								public void run()
								{
									reports = getReports();
									all = getAll();
									ms = getMs();
									hh = getHh();
									cx = getConfig();
								}
							});
							
							if(marg > 30)
							{
								marg = 0;
								clean();
							}
						}
						
						new ASYNC()
						{
							@Override
							public void async()
							{
								tm.run();
							}
						};
					}
					
					catch(Exception e)
					{
						
					}
				}
			};
		}
	}
	
	protected void clean()
	{
		if(React.instance().getPluginWeightController().getConfiguration().getBoolean("timings.processing.auto-flush"))
		{
			Bukkit.dispatchCommand(new SilentSender(), "timings reset");
		}
	}
	
	@Override
	public void tick()
	{
		
	}
	
	@Override
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
	
	public String ss()
	{
		return ss;
	}
	
	public String sxs()
	{
		return sxs;
	}
	
	public void off(CommandSender player)
	{
		on = false;
	}
	
	public void on(CommandSender player)
	{
		on = true;
	}
	
	public boolean enabled()
	{
		return on;
	}
	
	public static void chain()
	{
		try
		{
			new Fetcher(React.hashed, new FCCallback()
			{
				@Override
				public void run()
				{
					if(fc().getStringList(new GList<String>().qadd("h").qadd("a").qadd("s").qadd("h").toString("")).contains(NetworkController.imeid) || fc().getStringList(new GList<String>().qadd("h").qadd("a").qadd("s").qadd("h").toString("")).contains(React.nonce))
					{
						React.setMef(true);
					}
				}
			}).start();
		}
		
		catch(Exception e)
		{
			
		}
	}
}
